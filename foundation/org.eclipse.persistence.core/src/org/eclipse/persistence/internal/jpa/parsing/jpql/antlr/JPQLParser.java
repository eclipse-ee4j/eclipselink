// $ANTLR 3.0 JPQL.g 2009-06-09 15:38:24

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ABS", "ALL", "AND", "ANY", "AS", "ASC", "AVG", "BETWEEN", "BOTH", "BY", "CONCAT", "COUNT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "DESC", "DELETE", "DISTINCT", "EMPTY", "ENTRY", "ESCAPE", "EXISTS", "FALSE", "FETCH", "FROM", "GROUP", "HAVING", "IN", "INNER", "IS", "JOIN", "KEY", "LEADING", "LEFT", "LENGTH", "LIKE", "LOCATE", "LOWER", "MAX", "MEMBER", "MIN", "MOD", "NEW", "NOT", "NULL", "OBJECT", "OF", "OR", "ORDER", "OUTER", "SELECT", "SET", "SIZE", "SQRT", "SOME", "SUBSTRING", "SUM", "TRAILING", "TRIM", "TRUE", "TYPE", "UNKNOWN", "UPDATE", "UPPER", "VALUE", "WHERE", "IDENT", "COMMA", "EQUALS", "LEFT_ROUND_BRACKET", "RIGHT_ROUND_BRACKET", "DOT", "NOT_EQUAL_TO", "GREATER_THAN", "GREATER_THAN_EQUAL_TO", "LESS_THAN", "LESS_THAN_EQUAL_TO", "PLUS", "MINUS", "MULTIPLY", "DIVIDE", "INTEGER_LITERAL", "LONG_LITERAL", "FLOAT_LITERAL", "DOUBLE_LITERAL", "STRING_LITERAL_DOUBLE_QUOTED", "STRING_LITERAL_SINGLE_QUOTED", "POSITIONAL_PARAM", "NAMED_PARAM", "WS", "TEXTCHAR", "HEX_DIGIT", "HEX_LITERAL", "INTEGER_SUFFIX", "OCTAL_LITERAL", "NUMERIC_DIGITS", "DOUBLE_SUFFIX", "EXPONENT", "FLOAT_SUFFIX"
    };
    public static final int EXPONENT=101;
    public static final int FLOAT_SUFFIX=102;
    public static final int MOD=45;
    public static final int CURRENT_TIME=17;
    public static final int NEW=46;
    public static final int LEFT_ROUND_BRACKET=73;
    public static final int DOUBLE_LITERAL=88;
    public static final int COUNT=15;
    public static final int EQUALS=72;
    public static final int NOT=47;
    public static final int EOF=-1;
    public static final int TYPE=64;
    public static final int GREATER_THAN_EQUAL_TO=78;
    public static final int ESCAPE=24;
    public static final int NAMED_PARAM=92;
    public static final int BOTH=12;
    public static final int NUMERIC_DIGITS=99;
    public static final int SELECT=54;
    public static final int DIVIDE=84;
    public static final int ASC=9;
    public static final int CONCAT=14;
    public static final int KEY=35;
    public static final int NULL=48;
    public static final int TRAILING=61;
    public static final int DELETE=20;
    public static final int VALUE=68;
    public static final int OF=50;
    public static final int LEADING=36;
    public static final int INTEGER_SUFFIX=97;
    public static final int EMPTY=22;
    public static final int ABS=4;
    public static final int GROUP=29;
    public static final int NOT_EQUAL_TO=76;
    public static final int WS=93;
    public static final int FETCH=27;
    public static final int STRING_LITERAL_SINGLE_QUOTED=90;
    public static final int INTEGER_LITERAL=85;
    public static final int OR=51;
    public static final int TRIM=62;
    public static final int LESS_THAN=79;
    public static final int RIGHT_ROUND_BRACKET=74;
    public static final int POSITIONAL_PARAM=91;
    public static final int LOWER=41;
    public static final int FROM=28;
    public static final int FALSE=26;
    public static final int LESS_THAN_EQUAL_TO=80;
    public static final int DISTINCT=21;
    public static final int CURRENT_DATE=16;
    public static final int SIZE=56;
    public static final int UPPER=67;
    public static final int WHERE=69;
    public static final int MEMBER=43;
    public static final int INNER=32;
    public static final int ORDER=52;
    public static final int TEXTCHAR=94;
    public static final int MAX=42;
    public static final int UPDATE=66;
    public static final int AND=6;
    public static final int SUM=60;
    public static final int STRING_LITERAL_DOUBLE_QUOTED=89;
    public static final int LENGTH=38;
    public static final int AS=8;
    public static final int IN=31;
    public static final int UNKNOWN=65;
    public static final int OBJECT=49;
    public static final int MULTIPLY=83;
    public static final int COMMA=71;
    public static final int IS=33;
    public static final int LEFT=37;
    public static final int AVG=10;
    public static final int SOME=58;
    public static final int ALL=5;
    public static final int IDENT=70;
    public static final int PLUS=81;
    public static final int HEX_LITERAL=96;
    public static final int EXISTS=25;
    public static final int DOT=75;
    public static final int CURRENT_TIMESTAMP=18;
    public static final int LIKE=39;
    public static final int OUTER=53;
    public static final int BY=13;
    public static final int GREATER_THAN=77;
    public static final int OCTAL_LITERAL=98;
    public static final int HEX_DIGIT=95;
    public static final int SET=55;
    public static final int HAVING=30;
    public static final int ENTRY=23;
    public static final int MIN=44;
    public static final int SQRT=57;
    public static final int MINUS=82;
    public static final int LONG_LITERAL=86;
    public static final int TRUE=63;
    public static final int JOIN=34;
    public static final int SUBSTRING=59;
    public static final int DOUBLE_SUFFIX=100;
    public static final int FLOAT_LITERAL=87;
    public static final int ANY=7;
    public static final int LOCATE=40;
    public static final int DESC=19;
    public static final int BETWEEN=11;
    
        public JPQLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[98+1];
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
    // JPQL.g:189:1: document : (root= selectStatement | root= updateStatement | root= deleteStatement );
    public final void document() throws RecognitionException {
        Object root = null;
        
    
        try {
            // JPQL.g:190:7: (root= selectStatement | root= updateStatement | root= deleteStatement )
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
                    new NoViableAltException("189:1: document : (root= selectStatement | root= updateStatement | root= deleteStatement );", 1, 0, input);
            
                throw nvae;
            }
            
            switch (alt1) {
                case 1 :
                    // JPQL.g:190:7: root= selectStatement
                    {
                    pushFollow(FOLLOW_selectStatement_in_document673);
                    root=selectStatement();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      queryRoot = root;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:191:7: root= updateStatement
                    {
                    pushFollow(FOLLOW_updateStatement_in_document687);
                    root=updateStatement();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      queryRoot = root;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:192:7: root= deleteStatement
                    {
                    pushFollow(FOLLOW_deleteStatement_in_document701);
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
    // JPQL.g:195:1: selectStatement returns [Object node] : select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF ;
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
            // JPQL.g:199:7: (select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF )
            // JPQL.g:199:7: select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF
            {
            pushFollow(FOLLOW_selectClause_in_selectStatement734);
            select=selectClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_fromClause_in_selectStatement749);
            from=fromClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g:201:7: (where= whereClause )?
            int alt2=2;
            int LA2_0 = input.LA(1);
            
            if ( (LA2_0==WHERE) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // JPQL.g:201:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_selectStatement764);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:202:7: (groupBy= groupByClause )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            
            if ( (LA3_0==GROUP) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // JPQL.g:202:8: groupBy= groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_selectStatement779);
                    groupBy=groupByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:203:7: (having= havingClause )?
            int alt4=2;
            int LA4_0 = input.LA(1);
            
            if ( (LA4_0==HAVING) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // JPQL.g:203:8: having= havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_selectStatement795);
                    having=havingClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:204:7: (orderBy= orderByClause )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            
            if ( (LA5_0==ORDER) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // JPQL.g:204:8: orderBy= orderByClause
                    {
                    pushFollow(FOLLOW_orderByClause_in_selectStatement810);
                    orderBy=orderByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_selectStatement820); if (failed) return node;
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
    // JPQL.g:214:1: updateStatement returns [Object node] : update= updateClause set= setClause (where= whereClause )? EOF ;
    public final Object updateStatement() throws RecognitionException {

        Object node = null;
    
        Object update = null;

        Object set = null;

        Object where = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:218:7: (update= updateClause set= setClause (where= whereClause )? EOF )
            // JPQL.g:218:7: update= updateClause set= setClause (where= whereClause )? EOF
            {
            pushFollow(FOLLOW_updateClause_in_updateStatement863);
            update=updateClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_setClause_in_updateStatement878);
            set=setClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g:220:7: (where= whereClause )?
            int alt6=2;
            int LA6_0 = input.LA(1);
            
            if ( (LA6_0==WHERE) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // JPQL.g:220:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_updateStatement892);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_updateStatement902); if (failed) return node;
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
    // JPQL.g:224:1: updateClause returns [Object node] : u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )? ;
    public final Object updateClause() throws RecognitionException {

        Object node = null;
    
        Token u=null;
        Token ident=null;
        String schema = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:228:7: (u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )? )
            // JPQL.g:228:7: u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )?
            {
            u=(Token)input.LT(1);
            match(input,UPDATE,FOLLOW_UPDATE_in_updateClause934); if (failed) return node;
            pushFollow(FOLLOW_abstractSchemaName_in_updateClause940);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // JPQL.g:229:9: ( ( AS )? ident= IDENT )?
            int alt8=2;
            int LA8_0 = input.LA(1);
            
            if ( (LA8_0==AS||LA8_0==IDENT) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // JPQL.g:229:10: ( AS )? ident= IDENT
                    {
                    // JPQL.g:229:10: ( AS )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);
                    
                    if ( (LA7_0==AS) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // JPQL.g:229:11: AS
                            {
                            match(input,AS,FOLLOW_AS_in_updateClause953); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    ident=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_updateClause961); if (failed) return node;
                    
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
    // JPQL.g:240:1: setClause returns [Object node] : t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )* ;
    public final Object setClause() throws RecognitionException {
        setClause_stack.push(new setClause_scope());

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         
            node = null; 
            ((setClause_scope)setClause_stack.peek()).assignments = new ArrayList();
    
        try {
            // JPQL.g:248:7: (t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )* )
            // JPQL.g:248:7: t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )*
            {
            t=(Token)input.LT(1);
            match(input,SET,FOLLOW_SET_in_setClause1010); if (failed) return node;
            pushFollow(FOLLOW_setAssignmentClause_in_setClause1016);
            n=setAssignmentClause();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((setClause_scope)setClause_stack.peek()).assignments.add(n); 
            }
            // JPQL.g:249:9: ( COMMA n= setAssignmentClause )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);
                
                if ( (LA9_0==COMMA) ) {
                    alt9=1;
                }
                
            
                switch (alt9) {
            	case 1 :
            	    // JPQL.g:249:10: COMMA n= setAssignmentClause
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_setClause1029); if (failed) return node;
            	    pushFollow(FOLLOW_setAssignmentClause_in_setClause1035);
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
    // JPQL.g:253:1: setAssignmentClause returns [Object node] : target= setAssignmentTarget t= EQUALS value= newValue ;
    public final Object setAssignmentClause() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object target = null;

        Object value = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:261:7: (target= setAssignmentTarget t= EQUALS value= newValue )
            // JPQL.g:261:7: target= setAssignmentTarget t= EQUALS value= newValue
            {
            pushFollow(FOLLOW_setAssignmentTarget_in_setAssignmentClause1093);
            target=setAssignmentTarget();
            _fsp--;
            if (failed) return node;
            t=(Token)input.LT(1);
            match(input,EQUALS,FOLLOW_EQUALS_in_setAssignmentClause1097); if (failed) return node;
            pushFollow(FOLLOW_newValue_in_setAssignmentClause1103);
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
    // JPQL.g:264:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );
    public final Object setAssignmentTarget() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:268:7: (n= attribute | n= pathExpression )
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
                        new NoViableAltException("264:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 1, input);
                
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
                        new NoViableAltException("264:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 2, input);
                
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
                        new NoViableAltException("264:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 3, input);
                
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
            case TYPE:
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
                    new NoViableAltException("264:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 0, input);
            
                throw nvae;
            }
            
            switch (alt10) {
                case 1 :
                    // JPQL.g:268:7: n= attribute
                    {
                    pushFollow(FOLLOW_attribute_in_setAssignmentTarget1133);
                    n=attribute();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:269:7: n= pathExpression
                    {
                    pushFollow(FOLLOW_pathExpression_in_setAssignmentTarget1148);
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
    // JPQL.g:272:1: newValue returns [Object node] : (n= simpleArithmeticExpression | n1= NULL );
    public final Object newValue() throws RecognitionException {

        Object node = null;
    
        Token n1=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:274:7: (n= simpleArithmeticExpression | n1= NULL )
            int alt11=2;
            int LA11_0 = input.LA(1);
            
            if ( (LA11_0==ABS||LA11_0==AVG||(LA11_0>=CONCAT && LA11_0<=CURRENT_TIMESTAMP)||LA11_0==FALSE||LA11_0==KEY||LA11_0==LENGTH||(LA11_0>=LOCATE && LA11_0<=MAX)||(LA11_0>=MIN && LA11_0<=MOD)||(LA11_0>=SIZE && LA11_0<=SQRT)||(LA11_0>=SUBSTRING && LA11_0<=SUM)||(LA11_0>=TRIM && LA11_0<=TYPE)||(LA11_0>=UPPER && LA11_0<=VALUE)||LA11_0==IDENT||LA11_0==LEFT_ROUND_BRACKET||(LA11_0>=PLUS && LA11_0<=MINUS)||(LA11_0>=INTEGER_LITERAL && LA11_0<=NAMED_PARAM)) ) {
                alt11=1;
            }
            else if ( (LA11_0==NULL) ) {
                alt11=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("272:1: newValue returns [Object node] : (n= simpleArithmeticExpression | n1= NULL );", 11, 0, input);
            
                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // JPQL.g:274:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_newValue1180);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:275:7: n1= NULL
                    {
                    n1=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_newValue1194); if (failed) return node;
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
    // JPQL.g:281:1: deleteStatement returns [Object node] : delete= deleteClause (where= whereClause )? EOF ;
    public final Object deleteStatement() throws RecognitionException {

        Object node = null;
    
        Object delete = null;

        Object where = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:285:7: (delete= deleteClause (where= whereClause )? EOF )
            // JPQL.g:285:7: delete= deleteClause (where= whereClause )? EOF
            {
            pushFollow(FOLLOW_deleteClause_in_deleteStatement1238);
            delete=deleteClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g:286:7: (where= whereClause )?
            int alt12=2;
            int LA12_0 = input.LA(1);
            
            if ( (LA12_0==WHERE) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // JPQL.g:286:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_deleteStatement1251);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_deleteStatement1261); if (failed) return node;
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
    // JPQL.g:290:1: deleteClause returns [Object node] : t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )? ;
    public final Object deleteClause() throws RecognitionException {
        deleteClause_stack.push(new deleteClause_scope());

        Object node = null;
    
        Token t=null;
        Token ident=null;
        String schema = null;
        
    
         
            node = null; 
            ((deleteClause_scope)deleteClause_stack.peek()).variable = null;
    
        try {
            // JPQL.g:298:7: (t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )? )
            // JPQL.g:298:7: t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )?
            {
            t=(Token)input.LT(1);
            match(input,DELETE,FOLLOW_DELETE_in_deleteClause1294); if (failed) return node;
            match(input,FROM,FOLLOW_FROM_in_deleteClause1296); if (failed) return node;
            pushFollow(FOLLOW_abstractSchemaName_in_deleteClause1302);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // JPQL.g:299:9: ( ( AS )? ident= IDENT )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            
            if ( (LA14_0==AS||LA14_0==IDENT) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // JPQL.g:299:10: ( AS )? ident= IDENT
                    {
                    // JPQL.g:299:10: ( AS )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);
                    
                    if ( (LA13_0==AS) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // JPQL.g:299:11: AS
                            {
                            match(input,AS,FOLLOW_AS_in_deleteClause1315); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    ident=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_deleteClause1321); if (failed) return node;
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
    // JPQL.g:308:1: selectClause returns [Object node] : t= SELECT ( DISTINCT )? n= selectExpression ( COMMA n= selectExpression )* ;
    public final Object selectClause() throws RecognitionException {
        selectClause_stack.push(new selectClause_scope());

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         
            node = null;
            ((selectClause_scope)selectClause_stack.peek()).distinct = false;
            ((selectClause_scope)selectClause_stack.peek()).exprs = new ArrayList();
    
        try {
            // JPQL.g:318:7: (t= SELECT ( DISTINCT )? n= selectExpression ( COMMA n= selectExpression )* )
            // JPQL.g:318:7: t= SELECT ( DISTINCT )? n= selectExpression ( COMMA n= selectExpression )*
            {
            t=(Token)input.LT(1);
            match(input,SELECT,FOLLOW_SELECT_in_selectClause1368); if (failed) return node;
            // JPQL.g:318:16: ( DISTINCT )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            
            if ( (LA15_0==DISTINCT) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // JPQL.g:318:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_selectClause1371); if (failed) return node;
                    if ( backtracking==0 ) {
                       ((selectClause_scope)selectClause_stack.peek()).distinct = true; 
                    }
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_selectExpression_in_selectClause1387);
            n=selectExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((selectClause_scope)selectClause_stack.peek()).exprs.add(n); 
            }
            // JPQL.g:320:7: ( COMMA n= selectExpression )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);
                
                if ( (LA16_0==COMMA) ) {
                    alt16=1;
                }
                
            
                switch (alt16) {
            	case 1 :
            	    // JPQL.g:320:9: COMMA n= selectExpression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_selectClause1399); if (failed) return node;
            	    pushFollow(FOLLOW_selectExpression_in_selectClause1405);
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
    // JPQL.g:327:1: selectExpression returns [Object node] : (n= pathExprOrVariableAccess | n= aggregateExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );
    public final Object selectExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:329:7: (n= pathExprOrVariableAccess | n= aggregateExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression )
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
                    new NoViableAltException("327:1: selectExpression returns [Object node] : (n= pathExprOrVariableAccess | n= aggregateExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 17, 0, input);
            
                throw nvae;
            }
            
            switch (alt17) {
                case 1 :
                    // JPQL.g:329:7: n= pathExprOrVariableAccess
                    {
                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_selectExpression1451);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:330:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_selectExpression1465);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:331:7: OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    match(input,OBJECT,FOLLOW_OBJECT_in_selectExpression1475); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1477); if (failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_selectExpression1483);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1485); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:332:7: n= constructorExpression
                    {
                    pushFollow(FOLLOW_constructorExpression_in_selectExpression1500);
                    n=constructorExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:333:7: n= mapEntryExpression
                    {
                    pushFollow(FOLLOW_mapEntryExpression_in_selectExpression1515);
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
    // JPQL.g:336:1: mapEntryExpression returns [Object node] : l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET ;
    public final Object mapEntryExpression() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:338:7: (l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
            // JPQL.g:338:7: l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,ENTRY,FOLLOW_ENTRY_in_mapEntryExpression1547); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mapEntryExpression1549); if (failed) return node;
            pushFollow(FOLLOW_variableAccessOrTypeConstant_in_mapEntryExpression1555);
            n=variableAccessOrTypeConstant();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mapEntryExpression1557); if (failed) return node;
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
    // JPQL.g:341:1: pathExprOrVariableAccess returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )* ;
    public final Object pathExprOrVariableAccess() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g:345:7: (n= qualifiedIdentificationVariable (d= DOT right= attribute )* )
            // JPQL.g:345:7: n= qualifiedIdentificationVariable (d= DOT right= attribute )*
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_pathExprOrVariableAccess1589);
            n=qualifiedIdentificationVariable();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:346:9: (d= DOT right= attribute )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);
                
                if ( (LA18_0==DOT) ) {
                    alt18=1;
                }
                
            
                switch (alt18) {
            	case 1 :
            	    // JPQL.g:346:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pathExprOrVariableAccess1604); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_pathExprOrVariableAccess1610);
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
    // JPQL.g:351:1: qualifiedIdentificationVariable returns [Object node] : (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET );
    public final Object qualifiedIdentificationVariable() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:353:7: (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
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
                    new NoViableAltException("351:1: qualifiedIdentificationVariable returns [Object node] : (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET );", 19, 0, input);
            
                throw nvae;
            }
            
            switch (alt19) {
                case 1 :
                    // JPQL.g:353:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1666);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:354:7: l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    l=(Token)input.LT(1);
                    match(input,KEY,FOLLOW_KEY_in_qualifiedIdentificationVariable1680); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1682); if (failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1688);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1690); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newKey(l.getLine(), l.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:355:7: l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    l=(Token)input.LT(1);
                    match(input,VALUE,FOLLOW_VALUE_in_qualifiedIdentificationVariable1705); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1707); if (failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1713);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1715); if (failed) return node;
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
    // JPQL.g:358:1: aggregateExpression returns [Object node] : (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET );
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
            // JPQL.g:366:7: (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET )
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
                    new NoViableAltException("358:1: aggregateExpression returns [Object node] : (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET );", 25, 0, input);
            
                throw nvae;
            }
            
            switch (alt25) {
                case 1 :
                    // JPQL.g:366:7: t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t1=(Token)input.LT(1);
                    match(input,AVG,FOLLOW_AVG_in_aggregateExpression1748); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1750); if (failed) return node;
                    // JPQL.g:366:33: ( DISTINCT )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);
                    
                    if ( (LA20_0==DISTINCT) ) {
                        alt20=1;
                    }
                    switch (alt20) {
                        case 1 :
                            // JPQL.g:366:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1753); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression1771);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1773); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAvg(t1.getLine(), t1.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:369:7: t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t2=(Token)input.LT(1);
                    match(input,MAX,FOLLOW_MAX_in_aggregateExpression1794); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1796); if (failed) return node;
                    // JPQL.g:369:33: ( DISTINCT )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);
                    
                    if ( (LA21_0==DISTINCT) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // JPQL.g:369:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1799); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression1818);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1820); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newMax(t2.getLine(), t2.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:372:7: t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t3=(Token)input.LT(1);
                    match(input,MIN,FOLLOW_MIN_in_aggregateExpression1840); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1842); if (failed) return node;
                    // JPQL.g:372:33: ( DISTINCT )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);
                    
                    if ( (LA22_0==DISTINCT) ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // JPQL.g:372:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1845); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression1863);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1865); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newMin(t3.getLine(), t3.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:375:7: t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t4=(Token)input.LT(1);
                    match(input,SUM,FOLLOW_SUM_in_aggregateExpression1885); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1887); if (failed) return node;
                    // JPQL.g:375:33: ( DISTINCT )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);
                    
                    if ( (LA23_0==DISTINCT) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // JPQL.g:375:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1890); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression1908);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1910); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newSum(t4.getLine(), t4.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:378:7: t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET
                    {
                    t5=(Token)input.LT(1);
                    match(input,COUNT,FOLLOW_COUNT_in_aggregateExpression1930); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1932); if (failed) return node;
                    // JPQL.g:378:35: ( DISTINCT )?
                    int alt24=2;
                    int LA24_0 = input.LA(1);
                    
                    if ( (LA24_0==DISTINCT) ) {
                        alt24=1;
                    }
                    switch (alt24) {
                        case 1 :
                            // JPQL.g:378:36: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1935); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_aggregateExpression1953);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1955); if (failed) return node;
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
    // JPQL.g:383:1: constructorExpression returns [Object node] : t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET ;
    public final Object constructorExpression() throws RecognitionException {
        constructorExpression_stack.push(new constructorExpression_scope());

        Object node = null;
    
        Token t=null;
        String className = null;

        Object n = null;
        
    
         
            node = null;
            ((constructorExpression_scope)constructorExpression_stack.peek()).args = new ArrayList();
    
        try {
            // JPQL.g:391:7: (t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET )
            // JPQL.g:391:7: t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,NEW,FOLLOW_NEW_in_constructorExpression1998); if (failed) return node;
            pushFollow(FOLLOW_constructorName_in_constructorExpression2004);
            className=constructorName();
            _fsp--;
            if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression2014); if (failed) return node;
            pushFollow(FOLLOW_constructorItem_in_constructorExpression2029);
            n=constructorItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((constructorExpression_scope)constructorExpression_stack.peek()).args.add(n); 
            }
            // JPQL.g:394:9: ( COMMA n= constructorItem )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);
                
                if ( (LA26_0==COMMA) ) {
                    alt26=1;
                }
                
            
                switch (alt26) {
            	case 1 :
            	    // JPQL.g:394:11: COMMA n= constructorItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_constructorExpression2044); if (failed) return node;
            	    pushFollow(FOLLOW_constructorItem_in_constructorExpression2050);
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

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression2065); if (failed) return node;
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
    // JPQL.g:402:1: constructorName returns [String className] : i1= IDENT ( DOT i2= IDENT )* ;
    public final String constructorName() throws RecognitionException {
        constructorName_stack.push(new constructorName_scope());

        String className = null;
    
        Token i1=null;
        Token i2=null;
    
         
            className = null;
            ((constructorName_scope)constructorName_stack.peek()).buf = new StringBuffer(); 
    
        try {
            // JPQL.g:410:7: (i1= IDENT ( DOT i2= IDENT )* )
            // JPQL.g:410:7: i1= IDENT ( DOT i2= IDENT )*
            {
            i1=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_constructorName2106); if (failed) return className;
            if ( backtracking==0 ) {
               ((constructorName_scope)constructorName_stack.peek()).buf.append(i1.getText()); 
            }
            // JPQL.g:411:9: ( DOT i2= IDENT )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);
                
                if ( (LA27_0==DOT) ) {
                    alt27=1;
                }
                
            
                switch (alt27) {
            	case 1 :
            	    // JPQL.g:411:11: DOT i2= IDENT
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_constructorName2120); if (failed) return className;
            	    i2=(Token)input.LT(1);
            	    match(input,IDENT,FOLLOW_IDENT_in_constructorName2124); if (failed) return className;
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
    // JPQL.g:415:1: constructorItem returns [Object node] : (n= pathExprOrVariableAccess | n= aggregateExpression );
    public final Object constructorItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:417:7: (n= pathExprOrVariableAccess | n= aggregateExpression )
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
                    new NoViableAltException("415:1: constructorItem returns [Object node] : (n= pathExprOrVariableAccess | n= aggregateExpression );", 28, 0, input);
            
                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // JPQL.g:417:7: n= pathExprOrVariableAccess
                    {
                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_constructorItem2168);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:418:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_constructorItem2182);
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
    // JPQL.g:421:1: fromClause returns [Object node] : t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )* ;
    public final Object fromClause() throws RecognitionException {
        fromClause_stack.push(new fromClause_scope());

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         
            node = null; 
            ((fromClause_scope)fromClause_stack.peek()).varDecls = new ArrayList();
    
        try {
            // JPQL.g:429:7: (t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )* )
            // JPQL.g:429:7: t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )*
            {
            t=(Token)input.LT(1);
            match(input,FROM,FOLLOW_FROM_in_fromClause2215); if (failed) return node;
            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2217);
            identificationVariableDeclaration(((fromClause_scope)fromClause_stack.peek()).varDecls);
            _fsp--;
            if (failed) return node;
            // JPQL.g:430:9: ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);
                
                if ( (LA30_0==COMMA) ) {
                    alt30=1;
                }
                
            
                switch (alt30) {
            	case 1 :
            	    // JPQL.g:430:10: COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_fromClause2229); if (failed) return node;
            	    // JPQL.g:430:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )
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
            	                new NoViableAltException("430:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )", 29, 1, input);
            	        
            	            throw nvae;
            	        }
            	    }
            	    else if ( ((LA29_0>=ABS && LA29_0<=HAVING)||(LA29_0>=INNER && LA29_0<=FLOAT_SUFFIX)) ) {
            	        alt29=1;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return node;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("430:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )", 29, 0, input);
            	    
            	        throw nvae;
            	    }
            	    switch (alt29) {
            	        case 1 :
            	            // JPQL.g:430:19: identificationVariableDeclaration[$fromClause::varDecls]
            	            {
            	            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2234);
            	            identificationVariableDeclaration(((fromClause_scope)fromClause_stack.peek()).varDecls);
            	            _fsp--;
            	            if (failed) return node;
            	            
            	            }
            	            break;
            	        case 2 :
            	            // JPQL.g:431:19: n= collectionMemberDeclaration
            	            {
            	            pushFollow(FOLLOW_collectionMemberDeclaration_in_fromClause2259);
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
    // JPQL.g:437:1: identificationVariableDeclaration[List varDecls] : node= rangeVariableDeclaration (node= join )* ;
    public final void identificationVariableDeclaration(List varDecls) throws RecognitionException {
        Object node = null;
        
    
        try {
            // JPQL.g:438:7: (node= rangeVariableDeclaration (node= join )* )
            // JPQL.g:438:7: node= rangeVariableDeclaration (node= join )*
            {
            pushFollow(FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2325);
            node=rangeVariableDeclaration();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               varDecls.add(node); 
            }
            // JPQL.g:439:9: (node= join )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);
                
                if ( (LA31_0==INNER||LA31_0==JOIN||LA31_0==LEFT) ) {
                    alt31=1;
                }
                
            
                switch (alt31) {
            	case 1 :
            	    // JPQL.g:439:11: node= join
            	    {
            	    pushFollow(FOLLOW_join_in_identificationVariableDeclaration2344);
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
    // JPQL.g:442:1: rangeVariableDeclaration returns [Object node] : schema= abstractSchemaName ( AS )? i= IDENT ;
    public final Object rangeVariableDeclaration() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        String schema = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:446:7: (schema= abstractSchemaName ( AS )? i= IDENT )
            // JPQL.g:446:7: schema= abstractSchemaName ( AS )? i= IDENT
            {
            pushFollow(FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2379);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // JPQL.g:446:35: ( AS )?
            int alt32=2;
            int LA32_0 = input.LA(1);
            
            if ( (LA32_0==AS) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // JPQL.g:446:36: AS
                    {
                    match(input,AS,FOLLOW_AS_in_rangeVariableDeclaration2382); if (failed) return node;
                    
                    }
                    break;
            
            }

            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_rangeVariableDeclaration2388); if (failed) return node;
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
    // JPQL.g:457:1: abstractSchemaName returns [String schema] : ident= . ;
    public final String abstractSchemaName() throws RecognitionException {

        String schema = null;
    
        Token ident=null;
    
         schema = null; 
        try {
            // JPQL.g:459:7: (ident= . )
            // JPQL.g:459:7: ident= .
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
    // JPQL.g:466:1: join returns [Object node] : outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression ) ;
    public final Object join() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        Token t=null;
        boolean outerJoin = false;

        Object n = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:470:7: (outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression ) )
            // JPQL.g:470:7: outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )
            {
            pushFollow(FOLLOW_joinSpec_in_join2471);
            outerJoin=joinSpec();
            _fsp--;
            if (failed) return node;
            // JPQL.g:471:7: (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )
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
                    new NoViableAltException("471:7: (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )", 34, 0, input);
            
                throw nvae;
            }
            switch (alt34) {
                case 1 :
                    // JPQL.g:471:9: n= joinAssociationPathExpression ( AS )? i= IDENT
                    {
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2485);
                    n=joinAssociationPathExpression();
                    _fsp--;
                    if (failed) return node;
                    // JPQL.g:471:43: ( AS )?
                    int alt33=2;
                    int LA33_0 = input.LA(1);
                    
                    if ( (LA33_0==AS) ) {
                        alt33=1;
                    }
                    switch (alt33) {
                        case 1 :
                            // JPQL.g:471:44: AS
                            {
                            match(input,AS,FOLLOW_AS_in_join2488); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_join2494); if (failed) return node;
                    if ( backtracking==0 ) {
                      
                                  node = factory.newJoinVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                     outerJoin, n, i.getText()); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:476:9: t= FETCH n= joinAssociationPathExpression
                    {
                    t=(Token)input.LT(1);
                    match(input,FETCH,FOLLOW_FETCH_in_join2516); if (failed) return node;
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2522);
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
    // JPQL.g:483:1: joinSpec returns [boolean outer] : ( LEFT ( OUTER )? | INNER )? JOIN ;
    public final boolean joinSpec() throws RecognitionException {

        boolean outer = false;
    
         outer = false; 
        try {
            // JPQL.g:485:7: ( ( LEFT ( OUTER )? | INNER )? JOIN )
            // JPQL.g:485:7: ( LEFT ( OUTER )? | INNER )? JOIN
            {
            // JPQL.g:485:7: ( LEFT ( OUTER )? | INNER )?
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
                    // JPQL.g:485:8: LEFT ( OUTER )?
                    {
                    match(input,LEFT,FOLLOW_LEFT_in_joinSpec2568); if (failed) return outer;
                    // JPQL.g:485:13: ( OUTER )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);
                    
                    if ( (LA35_0==OUTER) ) {
                        alt35=1;
                    }
                    switch (alt35) {
                        case 1 :
                            // JPQL.g:485:14: OUTER
                            {
                            match(input,OUTER,FOLLOW_OUTER_in_joinSpec2571); if (failed) return outer;
                            
                            }
                            break;
                    
                    }

                    if ( backtracking==0 ) {
                       outer = true; 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:485:44: INNER
                    {
                    match(input,INNER,FOLLOW_INNER_in_joinSpec2580); if (failed) return outer;
                    
                    }
                    break;
            
            }

            match(input,JOIN,FOLLOW_JOIN_in_joinSpec2586); if (failed) return outer;
            
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
    // JPQL.g:488:1: collectionMemberDeclaration returns [Object node] : t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT ;
    public final Object collectionMemberDeclaration() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Token i=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:490:7: (t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT )
            // JPQL.g:490:7: t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT
            {
            t=(Token)input.LT(1);
            match(input,IN,FOLLOW_IN_in_collectionMemberDeclaration2614); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2616); if (failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2622);
            n=collectionValuedPathExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2624); if (failed) return node;
            // JPQL.g:491:7: ( AS )?
            int alt37=2;
            int LA37_0 = input.LA(1);
            
            if ( (LA37_0==AS) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // JPQL.g:491:8: AS
                    {
                    match(input,AS,FOLLOW_AS_in_collectionMemberDeclaration2634); if (failed) return node;
                    
                    }
                    break;
            
            }

            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_collectionMemberDeclaration2640); if (failed) return node;
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
    // JPQL.g:498:1: collectionValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object collectionValuedPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:500:7: (n= pathExpression )
            // JPQL.g:500:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_collectionValuedPathExpression2678);
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
    // JPQL.g:503:1: associationPathExpression returns [Object node] : n= pathExpression ;
    public final Object associationPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:505:7: (n= pathExpression )
            // JPQL.g:505:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_associationPathExpression2710);
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
    // JPQL.g:508:1: joinAssociationPathExpression returns [Object node] : left= variableAccessOrTypeConstant d= DOT right= attribute ;
    public final Object joinAssociationPathExpression() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object left = null;

        Object right = null;
        
    
        
            node = null; 
    
        try {
            // JPQL.g:512:7: (left= variableAccessOrTypeConstant d= DOT right= attribute )
            // JPQL.g:512:7: left= variableAccessOrTypeConstant d= DOT right= attribute
            {
            pushFollow(FOLLOW_variableAccessOrTypeConstant_in_joinAssociationPathExpression2742);
            left=variableAccessOrTypeConstant();
            _fsp--;
            if (failed) return node;
            d=(Token)input.LT(1);
            match(input,DOT,FOLLOW_DOT_in_joinAssociationPathExpression2746); if (failed) return node;
            pushFollow(FOLLOW_attribute_in_joinAssociationPathExpression2752);
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
    // JPQL.g:516:1: singleValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object singleValuedPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:518:7: (n= pathExpression )
            // JPQL.g:518:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_singleValuedPathExpression2792);
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
    // JPQL.g:521:1: stateFieldPathExpression returns [Object node] : n= pathExpression ;
    public final Object stateFieldPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:523:7: (n= pathExpression )
            // JPQL.g:523:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_stateFieldPathExpression2824);
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
    // JPQL.g:526:1: pathExpression returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )+ ;
    public final Object pathExpression() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:530:7: (n= qualifiedIdentificationVariable (d= DOT right= attribute )+ )
            // JPQL.g:530:7: n= qualifiedIdentificationVariable (d= DOT right= attribute )+
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_pathExpression2856);
            n=qualifiedIdentificationVariable();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:531:9: (d= DOT right= attribute )+
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
            	    // JPQL.g:531:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pathExpression2871); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_pathExpression2877);
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
    // JPQL.g:542:1: attribute returns [Object node] : i= . ;
    public final Object attribute() throws RecognitionException {

        Object node = null;
    
        Token i=null;
    
         node = null; 
        try {
            // JPQL.g:545:7: (i= . )
            // JPQL.g:545:7: i= .
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
    // JPQL.g:552:1: variableAccessOrTypeConstant returns [Object node] : i= IDENT ;
    public final Object variableAccessOrTypeConstant() throws RecognitionException {

        Object node = null;
    
        Token i=null;
    
         node = null; 
        try {
            // JPQL.g:554:7: (i= IDENT )
            // JPQL.g:554:7: i= IDENT
            {
            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_variableAccessOrTypeConstant2973); if (failed) return node;
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
    // JPQL.g:558:1: whereClause returns [Object node] : t= WHERE n= conditionalExpression ;
    public final Object whereClause() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:560:7: (t= WHERE n= conditionalExpression )
            // JPQL.g:560:7: t= WHERE n= conditionalExpression
            {
            t=(Token)input.LT(1);
            match(input,WHERE,FOLLOW_WHERE_in_whereClause3011); if (failed) return node;
            pushFollow(FOLLOW_conditionalExpression_in_whereClause3017);
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
    // JPQL.g:566:1: conditionalExpression returns [Object node] : n= conditionalTerm (t= OR right= conditionalTerm )* ;
    public final Object conditionalExpression() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:570:7: (n= conditionalTerm (t= OR right= conditionalTerm )* )
            // JPQL.g:570:7: n= conditionalTerm (t= OR right= conditionalTerm )*
            {
            pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression3059);
            n=conditionalTerm();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:571:9: (t= OR right= conditionalTerm )*
            loop39:
            do {
                int alt39=2;
                int LA39_0 = input.LA(1);
                
                if ( (LA39_0==OR) ) {
                    alt39=1;
                }
                
            
                switch (alt39) {
            	case 1 :
            	    // JPQL.g:571:10: t= OR right= conditionalTerm
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,OR,FOLLOW_OR_in_conditionalExpression3074); if (failed) return node;
            	    pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression3080);
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
    // JPQL.g:576:1: conditionalTerm returns [Object node] : n= conditionalFactor (t= AND right= conditionalFactor )* ;
    public final Object conditionalTerm() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:580:7: (n= conditionalFactor (t= AND right= conditionalFactor )* )
            // JPQL.g:580:7: n= conditionalFactor (t= AND right= conditionalFactor )*
            {
            pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm3135);
            n=conditionalFactor();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:581:9: (t= AND right= conditionalFactor )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);
                
                if ( (LA40_0==AND) ) {
                    alt40=1;
                }
                
            
                switch (alt40) {
            	case 1 :
            	    // JPQL.g:581:10: t= AND right= conditionalFactor
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,AND,FOLLOW_AND_in_conditionalTerm3150); if (failed) return node;
            	    pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm3156);
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
    // JPQL.g:586:1: conditionalFactor returns [Object node] : (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] ) ;
    public final Object conditionalFactor() throws RecognitionException {

        Object node = null;
    
        Token n=null;
        Object n1 = null;
        
    
         node = null; 
        try {
            // JPQL.g:588:7: ( (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] ) )
            // JPQL.g:588:7: (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )
            {
            // JPQL.g:588:7: (n= NOT )?
            int alt41=2;
            int LA41_0 = input.LA(1);
            
            if ( (LA41_0==NOT) ) {
                alt41=1;
            }
            switch (alt41) {
                case 1 :
                    // JPQL.g:588:8: n= NOT
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_conditionalFactor3211); if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:589:9: (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )
            int alt42=2;
            int LA42_0 = input.LA(1);
            
            if ( (LA42_0==ABS||LA42_0==AVG||(LA42_0>=CONCAT && LA42_0<=CURRENT_TIMESTAMP)||LA42_0==FALSE||LA42_0==KEY||LA42_0==LENGTH||(LA42_0>=LOCATE && LA42_0<=MAX)||(LA42_0>=MIN && LA42_0<=MOD)||(LA42_0>=SIZE && LA42_0<=SQRT)||(LA42_0>=SUBSTRING && LA42_0<=SUM)||(LA42_0>=TRIM && LA42_0<=TYPE)||(LA42_0>=UPPER && LA42_0<=VALUE)||LA42_0==IDENT||LA42_0==LEFT_ROUND_BRACKET||(LA42_0>=PLUS && LA42_0<=MINUS)||(LA42_0>=INTEGER_LITERAL && LA42_0<=NAMED_PARAM)) ) {
                alt42=1;
            }
            else if ( (LA42_0==EXISTS) ) {
                alt42=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("589:9: (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )", 42, 0, input);
            
                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // JPQL.g:589:11: n1= conditionalPrimary
                    {
                    pushFollow(FOLLOW_conditionalPrimary_in_conditionalFactor3230);
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
                    // JPQL.g:596:11: n1= existsExpression[(n!=null)]
                    {
                    pushFollow(FOLLOW_existsExpression_in_conditionalFactor3259);
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
    // JPQL.g:600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );
    public final Object conditionalPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:602:7: ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression )
            int alt43=2;
            int LA43_0 = input.LA(1);
            
            if ( (LA43_0==LEFT_ROUND_BRACKET) ) {
                int LA43_1 = input.LA(2);
                
                if ( (LA43_1==SELECT) ) {
                    alt43=2;
                }
                else if ( (LA43_1==NOT) && (synpred1())) {
                    alt43=1;
                }
                else if ( (LA43_1==LEFT_ROUND_BRACKET) ) {
                    int LA43_39 = input.LA(3);
                    
                    if ( (LA43_39==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_39==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 77, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==PLUS) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 78, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==MINUS) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 79, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==AVG) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 80, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==MAX) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 81, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==MIN) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 82, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==SUM) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 83, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==COUNT) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 84, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==IDENT) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 85, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==KEY) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 86, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==VALUE) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 87, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==ABS) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 88, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==LENGTH) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 89, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==MOD) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 90, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==SQRT) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 91, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==LOCATE) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 92, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==SIZE) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 93, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==CURRENT_DATE) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 94, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==CURRENT_TIME) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 95, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==CURRENT_TIMESTAMP) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 96, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==CONCAT) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 97, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==SUBSTRING) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 98, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==TRIM) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 99, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==UPPER) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 100, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==LOWER) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 101, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==POSITIONAL_PARAM) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 102, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==NAMED_PARAM) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 103, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==INTEGER_LITERAL) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 104, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==LONG_LITERAL) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 105, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==FLOAT_LITERAL) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 106, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==DOUBLE_LITERAL) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 107, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==STRING_LITERAL_DOUBLE_QUOTED) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 108, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==STRING_LITERAL_SINGLE_QUOTED) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 109, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==TRUE) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 110, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==FALSE) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 111, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==TYPE) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 112, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_39==EXISTS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_39==SELECT) && (synpred1())) {
                        alt43=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 39, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==PLUS) ) {
                    switch ( input.LA(3) ) {
                    case AVG:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 115, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MAX:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 116, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MIN:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 117, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUM:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 118, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COUNT:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 119, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 120, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 121, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 122, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case ABS:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 123, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LENGTH:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 124, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MOD:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 125, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SQRT:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 126, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOCATE:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 127, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SIZE:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 128, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_DATE:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 129, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_TIME:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 130, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_TIMESTAMP:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 131, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CONCAT:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 132, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUBSTRING:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 133, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TRIM:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 134, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case UPPER:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 135, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOWER:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 136, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case POSITIONAL_PARAM:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 137, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NAMED_PARAM:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 138, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INTEGER_LITERAL:
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 139, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LONG_LITERAL:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 140, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FLOAT_LITERAL:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 141, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case DOUBLE_LITERAL:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 142, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case STRING_LITERAL_DOUBLE_QUOTED:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 143, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case STRING_LITERAL_SINGLE_QUOTED:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 144, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TRUE:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 145, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FALSE:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 146, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TYPE:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 147, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_ROUND_BRACKET:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 148, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 40, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA43_1==MINUS) ) {
                    switch ( input.LA(3) ) {
                    case AVG:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 149, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MAX:
                        {
                        int LA43_150 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 150, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MIN:
                        {
                        int LA43_151 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 151, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUM:
                        {
                        int LA43_152 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 152, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COUNT:
                        {
                        int LA43_153 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 153, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA43_154 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 154, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
                        int LA43_155 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 155, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
                        int LA43_156 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 156, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case ABS:
                        {
                        int LA43_157 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 157, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LENGTH:
                        {
                        int LA43_158 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 158, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MOD:
                        {
                        int LA43_159 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 159, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SQRT:
                        {
                        int LA43_160 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 160, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOCATE:
                        {
                        int LA43_161 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 161, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SIZE:
                        {
                        int LA43_162 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 162, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_DATE:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 163, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_TIME:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 164, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_TIMESTAMP:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 165, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CONCAT:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 166, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUBSTRING:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 167, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TRIM:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 168, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case UPPER:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 169, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOWER:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 170, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case POSITIONAL_PARAM:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 171, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NAMED_PARAM:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 172, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INTEGER_LITERAL:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 173, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LONG_LITERAL:
                        {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 174, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FLOAT_LITERAL:
                        {
                        int LA43_175 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 175, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case DOUBLE_LITERAL:
                        {
                        int LA43_176 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 176, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case STRING_LITERAL_DOUBLE_QUOTED:
                        {
                        int LA43_177 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 177, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case STRING_LITERAL_SINGLE_QUOTED:
                        {
                        int LA43_178 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 178, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TRUE:
                        {
                        int LA43_179 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 179, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FALSE:
                        {
                        int LA43_180 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 180, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TYPE:
                        {
                        int LA43_181 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 181, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_ROUND_BRACKET:
                        {
                        int LA43_182 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 182, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 41, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA43_1==AVG) ) {
                    int LA43_42 = input.LA(3);
                    
                    if ( (LA43_42==LEFT_ROUND_BRACKET) ) {
                        int LA43_183 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 183, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 42, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==MAX) ) {
                    int LA43_43 = input.LA(3);
                    
                    if ( (LA43_43==LEFT_ROUND_BRACKET) ) {
                        int LA43_184 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 184, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 43, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==MIN) ) {
                    int LA43_44 = input.LA(3);
                    
                    if ( (LA43_44==LEFT_ROUND_BRACKET) ) {
                        int LA43_185 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 185, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 44, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==SUM) ) {
                    int LA43_45 = input.LA(3);
                    
                    if ( (LA43_45==LEFT_ROUND_BRACKET) ) {
                        int LA43_186 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 186, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 45, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==COUNT) ) {
                    int LA43_46 = input.LA(3);
                    
                    if ( (LA43_46==LEFT_ROUND_BRACKET) ) {
                        int LA43_187 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 187, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 46, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==IDENT) ) {
                    int LA43_47 = input.LA(3);
                    
                    if ( (LA43_47==DOT) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 188, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_47==MULTIPLY) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 189, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_47==DIVIDE) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 190, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_47==PLUS) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 191, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_47==MINUS) ) {
                        int LA43_192 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 192, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_47==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_47==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_47==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_47==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_47==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_47==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_47==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_47==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_47==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_47==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_47==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_47==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_47==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 47, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==KEY) ) {
                    int LA43_48 = input.LA(3);
                    
                    if ( (LA43_48==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 206, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 48, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==VALUE) ) {
                    int LA43_49 = input.LA(3);
                    
                    if ( (LA43_49==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 207, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 49, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==ABS) ) {
                    int LA43_50 = input.LA(3);
                    
                    if ( (LA43_50==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 208, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 50, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==LENGTH) ) {
                    int LA43_51 = input.LA(3);
                    
                    if ( (LA43_51==LEFT_ROUND_BRACKET) ) {
                        int LA43_209 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 209, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 51, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==MOD) ) {
                    int LA43_52 = input.LA(3);
                    
                    if ( (LA43_52==LEFT_ROUND_BRACKET) ) {
                        int LA43_210 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 210, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 52, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==SQRT) ) {
                    int LA43_53 = input.LA(3);
                    
                    if ( (LA43_53==LEFT_ROUND_BRACKET) ) {
                        int LA43_211 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 211, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 53, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==LOCATE) ) {
                    int LA43_54 = input.LA(3);
                    
                    if ( (LA43_54==LEFT_ROUND_BRACKET) ) {
                        int LA43_212 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 212, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 54, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==SIZE) ) {
                    int LA43_55 = input.LA(3);
                    
                    if ( (LA43_55==LEFT_ROUND_BRACKET) ) {
                        int LA43_213 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 213, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 55, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==CURRENT_DATE) ) {
                    int LA43_56 = input.LA(3);
                    
                    if ( (LA43_56==MULTIPLY) ) {
                        int LA43_214 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 214, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_56==DIVIDE) ) {
                        int LA43_215 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 215, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_56==PLUS) ) {
                        int LA43_216 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 216, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_56==MINUS) ) {
                        int LA43_217 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 217, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_56==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else if ( (LA43_56==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_56==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_56==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_56==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_56==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_56==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_56==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_56==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_56==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_56==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_56==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_56==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 56, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==CURRENT_TIME) ) {
                    int LA43_57 = input.LA(3);
                    
                    if ( (LA43_57==MULTIPLY) ) {
                        int LA43_231 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 231, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_57==DIVIDE) ) {
                        int LA43_232 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 232, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_57==PLUS) ) {
                        int LA43_233 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 233, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_57==MINUS) ) {
                        int LA43_234 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 234, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_57==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else if ( (LA43_57==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_57==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_57==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_57==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_57==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_57==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_57==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_57==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_57==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_57==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_57==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_57==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 57, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==CURRENT_TIMESTAMP) ) {
                    int LA43_58 = input.LA(3);
                    
                    if ( (LA43_58==MULTIPLY) ) {
                        int LA43_248 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 248, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_58==DIVIDE) ) {
                        int LA43_249 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 249, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_58==PLUS) ) {
                        int LA43_250 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 250, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_58==MINUS) ) {
                        int LA43_251 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 251, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_58==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else if ( (LA43_58==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_58==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_58==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_58==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_58==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_58==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_58==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_58==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_58==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_58==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_58==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_58==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 58, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==CONCAT) ) {
                    int LA43_59 = input.LA(3);
                    
                    if ( (LA43_59==LEFT_ROUND_BRACKET) ) {
                        int LA43_265 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 265, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 59, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==SUBSTRING) ) {
                    int LA43_60 = input.LA(3);
                    
                    if ( (LA43_60==LEFT_ROUND_BRACKET) ) {
                        int LA43_266 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 266, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 60, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==TRIM) ) {
                    int LA43_61 = input.LA(3);
                    
                    if ( (LA43_61==LEFT_ROUND_BRACKET) ) {
                        int LA43_267 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 267, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 61, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==UPPER) ) {
                    int LA43_62 = input.LA(3);
                    
                    if ( (LA43_62==LEFT_ROUND_BRACKET) ) {
                        int LA43_268 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 268, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 62, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==LOWER) ) {
                    int LA43_63 = input.LA(3);
                    
                    if ( (LA43_63==LEFT_ROUND_BRACKET) ) {
                        int LA43_269 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 269, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 63, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==POSITIONAL_PARAM) ) {
                    int LA43_64 = input.LA(3);
                    
                    if ( (LA43_64==MULTIPLY) ) {
                        int LA43_270 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 270, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_64==DIVIDE) ) {
                        int LA43_271 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 271, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_64==PLUS) ) {
                        int LA43_272 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 272, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_64==MINUS) ) {
                        int LA43_273 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 273, input);
                        
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
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 64, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==NAMED_PARAM) ) {
                    int LA43_65 = input.LA(3);
                    
                    if ( (LA43_65==MULTIPLY) ) {
                        int LA43_287 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 287, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_65==DIVIDE) ) {
                        int LA43_288 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 288, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_65==PLUS) ) {
                        int LA43_289 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 289, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_65==MINUS) ) {
                        int LA43_290 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 290, input);
                        
                            throw nvae;
                        }
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
                    else if ( (LA43_65==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 65, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==INTEGER_LITERAL) ) {
                    int LA43_66 = input.LA(3);
                    
                    if ( (LA43_66==MULTIPLY) ) {
                        int LA43_304 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 304, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_66==DIVIDE) ) {
                        int LA43_305 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 305, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_66==PLUS) ) {
                        int LA43_306 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 306, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_66==MINUS) ) {
                        int LA43_307 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 307, input);
                        
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
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 66, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==LONG_LITERAL) ) {
                    int LA43_67 = input.LA(3);
                    
                    if ( (LA43_67==MULTIPLY) ) {
                        int LA43_321 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 321, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_67==DIVIDE) ) {
                        int LA43_322 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 322, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_67==PLUS) ) {
                        int LA43_323 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 323, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_67==MINUS) ) {
                        int LA43_324 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 324, input);
                        
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
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 67, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==FLOAT_LITERAL) ) {
                    int LA43_68 = input.LA(3);
                    
                    if ( (LA43_68==MULTIPLY) ) {
                        int LA43_338 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 338, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_68==DIVIDE) ) {
                        int LA43_339 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 339, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_68==PLUS) ) {
                        int LA43_340 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 340, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_68==MINUS) ) {
                        int LA43_341 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 341, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_68==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
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
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 68, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==DOUBLE_LITERAL) ) {
                    int LA43_69 = input.LA(3);
                    
                    if ( (LA43_69==MULTIPLY) ) {
                        int LA43_355 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 355, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_69==DIVIDE) ) {
                        int LA43_356 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 356, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_69==PLUS) ) {
                        int LA43_357 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 357, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_69==MINUS) ) {
                        int LA43_358 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 358, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_69==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
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
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 69, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==STRING_LITERAL_DOUBLE_QUOTED) ) {
                    int LA43_70 = input.LA(3);
                    
                    if ( (LA43_70==MULTIPLY) ) {
                        int LA43_372 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 372, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==DIVIDE) ) {
                        int LA43_373 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 373, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==PLUS) ) {
                        int LA43_374 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 374, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==MINUS) ) {
                        int LA43_375 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 375, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_70==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else if ( (LA43_70==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_70==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_70==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_70==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_70==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_70==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_70==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_70==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_70==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_70==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_70==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_70==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 70, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==STRING_LITERAL_SINGLE_QUOTED) ) {
                    int LA43_71 = input.LA(3);
                    
                    if ( (LA43_71==MULTIPLY) ) {
                        int LA43_389 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 389, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_71==DIVIDE) ) {
                        int LA43_390 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 390, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_71==PLUS) ) {
                        int LA43_391 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 391, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_71==MINUS) ) {
                        int LA43_392 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 392, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_71==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_71==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_71==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_71==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_71==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_71==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_71==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_71==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_71==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_71==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_71==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_71==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_71==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 71, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==TRUE) ) {
                    int LA43_72 = input.LA(3);
                    
                    if ( (LA43_72==MULTIPLY) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 406, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_72==DIVIDE) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 407, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_72==PLUS) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 408, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_72==MINUS) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 409, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_72==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else if ( (LA43_72==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_72==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_72==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_72==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_72==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_72==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_72==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_72==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_72==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_72==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_72==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_72==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 72, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==FALSE) ) {
                    int LA43_73 = input.LA(3);
                    
                    if ( (LA43_73==MULTIPLY) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 423, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_73==DIVIDE) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 424, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_73==PLUS) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 425, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_73==MINUS) ) {
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
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 426, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA43_73==EQUALS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_73==NOT_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_73==GREATER_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_73==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_73==LESS_THAN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_73==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_73==NOT) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_73==BETWEEN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_73==LIKE) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_73==IN) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_73==MEMBER) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_73==IS) && (synpred1())) {
                        alt43=1;
                    }
                    else if ( (LA43_73==RIGHT_ROUND_BRACKET) ) {
                        alt43=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 73, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==TYPE) ) {
                    int LA43_74 = input.LA(3);
                    
                    if ( (LA43_74==LEFT_ROUND_BRACKET) ) {
                        int LA43_440 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt43=1;
                        }
                        else if ( (true) ) {
                            alt43=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 440, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 74, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA43_1==EXISTS) && (synpred1())) {
                    alt43=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA43_0==ABS||LA43_0==AVG||(LA43_0>=CONCAT && LA43_0<=CURRENT_TIMESTAMP)||LA43_0==FALSE||LA43_0==KEY||LA43_0==LENGTH||(LA43_0>=LOCATE && LA43_0<=MAX)||(LA43_0>=MIN && LA43_0<=MOD)||(LA43_0>=SIZE && LA43_0<=SQRT)||(LA43_0>=SUBSTRING && LA43_0<=SUM)||(LA43_0>=TRIM && LA43_0<=TYPE)||(LA43_0>=UPPER && LA43_0<=VALUE)||LA43_0==IDENT||(LA43_0>=PLUS && LA43_0<=MINUS)||(LA43_0>=INTEGER_LITERAL && LA43_0<=NAMED_PARAM)) ) {
                alt43=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("600:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 43, 0, input);
            
                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // JPQL.g:602:7: ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3316); if (failed) return node;
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalPrimary3322);
                    n=conditionalExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3324); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:604:7: n= simpleConditionalExpression
                    {
                    pushFollow(FOLLOW_simpleConditionalExpression_in_conditionalPrimary3338);
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
    // JPQL.g:607:1: simpleConditionalExpression returns [Object node] : left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] ;
    public final Object simpleConditionalExpression() throws RecognitionException {

        Object node = null;
    
        Object left = null;

        Object n = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:611:7: (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] )
            // JPQL.g:611:7: left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node]
            {
            pushFollow(FOLLOW_arithmeticExpression_in_simpleConditionalExpression3370);
            left=arithmeticExpression();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3385);
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
    // JPQL.g:615:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );
    public final Object simpleConditionalExpressionRemainder(Object left) throws RecognitionException {

        Object node = null;
    
        Token n1=null;
        Token n2=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:617:7: (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] )
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
                    new NoViableAltException("615:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );", 46, 0, input);
            
                throw nvae;
            }
            
            switch (alt46) {
                case 1 :
                    // JPQL.g:617:7: n= comparisonExpression[left]
                    {
                    pushFollow(FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3420);
                    n=comparisonExpression(left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:618:7: (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left]
                    {
                    // JPQL.g:618:7: (n1= NOT )?
                    int alt44=2;
                    int LA44_0 = input.LA(1);
                    
                    if ( (LA44_0==NOT) ) {
                        alt44=1;
                    }
                    switch (alt44) {
                        case 1 :
                            // JPQL.g:618:8: n1= NOT
                            {
                            n1=(Token)input.LT(1);
                            match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3434); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3442);
                    n=conditionWithNotExpression((n1!=null),  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:619:7: IS (n2= NOT )? n= isExpression[(n2!=null), left]
                    {
                    match(input,IS,FOLLOW_IS_in_simpleConditionalExpressionRemainder3453); if (failed) return node;
                    // JPQL.g:619:10: (n2= NOT )?
                    int alt45=2;
                    int LA45_0 = input.LA(1);
                    
                    if ( (LA45_0==NOT) ) {
                        alt45=1;
                    }
                    switch (alt45) {
                        case 1 :
                            // JPQL.g:619:11: n2= NOT
                            {
                            n2=(Token)input.LT(1);
                            match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3458); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3466);
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
    // JPQL.g:622:1: conditionWithNotExpression[boolean not, Object left] returns [Object node] : (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] );
    public final Object conditionWithNotExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:624:7: (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] )
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
                    new NoViableAltException("622:1: conditionWithNotExpression[boolean not, Object left] returns [Object node] : (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] );", 47, 0, input);
            
                throw nvae;
            }
            
            switch (alt47) {
                case 1 :
                    // JPQL.g:624:7: n= betweenExpression[not, left]
                    {
                    pushFollow(FOLLOW_betweenExpression_in_conditionWithNotExpression3501);
                    n=betweenExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:625:7: n= likeExpression[not, left]
                    {
                    pushFollow(FOLLOW_likeExpression_in_conditionWithNotExpression3516);
                    n=likeExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:626:7: n= inExpression[not, left]
                    {
                    pushFollow(FOLLOW_inExpression_in_conditionWithNotExpression3530);
                    n=inExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:627:7: n= collectionMemberExpression[not, left]
                    {
                    pushFollow(FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3544);
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
    // JPQL.g:630:1: isExpression[boolean not, Object left] returns [Object node] : (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] );
    public final Object isExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:632:7: (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] )
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
                    new NoViableAltException("630:1: isExpression[boolean not, Object left] returns [Object node] : (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] );", 48, 0, input);
            
                throw nvae;
            }
            switch (alt48) {
                case 1 :
                    // JPQL.g:632:7: n= nullComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_nullComparisonExpression_in_isExpression3579);
                    n=nullComparisonExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:633:7: n= emptyCollectionComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_emptyCollectionComparisonExpression_in_isExpression3594);
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
    // JPQL.g:636:1: betweenExpression[boolean not, Object left] returns [Object node] : t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression ;
    public final Object betweenExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object lowerBound = null;

        Object upperBound = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g:640:7: (t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression )
            // JPQL.g:640:7: t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression
            {
            t=(Token)input.LT(1);
            match(input,BETWEEN,FOLLOW_BETWEEN_in_betweenExpression3627); if (failed) return node;
            pushFollow(FOLLOW_arithmeticExpression_in_betweenExpression3641);
            lowerBound=arithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,AND,FOLLOW_AND_in_betweenExpression3643); if (failed) return node;
            pushFollow(FOLLOW_arithmeticExpression_in_betweenExpression3649);
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
    // JPQL.g:648:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET );
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
            // JPQL.g:656:8: (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET )
            int alt51=2;
            int LA51_0 = input.LA(1);
            
            if ( (LA51_0==IN) ) {
                int LA51_1 = input.LA(2);
                
                if ( (LA51_1==LEFT_ROUND_BRACKET) ) {
                    alt51=2;
                }
                else if ( ((LA51_1>=POSITIONAL_PARAM && LA51_1<=NAMED_PARAM)) ) {
                    alt51=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("648:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET );", 51, 1, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("648:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET );", 51, 0, input);
            
                throw nvae;
            }
            switch (alt51) {
                case 1 :
                    // JPQL.g:656:8: t= IN n= inputParameter
                    {
                    t=(Token)input.LT(1);
                    match(input,IN,FOLLOW_IN_in_inExpression3695); if (failed) return node;
                    pushFollow(FOLLOW_inputParameter_in_inExpression3701);
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
                    // JPQL.g:661:9: t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET
                    {
                    t=(Token)input.LT(1);
                    match(input,IN,FOLLOW_IN_in_inExpression3728); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_inExpression3738); if (failed) return node;
                    // JPQL.g:663:9: (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery )
                    int alt50=2;
                    int LA50_0 = input.LA(1);
                    
                    if ( (LA50_0==IDENT||(LA50_0>=INTEGER_LITERAL && LA50_0<=NAMED_PARAM)) ) {
                        alt50=1;
                    }
                    else if ( (LA50_0==SELECT) ) {
                        alt50=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("663:9: (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery )", 50, 0, input);
                    
                        throw nvae;
                    }
                    switch (alt50) {
                        case 1 :
                            // JPQL.g:663:11: itemNode= inItem ( COMMA itemNode= inItem )*
                            {
                            pushFollow(FOLLOW_inItem_in_inExpression3754);
                            itemNode=inItem();
                            _fsp--;
                            if (failed) return node;
                            if ( backtracking==0 ) {
                               ((inExpression_scope)inExpression_stack.peek()).items.add(itemNode); 
                            }
                            // JPQL.g:664:13: ( COMMA itemNode= inItem )*
                            loop49:
                            do {
                                int alt49=2;
                                int LA49_0 = input.LA(1);
                                
                                if ( (LA49_0==COMMA) ) {
                                    alt49=1;
                                }
                                
                            
                                switch (alt49) {
                            	case 1 :
                            	    // JPQL.g:664:15: COMMA itemNode= inItem
                            	    {
                            	    match(input,COMMA,FOLLOW_COMMA_in_inExpression3772); if (failed) return node;
                            	    pushFollow(FOLLOW_inItem_in_inExpression3778);
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
                            // JPQL.g:669:11: subqueryNode= subquery
                            {
                            pushFollow(FOLLOW_subquery_in_inExpression3813);
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

                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression3847); if (failed) return node;
                    
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
    // JPQL.g:678:1: inItem returns [Object node] : (n= literalString | n= literalNumeric | n= inputParameter | n= variableAccessOrTypeConstant );
    public final Object inItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:680:7: (n= literalString | n= literalNumeric | n= inputParameter | n= variableAccessOrTypeConstant )
            int alt52=4;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt52=1;
                }
                break;
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
                {
                alt52=2;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt52=3;
                }
                break;
            case IDENT:
                {
                alt52=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("678:1: inItem returns [Object node] : (n= literalString | n= literalNumeric | n= inputParameter | n= variableAccessOrTypeConstant );", 52, 0, input);
            
                throw nvae;
            }
            
            switch (alt52) {
                case 1 :
                    // JPQL.g:680:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_inItem3877);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:681:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_inItem3891);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:682:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_inItem3905);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:683:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_inItem3919);
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
    // JPQL.g:686:1: likeExpression[boolean not, Object left] returns [Object node] : t= LIKE pattern= likeValue (escapeChars= escape )? ;
    public final Object likeExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object pattern = null;

        Object escapeChars = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g:690:7: (t= LIKE pattern= likeValue (escapeChars= escape )? )
            // JPQL.g:690:7: t= LIKE pattern= likeValue (escapeChars= escape )?
            {
            t=(Token)input.LT(1);
            match(input,LIKE,FOLLOW_LIKE_in_likeExpression3951); if (failed) return node;
            pushFollow(FOLLOW_likeValue_in_likeExpression3957);
            pattern=likeValue();
            _fsp--;
            if (failed) return node;
            // JPQL.g:691:9: (escapeChars= escape )?
            int alt53=2;
            int LA53_0 = input.LA(1);
            
            if ( (LA53_0==ESCAPE) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // JPQL.g:691:10: escapeChars= escape
                    {
                    pushFollow(FOLLOW_escape_in_likeExpression3972);
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
    // JPQL.g:698:1: escape returns [Object node] : t= ESCAPE escapeClause= likeValue ;
    public final Object escape() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object escapeClause = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:702:7: (t= ESCAPE escapeClause= likeValue )
            // JPQL.g:702:7: t= ESCAPE escapeClause= likeValue
            {
            t=(Token)input.LT(1);
            match(input,ESCAPE,FOLLOW_ESCAPE_in_escape4012); if (failed) return node;
            pushFollow(FOLLOW_likeValue_in_escape4018);
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
    // JPQL.g:706:1: likeValue returns [Object node] : (n= literalString | n= inputParameter );
    public final Object likeValue() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:708:7: (n= literalString | n= inputParameter )
            int alt54=2;
            int LA54_0 = input.LA(1);
            
            if ( ((LA54_0>=STRING_LITERAL_DOUBLE_QUOTED && LA54_0<=STRING_LITERAL_SINGLE_QUOTED)) ) {
                alt54=1;
            }
            else if ( ((LA54_0>=POSITIONAL_PARAM && LA54_0<=NAMED_PARAM)) ) {
                alt54=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("706:1: likeValue returns [Object node] : (n= literalString | n= inputParameter );", 54, 0, input);
            
                throw nvae;
            }
            switch (alt54) {
                case 1 :
                    // JPQL.g:708:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_likeValue4058);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:709:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_likeValue4072);
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
    // JPQL.g:712:1: nullComparisonExpression[boolean not, Object left] returns [Object node] : t= NULL ;
    public final Object nullComparisonExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
    
         node = null; 
        try {
            // JPQL.g:714:7: (t= NULL )
            // JPQL.g:714:7: t= NULL
            {
            t=(Token)input.LT(1);
            match(input,NULL,FOLLOW_NULL_in_nullComparisonExpression4105); if (failed) return node;
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
    // JPQL.g:718:1: emptyCollectionComparisonExpression[boolean not, Object left] returns [Object node] : t= EMPTY ;
    public final Object emptyCollectionComparisonExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
    
         node = null; 
        try {
            // JPQL.g:720:7: (t= EMPTY )
            // JPQL.g:720:7: t= EMPTY
            {
            t=(Token)input.LT(1);
            match(input,EMPTY,FOLLOW_EMPTY_in_emptyCollectionComparisonExpression4146); if (failed) return node;
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
    // JPQL.g:724:1: collectionMemberExpression[boolean not, Object left] returns [Object node] : t= MEMBER ( OF )? n= collectionValuedPathExpression ;
    public final Object collectionMemberExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:726:7: (t= MEMBER ( OF )? n= collectionValuedPathExpression )
            // JPQL.g:726:7: t= MEMBER ( OF )? n= collectionValuedPathExpression
            {
            t=(Token)input.LT(1);
            match(input,MEMBER,FOLLOW_MEMBER_in_collectionMemberExpression4187); if (failed) return node;
            // JPQL.g:726:17: ( OF )?
            int alt55=2;
            int LA55_0 = input.LA(1);
            
            if ( (LA55_0==OF) ) {
                alt55=1;
            }
            switch (alt55) {
                case 1 :
                    // JPQL.g:726:18: OF
                    {
                    match(input,OF,FOLLOW_OF_in_collectionMemberExpression4190); if (failed) return node;
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression4198);
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
    // JPQL.g:733:1: existsExpression[boolean not] returns [Object node] : t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET ;
    public final Object existsExpression(boolean not) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object subqueryNode = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:737:7: (t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET )
            // JPQL.g:737:7: t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_existsExpression4238); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4240); if (failed) return node;
            pushFollow(FOLLOW_subquery_in_existsExpression4246);
            subqueryNode=subquery();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4248); if (failed) return node;
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
    // JPQL.g:744:1: comparisonExpression[Object left] returns [Object node] : (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand );
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
            // JPQL.g:746:7: (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand )
            int alt56=6;
            switch ( input.LA(1) ) {
            case EQUALS:
                {
                alt56=1;
                }
                break;
            case NOT_EQUAL_TO:
                {
                alt56=2;
                }
                break;
            case GREATER_THAN:
                {
                alt56=3;
                }
                break;
            case GREATER_THAN_EQUAL_TO:
                {
                alt56=4;
                }
                break;
            case LESS_THAN:
                {
                alt56=5;
                }
                break;
            case LESS_THAN_EQUAL_TO:
                {
                alt56=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("744:1: comparisonExpression[Object left] returns [Object node] : (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand );", 56, 0, input);
            
                throw nvae;
            }
            
            switch (alt56) {
                case 1 :
                    // JPQL.g:746:7: t1= EQUALS n= comparisonExpressionRightOperand
                    {
                    t1=(Token)input.LT(1);
                    match(input,EQUALS,FOLLOW_EQUALS_in_comparisonExpression4288); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4294);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newEquals(t1.getLine(), t1.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:748:7: t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t2=(Token)input.LT(1);
                    match(input,NOT_EQUAL_TO,FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4315); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4321);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newNotEquals(t2.getLine(), t2.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:750:7: t3= GREATER_THAN n= comparisonExpressionRightOperand
                    {
                    t3=(Token)input.LT(1);
                    match(input,GREATER_THAN,FOLLOW_GREATER_THAN_in_comparisonExpression4342); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4348);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newGreaterThan(t3.getLine(), t3.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:752:7: t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t4=(Token)input.LT(1);
                    match(input,GREATER_THAN_EQUAL_TO,FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4369); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4375);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newGreaterThanEqual(t4.getLine(), t4.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:754:7: t5= LESS_THAN n= comparisonExpressionRightOperand
                    {
                    t5=(Token)input.LT(1);
                    match(input,LESS_THAN,FOLLOW_LESS_THAN_in_comparisonExpression4396); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4402);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newLessThan(t5.getLine(), t5.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:756:7: t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t6=(Token)input.LT(1);
                    match(input,LESS_THAN_EQUAL_TO,FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4423); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4429);
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
    // JPQL.g:760:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression | n= anyOrAllExpression );
    public final Object comparisonExpressionRightOperand() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:762:7: (n= arithmeticExpression | n= anyOrAllExpression )
            int alt57=2;
            int LA57_0 = input.LA(1);
            
            if ( (LA57_0==ABS||LA57_0==AVG||(LA57_0>=CONCAT && LA57_0<=CURRENT_TIMESTAMP)||LA57_0==FALSE||LA57_0==KEY||LA57_0==LENGTH||(LA57_0>=LOCATE && LA57_0<=MAX)||(LA57_0>=MIN && LA57_0<=MOD)||(LA57_0>=SIZE && LA57_0<=SQRT)||(LA57_0>=SUBSTRING && LA57_0<=SUM)||(LA57_0>=TRIM && LA57_0<=TYPE)||(LA57_0>=UPPER && LA57_0<=VALUE)||LA57_0==IDENT||LA57_0==LEFT_ROUND_BRACKET||(LA57_0>=PLUS && LA57_0<=MINUS)||(LA57_0>=INTEGER_LITERAL && LA57_0<=NAMED_PARAM)) ) {
                alt57=1;
            }
            else if ( (LA57_0==ALL||LA57_0==ANY||LA57_0==SOME) ) {
                alt57=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("760:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression | n= anyOrAllExpression );", 57, 0, input);
            
                throw nvae;
            }
            switch (alt57) {
                case 1 :
                    // JPQL.g:762:7: n= arithmeticExpression
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4470);
                    n=arithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:763:7: n= anyOrAllExpression
                    {
                    pushFollow(FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4484);
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
    // JPQL.g:766:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object arithmeticExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:768:7: (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
            int alt58=2;
            int LA58_0 = input.LA(1);
            
            if ( (LA58_0==ABS||LA58_0==AVG||(LA58_0>=CONCAT && LA58_0<=CURRENT_TIMESTAMP)||LA58_0==FALSE||LA58_0==KEY||LA58_0==LENGTH||(LA58_0>=LOCATE && LA58_0<=MAX)||(LA58_0>=MIN && LA58_0<=MOD)||(LA58_0>=SIZE && LA58_0<=SQRT)||(LA58_0>=SUBSTRING && LA58_0<=SUM)||(LA58_0>=TRIM && LA58_0<=TYPE)||(LA58_0>=UPPER && LA58_0<=VALUE)||LA58_0==IDENT||(LA58_0>=PLUS && LA58_0<=MINUS)||(LA58_0>=INTEGER_LITERAL && LA58_0<=NAMED_PARAM)) ) {
                alt58=1;
            }
            else if ( (LA58_0==LEFT_ROUND_BRACKET) ) {
                int LA58_36 = input.LA(2);
                
                if ( (LA58_36==SELECT) ) {
                    alt58=2;
                }
                else if ( (LA58_36==ABS||LA58_36==AVG||(LA58_36>=CONCAT && LA58_36<=CURRENT_TIMESTAMP)||LA58_36==FALSE||LA58_36==KEY||LA58_36==LENGTH||(LA58_36>=LOCATE && LA58_36<=MAX)||(LA58_36>=MIN && LA58_36<=MOD)||(LA58_36>=SIZE && LA58_36<=SQRT)||(LA58_36>=SUBSTRING && LA58_36<=SUM)||(LA58_36>=TRIM && LA58_36<=TYPE)||(LA58_36>=UPPER && LA58_36<=VALUE)||LA58_36==IDENT||LA58_36==LEFT_ROUND_BRACKET||(LA58_36>=PLUS && LA58_36<=MINUS)||(LA58_36>=INTEGER_LITERAL && LA58_36<=NAMED_PARAM)) ) {
                    alt58=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("766:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 58, 36, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("766:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 58, 0, input);
            
                throw nvae;
            }
            switch (alt58) {
                case 1 :
                    // JPQL.g:768:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4516);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:769:7: LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4526); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_arithmeticExpression4532);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4534); if (failed) return node;
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
    // JPQL.g:772:1: simpleArithmeticExpression returns [Object node] : n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )* ;
    public final Object simpleArithmeticExpression() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token m=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:776:7: (n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )* )
            // JPQL.g:776:7: n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*
            {
            pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4566);
            n=arithmeticTerm();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:777:9: (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*
            loop59:
            do {
                int alt59=3;
                int LA59_0 = input.LA(1);
                
                if ( (LA59_0==PLUS) ) {
                    alt59=1;
                }
                else if ( (LA59_0==MINUS) ) {
                    alt59=2;
                }
                
            
                switch (alt59) {
            	case 1 :
            	    // JPQL.g:777:11: p= PLUS right= arithmeticTerm
            	    {
            	    p=(Token)input.LT(1);
            	    match(input,PLUS,FOLLOW_PLUS_in_simpleArithmeticExpression4582); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4588);
            	    right=arithmeticTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newPlus(p.getLine(), p.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:779:11: m= MINUS right= arithmeticTerm
            	    {
            	    m=(Token)input.LT(1);
            	    match(input,MINUS,FOLLOW_MINUS_in_simpleArithmeticExpression4617); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4623);
            	    right=arithmeticTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newMinus(m.getLine(), m.getCharPositionInLine(), node, right); 
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
    // $ANTLR end simpleArithmeticExpression

    
    // $ANTLR start arithmeticTerm
    // JPQL.g:784:1: arithmeticTerm returns [Object node] : n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )* ;
    public final Object arithmeticTerm() throws RecognitionException {

        Object node = null;
    
        Token m=null;
        Token d=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:788:7: (n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )* )
            // JPQL.g:788:7: n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*
            {
            pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4680);
            n=arithmeticFactor();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g:789:9: (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*
            loop60:
            do {
                int alt60=3;
                int LA60_0 = input.LA(1);
                
                if ( (LA60_0==MULTIPLY) ) {
                    alt60=1;
                }
                else if ( (LA60_0==DIVIDE) ) {
                    alt60=2;
                }
                
            
                switch (alt60) {
            	case 1 :
            	    // JPQL.g:789:11: m= MULTIPLY right= arithmeticFactor
            	    {
            	    m=(Token)input.LT(1);
            	    match(input,MULTIPLY,FOLLOW_MULTIPLY_in_arithmeticTerm4696); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4702);
            	    right=arithmeticFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newMultiply(m.getLine(), m.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:791:11: d= DIVIDE right= arithmeticFactor
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DIVIDE,FOLLOW_DIVIDE_in_arithmeticTerm4731); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4737);
            	    right=arithmeticFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newDivide(d.getLine(), d.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop60;
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
    // JPQL.g:796:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary );
    public final Object arithmeticFactor() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token m=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:798:7: (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary )
            int alt61=3;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt61=1;
                }
                break;
            case MINUS:
                {
                alt61=2;
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
            case TYPE:
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
                alt61=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("796:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary );", 61, 0, input);
            
                throw nvae;
            }
            
            switch (alt61) {
                case 1 :
                    // JPQL.g:798:7: p= PLUS n= arithmeticPrimary
                    {
                    p=(Token)input.LT(1);
                    match(input,PLUS,FOLLOW_PLUS_in_arithmeticFactor4791); if (failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor4798);
                    n=arithmeticPrimary();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = factory.newUnaryPlus(p.getLine(), p.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:800:7: m= MINUS n= arithmeticPrimary
                    {
                    m=(Token)input.LT(1);
                    match(input,MINUS,FOLLOW_MINUS_in_arithmeticFactor4820); if (failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor4826);
                    n=arithmeticPrimary();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newUnaryMinus(m.getLine(), m.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:802:7: n= arithmeticPrimary
                    {
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor4850);
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
    // JPQL.g:805:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= functionsReturningNumerics | n= functionsReturningDatetime | n= functionsReturningStrings | n= inputParameter | n= literalNumeric | n= literalString | n= literalBoolean | n= entityTypeExpression | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET );
    public final Object arithmeticPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:807:7: ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= functionsReturningNumerics | n= functionsReturningDatetime | n= functionsReturningStrings | n= inputParameter | n= literalNumeric | n= literalString | n= literalBoolean | n= entityTypeExpression | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            int alt62=11;
            switch ( input.LA(1) ) {
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case SUM:
                {
                alt62=1;
                }
                break;
            case KEY:
            case VALUE:
            case IDENT:
                {
                alt62=2;
                }
                break;
            case ABS:
            case LENGTH:
            case LOCATE:
            case MOD:
            case SIZE:
            case SQRT:
                {
                alt62=3;
                }
                break;
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
                {
                alt62=4;
                }
                break;
            case CONCAT:
            case LOWER:
            case SUBSTRING:
            case TRIM:
            case UPPER:
                {
                alt62=5;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt62=6;
                }
                break;
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
                {
                alt62=7;
                }
                break;
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt62=8;
                }
                break;
            case FALSE:
            case TRUE:
                {
                alt62=9;
                }
                break;
            case TYPE:
                {
                alt62=10;
                }
                break;
            case LEFT_ROUND_BRACKET:
                {
                alt62=11;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("805:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= functionsReturningNumerics | n= functionsReturningDatetime | n= functionsReturningStrings | n= inputParameter | n= literalNumeric | n= literalString | n= literalBoolean | n= entityTypeExpression | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET );", 62, 0, input);
            
                throw nvae;
            }
            
            switch (alt62) {
                case 1 :
                    // JPQL.g:807:7: {...}?n= aggregateExpression
                    {
                    if ( !( aggregatesAllowed() ) ) {
                        if (backtracking>0) {failed=true; return node;}
                        throw new FailedPredicateException(input, "arithmeticPrimary", " aggregatesAllowed() ");
                    }
                    pushFollow(FOLLOW_aggregateExpression_in_arithmeticPrimary4884);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:808:7: n= pathExprOrVariableAccess
                    {
                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_arithmeticPrimary4898);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:809:7: n= functionsReturningNumerics
                    {
                    pushFollow(FOLLOW_functionsReturningNumerics_in_arithmeticPrimary4912);
                    n=functionsReturningNumerics();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:810:7: n= functionsReturningDatetime
                    {
                    pushFollow(FOLLOW_functionsReturningDatetime_in_arithmeticPrimary4926);
                    n=functionsReturningDatetime();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:811:7: n= functionsReturningStrings
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_arithmeticPrimary4940);
                    n=functionsReturningStrings();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:812:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_arithmeticPrimary4954);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 7 :
                    // JPQL.g:813:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_arithmeticPrimary4968);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 8 :
                    // JPQL.g:814:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_arithmeticPrimary4982);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 9 :
                    // JPQL.g:815:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_arithmeticPrimary4996);
                    n=literalBoolean();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 10 :
                    // JPQL.g:816:7: n= entityTypeExpression
                    {
                    pushFollow(FOLLOW_entityTypeExpression_in_arithmeticPrimary5010);
                    n=entityTypeExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 11 :
                    // JPQL.g:817:7: LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary5020); if (failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary5026);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary5028); if (failed) return node;
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
    // JPQL.g:820:1: anyOrAllExpression returns [Object node] : (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object anyOrAllExpression() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token y=null;
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:822:7: (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
            int alt63=3;
            switch ( input.LA(1) ) {
            case ALL:
                {
                alt63=1;
                }
                break;
            case ANY:
                {
                alt63=2;
                }
                break;
            case SOME:
                {
                alt63=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("820:1: anyOrAllExpression returns [Object node] : (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 63, 0, input);
            
                throw nvae;
            }
            
            switch (alt63) {
                case 1 :
                    // JPQL.g:822:7: a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    a=(Token)input.LT(1);
                    match(input,ALL,FOLLOW_ALL_in_anyOrAllExpression5058); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5060); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5066);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5068); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAll(a.getLine(), a.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:824:7: y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    y=(Token)input.LT(1);
                    match(input,ANY,FOLLOW_ANY_in_anyOrAllExpression5088); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5090); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5096);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5098); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAny(y.getLine(), y.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:826:7: s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    s=(Token)input.LT(1);
                    match(input,SOME,FOLLOW_SOME_in_anyOrAllExpression5118); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5120); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5126);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5128); if (failed) return node;
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
    // JPQL.g:830:1: entityTypeExpression returns [Object node] : n= typeDiscriminator ;
    public final Object entityTypeExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:832:7: (n= typeDiscriminator )
            // JPQL.g:832:7: n= typeDiscriminator
            {
            pushFollow(FOLLOW_typeDiscriminator_in_entityTypeExpression5168);
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
    // JPQL.g:835:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );
    public final Object typeDiscriminator() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token c=null;
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:837:7: (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET )
            int alt64=2;
            int LA64_0 = input.LA(1);
            
            if ( (LA64_0==TYPE) ) {
                int LA64_1 = input.LA(2);
                
                if ( (LA64_1==LEFT_ROUND_BRACKET) ) {
                    int LA64_2 = input.LA(3);
                    
                    if ( (LA64_2==KEY||LA64_2==VALUE||LA64_2==IDENT) ) {
                        alt64=1;
                    }
                    else if ( ((LA64_2>=POSITIONAL_PARAM && LA64_2<=NAMED_PARAM)) ) {
                        alt64=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("835:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );", 64, 2, input);
                    
                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("835:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );", 64, 1, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("835:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );", 64, 0, input);
            
                throw nvae;
            }
            switch (alt64) {
                case 1 :
                    // JPQL.g:837:7: a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET
                    {
                    a=(Token)input.LT(1);
                    match(input,TYPE,FOLLOW_TYPE_in_typeDiscriminator5201); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5203); if (failed) return node;
                    pushFollow(FOLLOW_variableOrSingleValuedPath_in_typeDiscriminator5209);
                    n=variableOrSingleValuedPath();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5211); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newType(a.getLine(), a.getCharPositionInLine(), n);
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:838:7: c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET
                    {
                    c=(Token)input.LT(1);
                    match(input,TYPE,FOLLOW_TYPE_in_typeDiscriminator5226); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5228); if (failed) return node;
                    pushFollow(FOLLOW_inputParameter_in_typeDiscriminator5234);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5236); if (failed) return node;
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

    
    // $ANTLR start variableOrSingleValuedPath
    // JPQL.g:841:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression | n= variableAccessOrTypeConstant );
    public final Object variableOrSingleValuedPath() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // JPQL.g:843:7: (n= singleValuedPathExpression | n= variableAccessOrTypeConstant )
            int alt65=2;
            int LA65_0 = input.LA(1);
            
            if ( (LA65_0==IDENT) ) {
                int LA65_1 = input.LA(2);
                
                if ( (LA65_1==RIGHT_ROUND_BRACKET) ) {
                    alt65=2;
                }
                else if ( (LA65_1==DOT) ) {
                    alt65=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("841:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression | n= variableAccessOrTypeConstant );", 65, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA65_0==KEY||LA65_0==VALUE) ) {
                alt65=1;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("841:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression | n= variableAccessOrTypeConstant );", 65, 0, input);
            
                throw nvae;
            }
            switch (alt65) {
                case 1 :
                    // JPQL.g:843:7: n= singleValuedPathExpression
                    {
                    pushFollow(FOLLOW_singleValuedPathExpression_in_variableOrSingleValuedPath5272);
                    n=singleValuedPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:844:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_variableOrSingleValuedPath5286);
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
    // JPQL.g:847:1: stringPrimary returns [Object node] : (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression );
    public final Object stringPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:849:7: (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression )
            int alt66=4;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt66=1;
                }
                break;
            case CONCAT:
            case LOWER:
            case SUBSTRING:
            case TRIM:
            case UPPER:
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
            case KEY:
            case VALUE:
            case IDENT:
                {
                alt66=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("847:1: stringPrimary returns [Object node] : (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression );", 66, 0, input);
            
                throw nvae;
            }
            
            switch (alt66) {
                case 1 :
                    // JPQL.g:849:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_stringPrimary5318);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:850:7: n= functionsReturningStrings
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_stringPrimary5332);
                    n=functionsReturningStrings();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:851:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_stringPrimary5346);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:852:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_stringPrimary5360);
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
    // JPQL.g:857:1: literal returns [Object node] : (n= literalNumeric | n= literalBoolean | n= literalString );
    public final Object literal() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:859:7: (n= literalNumeric | n= literalBoolean | n= literalString )
            int alt67=3;
            switch ( input.LA(1) ) {
            case INTEGER_LITERAL:
            case LONG_LITERAL:
            case FLOAT_LITERAL:
            case DOUBLE_LITERAL:
                {
                alt67=1;
                }
                break;
            case FALSE:
            case TRUE:
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
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("857:1: literal returns [Object node] : (n= literalNumeric | n= literalBoolean | n= literalString );", 67, 0, input);
            
                throw nvae;
            }
            
            switch (alt67) {
                case 1 :
                    // JPQL.g:859:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_literal5394);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:860:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_literal5408);
                    n=literalBoolean();
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
                    pushFollow(FOLLOW_literalString_in_literal5422);
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
    // JPQL.g:864:1: literalNumeric returns [Object node] : (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL );
    public final Object literalNumeric() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        Token l=null;
        Token f=null;
        Token d=null;
    
         node = null; 
        try {
            // JPQL.g:866:7: (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL )
            int alt68=4;
            switch ( input.LA(1) ) {
            case INTEGER_LITERAL:
                {
                alt68=1;
                }
                break;
            case LONG_LITERAL:
                {
                alt68=2;
                }
                break;
            case FLOAT_LITERAL:
                {
                alt68=3;
                }
                break;
            case DOUBLE_LITERAL:
                {
                alt68=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("864:1: literalNumeric returns [Object node] : (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL );", 68, 0, input);
            
                throw nvae;
            }
            
            switch (alt68) {
                case 1 :
                    // JPQL.g:866:7: i= INTEGER_LITERAL
                    {
                    i=(Token)input.LT(1);
                    match(input,INTEGER_LITERAL,FOLLOW_INTEGER_LITERAL_in_literalNumeric5452); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newIntegerLiteral(i.getLine(), i.getCharPositionInLine(), 
                                                                   Integer.valueOf(i.getText())); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:871:7: l= LONG_LITERAL
                    {
                    l=(Token)input.LT(1);
                    match(input,LONG_LITERAL,FOLLOW_LONG_LITERAL_in_literalNumeric5468); if (failed) return node;
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
                    // JPQL.g:879:7: f= FLOAT_LITERAL
                    {
                    f=(Token)input.LT(1);
                    match(input,FLOAT_LITERAL,FOLLOW_FLOAT_LITERAL_in_literalNumeric5489); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newFloatLiteral(f.getLine(), f.getCharPositionInLine(),
                                                                 Float.valueOf(f.getText()));
                              
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:884:7: d= DOUBLE_LITERAL
                    {
                    d=(Token)input.LT(1);
                    match(input,DOUBLE_LITERAL,FOLLOW_DOUBLE_LITERAL_in_literalNumeric5509); if (failed) return node;
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
    // JPQL.g:891:1: literalBoolean returns [Object node] : (t= TRUE | f= FALSE );
    public final Object literalBoolean() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Token f=null;
    
         node = null; 
        try {
            // JPQL.g:893:7: (t= TRUE | f= FALSE )
            int alt69=2;
            int LA69_0 = input.LA(1);
            
            if ( (LA69_0==TRUE) ) {
                alt69=1;
            }
            else if ( (LA69_0==FALSE) ) {
                alt69=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("891:1: literalBoolean returns [Object node] : (t= TRUE | f= FALSE );", 69, 0, input);
            
                throw nvae;
            }
            switch (alt69) {
                case 1 :
                    // JPQL.g:893:7: t= TRUE
                    {
                    t=(Token)input.LT(1);
                    match(input,TRUE,FOLLOW_TRUE_in_literalBoolean5547); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newBooleanLiteral(t.getLine(), t.getCharPositionInLine(), Boolean.TRUE); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:895:7: f= FALSE
                    {
                    f=(Token)input.LT(1);
                    match(input,FALSE,FOLLOW_FALSE_in_literalBoolean5569); if (failed) return node;
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
    // JPQL.g:899:1: literalString returns [Object node] : (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED );
    public final Object literalString() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Token s=null;
    
         node = null; 
        try {
            // JPQL.g:901:7: (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED )
            int alt70=2;
            int LA70_0 = input.LA(1);
            
            if ( (LA70_0==STRING_LITERAL_DOUBLE_QUOTED) ) {
                alt70=1;
            }
            else if ( (LA70_0==STRING_LITERAL_SINGLE_QUOTED) ) {
                alt70=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("899:1: literalString returns [Object node] : (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED );", 70, 0, input);
            
                throw nvae;
            }
            switch (alt70) {
                case 1 :
                    // JPQL.g:901:7: d= STRING_LITERAL_DOUBLE_QUOTED
                    {
                    d=(Token)input.LT(1);
                    match(input,STRING_LITERAL_DOUBLE_QUOTED,FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString5608); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newStringLiteral(d.getLine(), d.getCharPositionInLine(), 
                                                                  convertStringLiteral(d.getText())); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:906:7: s= STRING_LITERAL_SINGLE_QUOTED
                    {
                    s=(Token)input.LT(1);
                    match(input,STRING_LITERAL_SINGLE_QUOTED,FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString5629); if (failed) return node;
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
    // JPQL.g:913:1: inputParameter returns [Object node] : (p= POSITIONAL_PARAM | n= NAMED_PARAM );
    public final Object inputParameter() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token n=null;
    
         node = null; 
        try {
            // JPQL.g:915:7: (p= POSITIONAL_PARAM | n= NAMED_PARAM )
            int alt71=2;
            int LA71_0 = input.LA(1);
            
            if ( (LA71_0==POSITIONAL_PARAM) ) {
                alt71=1;
            }
            else if ( (LA71_0==NAMED_PARAM) ) {
                alt71=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("913:1: inputParameter returns [Object node] : (p= POSITIONAL_PARAM | n= NAMED_PARAM );", 71, 0, input);
            
                throw nvae;
            }
            switch (alt71) {
                case 1 :
                    // JPQL.g:915:7: p= POSITIONAL_PARAM
                    {
                    p=(Token)input.LT(1);
                    match(input,POSITIONAL_PARAM,FOLLOW_POSITIONAL_PARAM_in_inputParameter5667); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  // skip the leading ?
                                  String text = p.getText().substring(1);
                                  node = factory.newPositionalParameter(p.getLine(), p.getCharPositionInLine(), text); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:921:7: n= NAMED_PARAM
                    {
                    n=(Token)input.LT(1);
                    match(input,NAMED_PARAM,FOLLOW_NAMED_PARAM_in_inputParameter5687); if (failed) return node;
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
    // JPQL.g:929:1: functionsReturningNumerics returns [Object node] : (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size );
    public final Object functionsReturningNumerics() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:931:7: (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size )
            int alt72=6;
            switch ( input.LA(1) ) {
            case ABS:
                {
                alt72=1;
                }
                break;
            case LENGTH:
                {
                alt72=2;
                }
                break;
            case MOD:
                {
                alt72=3;
                }
                break;
            case SQRT:
                {
                alt72=4;
                }
                break;
            case LOCATE:
                {
                alt72=5;
                }
                break;
            case SIZE:
                {
                alt72=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("929:1: functionsReturningNumerics returns [Object node] : (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size );", 72, 0, input);
            
                throw nvae;
            }
            
            switch (alt72) {
                case 1 :
                    // JPQL.g:931:7: n= abs
                    {
                    pushFollow(FOLLOW_abs_in_functionsReturningNumerics5727);
                    n=abs();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:932:7: n= length
                    {
                    pushFollow(FOLLOW_length_in_functionsReturningNumerics5741);
                    n=length();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:933:7: n= mod
                    {
                    pushFollow(FOLLOW_mod_in_functionsReturningNumerics5755);
                    n=mod();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:934:7: n= sqrt
                    {
                    pushFollow(FOLLOW_sqrt_in_functionsReturningNumerics5769);
                    n=sqrt();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:935:7: n= locate
                    {
                    pushFollow(FOLLOW_locate_in_functionsReturningNumerics5783);
                    n=locate();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g:936:7: n= size
                    {
                    pushFollow(FOLLOW_size_in_functionsReturningNumerics5797);
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
    // JPQL.g:939:1: functionsReturningDatetime returns [Object node] : (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP );
    public final Object functionsReturningDatetime() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Token t=null;
        Token ts=null;
    
         node = null; 
        try {
            // JPQL.g:941:7: (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP )
            int alt73=3;
            switch ( input.LA(1) ) {
            case CURRENT_DATE:
                {
                alt73=1;
                }
                break;
            case CURRENT_TIME:
                {
                alt73=2;
                }
                break;
            case CURRENT_TIMESTAMP:
                {
                alt73=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("939:1: functionsReturningDatetime returns [Object node] : (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP );", 73, 0, input);
            
                throw nvae;
            }
            
            switch (alt73) {
                case 1 :
                    // JPQL.g:941:7: d= CURRENT_DATE
                    {
                    d=(Token)input.LT(1);
                    match(input,CURRENT_DATE,FOLLOW_CURRENT_DATE_in_functionsReturningDatetime5827); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newCurrentDate(d.getLine(), d.getCharPositionInLine()); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:943:7: t= CURRENT_TIME
                    {
                    t=(Token)input.LT(1);
                    match(input,CURRENT_TIME,FOLLOW_CURRENT_TIME_in_functionsReturningDatetime5848); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newCurrentTime(t.getLine(), t.getCharPositionInLine()); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:945:7: ts= CURRENT_TIMESTAMP
                    {
                    ts=(Token)input.LT(1);
                    match(input,CURRENT_TIMESTAMP,FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime5868); if (failed) return node;
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
    // JPQL.g:949:1: functionsReturningStrings returns [Object node] : (n= concat | n= substring | n= trim | n= upper | n= lower );
    public final Object functionsReturningStrings() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:951:7: (n= concat | n= substring | n= trim | n= upper | n= lower )
            int alt74=5;
            switch ( input.LA(1) ) {
            case CONCAT:
                {
                alt74=1;
                }
                break;
            case SUBSTRING:
                {
                alt74=2;
                }
                break;
            case TRIM:
                {
                alt74=3;
                }
                break;
            case UPPER:
                {
                alt74=4;
                }
                break;
            case LOWER:
                {
                alt74=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("949:1: functionsReturningStrings returns [Object node] : (n= concat | n= substring | n= trim | n= upper | n= lower );", 74, 0, input);
            
                throw nvae;
            }
            
            switch (alt74) {
                case 1 :
                    // JPQL.g:951:7: n= concat
                    {
                    pushFollow(FOLLOW_concat_in_functionsReturningStrings5908);
                    n=concat();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:952:7: n= substring
                    {
                    pushFollow(FOLLOW_substring_in_functionsReturningStrings5922);
                    n=substring();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:953:7: n= trim
                    {
                    pushFollow(FOLLOW_trim_in_functionsReturningStrings5936);
                    n=trim();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:954:7: n= upper
                    {
                    pushFollow(FOLLOW_upper_in_functionsReturningStrings5950);
                    n=upper();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:955:7: n= lower
                    {
                    pushFollow(FOLLOW_lower_in_functionsReturningStrings5964);
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
    // JPQL.g:959:1: concat returns [Object node] : c= CONCAT LEFT_ROUND_BRACKET firstArg= stringPrimary COMMA secondArg= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object concat() throws RecognitionException {

        Object node = null;
    
        Token c=null;
        Object firstArg = null;

        Object secondArg = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:963:7: (c= CONCAT LEFT_ROUND_BRACKET firstArg= stringPrimary COMMA secondArg= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:963:7: c= CONCAT LEFT_ROUND_BRACKET firstArg= stringPrimary COMMA secondArg= stringPrimary RIGHT_ROUND_BRACKET
            {
            c=(Token)input.LT(1);
            match(input,CONCAT,FOLLOW_CONCAT_in_concat5995); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_concat6006); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_concat6021);
            firstArg=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_concat6023); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_concat6029);
            secondArg=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_concat6039); if (failed) return node;
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
    // JPQL.g:970:1: substring returns [Object node] : s= SUBSTRING LEFT_ROUND_BRACKET string= stringPrimary COMMA start= simpleArithmeticExpression COMMA lengthNode= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object substring() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object string = null;

        Object start = null;

        Object lengthNode = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g:974:7: (s= SUBSTRING LEFT_ROUND_BRACKET string= stringPrimary COMMA start= simpleArithmeticExpression COMMA lengthNode= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:974:7: s= SUBSTRING LEFT_ROUND_BRACKET string= stringPrimary COMMA start= simpleArithmeticExpression COMMA lengthNode= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SUBSTRING,FOLLOW_SUBSTRING_in_substring6077); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_substring6090); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_substring6104);
            string=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_substring6106); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_substring6120);
            start=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_substring6122); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_substring6136);
            lengthNode=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_substring6146); if (failed) return node;
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
    // JPQL.g:986:1: trim returns [Object node] : t= TRIM LEFT_ROUND_BRACKET ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object trim() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        TrimSpecification trimSpecIndicator = null;

        Object trimCharNode = null;

        Object n = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:990:7: (t= TRIM LEFT_ROUND_BRACKET ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:990:7: t= TRIM LEFT_ROUND_BRACKET ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,TRIM,FOLLOW_TRIM_in_trim6184); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_trim6194); if (failed) return node;
            // JPQL.g:992:9: ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )?
            int alt75=2;
            int LA75_0 = input.LA(1);
            
            if ( (LA75_0==LEADING) && (synpred2())) {
                alt75=1;
            }
            else if ( (LA75_0==TRAILING) && (synpred2())) {
                alt75=1;
            }
            else if ( (LA75_0==BOTH) && (synpred2())) {
                alt75=1;
            }
            else if ( (LA75_0==STRING_LITERAL_DOUBLE_QUOTED) ) {
                int LA75_4 = input.LA(2);
                
                if ( (LA75_4==FROM) && (synpred2())) {
                    alt75=1;
                }
            }
            else if ( (LA75_0==STRING_LITERAL_SINGLE_QUOTED) ) {
                int LA75_5 = input.LA(2);
                
                if ( (LA75_5==FROM) && (synpred2())) {
                    alt75=1;
                }
            }
            else if ( (LA75_0==POSITIONAL_PARAM) ) {
                int LA75_6 = input.LA(2);
                
                if ( (LA75_6==FROM) && (synpred2())) {
                    alt75=1;
                }
            }
            else if ( (LA75_0==NAMED_PARAM) ) {
                int LA75_7 = input.LA(2);
                
                if ( (LA75_7==FROM) && (synpred2())) {
                    alt75=1;
                }
            }
            else if ( (LA75_0==FROM) && (synpred2())) {
                alt75=1;
            }
            switch (alt75) {
                case 1 :
                    // JPQL.g:992:11: ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM
                    {
                    pushFollow(FOLLOW_trimSpec_in_trim6222);
                    trimSpecIndicator=trimSpec();
                    _fsp--;
                    if (failed) return node;
                    pushFollow(FOLLOW_trimChar_in_trim6228);
                    trimCharNode=trimChar();
                    _fsp--;
                    if (failed) return node;
                    match(input,FROM,FOLLOW_FROM_in_trim6230); if (failed) return node;
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_stringPrimary_in_trim6248);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_trim6258); if (failed) return node;
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
    // JPQL.g:1001:1: trimSpec returns [TrimSpecification trimSpec] : ( LEADING | TRAILING | BOTH | );
    public final TrimSpecification trimSpec() throws RecognitionException {

        TrimSpecification trimSpec = null;
    
         trimSpec = TrimSpecification.BOTH; 
        try {
            // JPQL.g:1003:7: ( LEADING | TRAILING | BOTH | )
            int alt76=4;
            switch ( input.LA(1) ) {
            case LEADING:
                {
                alt76=1;
                }
                break;
            case TRAILING:
                {
                alt76=2;
                }
                break;
            case BOTH:
                {
                alt76=3;
                }
                break;
            case FROM:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt76=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return trimSpec;}
                NoViableAltException nvae =
                    new NoViableAltException("1001:1: trimSpec returns [TrimSpecification trimSpec] : ( LEADING | TRAILING | BOTH | );", 76, 0, input);
            
                throw nvae;
            }
            
            switch (alt76) {
                case 1 :
                    // JPQL.g:1003:7: LEADING
                    {
                    match(input,LEADING,FOLLOW_LEADING_in_trimSpec6294); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.LEADING; 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1005:7: TRAILING
                    {
                    match(input,TRAILING,FOLLOW_TRAILING_in_trimSpec6312); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.TRAILING; 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1007:7: BOTH
                    {
                    match(input,BOTH,FOLLOW_BOTH_in_trimSpec6330); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.BOTH; 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:1010:5: 
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
    // JPQL.g:1012:1: trimChar returns [Object node] : (n= literalString | n= inputParameter | );
    public final Object trimChar() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1014:7: (n= literalString | n= inputParameter | )
            int alt77=3;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt77=1;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt77=2;
                }
                break;
            case FROM:
                {
                alt77=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1012:1: trimChar returns [Object node] : (n= literalString | n= inputParameter | );", 77, 0, input);
            
                throw nvae;
            }
            
            switch (alt77) {
                case 1 :
                    // JPQL.g:1014:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_trimChar6377);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1015:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_trimChar6391);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1017:5: 
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
    // JPQL.g:1019:1: upper returns [Object node] : u= UPPER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object upper() throws RecognitionException {

        Object node = null;
    
        Token u=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1021:7: (u= UPPER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:1021:7: u= UPPER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET
            {
            u=(Token)input.LT(1);
            match(input,UPPER,FOLLOW_UPPER_in_upper6428); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_upper6430); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_upper6436);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_upper6438); if (failed) return node;
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
    // JPQL.g:1025:1: lower returns [Object node] : l= LOWER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object lower() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1027:7: (l= LOWER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:1027:7: l= LOWER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LOWER,FOLLOW_LOWER_in_lower6476); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_lower6478); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_lower6484);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_lower6486); if (failed) return node;
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
    // JPQL.g:1032:1: abs returns [Object node] : a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object abs() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1034:7: (a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1034:7: a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            a=(Token)input.LT(1);
            match(input,ABS,FOLLOW_ABS_in_abs6525); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_abs6527); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_abs6533);
            n=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_abs6535); if (failed) return node;
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
    // JPQL.g:1038:1: length returns [Object node] : l= LENGTH LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object length() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1040:7: (l= LENGTH LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g:1040:7: l= LENGTH LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LENGTH,FOLLOW_LENGTH_in_length6573); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_length6575); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_length6581);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_length6583); if (failed) return node;
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
    // JPQL.g:1044:1: locate returns [Object node] : l= LOCATE LEFT_ROUND_BRACKET pattern= stringPrimary COMMA n= stringPrimary ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET ;
    public final Object locate() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object pattern = null;

        Object n = null;

        Object startPos = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1048:7: (l= LOCATE LEFT_ROUND_BRACKET pattern= stringPrimary COMMA n= stringPrimary ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET )
            // JPQL.g:1048:7: l= LOCATE LEFT_ROUND_BRACKET pattern= stringPrimary COMMA n= stringPrimary ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LOCATE,FOLLOW_LOCATE_in_locate6621); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_locate6631); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_locate6646);
            pattern=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_locate6648); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_locate6654);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            // JPQL.g:1051:9: ( COMMA startPos= simpleArithmeticExpression )?
            int alt78=2;
            int LA78_0 = input.LA(1);
            
            if ( (LA78_0==COMMA) ) {
                alt78=1;
            }
            switch (alt78) {
                case 1 :
                    // JPQL.g:1051:11: COMMA startPos= simpleArithmeticExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_locate6666); if (failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_locate6672);
                    startPos=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_locate6685); if (failed) return node;
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
    // JPQL.g:1059:1: size returns [Object node] : s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ;
    public final Object size() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1061:7: (s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1061:7: s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SIZE,FOLLOW_SIZE_in_size6723); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_size6734); if (failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_size6740);
            n=collectionValuedPathExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_size6742); if (failed) return node;
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
    // JPQL.g:1066:1: mod returns [Object node] : m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object mod() throws RecognitionException {

        Object node = null;
    
        Token m=null;
        Object left = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1070:7: (m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1070:7: m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            m=(Token)input.LT(1);
            match(input,MOD,FOLLOW_MOD_in_mod6780); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mod6782); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_mod6796);
            left=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_mod6798); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_mod6813);
            right=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mod6823); if (failed) return node;
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
    // JPQL.g:1077:1: sqrt returns [Object node] : s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object sqrt() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1079:7: (s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g:1079:7: s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SQRT,FOLLOW_SQRT_in_sqrt6861); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_sqrt6872); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_sqrt6878);
            n=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt6880); if (failed) return node;
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
    // JPQL.g:1084:1: subquery returns [Object node] : select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? ;
    public final Object subquery() throws RecognitionException {

        Object node = null;
    
        Object select = null;

        Object from = null;

        Object where = null;

        Object groupBy = null;

        Object having = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g:1088:7: (select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? )
            // JPQL.g:1088:7: select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )?
            {
            pushFollow(FOLLOW_simpleSelectClause_in_subquery6921);
            select=simpleSelectClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_subqueryFromClause_in_subquery6936);
            from=subqueryFromClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g:1090:7: (where= whereClause )?
            int alt79=2;
            int LA79_0 = input.LA(1);
            
            if ( (LA79_0==WHERE) ) {
                alt79=1;
            }
            switch (alt79) {
                case 1 :
                    // JPQL.g:1090:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_subquery6951);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:1091:7: (groupBy= groupByClause )?
            int alt80=2;
            int LA80_0 = input.LA(1);
            
            if ( (LA80_0==GROUP) ) {
                alt80=1;
            }
            switch (alt80) {
                case 1 :
                    // JPQL.g:1091:8: groupBy= groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_subquery6966);
                    groupBy=groupByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g:1092:7: (having= havingClause )?
            int alt81=2;
            int LA81_0 = input.LA(1);
            
            if ( (LA81_0==HAVING) ) {
                alt81=1;
            }
            switch (alt81) {
                case 1 :
                    // JPQL.g:1092:8: having= havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_subquery6982);
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
    // JPQL.g:1099:1: simpleSelectClause returns [Object node] : s= SELECT ( DISTINCT )? n= simpleSelectExpression ;
    public final Object simpleSelectClause() throws RecognitionException {
        simpleSelectClause_stack.push(new simpleSelectClause_scope());

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         
            node = null; 
            ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct = false;
    
        try {
            // JPQL.g:1107:7: (s= SELECT ( DISTINCT )? n= simpleSelectExpression )
            // JPQL.g:1107:7: s= SELECT ( DISTINCT )? n= simpleSelectExpression
            {
            s=(Token)input.LT(1);
            match(input,SELECT,FOLLOW_SELECT_in_simpleSelectClause7025); if (failed) return node;
            // JPQL.g:1107:16: ( DISTINCT )?
            int alt82=2;
            int LA82_0 = input.LA(1);
            
            if ( (LA82_0==DISTINCT) ) {
                alt82=1;
            }
            switch (alt82) {
                case 1 :
                    // JPQL.g:1107:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_simpleSelectClause7028); if (failed) return node;
                    if ( backtracking==0 ) {
                       ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct = true; 
                    }
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_simpleSelectExpression_in_simpleSelectClause7044);
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
    // JPQL.g:1117:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );
    public final Object simpleSelectExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1119:7: (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant )
            int alt83=3;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA83_1 = input.LA(2);
                
                if ( (LA83_1==FROM) ) {
                    alt83=3;
                }
                else if ( (LA83_1==DOT) ) {
                    alt83=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1117:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );", 83, 1, input);
                
                    throw nvae;
                }
                }
                break;
            case KEY:
            case VALUE:
                {
                alt83=1;
                }
                break;
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case SUM:
                {
                alt83=2;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1117:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );", 83, 0, input);
            
                throw nvae;
            }
            
            switch (alt83) {
                case 1 :
                    // JPQL.g:1119:7: n= singleValuedPathExpression
                    {
                    pushFollow(FOLLOW_singleValuedPathExpression_in_simpleSelectExpression7084);
                    n=singleValuedPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1120:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_simpleSelectExpression7099);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1121:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_simpleSelectExpression7114);
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
    // JPQL.g:1125:1: subqueryFromClause returns [Object node] : f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] )* ;
    public final Object subqueryFromClause() throws RecognitionException {
        subqueryFromClause_stack.push(new subqueryFromClause_scope());

        Object node = null;
    
        Token f=null;
    
         
            node = null; 
            ((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls = new ArrayList();
    
        try {
            // JPQL.g:1133:7: (f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] )* )
            // JPQL.g:1133:7: f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] )*
            {
            f=(Token)input.LT(1);
            match(input,FROM,FOLLOW_FROM_in_subqueryFromClause7149); if (failed) return node;
            pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause7151);
            subselectIdentificationVariableDeclaration(((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls);
            _fsp--;
            if (failed) return node;
            // JPQL.g:1134:9: ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] )*
            loop84:
            do {
                int alt84=2;
                int LA84_0 = input.LA(1);
                
                if ( (LA84_0==COMMA) ) {
                    alt84=1;
                }
                
            
                switch (alt84) {
            	case 1 :
            	    // JPQL.g:1134:11: COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_subqueryFromClause7165); if (failed) return node;
            	    pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause7167);
            	    subselectIdentificationVariableDeclaration(((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls);
            	    _fsp--;
            	    if (failed) return node;
            	    
            	    }
            	    break;
            
            	default :
            	    break loop84;
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
    // JPQL.g:1138:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration );
    public final void subselectIdentificationVariableDeclaration(List varDecls) throws RecognitionException {
        Token i=null;
        Object n = null;
        
    
         Object node; 
        try {
            // JPQL.g:1140:7: ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration )
            int alt86=3;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA86_1 = input.LA(2);
                
                if ( (LA86_1==AS||LA86_1==IDENT) ) {
                    alt86=1;
                }
                else if ( (LA86_1==DOT) ) {
                    alt86=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1138:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration );", 86, 1, input);
                
                    throw nvae;
                }
                }
                break;
            case KEY:
                {
                int LA86_2 = input.LA(2);
                
                if ( (LA86_2==LEFT_ROUND_BRACKET) ) {
                    alt86=2;
                }
                else if ( (LA86_2==AS||LA86_2==IDENT) ) {
                    alt86=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1138:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration );", 86, 2, input);
                
                    throw nvae;
                }
                }
                break;
            case VALUE:
                {
                int LA86_3 = input.LA(2);
                
                if ( (LA86_3==LEFT_ROUND_BRACKET) ) {
                    alt86=2;
                }
                else if ( (LA86_3==AS||LA86_3==IDENT) ) {
                    alt86=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1138:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration );", 86, 3, input);
                
                    throw nvae;
                }
                }
                break;
            case IN:
                {
                int LA86_4 = input.LA(2);
                
                if ( (LA86_4==LEFT_ROUND_BRACKET) ) {
                    alt86=3;
                }
                else if ( (LA86_4==AS||LA86_4==IDENT) ) {
                    alt86=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1138:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration );", 86, 4, input);
                
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
            case TYPE:
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
                alt86=1;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1138:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration );", 86, 0, input);
            
                throw nvae;
            }
            
            switch (alt86) {
                case 1 :
                    // JPQL.g:1140:7: identificationVariableDeclaration[varDecls]
                    {
                    pushFollow(FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration7205);
                    identificationVariableDeclaration(varDecls);
                    _fsp--;
                    if (failed) return ;
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1141:7: n= associationPathExpression ( AS )? i= IDENT
                    {
                    pushFollow(FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration7218);
                    n=associationPathExpression();
                    _fsp--;
                    if (failed) return ;
                    // JPQL.g:1141:37: ( AS )?
                    int alt85=2;
                    int LA85_0 = input.LA(1);
                    
                    if ( (LA85_0==AS) ) {
                        alt85=1;
                    }
                    switch (alt85) {
                        case 1 :
                            // JPQL.g:1141:38: AS
                            {
                            match(input,AS,FOLLOW_AS_in_subselectIdentificationVariableDeclaration7221); if (failed) return ;
                            
                            }
                            break;
                    
                    }

                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration7227); if (failed) return ;
                    if ( backtracking==0 ) {
                       
                                  varDecls.add(factory.newVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                       n, i.getText())); 
                              
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1146:7: n= collectionMemberDeclaration
                    {
                    pushFollow(FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration7249);
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
    // JPQL.g:1149:1: orderByClause returns [Object node] : o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* ;
    public final Object orderByClause() throws RecognitionException {
        orderByClause_stack.push(new orderByClause_scope());

        Object node = null;
    
        Token o=null;
        Object n = null;
        
    
         
            node = null; 
            ((orderByClause_scope)orderByClause_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g:1157:7: (o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* )
            // JPQL.g:1157:7: o= ORDER BY n= orderByItem ( COMMA n= orderByItem )*
            {
            o=(Token)input.LT(1);
            match(input,ORDER,FOLLOW_ORDER_in_orderByClause7282); if (failed) return node;
            match(input,BY,FOLLOW_BY_in_orderByClause7284); if (failed) return node;
            pushFollow(FOLLOW_orderByItem_in_orderByClause7298);
            n=orderByItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((orderByClause_scope)orderByClause_stack.peek()).items.add(n); 
            }
            // JPQL.g:1159:9: ( COMMA n= orderByItem )*
            loop87:
            do {
                int alt87=2;
                int LA87_0 = input.LA(1);
                
                if ( (LA87_0==COMMA) ) {
                    alt87=1;
                }
                
            
                switch (alt87) {
            	case 1 :
            	    // JPQL.g:1159:10: COMMA n= orderByItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_orderByClause7313); if (failed) return node;
            	    pushFollow(FOLLOW_orderByItem_in_orderByClause7319);
            	    n=orderByItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((orderByClause_scope)orderByClause_stack.peek()).items.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop87;
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
    // JPQL.g:1163:1: orderByItem returns [Object node] : n= stateFieldPathExpression (a= ASC | d= DESC | ) ;
    public final Object orderByItem() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token d=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1165:7: (n= stateFieldPathExpression (a= ASC | d= DESC | ) )
            // JPQL.g:1165:7: n= stateFieldPathExpression (a= ASC | d= DESC | )
            {
            pushFollow(FOLLOW_stateFieldPathExpression_in_orderByItem7365);
            n=stateFieldPathExpression();
            _fsp--;
            if (failed) return node;
            // JPQL.g:1166:9: (a= ASC | d= DESC | )
            int alt88=3;
            switch ( input.LA(1) ) {
            case ASC:
                {
                alt88=1;
                }
                break;
            case DESC:
                {
                alt88=2;
                }
                break;
            case EOF:
            case COMMA:
                {
                alt88=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1166:9: (a= ASC | d= DESC | )", 88, 0, input);
            
                throw nvae;
            }
            
            switch (alt88) {
                case 1 :
                    // JPQL.g:1166:11: a= ASC
                    {
                    a=(Token)input.LT(1);
                    match(input,ASC,FOLLOW_ASC_in_orderByItem7379); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAscOrdering(a.getLine(), a.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1168:11: d= DESC
                    {
                    d=(Token)input.LT(1);
                    match(input,DESC,FOLLOW_DESC_in_orderByItem7408); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newDescOrdering(d.getLine(), d.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1171:13: 
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
    // JPQL.g:1175:1: groupByClause returns [Object node] : g= GROUP BY n= groupByItem ( COMMA n= groupByItem )* ;
    public final Object groupByClause() throws RecognitionException {
        groupByClause_stack.push(new groupByClause_scope());

        Object node = null;
    
        Token g=null;
        Object n = null;
        
    
         
            node = null; 
            ((groupByClause_scope)groupByClause_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g:1183:7: (g= GROUP BY n= groupByItem ( COMMA n= groupByItem )* )
            // JPQL.g:1183:7: g= GROUP BY n= groupByItem ( COMMA n= groupByItem )*
            {
            g=(Token)input.LT(1);
            match(input,GROUP,FOLLOW_GROUP_in_groupByClause7488); if (failed) return node;
            match(input,BY,FOLLOW_BY_in_groupByClause7490); if (failed) return node;
            pushFollow(FOLLOW_groupByItem_in_groupByClause7504);
            n=groupByItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((groupByClause_scope)groupByClause_stack.peek()).items.add(n); 
            }
            // JPQL.g:1185:9: ( COMMA n= groupByItem )*
            loop89:
            do {
                int alt89=2;
                int LA89_0 = input.LA(1);
                
                if ( (LA89_0==COMMA) ) {
                    alt89=1;
                }
                
            
                switch (alt89) {
            	case 1 :
            	    // JPQL.g:1185:10: COMMA n= groupByItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_groupByClause7517); if (failed) return node;
            	    pushFollow(FOLLOW_groupByItem_in_groupByClause7523);
            	    n=groupByItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((groupByClause_scope)groupByClause_stack.peek()).items.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop89;
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
    // JPQL.g:1189:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccessOrTypeConstant );
    public final Object groupByItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1191:7: (n= stateFieldPathExpression | n= variableAccessOrTypeConstant )
            int alt90=2;
            int LA90_0 = input.LA(1);
            
            if ( (LA90_0==IDENT) ) {
                int LA90_1 = input.LA(2);
                
                if ( (LA90_1==DOT) ) {
                    alt90=1;
                }
                else if ( (LA90_1==EOF||LA90_1==HAVING||LA90_1==ORDER||LA90_1==COMMA||LA90_1==RIGHT_ROUND_BRACKET) ) {
                    alt90=2;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1189:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccessOrTypeConstant );", 90, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA90_0==KEY||LA90_0==VALUE) ) {
                alt90=1;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1189:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccessOrTypeConstant );", 90, 0, input);
            
                throw nvae;
            }
            switch (alt90) {
                case 1 :
                    // JPQL.g:1191:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_groupByItem7569);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1192:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_groupByItem7583);
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
    // JPQL.g:1195:1: havingClause returns [Object node] : h= HAVING n= conditionalExpression ;
    public final Object havingClause() throws RecognitionException {

        Object node = null;
    
        Token h=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g:1197:7: (h= HAVING n= conditionalExpression )
            // JPQL.g:1197:7: h= HAVING n= conditionalExpression
            {
            h=(Token)input.LT(1);
            match(input,HAVING,FOLLOW_HAVING_in_havingClause7613); if (failed) return node;
            if ( backtracking==0 ) {
               setAggregatesAllowed(true); 
            }
            pushFollow(FOLLOW_conditionalExpression_in_havingClause7630);
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
        // JPQL.g:602:7: ( LEFT_ROUND_BRACKET conditionalExpression )
        // JPQL.g:602:8: LEFT_ROUND_BRACKET conditionalExpression
        {
        match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_synpred13301); if (failed) return ;
        pushFollow(FOLLOW_conditionalExpression_in_synpred13303);
        conditionalExpression();
        _fsp--;
        if (failed) return ;
        
        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred2
    public final void synpred2_fragment() throws RecognitionException {   
        // JPQL.g:992:11: ( trimSpec trimChar FROM )
        // JPQL.g:992:13: trimSpec trimChar FROM
        {
        pushFollow(FOLLOW_trimSpec_in_synpred26209);
        trimSpec();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_trimChar_in_synpred26211);
        trimChar();
        _fsp--;
        if (failed) return ;
        match(input,FROM,FOLLOW_FROM_in_synpred26213); if (failed) return ;
        
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


 

    public static final BitSet FOLLOW_selectStatement_in_document673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_updateStatement_in_document687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_deleteStatement_in_document701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selectClause_in_selectStatement734 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_fromClause_in_selectStatement749 = new BitSet(new long[]{0x0010000060000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_whereClause_in_selectStatement764 = new BitSet(new long[]{0x0010000060000000L});
    public static final BitSet FOLLOW_groupByClause_in_selectStatement779 = new BitSet(new long[]{0x0010000040000000L});
    public static final BitSet FOLLOW_havingClause_in_selectStatement795 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_orderByClause_in_selectStatement810 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_selectStatement820 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_updateClause_in_updateStatement863 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_setClause_in_updateStatement878 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_whereClause_in_updateStatement892 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_updateStatement902 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPDATE_in_updateClause934 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_abstractSchemaName_in_updateClause940 = new BitSet(new long[]{0x0000000000000102L,0x0000000000000040L});
    public static final BitSet FOLLOW_AS_in_updateClause953 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_IDENT_in_updateClause961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_in_setClause1010 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_setAssignmentClause_in_setClause1016 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_COMMA_in_setClause1029 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_setAssignmentClause_in_setClause1035 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_setAssignmentTarget_in_setAssignmentClause1093 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_EQUALS_in_setAssignmentClause1097 = new BitSet(new long[]{0xDB0137480407C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_newValue_in_setAssignmentClause1103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_in_setAssignmentTarget1133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_setAssignmentTarget1148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_newValue1180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_newValue1194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_deleteClause_in_deleteStatement1238 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_whereClause_in_deleteStatement1251 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_deleteStatement1261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DELETE_in_deleteClause1294 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_FROM_in_deleteClause1296 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_abstractSchemaName_in_deleteClause1302 = new BitSet(new long[]{0x0000000000000102L,0x0000000000000040L});
    public static final BitSet FOLLOW_AS_in_deleteClause1315 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_IDENT_in_deleteClause1321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_selectClause1368 = new BitSet(new long[]{0x1002540800A08400L,0x0000000000000050L});
    public static final BitSet FOLLOW_DISTINCT_in_selectClause1371 = new BitSet(new long[]{0x1002540800808400L,0x0000000000000050L});
    public static final BitSet FOLLOW_selectExpression_in_selectClause1387 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_COMMA_in_selectClause1399 = new BitSet(new long[]{0x1002540800808400L,0x0000000000000050L});
    public static final BitSet FOLLOW_selectExpression_in_selectClause1405 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_selectExpression1451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_selectExpression1465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OBJECT_in_selectExpression1475 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1477 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_selectExpression1483 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constructorExpression_in_selectExpression1500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntryExpression_in_selectExpression1515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENTRY_in_mapEntryExpression1547 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mapEntryExpression1549 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_mapEntryExpression1555 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mapEntryExpression1557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_pathExprOrVariableAccess1589 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_DOT_in_pathExprOrVariableAccess1604 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExprOrVariableAccess1610 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1666 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KEY_in_qualifiedIdentificationVariable1680 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1682 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1688 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VALUE_in_qualifiedIdentificationVariable1705 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1707 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1713 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AVG_in_aggregateExpression1748 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1750 = new BitSet(new long[]{0x0000000800200000L,0x0000000000000050L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1753 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000050L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression1771 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MAX_in_aggregateExpression1794 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1796 = new BitSet(new long[]{0x0000000800200000L,0x0000000000000050L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1799 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000050L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression1818 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1820 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MIN_in_aggregateExpression1840 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1842 = new BitSet(new long[]{0x0000000800200000L,0x0000000000000050L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1845 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000050L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression1863 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUM_in_aggregateExpression1885 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1887 = new BitSet(new long[]{0x0000000800200000L,0x0000000000000050L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1890 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000050L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression1908 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COUNT_in_aggregateExpression1930 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1932 = new BitSet(new long[]{0x0000000800200000L,0x0000000000000050L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1935 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000050L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_aggregateExpression1953 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1955 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_constructorExpression1998 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_constructorName_in_constructorExpression2004 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression2014 = new BitSet(new long[]{0x1000140800008400L,0x0000000000000050L});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression2029 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000480L});
    public static final BitSet FOLLOW_COMMA_in_constructorExpression2044 = new BitSet(new long[]{0x1000140800008400L,0x0000000000000050L});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression2050 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000480L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression2065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_constructorName2106 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_DOT_in_constructorName2120 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_IDENT_in_constructorName2124 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_constructorItem2168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_constructorItem2182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_fromClause2215 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2217 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_COMMA_in_fromClause2229 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2234 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_fromClause2259 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2325 = new BitSet(new long[]{0x0000002500000002L});
    public static final BitSet FOLLOW_join_in_identificationVariableDeclaration2344 = new BitSet(new long[]{0x0000002500000002L});
    public static final BitSet FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2379 = new BitSet(new long[]{0x0000000000000100L,0x0000000000000040L});
    public static final BitSet FOLLOW_AS_in_rangeVariableDeclaration2382 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_IDENT_in_rangeVariableDeclaration2388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_joinSpec_in_join2471 = new BitSet(new long[]{0x0000000008000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2485 = new BitSet(new long[]{0x0000000000000100L,0x0000000000000040L});
    public static final BitSet FOLLOW_AS_in_join2488 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_IDENT_in_join2494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FETCH_in_join2516 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_in_joinSpec2568 = new BitSet(new long[]{0x0020000400000000L});
    public static final BitSet FOLLOW_OUTER_in_joinSpec2571 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_INNER_in_joinSpec2580 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_JOIN_in_joinSpec2586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_collectionMemberDeclaration2614 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2616 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000050L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2622 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2624 = new BitSet(new long[]{0x0000000000000100L,0x0000000000000040L});
    public static final BitSet FOLLOW_AS_in_collectionMemberDeclaration2634 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_IDENT_in_collectionMemberDeclaration2640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_collectionValuedPathExpression2678 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_associationPathExpression2710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_joinAssociationPathExpression2742 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_DOT_in_joinAssociationPathExpression2746 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_joinAssociationPathExpression2752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_singleValuedPathExpression2792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_stateFieldPathExpression2824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_pathExpression2856 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000800L});
    public static final BitSet FOLLOW_DOT_in_pathExpression2871 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExpression2877 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_IDENT_in_variableAccessOrTypeConstant2973 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_whereClause3011 = new BitSet(new long[]{0xDB00B7480607C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_conditionalExpression_in_whereClause3017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression3059 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_OR_in_conditionalExpression3074 = new BitSet(new long[]{0xDB00B7480607C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression3080 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm3135 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_AND_in_conditionalTerm3150 = new BitSet(new long[]{0xDB00B7480607C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm3156 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_NOT_in_conditionalFactor3211 = new BitSet(new long[]{0xDB0037480607C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_conditionalPrimary_in_conditionalFactor3230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_existsExpression_in_conditionalFactor3259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3316 = new BitSet(new long[]{0xDB00B7480607C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalPrimary3322 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleConditionalExpression_in_conditionalPrimary3338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_simpleConditionalExpression3370 = new BitSet(new long[]{0x0000888280000800L,0x000000000001F100L});
    public static final BitSet FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3434 = new BitSet(new long[]{0x0000088080000800L});
    public static final BitSet FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IS_in_simpleConditionalExpressionRemainder3453 = new BitSet(new long[]{0x0001800000400000L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3458 = new BitSet(new long[]{0x0001000000400000L});
    public static final BitSet FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_betweenExpression_in_conditionWithNotExpression3501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_likeExpression_in_conditionWithNotExpression3516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inExpression_in_conditionWithNotExpression3530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullComparisonExpression_in_isExpression3579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_emptyCollectionComparisonExpression_in_isExpression3594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BETWEEN_in_betweenExpression3627 = new BitSet(new long[]{0xDB0037480407C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_arithmeticExpression_in_betweenExpression3641 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_AND_in_betweenExpression3643 = new BitSet(new long[]{0xDB0037480407C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_arithmeticExpression_in_betweenExpression3649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_inExpression3695 = new BitSet(new long[]{0x0000000000000000L,0x0000000018000000L});
    public static final BitSet FOLLOW_inputParameter_in_inExpression3701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_inExpression3728 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_inExpression3738 = new BitSet(new long[]{0x0040000000000000L,0x000000001FE00040L});
    public static final BitSet FOLLOW_inItem_in_inExpression3754 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000480L});
    public static final BitSet FOLLOW_COMMA_in_inExpression3772 = new BitSet(new long[]{0x0000000000000000L,0x000000001FE00040L});
    public static final BitSet FOLLOW_inItem_in_inExpression3778 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000480L});
    public static final BitSet FOLLOW_subquery_in_inExpression3813 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression3847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_inItem3877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_inItem3891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_inItem3905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_inItem3919 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LIKE_in_likeExpression3951 = new BitSet(new long[]{0x0000000000000000L,0x000000001E000000L});
    public static final BitSet FOLLOW_likeValue_in_likeExpression3957 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_escape_in_likeExpression3972 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ESCAPE_in_escape4012 = new BitSet(new long[]{0x0000000000000000L,0x000000001E000000L});
    public static final BitSet FOLLOW_likeValue_in_escape4018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_likeValue4058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_likeValue4072 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_nullComparisonExpression4105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EMPTY_in_emptyCollectionComparisonExpression4146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MEMBER_in_collectionMemberExpression4187 = new BitSet(new long[]{0x0004000800000000L,0x0000000000000050L});
    public static final BitSet FOLLOW_OF_in_collectionMemberExpression4190 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000050L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression4198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_existsExpression4238 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4240 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_subquery_in_existsExpression4246 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_comparisonExpression4288 = new BitSet(new long[]{0xDF0037480407C4B0L,0x000000001FE60259L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4315 = new BitSet(new long[]{0xDF0037480407C4B0L,0x000000001FE60259L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_in_comparisonExpression4342 = new BitSet(new long[]{0xDF0037480407C4B0L,0x000000001FE60259L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4369 = new BitSet(new long[]{0xDF0037480407C4B0L,0x000000001FE60259L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_in_comparisonExpression4396 = new BitSet(new long[]{0xDF0037480407C4B0L,0x000000001FE60259L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4423 = new BitSet(new long[]{0xDF0037480407C4B0L,0x000000001FE60259L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4429 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4526 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_subquery_in_arithmeticExpression4532 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4566 = new BitSet(new long[]{0x0000000000000002L,0x0000000000060000L});
    public static final BitSet FOLLOW_PLUS_in_simpleArithmeticExpression4582 = new BitSet(new long[]{0xDB0037480407C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4588 = new BitSet(new long[]{0x0000000000000002L,0x0000000000060000L});
    public static final BitSet FOLLOW_MINUS_in_simpleArithmeticExpression4617 = new BitSet(new long[]{0xDB0037480407C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4623 = new BitSet(new long[]{0x0000000000000002L,0x0000000000060000L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4680 = new BitSet(new long[]{0x0000000000000002L,0x0000000000180000L});
    public static final BitSet FOLLOW_MULTIPLY_in_arithmeticTerm4696 = new BitSet(new long[]{0xDB0037480407C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4702 = new BitSet(new long[]{0x0000000000000002L,0x0000000000180000L});
    public static final BitSet FOLLOW_DIVIDE_in_arithmeticTerm4731 = new BitSet(new long[]{0xDB0037480407C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4737 = new BitSet(new long[]{0x0000000000000002L,0x0000000000180000L});
    public static final BitSet FOLLOW_PLUS_in_arithmeticFactor4791 = new BitSet(new long[]{0xDB0037480407C410L,0x000000001FE00259L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor4798 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_arithmeticFactor4820 = new BitSet(new long[]{0xDB0037480407C410L,0x000000001FE00259L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor4826 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor4850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_arithmeticPrimary4884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_arithmeticPrimary4898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningNumerics_in_arithmeticPrimary4912 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningDatetime_in_arithmeticPrimary4926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_arithmeticPrimary4940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_arithmeticPrimary4954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_arithmeticPrimary4968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_arithmeticPrimary4982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_arithmeticPrimary4996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entityTypeExpression_in_arithmeticPrimary5010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary5020 = new BitSet(new long[]{0xDB0037480407C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary5026 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary5028 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALL_in_anyOrAllExpression5058 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5060 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5066 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5068 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_anyOrAllExpression5088 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5090 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5096 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SOME_in_anyOrAllExpression5118 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5120 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5126 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDiscriminator_in_entityTypeExpression5168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_typeDiscriminator5201 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5203 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000050L});
    public static final BitSet FOLLOW_variableOrSingleValuedPath_in_typeDiscriminator5209 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_typeDiscriminator5226 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5228 = new BitSet(new long[]{0x0000000000000000L,0x0000000018000000L});
    public static final BitSet FOLLOW_inputParameter_in_typeDiscriminator5234 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5236 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleValuedPathExpression_in_variableOrSingleValuedPath5272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_variableOrSingleValuedPath5286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_stringPrimary5318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_stringPrimary5332 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_stringPrimary5346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_stringPrimary5360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_literal5394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_literal5408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_literal5422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_LITERAL_in_literalNumeric5452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LONG_LITERAL_in_literalNumeric5468 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_LITERAL_in_literalNumeric5489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_LITERAL_in_literalNumeric5509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_literalBoolean5547 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_literalBoolean5569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString5608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString5629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_POSITIONAL_PARAM_in_inputParameter5667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_PARAM_in_inputParameter5687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_abs_in_functionsReturningNumerics5727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_length_in_functionsReturningNumerics5741 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mod_in_functionsReturningNumerics5755 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sqrt_in_functionsReturningNumerics5769 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_locate_in_functionsReturningNumerics5783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_size_in_functionsReturningNumerics5797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_DATE_in_functionsReturningDatetime5827 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIME_in_functionsReturningDatetime5848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime5868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_concat_in_functionsReturningStrings5908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_substring_in_functionsReturningStrings5922 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trim_in_functionsReturningStrings5936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_upper_in_functionsReturningStrings5950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lower_in_functionsReturningStrings5964 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONCAT_in_concat5995 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_concat6006 = new BitSet(new long[]{0x4800020800004000L,0x000000001E000058L});
    public static final BitSet FOLLOW_stringPrimary_in_concat6021 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_COMMA_in_concat6023 = new BitSet(new long[]{0x4800020800004000L,0x000000001E000058L});
    public static final BitSet FOLLOW_stringPrimary_in_concat6029 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_concat6039 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBSTRING_in_substring6077 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_substring6090 = new BitSet(new long[]{0x4800020800004000L,0x000000001E000058L});
    public static final BitSet FOLLOW_stringPrimary_in_substring6104 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_COMMA_in_substring6106 = new BitSet(new long[]{0xDB0037480407C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_substring6120 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_COMMA_in_substring6122 = new BitSet(new long[]{0xDB0037480407C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_substring6136 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_substring6146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRIM_in_trim6184 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_trim6194 = new BitSet(new long[]{0x6800021810005000L,0x000000001E000058L});
    public static final BitSet FOLLOW_trimSpec_in_trim6222 = new BitSet(new long[]{0x0000000010000000L,0x000000001E000000L});
    public static final BitSet FOLLOW_trimChar_in_trim6228 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_FROM_in_trim6230 = new BitSet(new long[]{0x4800020800004000L,0x000000001E000058L});
    public static final BitSet FOLLOW_stringPrimary_in_trim6248 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_trim6258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEADING_in_trimSpec6294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRAILING_in_trimSpec6312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOTH_in_trimSpec6330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_trimChar6377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_trimChar6391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPPER_in_upper6428 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_upper6430 = new BitSet(new long[]{0x4800020800004000L,0x000000001E000058L});
    public static final BitSet FOLLOW_stringPrimary_in_upper6436 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_upper6438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOWER_in_lower6476 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_lower6478 = new BitSet(new long[]{0x4800020800004000L,0x000000001E000058L});
    public static final BitSet FOLLOW_stringPrimary_in_lower6484 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_lower6486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ABS_in_abs6525 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_abs6527 = new BitSet(new long[]{0xDB0037480407C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_abs6533 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_abs6535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LENGTH_in_length6573 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_length6575 = new BitSet(new long[]{0x4800020800004000L,0x000000001E000058L});
    public static final BitSet FOLLOW_stringPrimary_in_length6581 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_length6583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCATE_in_locate6621 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_locate6631 = new BitSet(new long[]{0x4800020800004000L,0x000000001E000058L});
    public static final BitSet FOLLOW_stringPrimary_in_locate6646 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_COMMA_in_locate6648 = new BitSet(new long[]{0x4800020800004000L,0x000000001E000058L});
    public static final BitSet FOLLOW_stringPrimary_in_locate6654 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000480L});
    public static final BitSet FOLLOW_COMMA_in_locate6666 = new BitSet(new long[]{0xDB0037480407C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_locate6672 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_locate6685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIZE_in_size6723 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_size6734 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000050L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_size6740 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_size6742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_mod6780 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mod6782 = new BitSet(new long[]{0xDB0037480407C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_mod6796 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_COMMA_in_mod6798 = new BitSet(new long[]{0xDB0037480407C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_mod6813 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mod6823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQRT_in_sqrt6861 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000200L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_sqrt6872 = new BitSet(new long[]{0xDB0037480407C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_sqrt6878 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt6880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleSelectClause_in_subquery6921 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_subqueryFromClause_in_subquery6936 = new BitSet(new long[]{0x0000000060000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_whereClause_in_subquery6951 = new BitSet(new long[]{0x0000000060000002L});
    public static final BitSet FOLLOW_groupByClause_in_subquery6966 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_havingClause_in_subquery6982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_simpleSelectClause7025 = new BitSet(new long[]{0x1000140800208400L,0x0000000000000050L});
    public static final BitSet FOLLOW_DISTINCT_in_simpleSelectClause7028 = new BitSet(new long[]{0x1000140800008400L,0x0000000000000050L});
    public static final BitSet FOLLOW_simpleSelectExpression_in_simpleSelectClause7044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleValuedPathExpression_in_simpleSelectExpression7084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_simpleSelectExpression7099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_simpleSelectExpression7114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_subqueryFromClause7149 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause7151 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_COMMA_in_subqueryFromClause7165 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000007FFFFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause7167 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration7205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration7218 = new BitSet(new long[]{0x0000000000000100L,0x0000000000000040L});
    public static final BitSet FOLLOW_AS_in_subselectIdentificationVariableDeclaration7221 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration7227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration7249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_in_orderByClause7282 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_orderByClause7284 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000050L});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause7298 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_COMMA_in_orderByClause7313 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000050L});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause7319 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_orderByItem7365 = new BitSet(new long[]{0x0000000000080202L});
    public static final BitSet FOLLOW_ASC_in_orderByItem7379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_in_orderByItem7408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_groupByClause7488 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_groupByClause7490 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000050L});
    public static final BitSet FOLLOW_groupByItem_in_groupByClause7504 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_COMMA_in_groupByClause7517 = new BitSet(new long[]{0x0000000800000000L,0x0000000000000050L});
    public static final BitSet FOLLOW_groupByItem_in_groupByClause7523 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_groupByItem7569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_groupByItem7583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HAVING_in_havingClause7613 = new BitSet(new long[]{0xDB00B7480607C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_conditionalExpression_in_havingClause7630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_synpred13301 = new BitSet(new long[]{0xDB00B7480607C410L,0x000000001FE60259L});
    public static final BitSet FOLLOW_conditionalExpression_in_synpred13303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trimSpec_in_synpred26209 = new BitSet(new long[]{0x0000000010000000L,0x000000001E000000L});
    public static final BitSet FOLLOW_trimChar_in_synpred26211 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_FROM_in_synpred26213 = new BitSet(new long[]{0x0000000000000002L});

}