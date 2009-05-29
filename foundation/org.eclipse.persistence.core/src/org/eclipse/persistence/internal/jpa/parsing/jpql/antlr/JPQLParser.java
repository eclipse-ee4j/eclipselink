// $ANTLR 3.0 JPQL.g 2009-05-12 10:01:55

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ABS", "ALL", "AND", "ANY", "AS", "ASC", "AVG", "BETWEEN", "BOTH", "BY", "CONCAT", "COUNT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "DESC", "DELETE", "DISTINCT", "EMPTY", "ENTRY", "ESCAPE", "EXISTS", "FALSE", "FETCH", "FROM", "GROUP", "HAVING", "IN", "INNER", "IS", "JOIN", "KEY", "LEADING", "LEFT", "LENGTH", "LIKE", "LOCATE", "LOWER", "MAX", "MEMBER", "MIN", "MOD", "NEW", "NOT", "NULL", "OBJECT", "OF", "OR", "ORDER", "OUTER", "SELECT", "SET", "SIZE", "SQRT", "SOME", "SUBSTRING", "SUM", "TRAILING", "TRIM", "TRUE", "UNKNOWN", "UPDATE", "UPPER", "VALUE", "WHERE", "IDENT", "COMMA", "EQUALS", "LEFT_ROUND_BRACKET", "RIGHT_ROUND_BRACKET", "DOT", "NOT_EQUAL_TO", "GREATER_THAN", "GREATER_THAN_EQUAL_TO", "LESS_THAN", "LESS_THAN_EQUAL_TO", "PLUS", "MINUS", "MULTIPLY", "DIVIDE", "INTEGER_LITERAL", "LONG_LITERAL", "FLOAT_LITERAL", "DOUBLE_LITERAL", "STRING_LITERAL_DOUBLE_QUOTED", "STRING_LITERAL_SINGLE_QUOTED", "POSITIONAL_PARAM", "NAMED_PARAM", "WS", "TEXTCHAR", "HEX_DIGIT", "HEX_LITERAL", "INTEGER_SUFFIX", "OCTAL_LITERAL", "NUMERIC_DIGITS", "DOUBLE_SUFFIX", "EXPONENT", "FLOAT_SUFFIX"
    };
    public static final int EXPONENT=100;
    public static final int FLOAT_SUFFIX=101;
    public static final int MOD=45;
    public static final int CURRENT_TIME=17;
    public static final int NEW=46;
    public static final int LEFT_ROUND_BRACKET=72;
    public static final int DOUBLE_LITERAL=87;
    public static final int COUNT=15;
    public static final int EQUALS=71;
    public static final int NOT=47;
    public static final int EOF=-1;
    public static final int GREATER_THAN_EQUAL_TO=77;
    public static final int ESCAPE=24;
    public static final int NAMED_PARAM=91;
    public static final int BOTH=12;
    public static final int NUMERIC_DIGITS=98;
    public static final int SELECT=54;
    public static final int DIVIDE=83;
    public static final int ASC=9;
    public static final int CONCAT=14;
    public static final int KEY=35;
    public static final int NULL=48;
    public static final int TRAILING=61;
    public static final int DELETE=20;
    public static final int VALUE=67;
    public static final int OF=50;
    public static final int LEADING=36;
    public static final int INTEGER_SUFFIX=96;
    public static final int EMPTY=22;
    public static final int ABS=4;
    public static final int GROUP=29;
    public static final int NOT_EQUAL_TO=75;
    public static final int WS=92;
    public static final int FETCH=27;
    public static final int STRING_LITERAL_SINGLE_QUOTED=89;
    public static final int INTEGER_LITERAL=84;
    public static final int OR=51;
    public static final int TRIM=62;
    public static final int LESS_THAN=78;
    public static final int RIGHT_ROUND_BRACKET=73;
    public static final int POSITIONAL_PARAM=90;
    public static final int LOWER=41;
    public static final int FROM=28;
    public static final int FALSE=26;
    public static final int LESS_THAN_EQUAL_TO=79;
    public static final int DISTINCT=21;
    public static final int CURRENT_DATE=16;
    public static final int SIZE=56;
    public static final int UPPER=66;
    public static final int WHERE=68;
    public static final int MEMBER=43;
    public static final int INNER=32;
    public static final int ORDER=52;
    public static final int TEXTCHAR=93;
    public static final int MAX=42;
    public static final int UPDATE=65;
    public static final int AND=6;
    public static final int SUM=60;
    public static final int STRING_LITERAL_DOUBLE_QUOTED=88;
    public static final int LENGTH=38;
    public static final int AS=8;
    public static final int IN=31;
    public static final int UNKNOWN=64;
    public static final int OBJECT=49;
    public static final int MULTIPLY=82;
    public static final int COMMA=70;
    public static final int IS=33;
    public static final int LEFT=37;
    public static final int AVG=10;
    public static final int SOME=58;
    public static final int ALL=5;
    public static final int IDENT=69;
    public static final int PLUS=80;
    public static final int HEX_LITERAL=95;
    public static final int EXISTS=25;
    public static final int DOT=74;
    public static final int CURRENT_TIMESTAMP=18;
    public static final int LIKE=39;
    public static final int OUTER=53;
    public static final int BY=13;
    public static final int GREATER_THAN=76;
    public static final int OCTAL_LITERAL=97;
    public static final int HEX_DIGIT=94;
    public static final int SET=55;
    public static final int HAVING=30;
    public static final int ENTRY=23;
    public static final int MIN=44;
    public static final int SQRT=57;
    public static final int MINUS=81;
    public static final int LONG_LITERAL=85;
    public static final int TRUE=63;
    public static final int JOIN=34;
    public static final int SUBSTRING=59;
    public static final int DOUBLE_SUFFIX=99;
    public static final int FLOAT_LITERAL=86;
    public static final int ANY=7;
    public static final int LOCATE=40;
    public static final int DESC=19;
    public static final int BETWEEN=11;
    
        public JPQLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[95+1];
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
    // JPQL.g:188:1: document : (root= selectStatement | root= updateStatement | root= deleteStatement );
    public final void document() throws RecognitionException {
        Object root = null;
        
    
        try {
            // JPQL.g:189:7: (root= selectStatement | root= updateStatement | root= deleteStatement )
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
                    new NoViableAltException("188:1: document : (root= selectStatement | root= updateStatement | root= deleteStatement );", 1, 0, input);
            
                throw nvae;
            }
            
            switch (alt1) {
                case 1 :
                    // JPQL.g:189:7: root= selectStatement
                    {
                    pushFollow(FOLLOW_selectStatement_in_document664);
                    root=selectStatement();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      queryRoot = root;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:190:7: root= updateStatement
                    {
                    pushFollow(FOLLOW_updateStatement_in_document678);
                    root=updateStatement();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      queryRoot = root;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:191:7: root= deleteStatement
                    {
                    pushFollow(FOLLOW_deleteStatement_in_document692);
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
    // JPQL.g:194:1: selectStatement returns [Object node] : select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF ;
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
            // JPQL.g:198:7: (select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF )
            // JPQL.g:198:7: select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF
            {
            pushFollow(FOLLOW_selectClause_in_selectStatement725);
            select=selectClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_fromClause_in_selectStatement740);
            from=fromClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g:200:7: (where= whereClause )?
            int alt2=2;
            int LA2_0 = input.LA(1);
            
            if ( (LA2_0==WHERE) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // JPQL.g:200:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_selectStatement755);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:201:7: (groupBy= groupByClause )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            
            if ( (LA3_0==GROUP) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // JPQL.g:201:8: groupBy= groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_selectStatement770);
                    groupBy=groupByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:202:7: (having= havingClause )?
            int alt4=2;
            int LA4_0 = input.LA(1);
            
            if ( (LA4_0==HAVING) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // JPQL.g:202:8: having= havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_selectStatement786);
                    having=havingClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:203:7: (orderBy= orderByClause )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            
            if ( (LA5_0==ORDER) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // JPQL.g:203:8: orderBy= orderByClause
                    {
                    pushFollow(FOLLOW_orderByClause_in_selectStatement801);
                    orderBy=orderByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_selectStatement811); if (failed) return node;
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
    // JPQL.g:213:1: updateStatement returns [Object node] : update= updateClause set= setClause (where= whereClause )? EOF ;
    public final Object updateStatement() throws RecognitionException {

        Object node = null;
    
        Object update = null;

        Object set = null;

        Object where = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:217:7: (update= updateClause set= setClause (where= whereClause )? EOF )
            // JPQL.g:217:7: update= updateClause set= setClause (where= whereClause )? EOF
            {
            pushFollow(FOLLOW_updateClause_in_updateStatement854);
            update=updateClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_setClause_in_updateStatement869);
            set=setClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g:219:7: (where= whereClause )?
            int alt6=2;
            int LA6_0 = input.LA(1);
            
            if ( (LA6_0==WHERE) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // JPQL.g:219:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_updateStatement883);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_updateStatement893); if (failed) return node;
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
    // JPQL.g:223:1: updateClause returns [Object node] : u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )? ;
    public final Object updateClause() throws RecognitionException {

        Object node = null;
    
        Token u=null;
        Token ident=null;
        String schema = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:227:7: (u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )? )
            // JPQL.g:227:7: u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )?
            {
            u=(Token)input.LT(1);
            match(input,UPDATE,FOLLOW_UPDATE_in_updateClause925); if (failed) return node;
            pushFollow(FOLLOW_abstractSchemaName_in_updateClause931);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // JPQL.g:228:9: ( ( AS )? ident= IDENT )?
            int alt8=2;
            int LA8_0 = input.LA(1);
            
            if ( (LA8_0==AS||LA8_0==IDENT) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // JPQL.g:228:10: ( AS )? ident= IDENT
                    {
                    // JPQL.g:228:10: ( AS )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);
                    
                    if ( (LA7_0==AS) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // JPQL.g:228:11: AS
                            {
                            match(input,AS,FOLLOW_AS_in_updateClause944); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    ident=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_updateClause952); if (failed) return node;
                    
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
    // JPQL.g:239:1: setClause returns [Object node] : t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )* ;
    public final Object setClause() throws RecognitionException {
        setClause_stack.push(new setClause_scope());

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         
            node = null; 
            ((setClause_scope)setClause_stack.peek()).assignments = new ArrayList();
    
        try {
            // JPQL.g:247:7: (t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )* )
            // JPQL.g:247:7: t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )*
            {
            t=(Token)input.LT(1);
            match(input,SET,FOLLOW_SET_in_setClause1001); if (failed) return node;
            pushFollow(FOLLOW_setAssignmentClause_in_setClause1007);
            n=setAssignmentClause();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((setClause_scope)setClause_stack.peek()).assignments.add(n); 
            }
            // JPQL.g:248:9: ( COMMA n= setAssignmentClause )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);
                
                if ( (LA9_0==COMMA) ) {
                    alt9=1;
                }
                
            
                switch (alt9) {
            	case 1 :
            	    // JPQL.g:248:10: COMMA n= setAssignmentClause
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_setClause1020); if (failed) return node;
            	    pushFollow(FOLLOW_setAssignmentClause_in_setClause1026);
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
    // JPQL.g:252:1: setAssignmentClause returns [Object node] : target= setAssignmentTarget t= EQUALS value= newValue ;
    public final Object setAssignmentClause() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object target = null;

        Object value = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:260:7: (target= setAssignmentTarget t= EQUALS value= newValue )
            // JPQL.g:260:7: target= setAssignmentTarget t= EQUALS value= newValue
            {
            pushFollow(FOLLOW_setAssignmentTarget_in_setAssignmentClause1084);
            target=setAssignmentTarget();
            _fsp--;
            if (failed) return node;
            t=(Token)input.LT(1);
            match(input,EQUALS,FOLLOW_EQUALS_in_setAssignmentClause1088); if (failed) return node;
            pushFollow(FOLLOW_newValue_in_setAssignmentClause1094);
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
    // JPQL.g:263:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );
    public final Object setAssignmentTarget() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:267:7: (n= attribute | n= pathExpression )
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
                        new NoViableAltException("263:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 1, input);
                
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
                        new NoViableAltException("263:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 2, input);
                
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
                        new NoViableAltException("263:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 3, input);
                
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
            case CONCAT:
            case COUNT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case DESC:
            case DELETE:
            case DISTINCT:
            case EMPTY:
            case ENTRY:
            case ESCAPE:
            case EXISTS:
            case FALSE:
            case FETCH:
            case FROM:
            case GROUP:
            case HAVING:
            case IN:
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
            case TRAILING:
            case TRIM:
            case TRUE:
            case UNKNOWN:
            case UPDATE:
            case UPPER:
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
                    new NoViableAltException("263:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 0, input);
            
                throw nvae;
            }
            
            switch (alt10) {
                case 1 :
                    // JPQL.g:267:7: n= attribute
                    {
                    pushFollow(FOLLOW_attribute_in_setAssignmentTarget1124);
                    n=attribute();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:268:7: n= pathExpression
                    {
                    pushFollow(FOLLOW_pathExpression_in_setAssignmentTarget1139);
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
    // JPQL.g:271:1: newValue returns [Object node] : (n= simpleArithmeticExpression | n1= NULL );
    public final Object newValue() throws RecognitionException {

        Object node = null;
    
        Token n1=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:273:7: (n= simpleArithmeticExpression | n1= NULL )
            int alt11=2;
            int LA11_0 = input.LA(1);
            
            if ( (LA11_0==ABS||LA11_0==AVG||(LA11_0>=CONCAT && LA11_0<=CURRENT_TIMESTAMP)||LA11_0==FALSE||LA11_0==KEY||LA11_0==LENGTH||(LA11_0>=LOCATE && LA11_0<=MAX)||(LA11_0>=MIN && LA11_0<=MOD)||(LA11_0>=SIZE && LA11_0<=SQRT)||(LA11_0>=SUBSTRING && LA11_0<=SUM)||(LA11_0>=TRIM && LA11_0<=TRUE)||(LA11_0>=UPPER && LA11_0<=VALUE)||LA11_0==IDENT||LA11_0==LEFT_ROUND_BRACKET||(LA11_0>=PLUS && LA11_0<=MINUS)||(LA11_0>=INTEGER_LITERAL && LA11_0<=NAMED_PARAM)) ) {
                alt11=1;
            }
            else if ( (LA11_0==NULL) ) {
                alt11=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("271:1: newValue returns [Object node] : (n= simpleArithmeticExpression | n1= NULL );", 11, 0, input);
            
                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // JPQL.g:273:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_newValue1171);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:274:7: n1= NULL
                    {
                    n1=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_newValue1185); if (failed) return node;
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
    // JPQL.g:280:1: deleteStatement returns [Object node] : delete= deleteClause (where= whereClause )? EOF ;
    public final Object deleteStatement() throws RecognitionException {

        Object node = null;
    
        Object delete = null;

        Object where = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:284:7: (delete= deleteClause (where= whereClause )? EOF )
            // JPQL.g:284:7: delete= deleteClause (where= whereClause )? EOF
            {
            pushFollow(FOLLOW_deleteClause_in_deleteStatement1229);
            delete=deleteClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g:285:7: (where= whereClause )?
            int alt12=2;
            int LA12_0 = input.LA(1);
            
            if ( (LA12_0==WHERE) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // JPQL.g:285:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_deleteStatement1242);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_deleteStatement1252); if (failed) return node;
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
    // JPQL.g:289:1: deleteClause returns [Object node] : t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )? ;
    public final Object deleteClause() throws RecognitionException {
        deleteClause_stack.push(new deleteClause_scope());

        Object node = null;
    
        Token t=null;
        Token ident=null;
        String schema = null;
        
    
         
            node = null; 
            ((deleteClause_scope)deleteClause_stack.peek()).variable = null;
    
        try {
            // JPQL.g:297:7: (t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )? )
            // JPQL.g:297:7: t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )?
            {
            t=(Token)input.LT(1);
            match(input,DELETE,FOLLOW_DELETE_in_deleteClause1285); if (failed) return node;
            match(input,FROM,FOLLOW_FROM_in_deleteClause1287); if (failed) return node;
            pushFollow(FOLLOW_abstractSchemaName_in_deleteClause1293);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // JPQL.g:298:9: ( ( AS )? ident= IDENT )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            
            if ( (LA14_0==AS||LA14_0==IDENT) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // JPQL.g:298:10: ( AS )? ident= IDENT
                    {
                    // JPQL.g:298:10: ( AS )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);
                    
                    if ( (LA13_0==AS) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // JPQL.g:298:11: AS
                            {
                            match(input,AS,FOLLOW_AS_in_deleteClause1306); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    ident=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_deleteClause1312); if (failed) return node;
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
    }
    protected Stack selectClause_stack = new Stack();
    
    
    // $ANTLR start selectClause
    // JPQL.g:307:1: selectClause returns [Object node] : t= SELECT ( DISTINCT )? n= selectExpression ( COMMA n= selectExpression )* ;
    public final Object selectClause() throws RecognitionException {
        selectClause_stack.push(new selectClause_scope());

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         
            node = null;
            ((selectClause_scope)selectClause_stack.peek()).distinct = false;
            ((selectClause_scope)selectClause_stack.peek()).exprs = new ArrayList();
    
        try {
            // JPQL.g:317:7: (t= SELECT ( DISTINCT )? n= selectExpression ( COMMA n= selectExpression )* )
            // JPQL.g:317:7: t= SELECT ( DISTINCT )? n= selectExpression ( COMMA n= selectExpression )*
            {
            t=(Token)input.LT(1);
            match(input,SELECT,FOLLOW_SELECT_in_selectClause1359); if (failed) return node;
            // JPQL.g:317:16: ( DISTINCT )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            
            if ( (LA15_0==DISTINCT) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // JPQL.g:317:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_selectClause1362); if (failed) return node;
                    if ( backtracking==0 ) {
                       ((selectClause_scope)selectClause_stack.peek()).distinct = true; 
                    }
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_selectExpression_in_selectClause1378);
            n=selectExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((selectClause_scope)selectClause_stack.peek()).exprs.add(n); 
            }
            // JPQL.g:319:7: ( COMMA n= selectExpression )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);
                
                if ( (LA16_0==COMMA) ) {
                    alt16=1;
                }
                
            
                switch (alt16) {
            	case 1 :
            	    // JPQL.g:319:9: COMMA n= selectExpression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_selectClause1390); if (failed) return node;
            	    pushFollow(FOLLOW_selectExpression_in_selectClause1396);
            	    n=selectExpression();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((selectClause_scope)selectClause_stack.peek()).exprs.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop16;
                }
            } while (true);

            if ( backtracking==0 ) {
               
                          node = factory.newSelectClause(t.getLine(), t.getCharPositionInLine(), 
                                                         ((selectClause_scope)selectClause_stack.peek()).distinct, ((selectClause_scope)selectClause_stack.peek()).exprs); 
                      
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

    
    // $ANTLR start selectExpression
    // JPQL.g:326:1: selectExpression returns [Object node] : (n= pathExprOrVariableAccess | n= aggregateExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );
    public final Object selectExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:328:7: (n= pathExprOrVariableAccess | n= aggregateExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression )
            int alt17=5;
            switch ( input.LA(1) ) {
            case KEY:
            case VALUE:
            case IDENT:
                {
                alt17=1;
                }
                break;
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case SUM:
                {
                alt17=2;
                }
                break;
            case OBJECT:
                {
                alt17=3;
                }
                break;
            case NEW:
                {
                alt17=4;
                }
                break;
            case ENTRY:
                {
                alt17=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("326:1: selectExpression returns [Object node] : (n= pathExprOrVariableAccess | n= aggregateExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 17, 0, input);
            
                throw nvae;
            }
            
            switch (alt17) {
                case 1 :
                    // JPQL.g:328:7: n= pathExprOrVariableAccess
                    {
                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_selectExpression1442);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:329:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_selectExpression1456);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:330:7: OBJECT LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET
                    {
                    match(input,OBJECT,FOLLOW_OBJECT_in_selectExpression1466); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1468); if (failed) return node;
                    pushFollow(FOLLOW_variableAccess_in_selectExpression1474);
                    n=variableAccess();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1476); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:331:7: n= constructorExpression
                    {
                    pushFollow(FOLLOW_constructorExpression_in_selectExpression1491);
                    n=constructorExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:332:7: n= mapEntryExpression
                    {
                    pushFollow(FOLLOW_mapEntryExpression_in_selectExpression1506);
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
    // JPQL.g:335:1: mapEntryExpression returns [Object node] : l= ENTRY LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET ;
    public final Object mapEntryExpression() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:337:7: (l= ENTRY LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET )
            // JPQL.g:337:7: l= ENTRY LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,ENTRY,FOLLOW_ENTRY_in_mapEntryExpression1538); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mapEntryExpression1540); if (failed) return node;
            pushFollow(FOLLOW_variableAccess_in_mapEntryExpression1546);
            n=variableAccess();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mapEntryExpression1548); if (failed) return node;
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
    // JPQL.g:340:1: pathExprOrVariableAccess returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )* ;
    public final Object pathExprOrVariableAccess() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g:344:7: (n= qualifiedIdentificationVariable (d= DOT right= attribute )* )
            // JPQL.g:344:7: n= qualifiedIdentificationVariable (d= DOT right= attribute )*
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_pathExprOrVariableAccess1580);
            n=qualifiedIdentificationVariable();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:345:9: (d= DOT right= attribute )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);
                
                if ( (LA18_0==DOT) ) {
                    alt18=1;
                }
                
            
                switch (alt18) {
            	case 1 :
            	    // JPQL.g:345:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pathExprOrVariableAccess1595); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_pathExprOrVariableAccess1601);
            	    right=attribute();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newDot(d.getLine(), d.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop18;
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
    // JPQL.g:350:1: qualifiedIdentificationVariable returns [Object node] : (n= variableAccess | l= KEY LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET );
    public final Object qualifiedIdentificationVariable() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:352:7: (n= variableAccess | l= KEY LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET )
            int alt19=3;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                alt19=1;
                }
                break;
            case KEY:
                {
                alt19=2;
                }
                break;
            case VALUE:
                {
                alt19=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("350:1: qualifiedIdentificationVariable returns [Object node] : (n= variableAccess | l= KEY LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET );", 19, 0, input);
            
                throw nvae;
            }
            
            switch (alt19) {
                case 1 :
                    // JPQL.g:352:7: n= variableAccess
                    {
                    pushFollow(FOLLOW_variableAccess_in_qualifiedIdentificationVariable1657);
                    n=variableAccess();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:353:7: l= KEY LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET
                    {
                    l=(Token)input.LT(1);
                    match(input,KEY,FOLLOW_KEY_in_qualifiedIdentificationVariable1671); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1673); if (failed) return node;
                    pushFollow(FOLLOW_variableAccess_in_qualifiedIdentificationVariable1679);
                    n=variableAccess();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1681); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newKey(l.getLine(), l.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:354:7: l= VALUE LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET
                    {
                    l=(Token)input.LT(1);
                    match(input,VALUE,FOLLOW_VALUE_in_qualifiedIdentificationVariable1696); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1698); if (failed) return node;
                    pushFollow(FOLLOW_variableAccess_in_qualifiedIdentificationVariable1704);
                    n=variableAccess();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1706); if (failed) return node;
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
    // JPQL.g:357:1: aggregateExpression returns [Object node] : (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET );
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
            // JPQL.g:365:7: (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET )
            int alt25=5;
            switch ( input.LA(1) ) {
            case AVG:
                {
                alt25=1;
                }
                break;
            case MAX:
                {
                alt25=2;
                }
                break;
            case MIN:
                {
                alt25=3;
                }
                break;
            case SUM:
                {
                alt25=4;
                }
                break;
            case COUNT:
                {
                alt25=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("357:1: aggregateExpression returns [Object node] : (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET );", 25, 0, input);
            
                throw nvae;
            }
            
            switch (alt25) {
                case 1 :
                    // JPQL.g:365:7: t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t1=(Token)input.LT(1);
                    match(input,AVG,FOLLOW_AVG_in_aggregateExpression1739); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1741); if (failed) return node;
                    // JPQL.g:365:33: ( DISTINCT )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);
                    
                    if ( (LA20_0==DISTINCT) ) {
                        alt20=1;
                    }
                    switch (alt20) {
                        case 1 :
                            // JPQL.g:365:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1744); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression1762);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1764); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAvg(t1.getLine(), t1.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:368:7: t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t2=(Token)input.LT(1);
                    match(input,MAX,FOLLOW_MAX_in_aggregateExpression1785); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1787); if (failed) return node;
                    // JPQL.g:368:33: ( DISTINCT )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);
                    
                    if ( (LA21_0==DISTINCT) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // JPQL.g:368:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1790); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression1809);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1811); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newMax(t2.getLine(), t2.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:371:7: t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t3=(Token)input.LT(1);
                    match(input,MIN,FOLLOW_MIN_in_aggregateExpression1831); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1833); if (failed) return node;
                    // JPQL.g:371:33: ( DISTINCT )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);
                    
                    if ( (LA22_0==DISTINCT) ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // JPQL.g:371:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1836); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression1854);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1856); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newMin(t3.getLine(), t3.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:374:7: t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t4=(Token)input.LT(1);
                    match(input,SUM,FOLLOW_SUM_in_aggregateExpression1876); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1878); if (failed) return node;
                    // JPQL.g:374:33: ( DISTINCT )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);
                    
                    if ( (LA23_0==DISTINCT) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // JPQL.g:374:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1881); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression1899);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1901); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newSum(t4.getLine(), t4.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:377:7: t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET
                    {
                    t5=(Token)input.LT(1);
                    match(input,COUNT,FOLLOW_COUNT_in_aggregateExpression1921); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1923); if (failed) return node;
                    // JPQL.g:377:35: ( DISTINCT )?
                    int alt24=2;
                    int LA24_0 = input.LA(1);
                    
                    if ( (LA24_0==DISTINCT) ) {
                        alt24=1;
                    }
                    switch (alt24) {
                        case 1 :
                            // JPQL.g:377:36: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1926); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_aggregateExpression1944);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1946); if (failed) return node;
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
    // JPQL.g:382:1: constructorExpression returns [Object node] : t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET ;
    public final Object constructorExpression() throws RecognitionException {
        constructorExpression_stack.push(new constructorExpression_scope());

        Object node = null;
    
        Token t=null;
        String className = null;

        Object n = null;
        
    
         
            node = null;
            ((constructorExpression_scope)constructorExpression_stack.peek()).args = new ArrayList();
    
        try {
            // JPQL.g:390:7: (t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET )
            // JPQL.g:390:7: t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,NEW,FOLLOW_NEW_in_constructorExpression1989); if (failed) return node;
            pushFollow(FOLLOW_constructorName_in_constructorExpression1995);
            className=constructorName();
            _fsp--;
            if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression2005); if (failed) return node;
            pushFollow(FOLLOW_constructorItem_in_constructorExpression2020);
            n=constructorItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((constructorExpression_scope)constructorExpression_stack.peek()).args.add(n); 
            }
            // JPQL.g:393:9: ( COMMA n= constructorItem )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);
                
                if ( (LA26_0==COMMA) ) {
                    alt26=1;
                }
                
            
                switch (alt26) {
            	case 1 :
            	    // JPQL.g:393:11: COMMA n= constructorItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_constructorExpression2035); if (failed) return node;
            	    pushFollow(FOLLOW_constructorItem_in_constructorExpression2041);
            	    n=constructorItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((constructorExpression_scope)constructorExpression_stack.peek()).args.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop26;
                }
            } while (true);

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression2056); if (failed) return node;
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
    // JPQL.g:401:1: constructorName returns [String className] : i1= IDENT ( DOT i2= IDENT )* ;
    public final String constructorName() throws RecognitionException {
        constructorName_stack.push(new constructorName_scope());

        String className = null;
    
        Token i1=null;
        Token i2=null;
    
         
            className = null;
            ((constructorName_scope)constructorName_stack.peek()).buf = new StringBuffer(); 
    
        try {
            // JPQL.g:409:7: (i1= IDENT ( DOT i2= IDENT )* )
            // JPQL.g:409:7: i1= IDENT ( DOT i2= IDENT )*
            {
            i1=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_constructorName2097); if (failed) return className;
            if ( backtracking==0 ) {
               ((constructorName_scope)constructorName_stack.peek()).buf.append(i1.getText()); 
            }
            // JPQL.g:410:9: ( DOT i2= IDENT )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);
                
                if ( (LA27_0==DOT) ) {
                    alt27=1;
                }
                
            
                switch (alt27) {
            	case 1 :
            	    // JPQL.g:410:11: DOT i2= IDENT
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_constructorName2111); if (failed) return className;
            	    i2=(Token)input.LT(1);
            	    match(input,IDENT,FOLLOW_IDENT_in_constructorName2115); if (failed) return className;
            	    if ( backtracking==0 ) {
            	       ((constructorName_scope)constructorName_stack.peek()).buf.append('.').append(i2.getText()); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop27;
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
    // JPQL.g:414:1: constructorItem returns [Object node] : (n= pathExprOrVariableAccess | n= aggregateExpression );
    public final Object constructorItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:416:7: (n= pathExprOrVariableAccess | n= aggregateExpression )
            int alt28=2;
            int LA28_0 = input.LA(1);
            
            if ( (LA28_0==KEY||LA28_0==VALUE||LA28_0==IDENT) ) {
                alt28=1;
            }
            else if ( (LA28_0==AVG||LA28_0==COUNT||LA28_0==MAX||LA28_0==MIN||LA28_0==SUM) ) {
                alt28=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("414:1: constructorItem returns [Object node] : (n= pathExprOrVariableAccess | n= aggregateExpression );", 28, 0, input);
            
                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // JPQL.g:416:7: n= pathExprOrVariableAccess
                    {
                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_constructorItem2159);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:417:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_constructorItem2173);
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
    // JPQL.g:420:1: fromClause returns [Object node] : t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )* ;
    public final Object fromClause() throws RecognitionException {
        fromClause_stack.push(new fromClause_scope());

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         
            node = null; 
            ((fromClause_scope)fromClause_stack.peek()).varDecls = new ArrayList();
    
        try {
            // JPQL.g:428:7: (t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )* )
            // JPQL.g:428:7: t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )*
            {
            t=(Token)input.LT(1);
            match(input,FROM,FOLLOW_FROM_in_fromClause2206); if (failed) return node;
            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2208);
            identificationVariableDeclaration(((fromClause_scope)fromClause_stack.peek()).varDecls);
            _fsp--;
            if (failed) return node;
            // JPQL.g:429:9: ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);
                
                if ( (LA30_0==COMMA) ) {
                    alt30=1;
                }
                
            
                switch (alt30) {
            	case 1 :
            	    // JPQL.g:429:10: COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_fromClause2220); if (failed) return node;
            	    // JPQL.g:429:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )
            	    int alt29=2;
            	    int LA29_0 = input.LA(1);
            	    
            	    if ( (LA29_0==IN) ) {
            	        int LA29_1 = input.LA(2);
            	        
            	        if ( (LA29_1==LEFT_ROUND_BRACKET) ) {
            	            alt29=2;
            	        }
            	        else if ( (LA29_1==AS||LA29_1==IDENT) ) {
            	            alt29=1;
            	        }
            	        else {
            	            if (backtracking>0) {failed=true; return node;}
            	            NoViableAltException nvae =
            	                new NoViableAltException("429:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )", 29, 1, input);
            	        
            	            throw nvae;
            	        }
            	    }
            	    else if ( ((LA29_0>=ABS && LA29_0<=HAVING)||(LA29_0>=INNER && LA29_0<=FLOAT_SUFFIX)) ) {
            	        alt29=1;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return node;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("429:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )", 29, 0, input);
            	    
            	        throw nvae;
            	    }
            	    switch (alt29) {
            	        case 1 :
            	            // JPQL.g:429:19: identificationVariableDeclaration[$fromClause::varDecls]
            	            {
            	            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2225);
            	            identificationVariableDeclaration(((fromClause_scope)fromClause_stack.peek()).varDecls);
            	            _fsp--;
            	            if (failed) return node;
            	            
            	            }
            	            break;
            	        case 2 :
            	            // JPQL.g:430:19: n= collectionMemberDeclaration
            	            {
            	            pushFollow(FOLLOW_collectionMemberDeclaration_in_fromClause2250);
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
            	    break loop30;
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
    // JPQL.g:436:1: identificationVariableDeclaration[List varDecls] : node= rangeVariableDeclaration (node= join )* ;
    public final void identificationVariableDeclaration(List varDecls) throws RecognitionException {
        Object node = null;
        
    
        try {
            // JPQL.g:437:7: (node= rangeVariableDeclaration (node= join )* )
            // JPQL.g:437:7: node= rangeVariableDeclaration (node= join )*
            {
            pushFollow(FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2316);
            node=rangeVariableDeclaration();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               varDecls.add(node); 
            }
            // JPQL.g:438:9: (node= join )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);
                
                if ( (LA31_0==INNER||LA31_0==JOIN||LA31_0==LEFT) ) {
                    alt31=1;
                }
                
            
                switch (alt31) {
            	case 1 :
            	    // JPQL.g:438:11: node= join
            	    {
            	    pushFollow(FOLLOW_join_in_identificationVariableDeclaration2335);
            	    node=join();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       varDecls.add(node); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop31;
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
    // JPQL.g:441:1: rangeVariableDeclaration returns [Object node] : schema= abstractSchemaName ( AS )? i= IDENT ;
    public final Object rangeVariableDeclaration() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        String schema = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:445:7: (schema= abstractSchemaName ( AS )? i= IDENT )
            // JPQL.g:445:7: schema= abstractSchemaName ( AS )? i= IDENT
            {
            pushFollow(FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2370);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // JPQL.g:445:35: ( AS )?
            int alt32=2;
            int LA32_0 = input.LA(1);
            
            if ( (LA32_0==AS) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // JPQL.g:445:36: AS
                    {
                    match(input,AS,FOLLOW_AS_in_rangeVariableDeclaration2373); if (failed) return node;
                    
                    }
                    break;
            
            }

            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_rangeVariableDeclaration2379); if (failed) return node;
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
    // JPQL.g:456:1: abstractSchemaName returns [String schema] : ident= . ;
    public final String abstractSchemaName() throws RecognitionException {

        String schema = null;
    
        Token ident=null;
    
         schema = null; 
        try {
            // JPQL.g:458:7: (ident= . )
            // JPQL.g:458:7: ident= .
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
    // JPQL.g:465:1: join returns [Object node] : outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression ) ;
    public final Object join() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        Token t=null;
        boolean outerJoin = false;

        Object n = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:469:7: (outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression ) )
            // JPQL.g:469:7: outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )
            {
            pushFollow(FOLLOW_joinSpec_in_join2462);
            outerJoin=joinSpec();
            _fsp--;
            if (failed) return node;
            // JPQL.g:470:7: (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )
            int alt34=2;
            int LA34_0 = input.LA(1);
            
            if ( (LA34_0==IDENT) ) {
                alt34=1;
            }
            else if ( (LA34_0==FETCH) ) {
                alt34=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("470:7: (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )", 34, 0, input);
            
                throw nvae;
            }
            switch (alt34) {
                case 1 :
                    // JPQL.g:470:9: n= joinAssociationPathExpression ( AS )? i= IDENT
                    {
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2476);
                    n=joinAssociationPathExpression();
                    _fsp--;
                    if (failed) return node;
                    // JPQL.g:470:43: ( AS )?
                    int alt33=2;
                    int LA33_0 = input.LA(1);
                    
                    if ( (LA33_0==AS) ) {
                        alt33=1;
                    }
                    switch (alt33) {
                        case 1 :
                            // JPQL.g:470:44: AS
                            {
                            match(input,AS,FOLLOW_AS_in_join2479); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_join2485); if (failed) return node;
                    if ( backtracking==0 ) {
                      
                                  node = factory.newJoinVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                     outerJoin, n, i.getText()); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:475:9: t= FETCH n= joinAssociationPathExpression
                    {
                    t=(Token)input.LT(1);
                    match(input,FETCH,FOLLOW_FETCH_in_join2507); if (failed) return node;
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2513);
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
    // JPQL.g:482:1: joinSpec returns [boolean outer] : ( LEFT ( OUTER )? | INNER )? JOIN ;
    public final boolean joinSpec() throws RecognitionException {

        boolean outer = false;
    
         outer = false; 
        try {
            // JPQL.g:484:7: ( ( LEFT ( OUTER )? | INNER )? JOIN )
            // JPQL.g:484:7: ( LEFT ( OUTER )? | INNER )? JOIN
            {
            // JPQL.g:484:7: ( LEFT ( OUTER )? | INNER )?
            int alt36=3;
            int LA36_0 = input.LA(1);
            
            if ( (LA36_0==LEFT) ) {
                alt36=1;
            }
            else if ( (LA36_0==INNER) ) {
                alt36=2;
            }
            switch (alt36) {
                case 1 :
                    // JPQL.g:484:8: LEFT ( OUTER )?
                    {
                    match(input,LEFT,FOLLOW_LEFT_in_joinSpec2559); if (failed) return outer;
                    // JPQL.g:484:13: ( OUTER )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);
                    
                    if ( (LA35_0==OUTER) ) {
                        alt35=1;
                    }
                    switch (alt35) {
                        case 1 :
                            // JPQL.g:484:14: OUTER
                            {
                            match(input,OUTER,FOLLOW_OUTER_in_joinSpec2562); if (failed) return outer;
                            
                            }
                            break;
                    
                    }

                    if ( backtracking==0 ) {
                       outer = true; 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:484:44: INNER
                    {
                    match(input,INNER,FOLLOW_INNER_in_joinSpec2571); if (failed) return outer;
                    
                    }
                    break;
            
            }

            match(input,JOIN,FOLLOW_JOIN_in_joinSpec2577); if (failed) return outer;
            
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
    // JPQL.g:487:1: collectionMemberDeclaration returns [Object node] : t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT ;
    public final Object collectionMemberDeclaration() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Token i=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:489:7: (t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT )
            // JPQL.g:489:7: t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT
            {
            t=(Token)input.LT(1);
            match(input,IN,FOLLOW_IN_in_collectionMemberDeclaration2605); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2607); if (failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2613);
            n=collectionValuedPathExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2615); if (failed) return node;
            // JPQL.g:490:7: ( AS )?
            int alt37=2;
            int LA37_0 = input.LA(1);
            
            if ( (LA37_0==AS) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // JPQL.g:490:8: AS
                    {
                    match(input,AS,FOLLOW_AS_in_collectionMemberDeclaration2625); if (failed) return node;
                    
                    }
                    break;
            
            }

            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_collectionMemberDeclaration2631); if (failed) return node;
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
    // JPQL.g:497:1: collectionValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object collectionValuedPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:499:7: (n= pathExpression )
            // JPQL.g:499:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_collectionValuedPathExpression2669);
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
    // JPQL.g:502:1: associationPathExpression returns [Object node] : n= pathExpression ;
    public final Object associationPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:504:7: (n= pathExpression )
            // JPQL.g:504:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_associationPathExpression2701);
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
    // JPQL.g:507:1: joinAssociationPathExpression returns [Object node] : left= variableAccess d= DOT right= attribute ;
    public final Object joinAssociationPathExpression() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object left = null;

        Object right = null;
        
    
        
            node = null; 
    
        try {
            // JPQL.g:511:7: (left= variableAccess d= DOT right= attribute )
            // JPQL.g:511:7: left= variableAccess d= DOT right= attribute
            {
            pushFollow(FOLLOW_variableAccess_in_joinAssociationPathExpression2733);
            left=variableAccess();
            _fsp--;
            if (failed) return node;
            d=(Token)input.LT(1);
            match(input,DOT,FOLLOW_DOT_in_joinAssociationPathExpression2737); if (failed) return node;
            pushFollow(FOLLOW_attribute_in_joinAssociationPathExpression2743);
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
    // JPQL.g:515:1: singleValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object singleValuedPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:517:7: (n= pathExpression )
            // JPQL.g:517:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_singleValuedPathExpression2783);
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
    // JPQL.g:520:1: stateFieldPathExpression returns [Object node] : n= pathExpression ;
    public final Object stateFieldPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:522:7: (n= pathExpression )
            // JPQL.g:522:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_stateFieldPathExpression2815);
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
    // JPQL.g:525:1: pathExpression returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )+ ;
    public final Object pathExpression() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:529:7: (n= qualifiedIdentificationVariable (d= DOT right= attribute )+ )
            // JPQL.g:529:7: n= qualifiedIdentificationVariable (d= DOT right= attribute )+
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_pathExpression2847);
            n=qualifiedIdentificationVariable();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:530:9: (d= DOT right= attribute )+
            int cnt38=0;
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);
                
                if ( (LA38_0==DOT) ) {
                    alt38=1;
                }
                
            
                switch (alt38) {
            	case 1 :
            	    // JPQL.g:530:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pathExpression2862); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_pathExpression2868);
            	    right=attribute();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      
            	                      node = factory.newDot(d.getLine(), d.getCharPositionInLine(), node, right); 
            	                  
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt38 >= 1 ) break loop38;
            	    if (backtracking>0) {failed=true; return node;}
                        EarlyExitException eee =
                            new EarlyExitException(38, input);
                        throw eee;
                }
                cnt38++;
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
    // JPQL.g:541:1: attribute returns [Object node] : i= . ;
    public final Object attribute() throws RecognitionException {

        Object node = null;
    
        Token i=null;
    
         node = null; 
        try {
            // JPQL.g:544:7: (i= . )
            // JPQL.g:544:7: i= .
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

    
    // $ANTLR start variableAccess
    // JPQL.g:551:1: variableAccess returns [Object node] : i= IDENT ;
    public final Object variableAccess() throws RecognitionException {

        Object node = null;
    
        Token i=null;
    
         node = null; 
        try {
            // JPQL.g:553:7: (i= IDENT )
            // JPQL.g:553:7: i= IDENT
            {
            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_variableAccess2964); if (failed) return node;
            if ( backtracking==0 ) {
               node = factory.newVariableAccess(i.getLine(), i.getCharPositionInLine(), i.getText()); 
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
    // $ANTLR end variableAccess

    
    // $ANTLR start whereClause
    // JPQL.g:557:1: whereClause returns [Object node] : t= WHERE n= conditionalExpression ;
    public final Object whereClause() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:559:7: (t= WHERE n= conditionalExpression )
            // JPQL.g:559:7: t= WHERE n= conditionalExpression
            {
            t=(Token)input.LT(1);
            match(input,WHERE,FOLLOW_WHERE_in_whereClause3002); if (failed) return node;
            pushFollow(FOLLOW_conditionalExpression_in_whereClause3008);
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
    // JPQL.g:565:1: conditionalExpression returns [Object node] : n= conditionalTerm (t= OR right= conditionalTerm )* ;
    public final Object conditionalExpression() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:569:7: (n= conditionalTerm (t= OR right= conditionalTerm )* )
            // JPQL.g:569:7: n= conditionalTerm (t= OR right= conditionalTerm )*
            {
            pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression3050);
            n=conditionalTerm();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:570:9: (t= OR right= conditionalTerm )*
            loop39:
            do {
                int alt39=2;
                int LA39_0 = input.LA(1);
                
                if ( (LA39_0==OR) ) {
                    alt39=1;
                }
                
            
                switch (alt39) {
            	case 1 :
            	    // JPQL.g:570:10: t= OR right= conditionalTerm
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,OR,FOLLOW_OR_in_conditionalExpression3065); if (failed) return node;
            	    pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression3071);
            	    right=conditionalTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newOr(t.getLine(), t.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop39;
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
    // JPQL.g:575:1: conditionalTerm returns [Object node] : n= conditionalFactor (t= AND right= conditionalFactor )* ;
    public final Object conditionalTerm() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:579:7: (n= conditionalFactor (t= AND right= conditionalFactor )* )
            // JPQL.g:579:7: n= conditionalFactor (t= AND right= conditionalFactor )*
            {
            pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm3126);
            n=conditionalFactor();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:580:9: (t= AND right= conditionalFactor )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);
                
                if ( (LA40_0==AND) ) {
                    alt40=1;
                }
                
            
                switch (alt40) {
            	case 1 :
            	    // JPQL.g:580:10: t= AND right= conditionalFactor
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,AND,FOLLOW_AND_in_conditionalTerm3141); if (failed) return node;
            	    pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm3147);
            	    right=conditionalFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newAnd(t.getLine(), t.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop40;
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
    // JPQL.g:585:1: conditionalFactor returns [Object node] : (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] ) ;
    public final Object conditionalFactor() throws RecognitionException {

        Object node = null;
    
        Token n=null;
        Object n1 = null;
        
    
         node = null; 
        try {
            // JPQL.g:587:7: ( (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] ) )
            // JPQL.g:587:7: (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )
            {
            // JPQL.g:587:7: (n= NOT )?
            int alt41=2;
            int LA41_0 = input.LA(1);
            
            if ( (LA41_0==NOT) ) {
                alt41=1;
            }
            switch (alt41) {
                case 1 :
                    // JPQL.g:587:8: n= NOT
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_conditionalFactor3202); if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:588:9: (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )
            int alt42=2;
            int LA42_0 = input.LA(1);
            
            if ( (LA42_0==ABS||LA42_0==AVG||(LA42_0>=CONCAT && LA42_0<=CURRENT_TIMESTAMP)||LA42_0==FALSE||LA42_0==KEY||LA42_0==LENGTH||(LA42_0>=LOCATE && LA42_0<=MAX)||(LA42_0>=MIN && LA42_0<=MOD)||(LA42_0>=SIZE && LA42_0<=SQRT)||(LA42_0>=SUBSTRING && LA42_0<=SUM)||(LA42_0>=TRIM && LA42_0<=TRUE)||(LA42_0>=UPPER && LA42_0<=VALUE)||LA42_0==IDENT||LA42_0==LEFT_ROUND_BRACKET||(LA42_0>=PLUS && LA42_0<=MINUS)||(LA42_0>=INTEGER_LITERAL && LA42_0<=NAMED_PARAM)) ) {
                alt42=1;
            }
            else if ( (LA42_0==EXISTS) ) {
                alt42=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("588:9: (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )", 42, 0, input);
            
                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // JPQL.g:588:11: n1= conditionalPrimary
                    {
                    pushFollow(FOLLOW_conditionalPrimary_in_conditionalFactor3221);
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
                    // JPQL.g:595:11: n1= existsExpression[(n!=null)]
                    {
                    pushFollow(FOLLOW_existsExpression_in_conditionalFactor3250);
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
    // JPQL.g:599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );
    public final Object conditionalPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:601:7: ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression )
            int alt43=2;
            int LA43_0 = input.LA(1);
            
            if ( (LA43_0==LEFT_ROUND_BRACKET) ) {
                int LA43_1 = input.LA(2);
                
                if ( (LA43_1==PLUS) ) {
                    switch ( input.LA(3) ) {
                    case AVG:
                        {
                        int LA43_74 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 74, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MAX:
                        {
                        int LA43_75 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 75, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MIN:
                        {
                        int LA43_76 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 76, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUM:
                        {
                        int LA43_77 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 77, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COUNT:
                        {
                        int LA43_78 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 78, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA43_79 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 79, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA43_80 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 80, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA43_81 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 81, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case ABS:
                        {
                        int LA43_82 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 82, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LENGTH:
                        {
                        int LA43_83 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 83, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MOD:
                        {
                        int LA43_84 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 84, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SQRT:
                        {
                        int LA43_85 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 85, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOCATE:
                        {
                        int LA43_86 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 86, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SIZE:
                        {
                        int LA43_87 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 87, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_DATE:
                        {
                        int LA43_88 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 88, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_TIME:
                        {
                        int LA43_89 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 89, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_TIMESTAMP:
                        {
                        int LA43_90 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 90, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CONCAT:
                        {
                        int LA43_91 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 91, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUBSTRING:
                        {
                        int LA43_92 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 92, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TRIM:
                        {
                        int LA43_93 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 93, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case UPPER:
                        {
                        int LA43_94 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 94, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOWER:
                        {
                        int LA43_95 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 95, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case POSITIONAL_PARAM:
                        {
                        int LA43_96 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 96, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NAMED_PARAM:
                        {
                        int LA43_97 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 97, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INTEGER_LITERAL:
                        {
                        int LA43_98 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 98, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LONG_LITERAL:
                        {
                        int LA43_99 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 99, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FLOAT_LITERAL:
                        {
                        int LA43_100 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 100, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case DOUBLE_LITERAL:
                        {
                        int LA43_101 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 101, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case STRING_LITERAL_DOUBLE_QUOTED:
                        {
                        int LA43_102 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 102, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case STRING_LITERAL_SINGLE_QUOTED:
                        {
                        int LA43_103 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 103, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TRUE:
                        {
                        int LA43_104 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 104, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FALSE:
                        {
                        int LA43_105 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 105, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_ROUND_BRACKET:
                        {
                        int LA43_106 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 106, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 36, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA43_1==MINUS) ) {
                    switch ( input.LA(3) ) {
                    case AVG:
                        {
                        int LA43_107 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 107, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MAX:
                        {
                        int LA43_108 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 108, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MIN:
                        {
                        int LA43_109 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 109, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUM:
                        {
                        int LA43_110 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 110, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COUNT:
                        {
                        int LA43_111 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 111, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA43_112 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 112, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA43_113 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 113, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA43_114 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 114, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case ABS:
                        {
                        int LA43_115 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 115, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LENGTH:
                        {
                        int LA43_116 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 116, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MOD:
                        {
                        int LA43_117 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 117, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SQRT:
                        {
                        int LA43_118 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 118, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOCATE:
                        {
                        int LA43_119 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 119, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SIZE:
                        {
                        int LA43_120 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 120, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_DATE:
                        {
                        int LA43_121 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 121, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_TIME:
                        {
                        int LA43_122 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 122, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_TIMESTAMP:
                        {
                        int LA43_123 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 123, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CONCAT:
                        {
                        int LA43_124 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 124, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUBSTRING:
                        {
                        int LA43_125 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 125, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TRIM:
                        {
                        int LA43_126 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 126, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case UPPER:
                        {
                        int LA43_127 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 127, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOWER:
                        {
                        int LA43_128 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 128, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case POSITIONAL_PARAM:
                        {
                        int LA43_129 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 129, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NAMED_PARAM:
                        {
                        int LA43_130 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 130, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INTEGER_LITERAL:
                        {
                        int LA43_131 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 131, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LONG_LITERAL:
                        {
                        int LA43_132 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 132, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FLOAT_LITERAL:
                        {
                        int LA43_133 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 133, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case DOUBLE_LITERAL:
                        {
                        int LA43_134 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 134, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case STRING_LITERAL_DOUBLE_QUOTED:
                        {
                        int LA43_135 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 135, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case STRING_LITERAL_SINGLE_QUOTED:
                        {
                        int LA43_136 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 136, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TRUE:
                        {
                        int LA43_137 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 137, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FALSE:
                        {
                        int LA43_138 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 138, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_ROUND_BRACKET:
                        {
                        int LA43_139 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 139, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 37, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA43_1==AVG) ) {
                    int LA43_38 = input.LA(3);
                    
                    if ( (LA43_38==LEFT_ROUND_BRACKET) ) {
                        int LA43_140 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 140, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 38, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==MAX) ) {
                    int LA43_39 = input.LA(3);
                    
                    if ( (LA43_39==LEFT_ROUND_BRACKET) ) {
                        int LA43_141 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 141, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 39, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==MIN) ) {
                    int LA43_40 = input.LA(3);
                    
                    if ( (LA43_40==LEFT_ROUND_BRACKET) ) {
                        int LA43_142 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 142, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 40, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==SUM) ) {
                    int LA43_41 = input.LA(3);
                    
                    if ( (LA43_41==LEFT_ROUND_BRACKET) ) {
                        int LA43_143 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 143, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 41, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==COUNT) ) {
                    int LA43_42 = input.LA(3);
                    
                    if ( (LA43_42==LEFT_ROUND_BRACKET) ) {
                        int LA43_144 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 144, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 42, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==IDENT) ) {
                    int LA43_43 = input.LA(3);
                    
                    if ( (LA43_43==DOT) ) {
                        int LA43_145 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 145, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_43==MULTIPLY) ) {
                        int LA43_146 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 146, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_43==DIVIDE) ) {
                        int LA43_147 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 147, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_43==PLUS) ) {
                        int LA43_148 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 148, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_43==MINUS) ) {
                        int LA43_149 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 149, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_43==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_43==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_43==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_43==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_43==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_43==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_43==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_43==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_43==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_43==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_43==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_43==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_43==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 43, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==KEY) ) {
                    int LA43_44 = input.LA(3);
                    
                    if ( (LA43_44==LEFT_ROUND_BRACKET) ) {
                        int LA43_163 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 163, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 44, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==VALUE) ) {
                    int LA43_45 = input.LA(3);
                    
                    if ( (LA43_45==LEFT_ROUND_BRACKET) ) {
                        int LA43_164 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 164, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 45, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==ABS) ) {
                    int LA43_46 = input.LA(3);
                    
                    if ( (LA43_46==LEFT_ROUND_BRACKET) ) {
                        int LA43_165 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 165, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 46, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==LENGTH) ) {
                    int LA43_47 = input.LA(3);
                    
                    if ( (LA43_47==LEFT_ROUND_BRACKET) ) {
                        int LA43_166 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 166, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 47, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==MOD) ) {
                    int LA43_48 = input.LA(3);
                    
                    if ( (LA43_48==LEFT_ROUND_BRACKET) ) {
                        int LA43_167 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 167, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 48, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==SQRT) ) {
                    int LA43_49 = input.LA(3);
                    
                    if ( (LA43_49==LEFT_ROUND_BRACKET) ) {
                        int LA43_168 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 168, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 49, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==LOCATE) ) {
                    int LA43_50 = input.LA(3);
                    
                    if ( (LA43_50==LEFT_ROUND_BRACKET) ) {
                        int LA43_169 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 169, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 50, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==SIZE) ) {
                    int LA43_51 = input.LA(3);
                    
                    if ( (LA43_51==LEFT_ROUND_BRACKET) ) {
                        int LA43_170 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 170, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 51, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==CURRENT_DATE) ) {
                    int LA43_52 = input.LA(3);
                    
                    if ( (LA43_52==MULTIPLY) ) {
                        int LA43_171 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 171, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_52==DIVIDE) ) {
                        int LA43_172 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 172, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_52==PLUS) ) {
                        int LA43_173 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 173, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_52==MINUS) ) {
                        int LA43_174 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 174, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_52==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else if ( (LA43_52==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_52==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_52==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_52==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_52==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_52==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_52==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_52==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_52==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_52==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_52==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_52==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 52, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==CURRENT_TIME) ) {
                    int LA43_53 = input.LA(3);
                    
                    if ( (LA43_53==MULTIPLY) ) {
                        int LA43_188 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 188, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_53==DIVIDE) ) {
                        int LA43_189 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 189, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_53==PLUS) ) {
                        int LA43_190 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 190, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_53==MINUS) ) {
                        int LA43_191 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 191, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_53==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else if ( (LA43_53==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_53==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_53==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_53==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_53==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_53==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_53==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_53==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_53==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_53==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_53==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_53==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 53, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==CURRENT_TIMESTAMP) ) {
                    int LA43_54 = input.LA(3);
                    
                    if ( (LA43_54==MULTIPLY) ) {
                        int LA43_205 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 205, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_54==DIVIDE) ) {
                        int LA43_206 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 206, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_54==PLUS) ) {
                        int LA43_207 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 207, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_54==MINUS) ) {
                        int LA43_208 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 208, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_54==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else if ( (LA43_54==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_54==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_54==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_54==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_54==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_54==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_54==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_54==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_54==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_54==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_54==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_54==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 54, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==CONCAT) ) {
                    int LA43_55 = input.LA(3);
                    
                    if ( (LA43_55==LEFT_ROUND_BRACKET) ) {
                        int LA43_222 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 222, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 55, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==SUBSTRING) ) {
                    int LA43_56 = input.LA(3);
                    
                    if ( (LA43_56==LEFT_ROUND_BRACKET) ) {
                        int LA43_223 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 223, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 56, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==TRIM) ) {
                    int LA43_57 = input.LA(3);
                    
                    if ( (LA43_57==LEFT_ROUND_BRACKET) ) {
                        int LA43_224 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 224, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 57, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==UPPER) ) {
                    int LA43_58 = input.LA(3);
                    
                    if ( (LA43_58==LEFT_ROUND_BRACKET) ) {
                        int LA43_225 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 225, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 58, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==LOWER) ) {
                    int LA43_59 = input.LA(3);
                    
                    if ( (LA43_59==LEFT_ROUND_BRACKET) ) {
                        int LA43_226 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 226, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 59, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==POSITIONAL_PARAM) ) {
                    int LA43_60 = input.LA(3);
                    
                    if ( (LA43_60==MULTIPLY) ) {
                        int LA43_227 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 227, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_60==DIVIDE) ) {
                        int LA43_228 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 228, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_60==PLUS) ) {
                        int LA43_229 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 229, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_60==MINUS) ) {
                        int LA43_230 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 230, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_60==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else if ( (LA43_60==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_60==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_60==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_60==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_60==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_60==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_60==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_60==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_60==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_60==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_60==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_60==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 60, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==NAMED_PARAM) ) {
                    int LA43_61 = input.LA(3);
                    
                    if ( (LA43_61==MULTIPLY) ) {
                        int LA43_244 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 244, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_61==DIVIDE) ) {
                        int LA43_245 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 245, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_61==PLUS) ) {
                        int LA43_246 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 246, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_61==MINUS) ) {
                        int LA43_247 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 247, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_61==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_61==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_61==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_61==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_61==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_61==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_61==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_61==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_61==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_61==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_61==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_61==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_61==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 61, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==INTEGER_LITERAL) ) {
                    int LA43_62 = input.LA(3);
                    
                    if ( (LA43_62==MULTIPLY) ) {
                        int LA43_261 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 261, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_62==DIVIDE) ) {
                        int LA43_262 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 262, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_62==PLUS) ) {
                        int LA43_263 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 263, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_62==MINUS) ) {
                        int LA43_264 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 264, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_62==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_62==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_62==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_62==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_62==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_62==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_62==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_62==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_62==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_62==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_62==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_62==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_62==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 62, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==LONG_LITERAL) ) {
                    int LA43_63 = input.LA(3);
                    
                    if ( (LA43_63==MULTIPLY) ) {
                        int LA43_278 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 278, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_63==DIVIDE) ) {
                        int LA43_279 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 279, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_63==PLUS) ) {
                        int LA43_280 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 280, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_63==MINUS) ) {
                        int LA43_281 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 281, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_63==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_63==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_63==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_63==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_63==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_63==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_63==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_63==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_63==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_63==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_63==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_63==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_63==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 63, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==FLOAT_LITERAL) ) {
                    int LA43_64 = input.LA(3);
                    
                    if ( (LA43_64==MULTIPLY) ) {
                        int LA43_295 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 295, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_64==DIVIDE) ) {
                        int LA43_296 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 296, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_64==PLUS) ) {
                        int LA43_297 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 297, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_64==MINUS) ) {
                        int LA43_298 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 298, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_64==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_64==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_64==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_64==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_64==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_64==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_64==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_64==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_64==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_64==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_64==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_64==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_64==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 64, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==DOUBLE_LITERAL) ) {
                    int LA43_65 = input.LA(3);
                    
                    if ( (LA43_65==MULTIPLY) ) {
                        int LA43_312 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 312, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_65==DIVIDE) ) {
                        int LA43_313 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 313, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_65==PLUS) ) {
                        int LA43_314 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 314, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_65==MINUS) ) {
                        int LA43_315 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 315, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_65==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else if ( (LA43_65==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_65==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_65==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_65==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_65==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_65==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_65==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_65==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_65==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_65==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_65==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_65==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 65, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==STRING_LITERAL_DOUBLE_QUOTED) ) {
                    int LA43_66 = input.LA(3);
                    
                    if ( (LA43_66==MULTIPLY) ) {
                        int LA43_329 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 329, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_66==DIVIDE) ) {
                        int LA43_330 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 330, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_66==PLUS) ) {
                        int LA43_331 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 331, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_66==MINUS) ) {
                        int LA43_332 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 332, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_66==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else if ( (LA43_66==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_66==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_66==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_66==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_66==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_66==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_66==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_66==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_66==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_66==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_66==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_66==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 66, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==STRING_LITERAL_SINGLE_QUOTED) ) {
                    int LA43_67 = input.LA(3);
                    
                    if ( (LA43_67==MULTIPLY) ) {
                        int LA43_346 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 346, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_67==DIVIDE) ) {
                        int LA43_347 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 347, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_67==PLUS) ) {
                        int LA43_348 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 348, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_67==MINUS) ) {
                        int LA43_349 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 349, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_67==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else if ( (LA43_67==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_67==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_67==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_67==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_67==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_67==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_67==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_67==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_67==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_67==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_67==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_67==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 67, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==TRUE) ) {
                    int LA43_68 = input.LA(3);
                    
                    if ( (LA43_68==MULTIPLY) ) {
                        int LA43_363 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 363, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_68==DIVIDE) ) {
                        int LA43_364 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 364, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_68==PLUS) ) {
                        int LA43_365 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 365, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_68==MINUS) ) {
                        int LA43_366 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 366, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_68==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_68==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_68==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_68==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_68==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_68==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_68==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_68==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_68==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_68==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_68==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_68==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_68==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 68, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==FALSE) ) {
                    int LA43_69 = input.LA(3);
                    
                    if ( (LA43_69==MULTIPLY) ) {
                        int LA43_380 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 380, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_69==DIVIDE) ) {
                        int LA43_381 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 381, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_69==PLUS) ) {
                        int LA43_382 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 382, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_69==MINUS) ) {
                        int LA43_383 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 383, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_69==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_69==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_69==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_69==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_69==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_69==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_69==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_69==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_69==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_69==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_69==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_69==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_69==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 69, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==LEFT_ROUND_BRACKET) ) {
                    int LA43_70 = input.LA(3);
                    
                    if ( (LA43_70==SELECT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_70==PLUS) ) {
                        int LA43_398 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 398, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==MINUS) ) {
                        int LA43_399 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 399, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==AVG) ) {
                        int LA43_400 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 400, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==MAX) ) {
                        int LA43_401 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 401, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==MIN) ) {
                        int LA43_402 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 402, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==SUM) ) {
                        int LA43_403 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 403, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==COUNT) ) {
                        int LA43_404 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 404, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==IDENT) ) {
                        int LA43_405 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 405, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==KEY) ) {
                        int LA43_406 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 406, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==VALUE) ) {
                        int LA43_407 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 407, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==ABS) ) {
                        int LA43_408 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 408, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==LENGTH) ) {
                        int LA43_409 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 409, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==MOD) ) {
                        int LA43_410 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 410, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==SQRT) ) {
                        int LA43_411 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 411, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==LOCATE) ) {
                        int LA43_412 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 412, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==SIZE) ) {
                        int LA43_413 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 413, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==CURRENT_DATE) ) {
                        int LA43_414 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 414, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==CURRENT_TIME) ) {
                        int LA43_415 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 415, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==CURRENT_TIMESTAMP) ) {
                        int LA43_416 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 416, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==CONCAT) ) {
                        int LA43_417 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 417, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==SUBSTRING) ) {
                        int LA43_418 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 418, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==TRIM) ) {
                        int LA43_419 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 419, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==UPPER) ) {
                        int LA43_420 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 420, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==LOWER) ) {
                        int LA43_421 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 421, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==POSITIONAL_PARAM) ) {
                        int LA43_422 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 422, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==NAMED_PARAM) ) {
                        int LA43_423 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 423, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==INTEGER_LITERAL) ) {
                        int LA43_424 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 424, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==LONG_LITERAL) ) {
                        int LA43_425 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 425, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==FLOAT_LITERAL) ) {
                        int LA43_426 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 426, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==DOUBLE_LITERAL) ) {
                        int LA43_427 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 427, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==STRING_LITERAL_DOUBLE_QUOTED) ) {
                        int LA43_428 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 428, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==STRING_LITERAL_SINGLE_QUOTED) ) {
                        int LA43_429 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 429, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==TRUE) ) {
                        int LA43_430 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 430, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==FALSE) ) {
                        int LA43_431 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 431, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==LEFT_ROUND_BRACKET) ) {
                        int LA43_432 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 432, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_70==EXISTS) && (synpred1())) {
                        alt43=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 70, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==NOT) && (synpred1())) {
                    alt43=1;
                }
                else if ( (LA43_1==EXISTS) && (synpred1())) {
                    alt43=1;
                }
                else if ( (LA43_1==SELECT) ) {
                    alt43=2;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA43_0==ABS||LA43_0==AVG||(LA43_0>=CONCAT && LA43_0<=CURRENT_TIMESTAMP)||LA43_0==FALSE||LA43_0==KEY||LA43_0==LENGTH||(LA43_0>=LOCATE && LA43_0<=MAX)||(LA43_0>=MIN && LA43_0<=MOD)||(LA43_0>=SIZE && LA43_0<=SQRT)||(LA43_0>=SUBSTRING && LA43_0<=SUM)||(LA43_0>=TRIM && LA43_0<=TRUE)||(LA43_0>=UPPER && LA43_0<=VALUE)||LA43_0==IDENT||(LA43_0>=PLUS && LA43_0<=MINUS)||(LA43_0>=INTEGER_LITERAL && LA43_0<=NAMED_PARAM)) ) {
                alt43=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("599:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 0, input);
            
                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // JPQL.g:601:7: ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3307); if (failed) return node;
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalPrimary3313);
                    n=conditionalExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3315); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:603:7: n= simpleConditionalExpression
                    {
                    pushFollow(FOLLOW_simpleConditionalExpression_in_conditionalPrimary3329);
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
    // JPQL.g:606:1: simpleConditionalExpression returns [Object node] : left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] ;
    public final Object simpleConditionalExpression() throws RecognitionException {

        Object node = null;
    
        Object left = null;

        Object n = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:610:7: (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] )
            // JPQL.g:610:7: left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node]
            {
            pushFollow(FOLLOW_arithmeticExpression_in_simpleConditionalExpression3361);
            left=arithmeticExpression();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3376);
            n=simpleConditionalExpressionRemainder(left);
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
    // $ANTLR end simpleConditionalExpression

    
    // $ANTLR start simpleConditionalExpressionRemainder
    // JPQL.g:614:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );
    public final Object simpleConditionalExpressionRemainder(Object left) throws RecognitionException {

        Object node = null;
    
        Token n1=null;
        Token n2=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:616:7: (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] )
            int alt46=3;
            switch ( input.LA(1) ) {
            case EQUALS:
            case NOT_EQUAL_TO:
            case GREATER_THAN:
            case GREATER_THAN_EQUAL_TO:
            case LESS_THAN:
            case LESS_THAN_EQUAL_TO:
                {
                alt46=1;
                }
                break;
            case BETWEEN:
            case IN:
            case LIKE:
            case MEMBER:
            case NOT:
                {
                alt46=2;
                }
                break;
            case IS:
                {
                alt46=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("614:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );", 46, 0, input);
            
                throw nvae;
            }
            
            switch (alt46) {
                case 1 :
                    // JPQL.g:616:7: n= comparisonExpression[left]
                    {
                    pushFollow(FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3411);
                    n=comparisonExpression(left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:617:7: (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left]
                    {
                    // JPQL.g:617:7: (n1= NOT )?
                    int alt44=2;
                    int LA44_0 = input.LA(1);
                    
                    if ( (LA44_0==NOT) ) {
                        alt44=1;
                    }
                    switch (alt44) {
                        case 1 :
                            // JPQL.g:617:8: n1= NOT
                            {
                            n1=(Token)input.LT(1);
                            match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3425); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3433);
                    n=conditionWithNotExpression((n1!=null),  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:618:7: IS (n2= NOT )? n= isExpression[(n2!=null), left]
                    {
                    match(input,IS,FOLLOW_IS_in_simpleConditionalExpressionRemainder3444); if (failed) return node;
                    // JPQL.g:618:10: (n2= NOT )?
                    int alt45=2;
                    int LA45_0 = input.LA(1);
                    
                    if ( (LA45_0==NOT) ) {
                        alt45=1;
                    }
                    switch (alt45) {
                        case 1 :
                            // JPQL.g:618:11: n2= NOT
                            {
                            n2=(Token)input.LT(1);
                            match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3449); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3457);
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
    // JPQL.g:621:1: conditionWithNotExpression[boolean not, Object left] returns [Object node] : (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] );
    public final Object conditionWithNotExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:623:7: (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] )
            int alt47=4;
            switch ( input.LA(1) ) {
            case BETWEEN:
                {
                alt47=1;
                }
                break;
            case LIKE:
                {
                alt47=2;
                }
                break;
            case IN:
                {
                alt47=3;
                }
                break;
            case MEMBER:
                {
                alt47=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("621:1: conditionWithNotExpression[boolean not, Object left] returns [Object node] : (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] );", 47, 0, input);
            
                throw nvae;
            }
            
            switch (alt47) {
                case 1 :
                    // JPQL.g:623:7: n= betweenExpression[not, left]
                    {
                    pushFollow(FOLLOW_betweenExpression_in_conditionWithNotExpression3492);
                    n=betweenExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:624:7: n= likeExpression[not, left]
                    {
                    pushFollow(FOLLOW_likeExpression_in_conditionWithNotExpression3507);
                    n=likeExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:625:7: n= inExpression[not, left]
                    {
                    pushFollow(FOLLOW_inExpression_in_conditionWithNotExpression3521);
                    n=inExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:626:7: n= collectionMemberExpression[not, left]
                    {
                    pushFollow(FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3535);
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
    // JPQL.g:629:1: isExpression[boolean not, Object left] returns [Object node] : (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] );
    public final Object isExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:631:7: (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] )
            int alt48=2;
            int LA48_0 = input.LA(1);
            
            if ( (LA48_0==NULL) ) {
                alt48=1;
            }
            else if ( (LA48_0==EMPTY) ) {
                alt48=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("629:1: isExpression[boolean not, Object left] returns [Object node] : (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] );", 48, 0, input);
            
                throw nvae;
            }
            switch (alt48) {
                case 1 :
                    // JPQL.g:631:7: n= nullComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_nullComparisonExpression_in_isExpression3570);
                    n=nullComparisonExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:632:7: n= emptyCollectionComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_emptyCollectionComparisonExpression_in_isExpression3585);
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
    // JPQL.g:635:1: betweenExpression[boolean not, Object left] returns [Object node] : t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression ;
    public final Object betweenExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object lowerBound = null;

        Object upperBound = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g:639:7: (t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression )
            // JPQL.g:639:7: t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression
            {
            t=(Token)input.LT(1);
            match(input,BETWEEN,FOLLOW_BETWEEN_in_betweenExpression3618); if (failed) return node;
            pushFollow(FOLLOW_arithmeticExpression_in_betweenExpression3632);
            lowerBound=arithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,AND,FOLLOW_AND_in_betweenExpression3634); if (failed) return node;
            pushFollow(FOLLOW_arithmeticExpression_in_betweenExpression3640);
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
    // JPQL.g:647:1: inExpression[boolean not, Object left] returns [Object node] : t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET ;
    public final Object inExpression(boolean not, Object left) throws RecognitionException {
        inExpression_stack.push(new inExpression_scope());

        Object node = null;
    
        Token t=null;
        Object itemNode = null;

        Object subqueryNode = null;
        
    
        
            node = null;
            ((inExpression_scope)inExpression_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g:655:7: (t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET )
            // JPQL.g:655:7: t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,IN,FOLLOW_IN_in_inExpression3683); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_inExpression3693); if (failed) return node;
            // JPQL.g:657:9: (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery )
            int alt50=2;
            int LA50_0 = input.LA(1);
            
            if ( ((LA50_0>=INTEGER_LITERAL && LA50_0<=NAMED_PARAM)) ) {
                alt50=1;
            }
            else if ( (LA50_0==SELECT) ) {
                alt50=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("657:9: (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery )", 50, 0, input);
            
                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // JPQL.g:657:11: itemNode= inItem ( COMMA itemNode= inItem )*
                    {
                    pushFollow(FOLLOW_inItem_in_inExpression3709);
                    itemNode=inItem();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       ((inExpression_scope)inExpression_stack.peek()).items.add(itemNode); 
                    }
                    // JPQL.g:658:13: ( COMMA itemNode= inItem )*
                    loop49:
                    do {
                        int alt49=2;
                        int LA49_0 = input.LA(1);
                        
                        if ( (LA49_0==COMMA) ) {
                            alt49=1;
                        }
                        
                    
                        switch (alt49) {
                    	case 1 :
                    	    // JPQL.g:658:15: COMMA itemNode= inItem
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_inExpression3727); if (failed) return node;
                    	    pushFollow(FOLLOW_inItem_in_inExpression3733);
                    	    itemNode=inItem();
                    	    _fsp--;
                    	    if (failed) return node;
                    	    if ( backtracking==0 ) {
                    	       ((inExpression_scope)inExpression_stack.peek()).items.add(itemNode); 
                    	    }
                    	    
                    	    }
                    	    break;
                    
                    	default :
                    	    break loop49;
                        }
                    } while (true);

                    if ( backtracking==0 ) {
                      
                                      node = factory.newIn(t.getLine(), t.getCharPositionInLine(),
                                                           not, left, ((inExpression_scope)inExpression_stack.peek()).items);
                                  
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:663:11: subqueryNode= subquery
                    {
                    pushFollow(FOLLOW_subquery_in_inExpression3768);
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

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression3802); if (failed) return node;
            
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
    // JPQL.g:672:1: inItem returns [Object node] : (n= literalString | n= literalNumeric | n= inputParameter );
    public final Object inItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:674:7: (n= literalString | n= literalNumeric | n= inputParameter )
            int alt51=3;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt51=1;
                }
                break;
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
                {
                alt51=2;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt51=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("672:1: inItem returns [Object node] : (n= literalString | n= literalNumeric | n= inputParameter );", 51, 0, input);
            
                throw nvae;
            }
            
            switch (alt51) {
                case 1 :
                    // JPQL.g:674:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_inItem3832);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:675:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_inItem3846);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:676:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_inItem3860);
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
    // $ANTLR end inItem

    
    // $ANTLR start likeExpression
    // JPQL.g:679:1: likeExpression[boolean not, Object left] returns [Object node] : t= LIKE pattern= likeValue (escapeChars= escape )? ;
    public final Object likeExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object pattern = null;

        Object escapeChars = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g:683:7: (t= LIKE pattern= likeValue (escapeChars= escape )? )
            // JPQL.g:683:7: t= LIKE pattern= likeValue (escapeChars= escape )?
            {
            t=(Token)input.LT(1);
            match(input,LIKE,FOLLOW_LIKE_in_likeExpression3892); if (failed) return node;
            pushFollow(FOLLOW_likeValue_in_likeExpression3898);
            pattern=likeValue();
            _fsp--;
            if (failed) return node;
            // JPQL.g:684:9: (escapeChars= escape )?
            int alt52=2;
            int LA52_0 = input.LA(1);
            
            if ( (LA52_0==ESCAPE) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // JPQL.g:684:10: escapeChars= escape
                    {
                    pushFollow(FOLLOW_escape_in_likeExpression3913);
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
    // JPQL.g:691:1: escape returns [Object node] : t= ESCAPE escapeClause= likeValue ;
    public final Object escape() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object escapeClause = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:695:7: (t= ESCAPE escapeClause= likeValue )
            // JPQL.g:695:7: t= ESCAPE escapeClause= likeValue
            {
            t=(Token)input.LT(1);
            match(input,ESCAPE,FOLLOW_ESCAPE_in_escape3953); if (failed) return node;
            pushFollow(FOLLOW_likeValue_in_escape3959);
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
    // JPQL.g:699:1: likeValue returns [Object node] : (n= literalString | n= inputParameter );
    public final Object likeValue() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:701:7: (n= literalString | n= inputParameter )
            int alt53=2;
            int LA53_0 = input.LA(1);
            
            if ( ((LA53_0>=STRING_LITERAL_DOUBLE_QUOTED && LA53_0<=STRING_LITERAL_SINGLE_QUOTED)) ) {
                alt53=1;
            }
            else if ( ((LA53_0>=POSITIONAL_PARAM && LA53_0<=NAMED_PARAM)) ) {
                alt53=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("699:1: likeValue returns [Object node] : (n= literalString | n= inputParameter );", 53, 0, input);
            
                throw nvae;
            }
            switch (alt53) {
                case 1 :
                    // JPQL.g:701:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_likeValue3999);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:702:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_likeValue4013);
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
    // JPQL.g:705:1: nullComparisonExpression[boolean not, Object left] returns [Object node] : t= NULL ;
    public final Object nullComparisonExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
    
         node = null; 
        try {
            // JPQL.g:707:7: (t= NULL )
            // JPQL.g:707:7: t= NULL
            {
            t=(Token)input.LT(1);
            match(input,NULL,FOLLOW_NULL_in_nullComparisonExpression4046); if (failed) return node;
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
    // JPQL.g:711:1: emptyCollectionComparisonExpression[boolean not, Object left] returns [Object node] : t= EMPTY ;
    public final Object emptyCollectionComparisonExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
    
         node = null; 
        try {
            // JPQL.g:713:7: (t= EMPTY )
            // JPQL.g:713:7: t= EMPTY
            {
            t=(Token)input.LT(1);
            match(input,EMPTY,FOLLOW_EMPTY_in_emptyCollectionComparisonExpression4087); if (failed) return node;
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
    // JPQL.g:717:1: collectionMemberExpression[boolean not, Object left] returns [Object node] : t= MEMBER ( OF )? n= collectionValuedPathExpression ;
    public final Object collectionMemberExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:719:7: (t= MEMBER ( OF )? n= collectionValuedPathExpression )
            // JPQL.g:719:7: t= MEMBER ( OF )? n= collectionValuedPathExpression
            {
            t=(Token)input.LT(1);
            match(input,MEMBER,FOLLOW_MEMBER_in_collectionMemberExpression4128); if (failed) return node;
            // JPQL.g:719:17: ( OF )?
            int alt54=2;
            int LA54_0 = input.LA(1);
            
            if ( (LA54_0==OF) ) {
                alt54=1;
            }
            switch (alt54) {
                case 1 :
                    // JPQL.g:719:18: OF
                    {
                    match(input,OF,FOLLOW_OF_in_collectionMemberExpression4131); if (failed) return node;
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression4139);
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
    // JPQL.g:726:1: existsExpression[boolean not] returns [Object node] : t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET ;
    public final Object existsExpression(boolean not) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object subqueryNode = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:730:7: (t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET )
            // JPQL.g:730:7: t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_existsExpression4179); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4181); if (failed) return node;
            pushFollow(FOLLOW_subquery_in_existsExpression4187);
            subqueryNode=subquery();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4189); if (failed) return node;
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
    // JPQL.g:737:1: comparisonExpression[Object left] returns [Object node] : (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand );
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
            // JPQL.g:739:7: (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand )
            int alt55=6;
            switch ( input.LA(1) ) {
            case EQUALS:
                {
                alt55=1;
                }
                break;
            case NOT_EQUAL_TO:
                {
                alt55=2;
                }
                break;
            case GREATER_THAN:
                {
                alt55=3;
                }
                break;
            case GREATER_THAN_EQUAL_TO:
                {
                alt55=4;
                }
                break;
            case LESS_THAN:
                {
                alt55=5;
                }
                break;
            case LESS_THAN_EQUAL_TO:
                {
                alt55=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("737:1: comparisonExpression[Object left] returns [Object node] : (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand );", 55, 0, input);
            
                throw nvae;
            }
            
            switch (alt55) {
                case 1 :
                    // JPQL.g:739:7: t1= EQUALS n= comparisonExpressionRightOperand
                    {
                    t1=(Token)input.LT(1);
                    match(input,EQUALS,FOLLOW_EQUALS_in_comparisonExpression4229); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4235);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newEquals(t1.getLine(), t1.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:741:7: t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t2=(Token)input.LT(1);
                    match(input,NOT_EQUAL_TO,FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4256); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4262);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newNotEquals(t2.getLine(), t2.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:743:7: t3= GREATER_THAN n= comparisonExpressionRightOperand
                    {
                    t3=(Token)input.LT(1);
                    match(input,GREATER_THAN,FOLLOW_GREATER_THAN_in_comparisonExpression4283); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4289);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newGreaterThan(t3.getLine(), t3.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:745:7: t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t4=(Token)input.LT(1);
                    match(input,GREATER_THAN_EQUAL_TO,FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4310); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4316);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newGreaterThanEqual(t4.getLine(), t4.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:747:7: t5= LESS_THAN n= comparisonExpressionRightOperand
                    {
                    t5=(Token)input.LT(1);
                    match(input,LESS_THAN,FOLLOW_LESS_THAN_in_comparisonExpression4337); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4343);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newLessThan(t5.getLine(), t5.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:749:7: t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t6=(Token)input.LT(1);
                    match(input,LESS_THAN_EQUAL_TO,FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4364); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4370);
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
    // JPQL.g:753:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression | n= anyOrAllExpression );
    public final Object comparisonExpressionRightOperand() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:755:7: (n= arithmeticExpression | n= anyOrAllExpression )
            int alt56=2;
            int LA56_0 = input.LA(1);
            
            if ( (LA56_0==ABS||LA56_0==AVG||(LA56_0>=CONCAT && LA56_0<=CURRENT_TIMESTAMP)||LA56_0==FALSE||LA56_0==KEY||LA56_0==LENGTH||(LA56_0>=LOCATE && LA56_0<=MAX)||(LA56_0>=MIN && LA56_0<=MOD)||(LA56_0>=SIZE && LA56_0<=SQRT)||(LA56_0>=SUBSTRING && LA56_0<=SUM)||(LA56_0>=TRIM && LA56_0<=TRUE)||(LA56_0>=UPPER && LA56_0<=VALUE)||LA56_0==IDENT||LA56_0==LEFT_ROUND_BRACKET||(LA56_0>=PLUS && LA56_0<=MINUS)||(LA56_0>=INTEGER_LITERAL && LA56_0<=NAMED_PARAM)) ) {
                alt56=1;
            }
            else if ( (LA56_0==ALL||LA56_0==ANY||LA56_0==SOME) ) {
                alt56=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("753:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression | n= anyOrAllExpression );", 56, 0, input);
            
                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // JPQL.g:755:7: n= arithmeticExpression
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4411);
                    n=arithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:756:7: n= anyOrAllExpression
                    {
                    pushFollow(FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4425);
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
    // JPQL.g:759:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object arithmeticExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:761:7: (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
            int alt57=2;
            int LA57_0 = input.LA(1);
            
            if ( (LA57_0==ABS||LA57_0==AVG||(LA57_0>=CONCAT && LA57_0<=CURRENT_TIMESTAMP)||LA57_0==FALSE||LA57_0==KEY||LA57_0==LENGTH||(LA57_0>=LOCATE && LA57_0<=MAX)||(LA57_0>=MIN && LA57_0<=MOD)||(LA57_0>=SIZE && LA57_0<=SQRT)||(LA57_0>=SUBSTRING && LA57_0<=SUM)||(LA57_0>=TRIM && LA57_0<=TRUE)||(LA57_0>=UPPER && LA57_0<=VALUE)||LA57_0==IDENT||(LA57_0>=PLUS && LA57_0<=MINUS)||(LA57_0>=INTEGER_LITERAL && LA57_0<=NAMED_PARAM)) ) {
                alt57=1;
            }
            else if ( (LA57_0==LEFT_ROUND_BRACKET) ) {
                int LA57_35 = input.LA(2);
                
                if ( (LA57_35==SELECT) ) {
                    alt57=2;
                }
                else if ( (LA57_35==ABS||LA57_35==AVG||(LA57_35>=CONCAT && LA57_35<=CURRENT_TIMESTAMP)||LA57_35==FALSE||LA57_35==KEY||LA57_35==LENGTH||(LA57_35>=LOCATE && LA57_35<=MAX)||(LA57_35>=MIN && LA57_35<=MOD)||(LA57_35>=SIZE && LA57_35<=SQRT)||(LA57_35>=SUBSTRING && LA57_35<=SUM)||(LA57_35>=TRIM && LA57_35<=TRUE)||(LA57_35>=UPPER && LA57_35<=VALUE)||LA57_35==IDENT||LA57_35==LEFT_ROUND_BRACKET||(LA57_35>=PLUS && LA57_35<=MINUS)||(LA57_35>=INTEGER_LITERAL && LA57_35<=NAMED_PARAM)) ) {
                    alt57=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("759:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 57, 35, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("759:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 57, 0, input);
            
                throw nvae;
            }
            switch (alt57) {
                case 1 :
                    // JPQL.g:761:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4457);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:762:7: LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4467); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_arithmeticExpression4473);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4475); if (failed) return node;
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
    // JPQL.g:765:1: simpleArithmeticExpression returns [Object node] : n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )* ;
    public final Object simpleArithmeticExpression() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token m=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:769:7: (n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )* )
            // JPQL.g:769:7: n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*
            {
            pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4507);
            n=arithmeticTerm();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:770:9: (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*
            loop58:
            do {
                int alt58=3;
                int LA58_0 = input.LA(1);
                
                if ( (LA58_0==PLUS) ) {
                    alt58=1;
                }
                else if ( (LA58_0==MINUS) ) {
                    alt58=2;
                }
                
            
                switch (alt58) {
            	case 1 :
            	    // JPQL.g:770:11: p= PLUS right= arithmeticTerm
            	    {
            	    p=(Token)input.LT(1);
            	    match(input,PLUS,FOLLOW_PLUS_in_simpleArithmeticExpression4523); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4529);
            	    right=arithmeticTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newPlus(p.getLine(), p.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:772:11: m= MINUS right= arithmeticTerm
            	    {
            	    m=(Token)input.LT(1);
            	    match(input,MINUS,FOLLOW_MINUS_in_simpleArithmeticExpression4558); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4564);
            	    right=arithmeticTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newMinus(m.getLine(), m.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop58;
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
    // JPQL.g:777:1: arithmeticTerm returns [Object node] : n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )* ;
    public final Object arithmeticTerm() throws RecognitionException {

        Object node = null;
    
        Token m=null;
        Token d=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:781:7: (n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )* )
            // JPQL.g:781:7: n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*
            {
            pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4621);
            n=arithmeticFactor();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:782:9: (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*
            loop59:
            do {
                int alt59=3;
                int LA59_0 = input.LA(1);
                
                if ( (LA59_0==MULTIPLY) ) {
                    alt59=1;
                }
                else if ( (LA59_0==DIVIDE) ) {
                    alt59=2;
                }
                
            
                switch (alt59) {
            	case 1 :
            	    // JPQL.g:782:11: m= MULTIPLY right= arithmeticFactor
            	    {
            	    m=(Token)input.LT(1);
            	    match(input,MULTIPLY,FOLLOW_MULTIPLY_in_arithmeticTerm4637); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4643);
            	    right=arithmeticFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newMultiply(m.getLine(), m.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:784:11: d= DIVIDE right= arithmeticFactor
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DIVIDE,FOLLOW_DIVIDE_in_arithmeticTerm4672); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4678);
            	    right=arithmeticFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newDivide(d.getLine(), d.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop59;
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
    // JPQL.g:789:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary );
    public final Object arithmeticFactor() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token m=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:791:7: (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary )
            int alt60=3;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt60=1;
                }
                break;
            case MINUS:
                {
                alt60=2;
                }
                break;
            case ABS:
            case AVG:
            case CONCAT:
            case COUNT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case FALSE:
            case KEY:
            case LENGTH:
            case LOCATE:
            case LOWER:
            case MAX:
            case MIN:
            case MOD:
            case SIZE:
            case SQRT:
            case SUBSTRING:
            case SUM:
            case TRIM:
            case TRUE:
            case UPPER:
            case VALUE:
            case IDENT:
            case LEFT_ROUND_BRACKET:
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt60=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("789:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary );", 60, 0, input);
            
                throw nvae;
            }
            
            switch (alt60) {
                case 1 :
                    // JPQL.g:791:7: p= PLUS n= arithmeticPrimary
                    {
                    p=(Token)input.LT(1);
                    match(input,PLUS,FOLLOW_PLUS_in_arithmeticFactor4732); if (failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor4739);
                    n=arithmeticPrimary();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = factory.newUnaryPlus(p.getLine(), p.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:793:7: m= MINUS n= arithmeticPrimary
                    {
                    m=(Token)input.LT(1);
                    match(input,MINUS,FOLLOW_MINUS_in_arithmeticFactor4761); if (failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor4767);
                    n=arithmeticPrimary();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newUnaryMinus(m.getLine(), m.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:795:7: n= arithmeticPrimary
                    {
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor4791);
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
    // JPQL.g:798:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= functionsReturningNumerics | n= functionsReturningDatetime | n= functionsReturningStrings | n= inputParameter | n= literalNumeric | n= literalString | n= literalBoolean | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET );
    public final Object arithmeticPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:800:7: ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= functionsReturningNumerics | n= functionsReturningDatetime | n= functionsReturningStrings | n= inputParameter | n= literalNumeric | n= literalString | n= literalBoolean | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            int alt61=10;
            switch ( input.LA(1) ) {
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case SUM:
                {
                alt61=1;
                }
                break;
            case KEY:
            case VALUE:
            case IDENT:
                {
                alt61=2;
                }
                break;
            case ABS:
            case LENGTH:
            case LOCATE:
            case MOD:
            case SIZE:
            case SQRT:
                {
                alt61=3;
                }
                break;
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
                {
                alt61=4;
                }
                break;
            case CONCAT:
            case LOWER:
            case SUBSTRING:
            case TRIM:
            case UPPER:
                {
                alt61=5;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt61=6;
                }
                break;
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
                {
                alt61=7;
                }
                break;
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt61=8;
                }
                break;
            case FALSE:
            case TRUE:
                {
                alt61=9;
                }
                break;
            case LEFT_ROUND_BRACKET:
                {
                alt61=10;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("798:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= functionsReturningNumerics | n= functionsReturningDatetime | n= functionsReturningStrings | n= inputParameter | n= literalNumeric | n= literalString | n= literalBoolean | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET );", 61, 0, input);
            
                throw nvae;
            }
            
            switch (alt61) {
                case 1 :
                    // JPQL.g:800:7: {...}?n= aggregateExpression
                    {
                    if ( !( aggregatesAllowed() ) ) {
                        if (backtracking>0) {failed=true; return node;}
                        throw new FailedPredicateException(input, "arithmeticPrimary", " aggregatesAllowed() ");
                    }
                    pushFollow(FOLLOW_aggregateExpression_in_arithmeticPrimary4825);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:801:7: n= pathExprOrVariableAccess
                    {
                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_arithmeticPrimary4839);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:802:7: n= functionsReturningNumerics
                    {
                    pushFollow(FOLLOW_functionsReturningNumerics_in_arithmeticPrimary4853);
                    n=functionsReturningNumerics();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:803:7: n= functionsReturningDatetime
                    {
                    pushFollow(FOLLOW_functionsReturningDatetime_in_arithmeticPrimary4867);
                    n=functionsReturningDatetime();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:804:7: n= functionsReturningStrings
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_arithmeticPrimary4881);
                    n=functionsReturningStrings();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:805:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_arithmeticPrimary4895);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 7 :
                    // JPQL.g:806:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_arithmeticPrimary4909);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 8 :
                    // JPQL.g:807:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_arithmeticPrimary4923);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 9 :
                    // JPQL.g:808:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_arithmeticPrimary4937);
                    n=literalBoolean();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 10 :
                    // JPQL.g:809:7: LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary4947); if (failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary4953);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary4955); if (failed) return node;
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

    
    // $ANTLR start anyOrAllExpression
    // JPQL.g:812:1: anyOrAllExpression returns [Object node] : (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object anyOrAllExpression() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token y=null;
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:814:7: (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
            int alt62=3;
            switch ( input.LA(1) ) {
            case ALL:
                {
                alt62=1;
                }
                break;
            case ANY:
                {
                alt62=2;
                }
                break;
            case SOME:
                {
                alt62=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("812:1: anyOrAllExpression returns [Object node] : (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 62, 0, input);
            
                throw nvae;
            }
            
            switch (alt62) {
                case 1 :
                    // JPQL.g:814:7: a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    a=(Token)input.LT(1);
                    match(input,ALL,FOLLOW_ALL_in_anyOrAllExpression4985); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression4987); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression4993);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression4995); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAll(a.getLine(), a.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:816:7: y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    y=(Token)input.LT(1);
                    match(input,ANY,FOLLOW_ANY_in_anyOrAllExpression5015); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5017); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5023);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5025); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAny(y.getLine(), y.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:818:7: s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    s=(Token)input.LT(1);
                    match(input,SOME,FOLLOW_SOME_in_anyOrAllExpression5045); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5047); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5053);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5055); if (failed) return node;
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

    
    // $ANTLR start stringPrimary
    // JPQL.g:822:1: stringPrimary returns [Object node] : (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression );
    public final Object stringPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:824:7: (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression )
            int alt63=4;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt63=1;
                }
                break;
            case CONCAT:
            case LOWER:
            case SUBSTRING:
            case TRIM:
            case UPPER:
                {
                alt63=2;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt63=3;
                }
                break;
            case KEY:
            case VALUE:
            case IDENT:
                {
                alt63=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("822:1: stringPrimary returns [Object node] : (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression );", 63, 0, input);
            
                throw nvae;
            }
            
            switch (alt63) {
                case 1 :
                    // JPQL.g:824:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_stringPrimary5095);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:825:7: n= functionsReturningStrings
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_stringPrimary5109);
                    n=functionsReturningStrings();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:826:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_stringPrimary5123);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:827:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_stringPrimary5137);
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
    // JPQL.g:832:1: literal returns [Object node] : (n= literalNumeric | n= literalBoolean | n= literalString );
    public final Object literal() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:834:7: (n= literalNumeric | n= literalBoolean | n= literalString )
            int alt64=3;
            switch ( input.LA(1) ) {
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
                {
                alt64=1;
                }
                break;
            case FALSE:
            case TRUE:
                {
                alt64=2;
                }
                break;
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt64=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("832:1: literal returns [Object node] : (n= literalNumeric | n= literalBoolean | n= literalString );", 64, 0, input);
            
                throw nvae;
            }
            
            switch (alt64) {
                case 1 :
                    // JPQL.g:834:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_literal5171);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:835:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_literal5185);
                    n=literalBoolean();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:836:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_literal5199);
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
    // JPQL.g:839:1: literalNumeric returns [Object node] : (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL );
    public final Object literalNumeric() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        Token l=null;
        Token f=null;
        Token d=null;
    
         node = null; 
        try {
            // JPQL.g:841:7: (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL )
            int alt65=4;
            switch ( input.LA(1) ) {
            case INTEGER_LITERAL:
                {
                alt65=1;
                }
                break;
            case LONG_LITERAL:
                {
                alt65=2;
                }
                break;
            case FLOAT_LITERAL:
                {
                alt65=3;
                }
                break;
            case DOUBLE_LITERAL:
                {
                alt65=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("839:1: literalNumeric returns [Object node] : (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL );", 65, 0, input);
            
                throw nvae;
            }
            
            switch (alt65) {
                case 1 :
                    // JPQL.g:841:7: i= INTEGER_LITERAL
                    {
                    i=(Token)input.LT(1);
                    match(input,INTEGER_LITERAL,FOLLOW_INTEGER_LITERAL_in_literalNumeric5229); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newIntegerLiteral(i.getLine(), i.getCharPositionInLine(), 
                                                                   Integer.valueOf(i.getText())); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:846:7: l= LONG_LITERAL
                    {
                    l=(Token)input.LT(1);
                    match(input,LONG_LITERAL,FOLLOW_LONG_LITERAL_in_literalNumeric5245); if (failed) return node;
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
                    // JPQL.g:854:7: f= FLOAT_LITERAL
                    {
                    f=(Token)input.LT(1);
                    match(input,FLOAT_LITERAL,FOLLOW_FLOAT_LITERAL_in_literalNumeric5266); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newFloatLiteral(f.getLine(), f.getCharPositionInLine(),
                                                                 Float.valueOf(f.getText()));
                              
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:859:7: d= DOUBLE_LITERAL
                    {
                    d=(Token)input.LT(1);
                    match(input,DOUBLE_LITERAL,FOLLOW_DOUBLE_LITERAL_in_literalNumeric5286); if (failed) return node;
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
    // JPQL.g:866:1: literalBoolean returns [Object node] : (t= TRUE | f= FALSE );
    public final Object literalBoolean() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Token f=null;
    
         node = null; 
        try {
            // JPQL.g:868:7: (t= TRUE | f= FALSE )
            int alt66=2;
            int LA66_0 = input.LA(1);
            
            if ( (LA66_0==TRUE) ) {
                alt66=1;
            }
            else if ( (LA66_0==FALSE) ) {
                alt66=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("866:1: literalBoolean returns [Object node] : (t= TRUE | f= FALSE );", 66, 0, input);
            
                throw nvae;
            }
            switch (alt66) {
                case 1 :
                    // JPQL.g:868:7: t= TRUE
                    {
                    t=(Token)input.LT(1);
                    match(input,TRUE,FOLLOW_TRUE_in_literalBoolean5324); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newBooleanLiteral(t.getLine(), t.getCharPositionInLine(), Boolean.TRUE); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:870:7: f= FALSE
                    {
                    f=(Token)input.LT(1);
                    match(input,FALSE,FOLLOW_FALSE_in_literalBoolean5346); if (failed) return node;
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
    // JPQL.g:874:1: literalString returns [Object node] : (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED );
    public final Object literalString() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Token s=null;
    
         node = null; 
        try {
            // JPQL.g:876:7: (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED )
            int alt67=2;
            int LA67_0 = input.LA(1);
            
            if ( (LA67_0==STRING_LITERAL_DOUBLE_QUOTED) ) {
                alt67=1;
            }
            else if ( (LA67_0==STRING_LITERAL_SINGLE_QUOTED) ) {
                alt67=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("874:1: literalString returns [Object node] : (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED );", 67, 0, input);
            
                throw nvae;
            }
            switch (alt67) {
                case 1 :
                    // JPQL.g:876:7: d= STRING_LITERAL_DOUBLE_QUOTED
                    {
                    d=(Token)input.LT(1);
                    match(input,STRING_LITERAL_DOUBLE_QUOTED,FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString5385); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newStringLiteral(d.getLine(), d.getCharPositionInLine(), 
                                                                  convertStringLiteral(d.getText())); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:881:7: s= STRING_LITERAL_SINGLE_QUOTED
                    {
                    s=(Token)input.LT(1);
                    match(input,STRING_LITERAL_SINGLE_QUOTED,FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString5406); if (failed) return node;
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
    // JPQL.g:888:1: inputParameter returns [Object node] : (p= POSITIONAL_PARAM | n= NAMED_PARAM );
    public final Object inputParameter() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token n=null;
    
         node = null; 
        try {
            // JPQL.g:890:7: (p= POSITIONAL_PARAM | n= NAMED_PARAM )
            int alt68=2;
            int LA68_0 = input.LA(1);
            
            if ( (LA68_0==POSITIONAL_PARAM) ) {
                alt68=1;
            }
            else if ( (LA68_0==NAMED_PARAM) ) {
                alt68=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("888:1: inputParameter returns [Object node] : (p= POSITIONAL_PARAM | n= NAMED_PARAM );", 68, 0, input);
            
                throw nvae;
            }
            switch (alt68) {
                case 1 :
                    // JPQL.g:890:7: p= POSITIONAL_PARAM
                    {
                    p=(Token)input.LT(1);
                    match(input,POSITIONAL_PARAM,FOLLOW_POSITIONAL_PARAM_in_inputParameter5444); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  // skip the leading ?
                                  String text = p.getText().substring(1);
                                  node = factory.newPositionalParameter(p.getLine(), p.getCharPositionInLine(), text); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:896:7: n= NAMED_PARAM
                    {
                    n=(Token)input.LT(1);
                    match(input,NAMED_PARAM,FOLLOW_NAMED_PARAM_in_inputParameter5464); if (failed) return node;
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
    // JPQL.g:904:1: functionsReturningNumerics returns [Object node] : (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size );
    public final Object functionsReturningNumerics() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:906:7: (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size )
            int alt69=6;
            switch ( input.LA(1) ) {
            case ABS:
                {
                alt69=1;
                }
                break;
            case LENGTH:
                {
                alt69=2;
                }
                break;
            case MOD:
                {
                alt69=3;
                }
                break;
            case SQRT:
                {
                alt69=4;
                }
                break;
            case LOCATE:
                {
                alt69=5;
                }
                break;
            case SIZE:
                {
                alt69=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("904:1: functionsReturningNumerics returns [Object node] : (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size );", 69, 0, input);
            
                throw nvae;
            }
            
            switch (alt69) {
                case 1 :
                    // JPQL.g:906:7: n= abs
                    {
                    pushFollow(FOLLOW_abs_in_functionsReturningNumerics5504);
                    n=abs();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:907:7: n= length
                    {
                    pushFollow(FOLLOW_length_in_functionsReturningNumerics5518);
                    n=length();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:908:7: n= mod
                    {
                    pushFollow(FOLLOW_mod_in_functionsReturningNumerics5532);
                    n=mod();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:909:7: n= sqrt
                    {
                    pushFollow(FOLLOW_sqrt_in_functionsReturningNumerics5546);
                    n=sqrt();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:910:7: n= locate
                    {
                    pushFollow(FOLLOW_locate_in_functionsReturningNumerics5560);
                    n=locate();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:911:7: n= size
                    {
                    pushFollow(FOLLOW_size_in_functionsReturningNumerics5574);
                    n=size();
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
    // JPQL.g:914:1: functionsReturningDatetime returns [Object node] : (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP );
    public final Object functionsReturningDatetime() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Token t=null;
        Token ts=null;
    
         node = null; 
        try {
            // JPQL.g:916:7: (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP )
            int alt70=3;
            switch ( input.LA(1) ) {
            case CURRENT_DATE:
                {
                alt70=1;
                }
                break;
            case CURRENT_TIME:
                {
                alt70=2;
                }
                break;
            case CURRENT_TIMESTAMP:
                {
                alt70=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("914:1: functionsReturningDatetime returns [Object node] : (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP );", 70, 0, input);
            
                throw nvae;
            }
            
            switch (alt70) {
                case 1 :
                    // JPQL.g:916:7: d= CURRENT_DATE
                    {
                    d=(Token)input.LT(1);
                    match(input,CURRENT_DATE,FOLLOW_CURRENT_DATE_in_functionsReturningDatetime5604); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newCurrentDate(d.getLine(), d.getCharPositionInLine()); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:918:7: t= CURRENT_TIME
                    {
                    t=(Token)input.LT(1);
                    match(input,CURRENT_TIME,FOLLOW_CURRENT_TIME_in_functionsReturningDatetime5625); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newCurrentTime(t.getLine(), t.getCharPositionInLine()); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:920:7: ts= CURRENT_TIMESTAMP
                    {
                    ts=(Token)input.LT(1);
                    match(input,CURRENT_TIMESTAMP,FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime5645); if (failed) return node;
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
    // JPQL.g:924:1: functionsReturningStrings returns [Object node] : (n= concat | n= substring | n= trim | n= upper | n= lower );
    public final Object functionsReturningStrings() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:926:7: (n= concat | n= substring | n= trim | n= upper | n= lower )
            int alt71=5;
            switch ( input.LA(1) ) {
            case CONCAT:
                {
                alt71=1;
                }
                break;
            case SUBSTRING:
                {
                alt71=2;
                }
                break;
            case TRIM:
                {
                alt71=3;
                }
                break;
            case UPPER:
                {
                alt71=4;
                }
                break;
            case LOWER:
                {
                alt71=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("924:1: functionsReturningStrings returns [Object node] : (n= concat | n= substring | n= trim | n= upper | n= lower );", 71, 0, input);
            
                throw nvae;
            }
            
            switch (alt71) {
                case 1 :
                    // JPQL.g:926:7: n= concat
                    {
                    pushFollow(FOLLOW_concat_in_functionsReturningStrings5685);
                    n=concat();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:927:7: n= substring
                    {
                    pushFollow(FOLLOW_substring_in_functionsReturningStrings5699);
                    n=substring();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:928:7: n= trim
                    {
                    pushFollow(FOLLOW_trim_in_functionsReturningStrings5713);
                    n=trim();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:929:7: n= upper
                    {
                    pushFollow(FOLLOW_upper_in_functionsReturningStrings5727);
                    n=upper();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:930:7: n= lower
                    {
                    pushFollow(FOLLOW_lower_in_functionsReturningStrings5741);
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

    
    // $ANTLR start concat
    // JPQL.g:934:1: concat returns [Object node] : c= CONCAT LEFT_ROUND_BRACKET firstArg= stringPrimary COMMA secondArg= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object concat() throws RecognitionException {

        Object node = null;
    
        Token c=null;
        Object firstArg = null;

        Object secondArg = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:938:7: (c= CONCAT LEFT_ROUND_BRACKET firstArg= stringPrimary COMMA secondArg= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:938:7: c= CONCAT LEFT_ROUND_BRACKET firstArg= stringPrimary COMMA secondArg= stringPrimary RIGHT_ROUND_BRACKET
            {
            c=(Token)input.LT(1);
            match(input,CONCAT,FOLLOW_CONCAT_in_concat5772); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_concat5783); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_concat5798);
            firstArg=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_concat5800); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_concat5806);
            secondArg=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_concat5816); if (failed) return node;
            if ( backtracking==0 ) {
               node = factory.newConcat(c.getLine(), c.getCharPositionInLine(), firstArg, secondArg); 
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
    // $ANTLR end concat

    
    // $ANTLR start substring
    // JPQL.g:945:1: substring returns [Object node] : s= SUBSTRING LEFT_ROUND_BRACKET string= stringPrimary COMMA start= simpleArithmeticExpression COMMA lengthNode= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object substring() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object string = null;

        Object start = null;

        Object lengthNode = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:949:7: (s= SUBSTRING LEFT_ROUND_BRACKET string= stringPrimary COMMA start= simpleArithmeticExpression COMMA lengthNode= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:949:7: s= SUBSTRING LEFT_ROUND_BRACKET string= stringPrimary COMMA start= simpleArithmeticExpression COMMA lengthNode= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SUBSTRING,FOLLOW_SUBSTRING_in_substring5854); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_substring5867); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_substring5881);
            string=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_substring5883); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_substring5897);
            start=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_substring5899); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_substring5913);
            lengthNode=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_substring5923); if (failed) return node;
            if ( backtracking==0 ) {
               
                          node = factory.newSubstring(s.getLine(), s.getCharPositionInLine(), 
                                                      string, start, lengthNode); 
                      
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
    // JPQL.g:961:1: trim returns [Object node] : t= TRIM LEFT_ROUND_BRACKET ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object trim() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        TrimSpecification trimSpecIndicator = null;

        Object trimCharNode = null;

        Object n = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:965:7: (t= TRIM LEFT_ROUND_BRACKET ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:965:7: t= TRIM LEFT_ROUND_BRACKET ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,TRIM,FOLLOW_TRIM_in_trim5961); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_trim5971); if (failed) return node;
            // JPQL.g:967:9: ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )?
            int alt72=2;
            int LA72_0 = input.LA(1);
            
            if ( (LA72_0==LEADING) && (synpred2())) {
                alt72=1;
            }
            else if ( (LA72_0==TRAILING) && (synpred2())) {
                alt72=1;
            }
            else if ( (LA72_0==BOTH) && (synpred2())) {
                alt72=1;
            }
            else if ( (LA72_0==STRING_LITERAL_DOUBLE_QUOTED) ) {
                int LA72_4 = input.LA(2);
                
                if ( (LA72_4==FROM) && (synpred2())) {
                    alt72=1;
                }
            }
            else if ( (LA72_0==STRING_LITERAL_SINGLE_QUOTED) ) {
                int LA72_5 = input.LA(2);
                
                if ( (LA72_5==FROM) && (synpred2())) {
                    alt72=1;
                }
            }
            else if ( (LA72_0==POSITIONAL_PARAM) ) {
                int LA72_6 = input.LA(2);
                
                if ( (LA72_6==FROM) && (synpred2())) {
                    alt72=1;
                }
            }
            else if ( (LA72_0==NAMED_PARAM) ) {
                int LA72_7 = input.LA(2);
                
                if ( (LA72_7==FROM) && (synpred2())) {
                    alt72=1;
                }
            }
            else if ( (LA72_0==FROM) && (synpred2())) {
                alt72=1;
            }
            switch (alt72) {
                case 1 :
                    // JPQL.g:967:11: ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM
                    {
                    pushFollow(FOLLOW_trimSpec_in_trim5999);
                    trimSpecIndicator=trimSpec();
                    _fsp--;
                    if (failed) return node;
                    pushFollow(FOLLOW_trimChar_in_trim6005);
                    trimCharNode=trimChar();
                    _fsp--;
                    if (failed) return node;
                    match(input,FROM,FOLLOW_FROM_in_trim6007); if (failed) return node;
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_stringPrimary_in_trim6025);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_trim6035); if (failed) return node;
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
    // JPQL.g:976:1: trimSpec returns [TrimSpecification trimSpec] : ( LEADING | TRAILING | BOTH | );
    public final TrimSpecification trimSpec() throws RecognitionException {

        TrimSpecification trimSpec = null;
    
         trimSpec = TrimSpecification.BOTH; 
        try {
            // JPQL.g:978:7: ( LEADING | TRAILING | BOTH | )
            int alt73=4;
            switch ( input.LA(1) ) {
            case LEADING:
                {
                alt73=1;
                }
                break;
            case TRAILING:
                {
                alt73=2;
                }
                break;
            case BOTH:
                {
                alt73=3;
                }
                break;
            case FROM:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt73=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return trimSpec;}
                NoViableAltException nvae =
                    new NoViableAltException("976:1: trimSpec returns [TrimSpecification trimSpec] : ( LEADING | TRAILING | BOTH | );", 73, 0, input);
            
                throw nvae;
            }
            
            switch (alt73) {
                case 1 :
                    // JPQL.g:978:7: LEADING
                    {
                    match(input,LEADING,FOLLOW_LEADING_in_trimSpec6071); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.LEADING; 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:980:7: TRAILING
                    {
                    match(input,TRAILING,FOLLOW_TRAILING_in_trimSpec6089); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.TRAILING; 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:982:7: BOTH
                    {
                    match(input,BOTH,FOLLOW_BOTH_in_trimSpec6107); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.BOTH; 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:985:5: 
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
    // JPQL.g:987:1: trimChar returns [Object node] : (n= literalString | n= inputParameter | );
    public final Object trimChar() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:989:7: (n= literalString | n= inputParameter | )
            int alt74=3;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt74=1;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt74=2;
                }
                break;
            case FROM:
                {
                alt74=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("987:1: trimChar returns [Object node] : (n= literalString | n= inputParameter | );", 74, 0, input);
            
                throw nvae;
            }
            
            switch (alt74) {
                case 1 :
                    // JPQL.g:989:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_trimChar6154);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:990:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_trimChar6168);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:992:5: 
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
    // JPQL.g:994:1: upper returns [Object node] : u= UPPER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object upper() throws RecognitionException {

        Object node = null;
    
        Token u=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:996:7: (u= UPPER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:996:7: u= UPPER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET
            {
            u=(Token)input.LT(1);
            match(input,UPPER,FOLLOW_UPPER_in_upper6205); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_upper6207); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_upper6213);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_upper6215); if (failed) return node;
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
    // JPQL.g:1000:1: lower returns [Object node] : l= LOWER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object lower() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1002:7: (l= LOWER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:1002:7: l= LOWER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LOWER,FOLLOW_LOWER_in_lower6253); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_lower6255); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_lower6261);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_lower6263); if (failed) return node;
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
    // JPQL.g:1007:1: abs returns [Object node] : a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object abs() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1009:7: (a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1009:7: a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            a=(Token)input.LT(1);
            match(input,ABS,FOLLOW_ABS_in_abs6302); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_abs6304); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_abs6310);
            n=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_abs6312); if (failed) return node;
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
    // JPQL.g:1013:1: length returns [Object node] : l= LENGTH LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object length() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1015:7: (l= LENGTH LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:1015:7: l= LENGTH LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LENGTH,FOLLOW_LENGTH_in_length6350); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_length6352); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_length6358);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_length6360); if (failed) return node;
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
    // JPQL.g:1019:1: locate returns [Object node] : l= LOCATE LEFT_ROUND_BRACKET pattern= stringPrimary COMMA n= stringPrimary ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET ;
    public final Object locate() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object pattern = null;

        Object n = null;

        Object startPos = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1023:7: (l= LOCATE LEFT_ROUND_BRACKET pattern= stringPrimary COMMA n= stringPrimary ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET )
            // JPQL.g:1023:7: l= LOCATE LEFT_ROUND_BRACKET pattern= stringPrimary COMMA n= stringPrimary ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LOCATE,FOLLOW_LOCATE_in_locate6398); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_locate6408); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_locate6423);
            pattern=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_locate6425); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_locate6431);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            // JPQL.g:1026:9: ( COMMA startPos= simpleArithmeticExpression )?
            int alt75=2;
            int LA75_0 = input.LA(1);
            
            if ( (LA75_0==COMMA) ) {
                alt75=1;
            }
            switch (alt75) {
                case 1 :
                    // JPQL.g:1026:11: COMMA startPos= simpleArithmeticExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_locate6443); if (failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_locate6449);
                    startPos=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_locate6462); if (failed) return node;
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
    // JPQL.g:1034:1: size returns [Object node] : s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ;
    public final Object size() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1036:7: (s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1036:7: s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SIZE,FOLLOW_SIZE_in_size6500); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_size6511); if (failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_size6517);
            n=collectionValuedPathExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_size6519); if (failed) return node;
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
    // JPQL.g:1041:1: mod returns [Object node] : m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object mod() throws RecognitionException {

        Object node = null;
    
        Token m=null;
        Object left = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1045:7: (m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1045:7: m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            m=(Token)input.LT(1);
            match(input,MOD,FOLLOW_MOD_in_mod6557); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mod6559); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_mod6573);
            left=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_mod6575); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_mod6590);
            right=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mod6600); if (failed) return node;
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
    // JPQL.g:1052:1: sqrt returns [Object node] : s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object sqrt() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1054:7: (s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1054:7: s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SQRT,FOLLOW_SQRT_in_sqrt6638); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_sqrt6649); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_sqrt6655);
            n=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt6657); if (failed) return node;
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

    
    // $ANTLR start subquery
    // JPQL.g:1059:1: subquery returns [Object node] : select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? ;
    public final Object subquery() throws RecognitionException {

        Object node = null;
    
        Object select = null;

        Object from = null;

        Object where = null;

        Object groupBy = null;

        Object having = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1063:7: (select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? )
            // JPQL.g:1063:7: select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )?
            {
            pushFollow(FOLLOW_simpleSelectClause_in_subquery6698);
            select=simpleSelectClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_subqueryFromClause_in_subquery6713);
            from=subqueryFromClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g:1065:7: (where= whereClause )?
            int alt76=2;
            int LA76_0 = input.LA(1);
            
            if ( (LA76_0==WHERE) ) {
                alt76=1;
            }
            switch (alt76) {
                case 1 :
                    // JPQL.g:1065:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_subquery6728);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:1066:7: (groupBy= groupByClause )?
            int alt77=2;
            int LA77_0 = input.LA(1);
            
            if ( (LA77_0==GROUP) ) {
                alt77=1;
            }
            switch (alt77) {
                case 1 :
                    // JPQL.g:1066:8: groupBy= groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_subquery6743);
                    groupBy=groupByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:1067:7: (having= havingClause )?
            int alt78=2;
            int LA78_0 = input.LA(1);
            
            if ( (LA78_0==HAVING) ) {
                alt78=1;
            }
            switch (alt78) {
                case 1 :
                    // JPQL.g:1067:8: having= havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_subquery6759);
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
    // JPQL.g:1074:1: simpleSelectClause returns [Object node] : s= SELECT ( DISTINCT )? n= simpleSelectExpression ;
    public final Object simpleSelectClause() throws RecognitionException {
        simpleSelectClause_stack.push(new simpleSelectClause_scope());

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         
            node = null; 
            ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct = false;
    
        try {
            // JPQL.g:1082:7: (s= SELECT ( DISTINCT )? n= simpleSelectExpression )
            // JPQL.g:1082:7: s= SELECT ( DISTINCT )? n= simpleSelectExpression
            {
            s=(Token)input.LT(1);
            match(input,SELECT,FOLLOW_SELECT_in_simpleSelectClause6802); if (failed) return node;
            // JPQL.g:1082:16: ( DISTINCT )?
            int alt79=2;
            int LA79_0 = input.LA(1);
            
            if ( (LA79_0==DISTINCT) ) {
                alt79=1;
            }
            switch (alt79) {
                case 1 :
                    // JPQL.g:1082:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_simpleSelectClause6805); if (failed) return node;
                    if ( backtracking==0 ) {
                       ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct = true; 
                    }
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_simpleSelectExpression_in_simpleSelectClause6821);
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
    // JPQL.g:1092:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccess );
    public final Object simpleSelectExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1094:7: (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccess )
            int alt80=3;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA80_1 = input.LA(2);
                
                if ( (LA80_1==DOT) ) {
                    alt80=1;
                }
                else if ( (LA80_1==FROM) ) {
                    alt80=3;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1092:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccess );", 80, 1, input);
                
                    throw nvae;
                }
                }
                break;
            case KEY:
            case VALUE:
                {
                alt80=1;
                }
                break;
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case SUM:
                {
                alt80=2;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1092:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccess );", 80, 0, input);
            
                throw nvae;
            }
            
            switch (alt80) {
                case 1 :
                    // JPQL.g:1094:7: n= singleValuedPathExpression
                    {
                    pushFollow(FOLLOW_singleValuedPathExpression_in_simpleSelectExpression6861);
                    n=singleValuedPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1095:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_simpleSelectExpression6876);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1096:7: n= variableAccess
                    {
                    pushFollow(FOLLOW_variableAccess_in_simpleSelectExpression6891);
                    n=variableAccess();
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
    // JPQL.g:1100:1: subqueryFromClause returns [Object node] : f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] )* ;
    public final Object subqueryFromClause() throws RecognitionException {
        subqueryFromClause_stack.push(new subqueryFromClause_scope());

        Object node = null;
    
        Token f=null;
    
         
            node = null; 
            ((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls = new ArrayList();
    
        try {
            // JPQL.g:1108:7: (f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] )* )
            // JPQL.g:1108:7: f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] )*
            {
            f=(Token)input.LT(1);
            match(input,FROM,FOLLOW_FROM_in_subqueryFromClause6926); if (failed) return node;
            pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause6928);
            subselectIdentificationVariableDeclaration(((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls);
            _fsp--;
            if (failed) return node;
            // JPQL.g:1109:9: ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] )*
            loop81:
            do {
                int alt81=2;
                int LA81_0 = input.LA(1);
                
                if ( (LA81_0==COMMA) ) {
                    alt81=1;
                }
                
            
                switch (alt81) {
            	case 1 :
            	    // JPQL.g:1109:11: COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_subqueryFromClause6942); if (failed) return node;
            	    pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause6944);
            	    subselectIdentificationVariableDeclaration(((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls);
            	    _fsp--;
            	    if (failed) return node;
            	    
            	    }
            	    break;
            
            	default :
            	    break loop81;
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
    // JPQL.g:1113:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration );
    public final void subselectIdentificationVariableDeclaration(List varDecls) throws RecognitionException {
        Token i=null;
        Object n = null;
        
    
         Object node; 
        try {
            // JPQL.g:1115:7: ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration )
            int alt83=3;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA83_1 = input.LA(2);
                
                if ( (LA83_1==DOT) ) {
                    alt83=2;
                }
                else if ( (LA83_1==AS||LA83_1==IDENT) ) {
                    alt83=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1113:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration );", 83, 1, input);
                
                    throw nvae;
                }
                }
                break;
            case KEY:
                {
                int LA83_2 = input.LA(2);
                
                if ( (LA83_2==LEFT_ROUND_BRACKET) ) {
                    alt83=2;
                }
                else if ( (LA83_2==AS||LA83_2==IDENT) ) {
                    alt83=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1113:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration );", 83, 2, input);
                
                    throw nvae;
                }
                }
                break;
            case VALUE:
                {
                int LA83_3 = input.LA(2);
                
                if ( (LA83_3==LEFT_ROUND_BRACKET) ) {
                    alt83=2;
                }
                else if ( (LA83_3==AS||LA83_3==IDENT) ) {
                    alt83=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1113:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration );", 83, 3, input);
                
                    throw nvae;
                }
                }
                break;
            case IN:
                {
                int LA83_4 = input.LA(2);
                
                if ( (LA83_4==LEFT_ROUND_BRACKET) ) {
                    alt83=3;
                }
                else if ( (LA83_4==AS||LA83_4==IDENT) ) {
                    alt83=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1113:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration );", 83, 4, input);
                
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
            case CONCAT:
            case COUNT:
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
            case DESC:
            case DELETE:
            case DISTINCT:
            case EMPTY:
            case ENTRY:
            case ESCAPE:
            case EXISTS:
            case FALSE:
            case FETCH:
            case FROM:
            case GROUP:
            case HAVING:
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
            case TRAILING:
            case TRIM:
            case TRUE:
            case UNKNOWN:
            case UPDATE:
            case UPPER:
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
                alt83=1;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1113:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration );", 83, 0, input);
            
                throw nvae;
            }
            
            switch (alt83) {
                case 1 :
                    // JPQL.g:1115:7: identificationVariableDeclaration[varDecls]
                    {
                    pushFollow(FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration6982);
                    identificationVariableDeclaration(varDecls);
                    _fsp--;
                    if (failed) return ;
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1116:7: n= associationPathExpression ( AS )? i= IDENT
                    {
                    pushFollow(FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration6995);
                    n=associationPathExpression();
                    _fsp--;
                    if (failed) return ;
                    // JPQL.g:1116:37: ( AS )?
                    int alt82=2;
                    int LA82_0 = input.LA(1);
                    
                    if ( (LA82_0==AS) ) {
                        alt82=1;
                    }
                    switch (alt82) {
                        case 1 :
                            // JPQL.g:1116:38: AS
                            {
                            match(input,AS,FOLLOW_AS_in_subselectIdentificationVariableDeclaration6998); if (failed) return ;
                            
                            }
                            break;
                    
                    }

                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration7004); if (failed) return ;
                    if ( backtracking==0 ) {
                       
                                  varDecls.add(factory.newVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                       n, i.getText())); 
                              
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1121:7: n= collectionMemberDeclaration
                    {
                    pushFollow(FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration7026);
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
    // JPQL.g:1124:1: orderByClause returns [Object node] : o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* ;
    public final Object orderByClause() throws RecognitionException {
        orderByClause_stack.push(new orderByClause_scope());

        Object node = null;
    
        Token o=null;
        Object n = null;
        
    
         
            node = null; 
            ((orderByClause_scope)orderByClause_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g:1132:7: (o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* )
            // JPQL.g:1132:7: o= ORDER BY n= orderByItem ( COMMA n= orderByItem )*
            {
            o=(Token)input.LT(1);
            match(input,ORDER,FOLLOW_ORDER_in_orderByClause7059); if (failed) return node;
            match(input,BY,FOLLOW_BY_in_orderByClause7061); if (failed) return node;
            pushFollow(FOLLOW_orderByItem_in_orderByClause7075);
            n=orderByItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((orderByClause_scope)orderByClause_stack.peek()).items.add(n); 
            }
            // JPQL.g:1134:9: ( COMMA n= orderByItem )*
            loop84:
            do {
                int alt84=2;
                int LA84_0 = input.LA(1);
                
                if ( (LA84_0==COMMA) ) {
                    alt84=1;
                }
                
            
                switch (alt84) {
            	case 1 :
            	    // JPQL.g:1134:10: COMMA n= orderByItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_orderByClause7090); if (failed) return node;
            	    pushFollow(FOLLOW_orderByItem_in_orderByClause7096);
            	    n=orderByItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((orderByClause_scope)orderByClause_stack.peek()).items.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop84;
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
    // JPQL.g:1138:1: orderByItem returns [Object node] : n= stateFieldPathExpression (a= ASC | d= DESC | ) ;
    public final Object orderByItem() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token d=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1140:7: (n= stateFieldPathExpression (a= ASC | d= DESC | ) )
            // JPQL.g:1140:7: n= stateFieldPathExpression (a= ASC | d= DESC | )
            {
            pushFollow(FOLLOW_stateFieldPathExpression_in_orderByItem7142);
            n=stateFieldPathExpression();
            _fsp--;
            if (failed) return node;
            // JPQL.g:1141:9: (a= ASC | d= DESC | )
            int alt85=3;
            switch ( input.LA(1) ) {
            case ASC:
                {
                alt85=1;
                }
                break;
            case DESC:
                {
                alt85=2;
                }
                break;
            case EOF:
            case COMMA:
                {
                alt85=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1141:9: (a= ASC | d= DESC | )", 85, 0, input);
            
                throw nvae;
            }
            
            switch (alt85) {
                case 1 :
                    // JPQL.g:1141:11: a= ASC
                    {
                    a=(Token)input.LT(1);
                    match(input,ASC,FOLLOW_ASC_in_orderByItem7156); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAscOrdering(a.getLine(), a.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1143:11: d= DESC
                    {
                    d=(Token)input.LT(1);
                    match(input,DESC,FOLLOW_DESC_in_orderByItem7185); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newDescOrdering(d.getLine(), d.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1146:13: 
                    {
                    if ( backtracking==0 ) {
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
    // $ANTLR end orderByItem

    protected static class groupByClause_scope {
        List items;
    }
    protected Stack groupByClause_stack = new Stack();
    
    
    // $ANTLR start groupByClause
    // JPQL.g:1150:1: groupByClause returns [Object node] : g= GROUP BY n= groupByItem ( COMMA n= groupByItem )* ;
    public final Object groupByClause() throws RecognitionException {
        groupByClause_stack.push(new groupByClause_scope());

        Object node = null;
    
        Token g=null;
        Object n = null;
        
    
         
            node = null; 
            ((groupByClause_scope)groupByClause_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g:1158:7: (g= GROUP BY n= groupByItem ( COMMA n= groupByItem )* )
            // JPQL.g:1158:7: g= GROUP BY n= groupByItem ( COMMA n= groupByItem )*
            {
            g=(Token)input.LT(1);
            match(input,GROUP,FOLLOW_GROUP_in_groupByClause7265); if (failed) return node;
            match(input,BY,FOLLOW_BY_in_groupByClause7267); if (failed) return node;
            pushFollow(FOLLOW_groupByItem_in_groupByClause7281);
            n=groupByItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((groupByClause_scope)groupByClause_stack.peek()).items.add(n); 
            }
            // JPQL.g:1160:9: ( COMMA n= groupByItem )*
            loop86:
            do {
                int alt86=2;
                int LA86_0 = input.LA(1);
                
                if ( (LA86_0==COMMA) ) {
                    alt86=1;
                }
                
            
                switch (alt86) {
            	case 1 :
            	    // JPQL.g:1160:10: COMMA n= groupByItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_groupByClause7294); if (failed) return node;
            	    pushFollow(FOLLOW_groupByItem_in_groupByClause7300);
            	    n=groupByItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((groupByClause_scope)groupByClause_stack.peek()).items.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop86;
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
    // JPQL.g:1164:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccess );
    public final Object groupByItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1166:7: (n= stateFieldPathExpression | n= variableAccess )
            int alt87=2;
            int LA87_0 = input.LA(1);
            
            if ( (LA87_0==IDENT) ) {
                int LA87_1 = input.LA(2);
                
                if ( (LA87_1==EOF||LA87_1==HAVING||LA87_1==ORDER||LA87_1==COMMA||LA87_1==RIGHT_ROUND_BRACKET) ) {
                    alt87=2;
                }
                else if ( (LA87_1==DOT) ) {
                    alt87=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1164:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccess );", 87, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA87_0==KEY||LA87_0==VALUE) ) {
                alt87=1;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1164:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccess );", 87, 0, input);
            
                throw nvae;
            }
            switch (alt87) {
                case 1 :
                    // JPQL.g:1166:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_groupByItem7346);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1167:7: n= variableAccess
                    {
                    pushFollow(FOLLOW_variableAccess_in_groupByItem7360);
                    n=variableAccess();
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
    // JPQL.g:1170:1: havingClause returns [Object node] : h= HAVING n= conditionalExpression ;
    public final Object havingClause() throws RecognitionException {

        Object node = null;
    
        Token h=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1172:7: (h= HAVING n= conditionalExpression )
            // JPQL.g:1172:7: h= HAVING n= conditionalExpression
            {
            h=(Token)input.LT(1);
            match(input,HAVING,FOLLOW_HAVING_in_havingClause7390); if (failed) return node;
            if ( backtracking==0 ) {
               setAggregatesAllowed(true); 
            }
            pushFollow(FOLLOW_conditionalExpression_in_havingClause7407);
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
        // JPQL.g:601:7: ( LEFT_ROUND_BRACKET conditionalExpression )
        // JPQL.g:601:8: LEFT_ROUND_BRACKET conditionalExpression
        {
        match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_synpred13292); if (failed) return ;
        pushFollow(FOLLOW_conditionalExpression_in_synpred13294);
        conditionalExpression();
        _fsp--;
        if (failed) return ;
        
        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred2
    public final void synpred2_fragment() throws RecognitionException {   
        // JPQL.g:967:11: ( trimSpec trimChar FROM )
        // JPQL.g:967:13: trimSpec trimChar FROM
        {
        pushFollow(FOLLOW_trimSpec_in_synpred25986);
        trimSpec();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_trimChar_in_synpred25988);
        trimChar();
        _fsp--;
        if (failed) return ;
        match(input,FROM,FOLLOW_FROM_in_synpred25990); if (failed) return ;
        
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


 

    public static final BitSet FOLLOW_selectStatement_in_document664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_updateStatement_in_document678 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_deleteStatement_in_document692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selectClause_in_selectStatement725 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_fromClause_in_selectStatement740 = new BitSet(new long[]{0x0010000060000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_whereClause_in_selectStatement755 = new BitSet(new long[]{0x0010000060000000L});
    public static final BitSet FOLLOW_groupByClause_in_selectStatement770 = new BitSet(new long[]{0x0010000040000000L});
    public static final BitSet FOLLOW_havingClause_in_selectStatement786 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_orderByClause_in_selectStatement801 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_selectStatement811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_updateClause_in_updateStatement854 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_setClause_in_updateStatement869 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_whereClause_in_updateStatement883 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_updateStatement893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPDATE_in_updateClause925 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_abstractSchemaName_in_updateClause931 = new BitSet(new long[]{0x0000000000000102L,0x0000000000000020L});
    public static final BitSet FOLLOW_AS_in_updateClause944 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_updateClause952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_in_setClause1001 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_setAssignmentClause_in_setClause1007 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_COMMA_in_setClause1020 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_setAssignmentClause_in_setClause1026 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_setAssignmentTarget_in_setAssignmentClause1084 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_EQUALS_in_setAssignmentClause1088 = new BitSet(new long[]{0xDB0137480407C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_newValue_in_setAssignmentClause1094 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_in_setAssignmentTarget1124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_setAssignmentTarget1139 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_newValue1171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_newValue1185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_deleteClause_in_deleteStatement1229 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_whereClause_in_deleteStatement1242 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_deleteStatement1252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DELETE_in_deleteClause1285 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_FROM_in_deleteClause1287 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_abstractSchemaName_in_deleteClause1293 = new BitSet(new long[]{0x0000000000000102L,0x0000000000000020L});
    public static final BitSet FOLLOW_AS_in_deleteClause1306 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_deleteClause1312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_selectClause1359 = new BitSet(new long[]{0x1002540800A08400L,0x0000000000000028L});
    public static final BitSet FOLLOW_DISTINCT_in_selectClause1362 = new BitSet(new long[]{0x1002540800808400L,0x0000000000000028L});
    public static final BitSet FOLLOW_selectExpression_in_selectClause1378 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_COMMA_in_selectClause1390 = new BitSet(new long[]{0x1002540800808400L,0x0000000000000028L});
    public static final BitSet FOLLOW_selectExpression_in_selectClause1396 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_selectExpression1442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_selectExpression1456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OBJECT_in_selectExpression1466 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1468 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_variableAccess_in_selectExpression1474 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constructorExpression_in_selectExpression1491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntryExpression_in_selectExpression1506 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENTRY_in_mapEntryExpression1538 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mapEntryExpression1540 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_variableAccess_in_mapEntryExpression1546 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mapEntryExpression1548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_pathExprOrVariableAccess1580 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000400L});
    public static final BitSet FOLLOW_DOT_in_pathExprOrVariableAccess1595 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExprOrVariableAccess1601 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000400L});
    public static final BitSet FOLLOW_variableAccess_in_qualifiedIdentificationVariable1657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KEY_in_qualifiedIdentificationVariable1671 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1673 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_variableAccess_in_qualifiedIdentificationVariable1679 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1681 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VALUE_in_qualifiedIdentificationVariable1696 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1698 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_variableAccess_in_qualifiedIdentificationVariable1704 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AVG_in_aggregateExpression1739 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1741 = new BitSet(new long[]{0x0000000800200000L,0x0000000000000028L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1744 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000028L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression1762 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1764 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MAX_in_aggregateExpression1785 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1787 = new BitSet(new long[]{0x0000000800200000L,0x0000000000000028L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1790 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000028L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression1809 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MIN_in_aggregateExpression1831 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1833 = new BitSet(new long[]{0x0000000800200000L,0x0000000000000028L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1836 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000028L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression1854 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUM_in_aggregateExpression1876 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1878 = new BitSet(new long[]{0x0000000800200000L,0x0000000000000028L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1881 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000028L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression1899 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1901 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COUNT_in_aggregateExpression1921 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1923 = new BitSet(new long[]{0x0000000800200000L,0x0000000000000028L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1926 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000028L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_aggregateExpression1944 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_constructorExpression1989 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_constructorName_in_constructorExpression1995 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression2005 = new BitSet(new long[]{0x1000140800008400L,0x0000000000000028L});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression2020 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000240L});
    public static final BitSet FOLLOW_COMMA_in_constructorExpression2035 = new BitSet(new long[]{0x1000140800008400L,0x0000000000000028L});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression2041 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000240L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression2056 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_constructorName2097 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000400L});
    public static final BitSet FOLLOW_DOT_in_constructorName2111 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_constructorName2115 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000400L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_constructorItem2159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_constructorItem2173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_fromClause2206 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2208 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_COMMA_in_fromClause2220 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2225 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_fromClause2250 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2316 = new BitSet(new long[]{0x0000002500000002L});
    public static final BitSet FOLLOW_join_in_identificationVariableDeclaration2335 = new BitSet(new long[]{0x0000002500000002L});
    public static final BitSet FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2370 = new BitSet(new long[]{0x0000000000000100L,0x0000000000000020L});
    public static final BitSet FOLLOW_AS_in_rangeVariableDeclaration2373 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_rangeVariableDeclaration2379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_joinSpec_in_join2462 = new BitSet(new long[]{0x0000000008000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2476 = new BitSet(new long[]{0x0000000000000100L,0x0000000000000020L});
    public static final BitSet FOLLOW_AS_in_join2479 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_join2485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FETCH_in_join2507 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_in_joinSpec2559 = new BitSet(new long[]{0x0020000400000000L});
    public static final BitSet FOLLOW_OUTER_in_joinSpec2562 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_INNER_in_joinSpec2571 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_JOIN_in_joinSpec2577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_collectionMemberDeclaration2605 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2607 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000028L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2613 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2615 = new BitSet(new long[]{0x0000000000000100L,0x0000000000000020L});
    public static final BitSet FOLLOW_AS_in_collectionMemberDeclaration2625 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_collectionMemberDeclaration2631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_collectionValuedPathExpression2669 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_associationPathExpression2701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccess_in_joinAssociationPathExpression2733 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_DOT_in_joinAssociationPathExpression2737 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_joinAssociationPathExpression2743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_singleValuedPathExpression2783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_stateFieldPathExpression2815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_pathExpression2847 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_DOT_in_pathExpression2862 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExpression2868 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000400L});
    public static final BitSet FOLLOW_IDENT_in_variableAccess2964 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_whereClause3002 = new BitSet(new long[]{0xDB00B7480607C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_conditionalExpression_in_whereClause3008 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression3050 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_OR_in_conditionalExpression3065 = new BitSet(new long[]{0xDB00B7480607C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression3071 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm3126 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_AND_in_conditionalTerm3141 = new BitSet(new long[]{0xDB00B7480607C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm3147 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_NOT_in_conditionalFactor3202 = new BitSet(new long[]{0xDB0037480607C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_conditionalPrimary_in_conditionalFactor3221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_existsExpression_in_conditionalFactor3250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3307 = new BitSet(new long[]{0xDB00B7480607C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalPrimary3313 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleConditionalExpression_in_conditionalPrimary3329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_simpleConditionalExpression3361 = new BitSet(new long[]{0x0000888280000800L,0x000000000000F880L});
    public static final BitSet FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3425 = new BitSet(new long[]{0x0000088080000800L});
    public static final BitSet FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IS_in_simpleConditionalExpressionRemainder3444 = new BitSet(new long[]{0x0001800000400000L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3449 = new BitSet(new long[]{0x0001000000400000L});
    public static final BitSet FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_betweenExpression_in_conditionWithNotExpression3492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_likeExpression_in_conditionWithNotExpression3507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inExpression_in_conditionWithNotExpression3521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullComparisonExpression_in_isExpression3570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_emptyCollectionComparisonExpression_in_isExpression3585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BETWEEN_in_betweenExpression3618 = new BitSet(new long[]{0xDB0037480407C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_arithmeticExpression_in_betweenExpression3632 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_AND_in_betweenExpression3634 = new BitSet(new long[]{0xDB0037480407C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_arithmeticExpression_in_betweenExpression3640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_inExpression3683 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_inExpression3693 = new BitSet(new long[]{0x0040000000000000L,0x000000000FF00000L});
    public static final BitSet FOLLOW_inItem_in_inExpression3709 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000240L});
    public static final BitSet FOLLOW_COMMA_in_inExpression3727 = new BitSet(new long[]{0x0000000000000000L,0x000000000FF00000L});
    public static final BitSet FOLLOW_inItem_in_inExpression3733 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000240L});
    public static final BitSet FOLLOW_subquery_in_inExpression3768 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression3802 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_inItem3832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_inItem3846 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_inItem3860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LIKE_in_likeExpression3892 = new BitSet(new long[]{0x0000000000000000L,0x000000000F000000L});
    public static final BitSet FOLLOW_likeValue_in_likeExpression3898 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_escape_in_likeExpression3913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ESCAPE_in_escape3953 = new BitSet(new long[]{0x0000000000000000L,0x000000000F000000L});
    public static final BitSet FOLLOW_likeValue_in_escape3959 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_likeValue3999 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_likeValue4013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_nullComparisonExpression4046 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EMPTY_in_emptyCollectionComparisonExpression4087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MEMBER_in_collectionMemberExpression4128 = new BitSet(new long[]{0x0004000800000000L,0x0000000000000028L});
    public static final BitSet FOLLOW_OF_in_collectionMemberExpression4131 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000028L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression4139 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_existsExpression4179 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4181 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_subquery_in_existsExpression4187 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4189 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_comparisonExpression4229 = new BitSet(new long[]{0xDF0037480407C4B0L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4235 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4256 = new BitSet(new long[]{0xDF0037480407C4B0L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_in_comparisonExpression4283 = new BitSet(new long[]{0xDF0037480407C4B0L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4310 = new BitSet(new long[]{0xDF0037480407C4B0L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_in_comparisonExpression4337 = new BitSet(new long[]{0xDF0037480407C4B0L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4364 = new BitSet(new long[]{0xDF0037480407C4B0L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4467 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_subquery_in_arithmeticExpression4473 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4507 = new BitSet(new long[]{0x0000000000000002L,0x0000000000030000L});
    public static final BitSet FOLLOW_PLUS_in_simpleArithmeticExpression4523 = new BitSet(new long[]{0xDB0037480407C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4529 = new BitSet(new long[]{0x0000000000000002L,0x0000000000030000L});
    public static final BitSet FOLLOW_MINUS_in_simpleArithmeticExpression4558 = new BitSet(new long[]{0xDB0037480407C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4564 = new BitSet(new long[]{0x0000000000000002L,0x0000000000030000L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4621 = new BitSet(new long[]{0x0000000000000002L,0x00000000000C0000L});
    public static final BitSet FOLLOW_MULTIPLY_in_arithmeticTerm4637 = new BitSet(new long[]{0xDB0037480407C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4643 = new BitSet(new long[]{0x0000000000000002L,0x00000000000C0000L});
    public static final BitSet FOLLOW_DIVIDE_in_arithmeticTerm4672 = new BitSet(new long[]{0xDB0037480407C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4678 = new BitSet(new long[]{0x0000000000000002L,0x00000000000C0000L});
    public static final BitSet FOLLOW_PLUS_in_arithmeticFactor4732 = new BitSet(new long[]{0xDB0037480407C410L,0x000000000FF0012CL});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor4739 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_arithmeticFactor4761 = new BitSet(new long[]{0xDB0037480407C410L,0x000000000FF0012CL});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor4767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor4791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_arithmeticPrimary4825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_arithmeticPrimary4839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningNumerics_in_arithmeticPrimary4853 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningDatetime_in_arithmeticPrimary4867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_arithmeticPrimary4881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_arithmeticPrimary4895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_arithmeticPrimary4909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_arithmeticPrimary4923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_arithmeticPrimary4937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary4947 = new BitSet(new long[]{0xDB0037480407C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary4953 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary4955 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALL_in_anyOrAllExpression4985 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression4987 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression4993 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression4995 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_anyOrAllExpression5015 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5017 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5023 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5025 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SOME_in_anyOrAllExpression5045 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5047 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5053 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5055 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_stringPrimary5095 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_stringPrimary5109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_stringPrimary5123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_stringPrimary5137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_literal5171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_literal5185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_literal5199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_LITERAL_in_literalNumeric5229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LONG_LITERAL_in_literalNumeric5245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_LITERAL_in_literalNumeric5266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_LITERAL_in_literalNumeric5286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_literalBoolean5324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_literalBoolean5346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString5385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString5406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_POSITIONAL_PARAM_in_inputParameter5444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_PARAM_in_inputParameter5464 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_abs_in_functionsReturningNumerics5504 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_length_in_functionsReturningNumerics5518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mod_in_functionsReturningNumerics5532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sqrt_in_functionsReturningNumerics5546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_locate_in_functionsReturningNumerics5560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_size_in_functionsReturningNumerics5574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_DATE_in_functionsReturningDatetime5604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIME_in_functionsReturningDatetime5625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime5645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_concat_in_functionsReturningStrings5685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_substring_in_functionsReturningStrings5699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trim_in_functionsReturningStrings5713 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_upper_in_functionsReturningStrings5727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lower_in_functionsReturningStrings5741 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONCAT_in_concat5772 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_concat5783 = new BitSet(new long[]{0x4800020800004000L,0x000000000F00002CL});
    public static final BitSet FOLLOW_stringPrimary_in_concat5798 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_COMMA_in_concat5800 = new BitSet(new long[]{0x4800020800004000L,0x000000000F00002CL});
    public static final BitSet FOLLOW_stringPrimary_in_concat5806 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_concat5816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBSTRING_in_substring5854 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_substring5867 = new BitSet(new long[]{0x4800020800004000L,0x000000000F00002CL});
    public static final BitSet FOLLOW_stringPrimary_in_substring5881 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_COMMA_in_substring5883 = new BitSet(new long[]{0xDB0037480407C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_substring5897 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_COMMA_in_substring5899 = new BitSet(new long[]{0xDB0037480407C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_substring5913 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_substring5923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRIM_in_trim5961 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_trim5971 = new BitSet(new long[]{0x6800021810005000L,0x000000000F00002CL});
    public static final BitSet FOLLOW_trimSpec_in_trim5999 = new BitSet(new long[]{0x0000000010000000L,0x000000000F000000L});
    public static final BitSet FOLLOW_trimChar_in_trim6005 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_FROM_in_trim6007 = new BitSet(new long[]{0x4800020800004000L,0x000000000F00002CL});
    public static final BitSet FOLLOW_stringPrimary_in_trim6025 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_trim6035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEADING_in_trimSpec6071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRAILING_in_trimSpec6089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOTH_in_trimSpec6107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_trimChar6154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_trimChar6168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPPER_in_upper6205 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_upper6207 = new BitSet(new long[]{0x4800020800004000L,0x000000000F00002CL});
    public static final BitSet FOLLOW_stringPrimary_in_upper6213 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_upper6215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOWER_in_lower6253 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_lower6255 = new BitSet(new long[]{0x4800020800004000L,0x000000000F00002CL});
    public static final BitSet FOLLOW_stringPrimary_in_lower6261 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_lower6263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ABS_in_abs6302 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_abs6304 = new BitSet(new long[]{0xDB0037480407C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_abs6310 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_abs6312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LENGTH_in_length6350 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_length6352 = new BitSet(new long[]{0x4800020800004000L,0x000000000F00002CL});
    public static final BitSet FOLLOW_stringPrimary_in_length6358 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_length6360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCATE_in_locate6398 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_locate6408 = new BitSet(new long[]{0x4800020800004000L,0x000000000F00002CL});
    public static final BitSet FOLLOW_stringPrimary_in_locate6423 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_COMMA_in_locate6425 = new BitSet(new long[]{0x4800020800004000L,0x000000000F00002CL});
    public static final BitSet FOLLOW_stringPrimary_in_locate6431 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000240L});
    public static final BitSet FOLLOW_COMMA_in_locate6443 = new BitSet(new long[]{0xDB0037480407C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_locate6449 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_locate6462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIZE_in_size6500 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_size6511 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000028L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_size6517 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_size6519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_mod6557 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mod6559 = new BitSet(new long[]{0xDB0037480407C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_mod6573 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_COMMA_in_mod6575 = new BitSet(new long[]{0xDB0037480407C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_mod6590 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mod6600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQRT_in_sqrt6638 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_sqrt6649 = new BitSet(new long[]{0xDB0037480407C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_sqrt6655 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt6657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleSelectClause_in_subquery6698 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_subqueryFromClause_in_subquery6713 = new BitSet(new long[]{0x0000000060000002L,0x0000000000000010L});
    public static final BitSet FOLLOW_whereClause_in_subquery6728 = new BitSet(new long[]{0x0000000060000002L});
    public static final BitSet FOLLOW_groupByClause_in_subquery6743 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_havingClause_in_subquery6759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_simpleSelectClause6802 = new BitSet(new long[]{0x1000140800208400L,0x0000000000000028L});
    public static final BitSet FOLLOW_DISTINCT_in_simpleSelectClause6805 = new BitSet(new long[]{0x1000140800008400L,0x0000000000000028L});
    public static final BitSet FOLLOW_simpleSelectExpression_in_simpleSelectClause6821 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleValuedPathExpression_in_simpleSelectExpression6861 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_simpleSelectExpression6876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccess_in_simpleSelectExpression6891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_subqueryFromClause6926 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause6928 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_COMMA_in_subqueryFromClause6942 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000003FFFFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause6944 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration6982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration6995 = new BitSet(new long[]{0x0000000000000100L,0x0000000000000020L});
    public static final BitSet FOLLOW_AS_in_subselectIdentificationVariableDeclaration6998 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration7004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration7026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_in_orderByClause7059 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_orderByClause7061 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000028L});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause7075 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_COMMA_in_orderByClause7090 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000028L});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause7096 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_orderByItem7142 = new BitSet(new long[]{0x0000000000080202L});
    public static final BitSet FOLLOW_ASC_in_orderByItem7156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_in_orderByItem7185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_groupByClause7265 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_groupByClause7267 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000028L});
    public static final BitSet FOLLOW_groupByItem_in_groupByClause7281 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_COMMA_in_groupByClause7294 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000028L});
    public static final BitSet FOLLOW_groupByItem_in_groupByClause7300 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_groupByItem7346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccess_in_groupByItem7360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HAVING_in_havingClause7390 = new BitSet(new long[]{0xDB00B7480607C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_conditionalExpression_in_havingClause7407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_synpred13292 = new BitSet(new long[]{0xDB00B7480607C410L,0x000000000FF3012CL});
    public static final BitSet FOLLOW_conditionalExpression_in_synpred13294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trimSpec_in_synpred25986 = new BitSet(new long[]{0x0000000010000000L,0x000000000F000000L});
    public static final BitSet FOLLOW_trimChar_in_synpred25988 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_FROM_in_synpred25990 = new BitSet(new long[]{0x0000000000000002L});

}