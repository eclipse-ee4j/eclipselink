// $ANTLR 3.0 JPQL.g3 2007-08-21 16:20:25

    package org.eclipse.persistence.internal.jpa.parsing.jpql.antlr;

    import java.util.List;
    import java.util.ArrayList;

    import static org.eclipse.persistence.internal.jpa.parsing.NodeFactory.*;
    import org.eclipse.persistence.exceptions.JPQLException;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
public class JPQLParser extends org.eclipse.persistence.internal.jpa.parsing.jpql.JPQLParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ABS", "ALL", "AND", "ANY", "AS", "ASC", "AVG", "BETWEEN", "BOTH", "BY", "CONCAT", "COUNT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "DESC", "DELETE", "DISTINCT", "EMPTY", "ESCAPE", "EXISTS", "FALSE", "FETCH", "FROM", "GROUP", "HAVING", "IN", "INNER", "IS", "JOIN", "LEADING", "LEFT", "LENGTH", "LIKE", "LOCATE", "LOWER", "MAX", "MEMBER", "MIN", "MOD", "NEW", "NOT", "NULL", "OBJECT", "OF", "OR", "ORDER", "OUTER", "SELECT", "SET", "SIZE", "SQRT", "SOME", "SUBSTRING", "SUM", "TRAILING", "TRIM", "TRUE", "UNKNOWN", "UPDATE", "UPPER", "WHERE", "IDENT", "COMMA", "EQUALS", "LEFT_ROUND_BRACKET", "RIGHT_ROUND_BRACKET", "DOT", "NOT_EQUAL_TO", "GREATER_THAN", "GREATER_THAN_EQUAL_TO", "LESS_THAN", "LESS_THAN_EQUAL_TO", "PLUS", "MINUS", "MULTIPLY", "DIVIDE", "NUM_INT", "NUM_LONG", "NUM_FLOAT", "NUM_DOUBLE", "STRING_LITERAL_DOUBLE_QUOTED", "STRING_LITERAL_SINGLE_QUOTED", "POSITIONAL_PARAM", "NAMED_PARAM", "HEX_DIGIT", "WS", "TEXTCHAR", "EXPONENT", "FLOAT_SUFFIX"
    };
    public static final int EXISTS=24;
    public static final int COMMA=67;
    public static final int GREATER_THAN=73;
    public static final int FETCH=26;
    public static final int CURRENT_TIMESTAMP=18;
    public static final int MINUS=78;
    public static final int AS=8;
    public static final int NAMED_PARAM=88;
    public static final int LESS_THAN=75;
    public static final int FALSE=25;
    public static final int DOT=71;
    public static final int ORDER=50;
    public static final int AND=6;
    public static final int TEXTCHAR=91;
    public static final int CONCAT=14;
    public static final int SELECT=52;
    public static final int BETWEEN=11;
    public static final int DESC=19;
    public static final int NUM_INT=81;
    public static final int BOTH=12;
    public static final int LESS_THAN_EQUAL_TO=76;
    public static final int PLUS=77;
    public static final int MEMBER=41;
    public static final int TRIM=60;
    public static final int MULTIPLY=79;
    public static final int DISTINCT=21;
    public static final int LOCATE=38;
    public static final int IDENT=66;
    public static final int WS=90;
    public static final int NEW=44;
    public static final int OF=48;
    public static final int LOWER=39;
    public static final int UPDATE=63;
    public static final int RIGHT_ROUND_BRACKET=70;
    public static final int POSITIONAL_PARAM=87;
    public static final int FLOAT_SUFFIX=93;
    public static final int ANY=7;
    public static final int NUM_FLOAT=83;
    public static final int EQUALS=68;
    public static final int COUNT=15;
    public static final int NULL=46;
    public static final int HAVING=29;
    public static final int ALL=5;
    public static final int SET=53;
    public static final int STRING_LITERAL_DOUBLE_QUOTED=85;
    public static final int TRUE=61;
    public static final int LEFT_ROUND_BRACKET=69;
    public static final int WHERE=65;
    public static final int UNKNOWN=62;
    public static final int LEADING=34;
    public static final int NUM_DOUBLE=84;
    public static final int GREATER_THAN_EQUAL_TO=74;
    public static final int INNER=31;
    public static final int MOD=43;
    public static final int OR=49;
    public static final int DIVIDE=80;
    public static final int BY=13;
    public static final int GROUP=28;
    public static final int ESCAPE=23;
    public static final int HEX_DIGIT=89;
    public static final int LEFT=35;
    public static final int TRAILING=59;
    public static final int JOIN=33;
    public static final int CURRENT_DATE=16;
    public static final int STRING_LITERAL_SINGLE_QUOTED=86;
    public static final int SUM=58;
    public static final int OUTER=51;
    public static final int FROM=27;
    public static final int DELETE=20;
    public static final int OBJECT=47;
    public static final int MAX=40;
    public static final int EMPTY=22;
    public static final int NUM_LONG=82;
    public static final int LENGTH=36;
    public static final int IS=32;
    public static final int SUBSTRING=57;
    public static final int CURRENT_TIME=17;
    public static final int MIN=42;
    public static final int ASC=9;
    public static final int SQRT=55;
    public static final int LIKE=37;
    public static final int IN=30;
    public static final int SOME=56;
    public static final int NOT_EQUAL_TO=72;
    public static final int ABS=4;
    public static final int EXPONENT=92;
    public static final int UPPER=64;
    public static final int EOF=-1;
    public static final int AVG=10;
    public static final int SIZE=54;
    public static final int NOT=45;
    
        public JPQLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[93+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "JPQL.g3"; }

    
        /** The root node of the parsed EJBQL query. */
        private Object root;
    
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
                throw new RuntimeException("could not validate abstract schema name " + token);
                //throw new NoViableAltException(token, getFilename());
            }
        }
    
        /** */
        protected void validateAttributeName(Token token) 
            throws RecognitionException {
            String text = token.getText();
            if (!isValidJavaIdentifier(token.getText())) {
                throw new RuntimeException("could not validate attribute name " + token);
                //throw new NoViableAltException(token, getFilename());
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
            return root;
        }
    /*
    }
    */


    
    // $ANTLR start document
    // JPQL.g3:194:1: document : (root= selectStatement | root= updateStatement | root= deleteStatement );
    public final void document() throws RecognitionException {
        Object root = null;
        
    
        try {
            // JPQL.g3:195:7: (root= selectStatement | root= updateStatement | root= deleteStatement )
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
                    new NoViableAltException("194:1: document : (root= selectStatement | root= updateStatement | root= deleteStatement );", 1, 0, input);
            
                throw nvae;
            }
            
            switch (alt1) {
                case 1 :
                    // JPQL.g3:195:7: root= selectStatement
                    {
                    pushFollow(FOLLOW_selectStatement_in_document650);
                    root=selectStatement();
                    _fsp--;
                    if (failed) return ;
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:196:7: root= updateStatement
                    {
                    pushFollow(FOLLOW_updateStatement_in_document662);
                    root=updateStatement();
                    _fsp--;
                    if (failed) return ;
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:197:7: root= deleteStatement
                    {
                    pushFollow(FOLLOW_deleteStatement_in_document674);
                    root=deleteStatement();
                    _fsp--;
                    if (failed) return ;
                    
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
    // JPQL.g3:200:1: selectStatement returns [Object node] : select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF ;
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
            // JPQL.g3:204:7: (select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF )
            // JPQL.g3:204:7: select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF
            {
            pushFollow(FOLLOW_selectClause_in_selectStatement705);
            select=selectClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_fromClause_in_selectStatement720);
            from=fromClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g3:206:7: (where= whereClause )?
            int alt2=2;
            int LA2_0 = input.LA(1);
            
            if ( (LA2_0==WHERE) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // JPQL.g3:206:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_selectStatement735);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g3:207:7: (groupBy= groupByClause )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            
            if ( (LA3_0==GROUP) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // JPQL.g3:207:8: groupBy= groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_selectStatement750);
                    groupBy=groupByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g3:208:7: (having= havingClause )?
            int alt4=2;
            int LA4_0 = input.LA(1);
            
            if ( (LA4_0==HAVING) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // JPQL.g3:208:8: having= havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_selectStatement766);
                    having=havingClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g3:209:7: (orderBy= orderByClause )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            
            if ( (LA5_0==ORDER) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // JPQL.g3:209:8: orderBy= orderByClause
                    {
                    pushFollow(FOLLOW_orderByClause_in_selectStatement781);
                    orderBy=orderByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_selectStatement791); if (failed) return node;
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
    // JPQL.g3:219:1: updateStatement returns [Object node] : update= updateClause set= setClause (where= whereClause )? EOF ;
    public final Object updateStatement() throws RecognitionException {

        Object node = null;
    
        Object update = null;

        Object set = null;

        Object where = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g3:223:7: (update= updateClause set= setClause (where= whereClause )? EOF )
            // JPQL.g3:223:7: update= updateClause set= setClause (where= whereClause )? EOF
            {
            pushFollow(FOLLOW_updateClause_in_updateStatement834);
            update=updateClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_setClause_in_updateStatement849);
            set=setClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g3:225:7: (where= whereClause )?
            int alt6=2;
            int LA6_0 = input.LA(1);
            
            if ( (LA6_0==WHERE) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // JPQL.g3:225:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_updateStatement863);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_updateStatement873); if (failed) return node;
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
    // JPQL.g3:229:1: updateClause returns [Object node] : u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )? ;
    public final Object updateClause() throws RecognitionException {

        Object node = null;
    
        Token u=null;
        Token ident=null;
        String schema = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g3:233:7: (u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )? )
            // JPQL.g3:233:7: u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )?
            {
            u=(Token)input.LT(1);
            match(input,UPDATE,FOLLOW_UPDATE_in_updateClause905); if (failed) return node;
            pushFollow(FOLLOW_abstractSchemaName_in_updateClause911);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // JPQL.g3:234:9: ( ( AS )? ident= IDENT )?
            int alt8=2;
            int LA8_0 = input.LA(1);
            
            if ( (LA8_0==AS||LA8_0==IDENT) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // JPQL.g3:234:10: ( AS )? ident= IDENT
                    {
                    // JPQL.g3:234:10: ( AS )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);
                    
                    if ( (LA7_0==AS) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // JPQL.g3:234:11: AS
                            {
                            match(input,AS,FOLLOW_AS_in_updateClause924); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    ident=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_updateClause932); if (failed) return node;
                    
                    }
                    break;
            
            }

            if ( backtracking==0 ) {
               
                          node = factory.newUpdateClause(u.getLine(), u.getCharPositionInLine(), 
                                                         schema, ident.getText()); 
                      
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
    // JPQL.g3:241:1: setClause returns [Object node] : t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )* ;
    public final Object setClause() throws RecognitionException {
        setClause_stack.push(new setClause_scope());

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         
            node = null; 
            ((setClause_scope)setClause_stack.peek()).assignments = new ArrayList();
    
        try {
            // JPQL.g3:249:7: (t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )* )
            // JPQL.g3:249:7: t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )*
            {
            t=(Token)input.LT(1);
            match(input,SET,FOLLOW_SET_in_setClause981); if (failed) return node;
            pushFollow(FOLLOW_setAssignmentClause_in_setClause987);
            n=setAssignmentClause();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((setClause_scope)setClause_stack.peek()).assignments.add(n); 
            }
            // JPQL.g3:250:9: ( COMMA n= setAssignmentClause )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);
                
                if ( (LA9_0==COMMA) ) {
                    alt9=1;
                }
                
            
                switch (alt9) {
            	case 1 :
            	    // JPQL.g3:250:10: COMMA n= setAssignmentClause
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_setClause1000); if (failed) return node;
            	    pushFollow(FOLLOW_setAssignmentClause_in_setClause1006);
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
    // JPQL.g3:254:1: setAssignmentClause returns [Object node] : target= setAssignmentTarget t= EQUALS value= newValue ;
    public final Object setAssignmentClause() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object target = null;

        Object value = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g3:262:7: (target= setAssignmentTarget t= EQUALS value= newValue )
            // JPQL.g3:262:7: target= setAssignmentTarget t= EQUALS value= newValue
            {
            pushFollow(FOLLOW_setAssignmentTarget_in_setAssignmentClause1064);
            target=setAssignmentTarget();
            _fsp--;
            if (failed) return node;
            t=(Token)input.LT(1);
            match(input,EQUALS,FOLLOW_EQUALS_in_setAssignmentClause1068); if (failed) return node;
            pushFollow(FOLLOW_newValue_in_setAssignmentClause1074);
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
    // JPQL.g3:265:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );
    public final Object setAssignmentTarget() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g3:269:7: (n= attribute | n= pathExpression )
            int alt10=2;
            int LA10_0 = input.LA(1);
            
            if ( (LA10_0==IDENT) ) {
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
                        new NoViableAltException("265:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 1, input);
                
                    throw nvae;
                }
            }
            else if ( ((LA10_0>=ABS && LA10_0<=WHERE)||(LA10_0>=COMMA && LA10_0<=FLOAT_SUFFIX)) ) {
                alt10=1;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("265:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 0, input);
            
                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // JPQL.g3:269:7: n= attribute
                    {
                    pushFollow(FOLLOW_attribute_in_setAssignmentTarget1104);
                    n=attribute();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:270:7: n= pathExpression
                    {
                    pushFollow(FOLLOW_pathExpression_in_setAssignmentTarget1119);
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
    // JPQL.g3:273:1: newValue returns [Object node] : (n= simpleArithmeticExpression | n1= NULL );
    public final Object newValue() throws RecognitionException {

        Object node = null;
    
        Token n1=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:275:7: (n= simpleArithmeticExpression | n1= NULL )
            int alt11=2;
            int LA11_0 = input.LA(1);
            
            if ( (LA11_0==ABS||LA11_0==AVG||(LA11_0>=CONCAT && LA11_0<=CURRENT_TIMESTAMP)||LA11_0==FALSE||LA11_0==LENGTH||(LA11_0>=LOCATE && LA11_0<=MAX)||(LA11_0>=MIN && LA11_0<=MOD)||(LA11_0>=SIZE && LA11_0<=SQRT)||(LA11_0>=SUBSTRING && LA11_0<=SUM)||(LA11_0>=TRIM && LA11_0<=TRUE)||LA11_0==UPPER||LA11_0==IDENT||LA11_0==LEFT_ROUND_BRACKET||(LA11_0>=PLUS && LA11_0<=MINUS)||(LA11_0>=NUM_INT && LA11_0<=NAMED_PARAM)) ) {
                alt11=1;
            }
            else if ( (LA11_0==NULL) ) {
                alt11=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("273:1: newValue returns [Object node] : (n= simpleArithmeticExpression | n1= NULL );", 11, 0, input);
            
                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // JPQL.g3:275:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_newValue1151);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:276:7: n1= NULL
                    {
                    n1=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_newValue1165); if (failed) return node;
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
    // JPQL.g3:282:1: deleteStatement returns [Object node] : delete= deleteClause (where= whereClause )? EOF ;
    public final Object deleteStatement() throws RecognitionException {

        Object node = null;
    
        Object delete = null;

        Object where = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g3:286:7: (delete= deleteClause (where= whereClause )? EOF )
            // JPQL.g3:286:7: delete= deleteClause (where= whereClause )? EOF
            {
            pushFollow(FOLLOW_deleteClause_in_deleteStatement1209);
            delete=deleteClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g3:287:7: (where= whereClause )?
            int alt12=2;
            int LA12_0 = input.LA(1);
            
            if ( (LA12_0==WHERE) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // JPQL.g3:287:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_deleteStatement1222);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_deleteStatement1232); if (failed) return node;
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
    // JPQL.g3:291:1: deleteClause returns [Object node] : t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )? ;
    public final Object deleteClause() throws RecognitionException {
        deleteClause_stack.push(new deleteClause_scope());

        Object node = null;
    
        Token t=null;
        Token ident=null;
        String schema = null;
        
    
         
            node = null; 
            ((deleteClause_scope)deleteClause_stack.peek()).variable = null;
    
        try {
            // JPQL.g3:299:7: (t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )? )
            // JPQL.g3:299:7: t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )?
            {
            t=(Token)input.LT(1);
            match(input,DELETE,FOLLOW_DELETE_in_deleteClause1265); if (failed) return node;
            match(input,FROM,FOLLOW_FROM_in_deleteClause1267); if (failed) return node;
            pushFollow(FOLLOW_abstractSchemaName_in_deleteClause1273);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // JPQL.g3:300:9: ( ( AS )? ident= IDENT )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            
            if ( (LA14_0==AS||LA14_0==IDENT) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // JPQL.g3:300:10: ( AS )? ident= IDENT
                    {
                    // JPQL.g3:300:10: ( AS )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);
                    
                    if ( (LA13_0==AS) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // JPQL.g3:300:11: AS
                            {
                            match(input,AS,FOLLOW_AS_in_deleteClause1286); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    ident=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_deleteClause1292); if (failed) return node;
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
    // JPQL.g3:309:1: selectClause returns [Object node] : t= SELECT ( DISTINCT )? n= selectExpression ( COMMA n= selectExpression )* ;
    public final Object selectClause() throws RecognitionException {
        selectClause_stack.push(new selectClause_scope());

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         
            node = null;
            ((selectClause_scope)selectClause_stack.peek()).distinct = false;
            ((selectClause_scope)selectClause_stack.peek()).exprs = new ArrayList();
    
        try {
            // JPQL.g3:319:7: (t= SELECT ( DISTINCT )? n= selectExpression ( COMMA n= selectExpression )* )
            // JPQL.g3:319:7: t= SELECT ( DISTINCT )? n= selectExpression ( COMMA n= selectExpression )*
            {
            t=(Token)input.LT(1);
            match(input,SELECT,FOLLOW_SELECT_in_selectClause1339); if (failed) return node;
            // JPQL.g3:319:16: ( DISTINCT )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            
            if ( (LA15_0==DISTINCT) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // JPQL.g3:319:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_selectClause1342); if (failed) return node;
                    if ( backtracking==0 ) {
                       ((selectClause_scope)selectClause_stack.peek()).distinct = true; 
                    }
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_selectExpression_in_selectClause1358);
            n=selectExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((selectClause_scope)selectClause_stack.peek()).exprs.add(n); 
            }
            // JPQL.g3:321:7: ( COMMA n= selectExpression )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);
                
                if ( (LA16_0==COMMA) ) {
                    alt16=1;
                }
                
            
                switch (alt16) {
            	case 1 :
            	    // JPQL.g3:321:9: COMMA n= selectExpression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_selectClause1370); if (failed) return node;
            	    pushFollow(FOLLOW_selectExpression_in_selectClause1376);
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
    // JPQL.g3:328:1: selectExpression returns [Object node] : (n= pathExprOrVariableAccess | n= aggregateExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET | n= constructorExpression );
    public final Object selectExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:330:7: (n= pathExprOrVariableAccess | n= aggregateExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET | n= constructorExpression )
            int alt17=4;
            switch ( input.LA(1) ) {
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
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("328:1: selectExpression returns [Object node] : (n= pathExprOrVariableAccess | n= aggregateExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET | n= constructorExpression );", 17, 0, input);
            
                throw nvae;
            }
            
            switch (alt17) {
                case 1 :
                    // JPQL.g3:330:7: n= pathExprOrVariableAccess
                    {
                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_selectExpression1422);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:331:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_selectExpression1436);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:332:7: OBJECT LEFT_ROUND_BRACKET n= variableAccess RIGHT_ROUND_BRACKET
                    {
                    match(input,OBJECT,FOLLOW_OBJECT_in_selectExpression1446); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1448); if (failed) return node;
                    pushFollow(FOLLOW_variableAccess_in_selectExpression1454);
                    n=variableAccess();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1456); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g3:333:7: n= constructorExpression
                    {
                    pushFollow(FOLLOW_constructorExpression_in_selectExpression1471);
                    n=constructorExpression();
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

    
    // $ANTLR start pathExprOrVariableAccess
    // JPQL.g3:336:1: pathExprOrVariableAccess returns [Object node] : n= variableAccess (d= DOT right= attribute )* ;
    public final Object pathExprOrVariableAccess() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g3:340:7: (n= variableAccess (d= DOT right= attribute )* )
            // JPQL.g3:340:7: n= variableAccess (d= DOT right= attribute )*
            {
            pushFollow(FOLLOW_variableAccess_in_pathExprOrVariableAccess1504);
            n=variableAccess();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g3:341:9: (d= DOT right= attribute )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);
                
                if ( (LA18_0==DOT) ) {
                    alt18=1;
                }
                
            
                switch (alt18) {
            	case 1 :
            	    // JPQL.g3:341:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pathExprOrVariableAccess1519); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_pathExprOrVariableAccess1525);
            	    right=attribute();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newDot(d.getLine(), d.getCharPositionInLine(), n, right); 
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

    protected static class aggregateExpression_scope {
        boolean distinct;
    }
    protected Stack aggregateExpression_stack = new Stack();
    
    
    // $ANTLR start aggregateExpression
    // JPQL.g3:346:1: aggregateExpression returns [Object node] : (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET );
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
            // JPQL.g3:354:7: (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET )
            int alt24=5;
            switch ( input.LA(1) ) {
            case AVG:
                {
                alt24=1;
                }
                break;
            case MAX:
                {
                alt24=2;
                }
                break;
            case MIN:
                {
                alt24=3;
                }
                break;
            case SUM:
                {
                alt24=4;
                }
                break;
            case COUNT:
                {
                alt24=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("346:1: aggregateExpression returns [Object node] : (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET );", 24, 0, input);
            
                throw nvae;
            }
            
            switch (alt24) {
                case 1 :
                    // JPQL.g3:354:7: t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t1=(Token)input.LT(1);
                    match(input,AVG,FOLLOW_AVG_in_aggregateExpression1582); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1584); if (failed) return node;
                    // JPQL.g3:354:33: ( DISTINCT )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);
                    
                    if ( (LA19_0==DISTINCT) ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // JPQL.g3:354:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1587); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression1605);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1607); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAvg(t1.getLine(), t1.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:357:7: t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t2=(Token)input.LT(1);
                    match(input,MAX,FOLLOW_MAX_in_aggregateExpression1628); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1630); if (failed) return node;
                    // JPQL.g3:357:33: ( DISTINCT )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);
                    
                    if ( (LA20_0==DISTINCT) ) {
                        alt20=1;
                    }
                    switch (alt20) {
                        case 1 :
                            // JPQL.g3:357:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1633); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression1652);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1654); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newMax(t2.getLine(), t2.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:360:7: t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t3=(Token)input.LT(1);
                    match(input,MIN,FOLLOW_MIN_in_aggregateExpression1674); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1676); if (failed) return node;
                    // JPQL.g3:360:33: ( DISTINCT )?
                    int alt21=2;
                    int LA21_0 = input.LA(1);
                    
                    if ( (LA21_0==DISTINCT) ) {
                        alt21=1;
                    }
                    switch (alt21) {
                        case 1 :
                            // JPQL.g3:360:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1679); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression1697);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1699); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newMin(t3.getLine(), t3.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g3:363:7: t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t4=(Token)input.LT(1);
                    match(input,SUM,FOLLOW_SUM_in_aggregateExpression1719); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1721); if (failed) return node;
                    // JPQL.g3:363:33: ( DISTINCT )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);
                    
                    if ( (LA22_0==DISTINCT) ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // JPQL.g3:363:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1724); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression1742);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1744); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newSum(t4.getLine(), t4.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g3:366:7: t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET
                    {
                    t5=(Token)input.LT(1);
                    match(input,COUNT,FOLLOW_COUNT_in_aggregateExpression1764); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1766); if (failed) return node;
                    // JPQL.g3:366:35: ( DISTINCT )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);
                    
                    if ( (LA23_0==DISTINCT) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // JPQL.g3:366:36: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1769); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_aggregateExpression1787);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1789); if (failed) return node;
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
    // JPQL.g3:371:1: constructorExpression returns [Object node] : t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET ;
    public final Object constructorExpression() throws RecognitionException {
        constructorExpression_stack.push(new constructorExpression_scope());

        Object node = null;
    
        Token t=null;
        String className = null;

        Object n = null;
        
    
         
            node = null;
            ((constructorExpression_scope)constructorExpression_stack.peek()).args = new ArrayList();
    
        try {
            // JPQL.g3:379:7: (t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET )
            // JPQL.g3:379:7: t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,NEW,FOLLOW_NEW_in_constructorExpression1832); if (failed) return node;
            pushFollow(FOLLOW_constructorName_in_constructorExpression1838);
            className=constructorName();
            _fsp--;
            if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression1848); if (failed) return node;
            pushFollow(FOLLOW_constructorItem_in_constructorExpression1863);
            n=constructorItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((constructorExpression_scope)constructorExpression_stack.peek()).args.add(n); 
            }
            // JPQL.g3:382:9: ( COMMA n= constructorItem )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);
                
                if ( (LA25_0==COMMA) ) {
                    alt25=1;
                }
                
            
                switch (alt25) {
            	case 1 :
            	    // JPQL.g3:382:11: COMMA n= constructorItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_constructorExpression1878); if (failed) return node;
            	    pushFollow(FOLLOW_constructorItem_in_constructorExpression1884);
            	    n=constructorItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((constructorExpression_scope)constructorExpression_stack.peek()).args.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop25;
                }
            } while (true);

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression1899); if (failed) return node;
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
    // JPQL.g3:390:1: constructorName returns [String className] : i1= IDENT ( DOT i2= IDENT )* ;
    public final String constructorName() throws RecognitionException {
        constructorName_stack.push(new constructorName_scope());

        String className = null;
    
        Token i1=null;
        Token i2=null;
    
         
            className = null;
            ((constructorName_scope)constructorName_stack.peek()).buf = new StringBuffer(); 
    
        try {
            // JPQL.g3:398:7: (i1= IDENT ( DOT i2= IDENT )* )
            // JPQL.g3:398:7: i1= IDENT ( DOT i2= IDENT )*
            {
            i1=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_constructorName1940); if (failed) return className;
            if ( backtracking==0 ) {
               ((constructorName_scope)constructorName_stack.peek()).buf.append(i1.getText()); 
            }
            // JPQL.g3:399:9: ( DOT i2= IDENT )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);
                
                if ( (LA26_0==DOT) ) {
                    alt26=1;
                }
                
            
                switch (alt26) {
            	case 1 :
            	    // JPQL.g3:399:11: DOT i2= IDENT
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_constructorName1954); if (failed) return className;
            	    i2=(Token)input.LT(1);
            	    match(input,IDENT,FOLLOW_IDENT_in_constructorName1958); if (failed) return className;
            	    if ( backtracking==0 ) {
            	       ((constructorName_scope)constructorName_stack.peek()).buf.append('.').append(i2.getText()); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop26;
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
    // JPQL.g3:403:1: constructorItem returns [Object node] : (n= pathExprOrVariableAccess | n= aggregateExpression );
    public final Object constructorItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:405:7: (n= pathExprOrVariableAccess | n= aggregateExpression )
            int alt27=2;
            int LA27_0 = input.LA(1);
            
            if ( (LA27_0==IDENT) ) {
                alt27=1;
            }
            else if ( (LA27_0==AVG||LA27_0==COUNT||LA27_0==MAX||LA27_0==MIN||LA27_0==SUM) ) {
                alt27=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("403:1: constructorItem returns [Object node] : (n= pathExprOrVariableAccess | n= aggregateExpression );", 27, 0, input);
            
                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // JPQL.g3:405:7: n= pathExprOrVariableAccess
                    {
                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_constructorItem2002);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:406:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_constructorItem2016);
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
    // JPQL.g3:409:1: fromClause returns [Object node] : t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )* ;
    public final Object fromClause() throws RecognitionException {
        fromClause_stack.push(new fromClause_scope());

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         
            node = null; 
            ((fromClause_scope)fromClause_stack.peek()).varDecls = new ArrayList();
    
        try {
            // JPQL.g3:417:7: (t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )* )
            // JPQL.g3:417:7: t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )*
            {
            t=(Token)input.LT(1);
            match(input,FROM,FOLLOW_FROM_in_fromClause2049); if (failed) return node;
            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2051);
            identificationVariableDeclaration(((fromClause_scope)fromClause_stack.peek()).varDecls);
            _fsp--;
            if (failed) return node;
            // JPQL.g3:418:9: ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);
                
                if ( (LA29_0==COMMA) ) {
                    alt29=1;
                }
                
            
                switch (alt29) {
            	case 1 :
            	    // JPQL.g3:418:10: COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_fromClause2063); if (failed) return node;
            	    // JPQL.g3:418:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )
            	    int alt28=2;
            	    int LA28_0 = input.LA(1);
            	    
            	    if ( (LA28_0==IN) ) {
            	        int LA28_1 = input.LA(2);
            	        
            	        if ( (LA28_1==LEFT_ROUND_BRACKET) ) {
            	            alt28=2;
            	        }
            	        else if ( (LA28_1==AS||LA28_1==IDENT) ) {
            	            alt28=1;
            	        }
            	        else {
            	            if (backtracking>0) {failed=true; return node;}
            	            NoViableAltException nvae =
            	                new NoViableAltException("418:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )", 28, 1, input);
            	        
            	            throw nvae;
            	        }
            	    }
            	    else if ( ((LA28_0>=ABS && LA28_0<=HAVING)||(LA28_0>=INNER && LA28_0<=FLOAT_SUFFIX)) ) {
            	        alt28=1;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return node;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("418:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )", 28, 0, input);
            	    
            	        throw nvae;
            	    }
            	    switch (alt28) {
            	        case 1 :
            	            // JPQL.g3:418:19: identificationVariableDeclaration[$fromClause::varDecls]
            	            {
            	            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2068);
            	            identificationVariableDeclaration(((fromClause_scope)fromClause_stack.peek()).varDecls);
            	            _fsp--;
            	            if (failed) return node;
            	            
            	            }
            	            break;
            	        case 2 :
            	            // JPQL.g3:419:19: n= collectionMemberDeclaration
            	            {
            	            pushFollow(FOLLOW_collectionMemberDeclaration_in_fromClause2093);
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
            	    break loop29;
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
    // JPQL.g3:425:1: identificationVariableDeclaration[List varDecls] : node= rangeVariableDeclaration (node= join )* ;
    public final void identificationVariableDeclaration(List varDecls) throws RecognitionException {
        Object node = null;
        
    
        try {
            // JPQL.g3:426:7: (node= rangeVariableDeclaration (node= join )* )
            // JPQL.g3:426:7: node= rangeVariableDeclaration (node= join )*
            {
            pushFollow(FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2159);
            node=rangeVariableDeclaration();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               varDecls.add(node); 
            }
            // JPQL.g3:427:9: (node= join )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);
                
                if ( (LA30_0==INNER||LA30_0==JOIN||LA30_0==LEFT) ) {
                    alt30=1;
                }
                
            
                switch (alt30) {
            	case 1 :
            	    // JPQL.g3:427:11: node= join
            	    {
            	    pushFollow(FOLLOW_join_in_identificationVariableDeclaration2178);
            	    node=join();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       varDecls.add(node); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop30;
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
    // JPQL.g3:430:1: rangeVariableDeclaration returns [Object node] : schema= abstractSchemaName ( AS )? i= IDENT ;
    public final Object rangeVariableDeclaration() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        String schema = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g3:434:7: (schema= abstractSchemaName ( AS )? i= IDENT )
            // JPQL.g3:434:7: schema= abstractSchemaName ( AS )? i= IDENT
            {
            pushFollow(FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2213);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // JPQL.g3:434:35: ( AS )?
            int alt31=2;
            int LA31_0 = input.LA(1);
            
            if ( (LA31_0==AS) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // JPQL.g3:434:36: AS
                    {
                    match(input,AS,FOLLOW_AS_in_rangeVariableDeclaration2216); if (failed) return node;
                    
                    }
                    break;
            
            }

            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_rangeVariableDeclaration2222); if (failed) return node;
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
    // JPQL.g3:445:1: abstractSchemaName returns [String schema] : ident= . ;
    public final String abstractSchemaName() throws RecognitionException {

        String schema = null;
    
        Token ident=null;
    
         schema = null; 
        try {
            // JPQL.g3:447:7: (ident= . )
            // JPQL.g3:447:7: ident= .
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
    // JPQL.g3:454:1: join returns [Object node] : outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression ) ;
    public final Object join() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        Token t=null;
        boolean outerJoin = false;

        Object n = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g3:458:7: (outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression ) )
            // JPQL.g3:458:7: outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )
            {
            pushFollow(FOLLOW_joinSpec_in_join2305);
            outerJoin=joinSpec();
            _fsp--;
            if (failed) return node;
            // JPQL.g3:459:7: (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )
            int alt33=2;
            int LA33_0 = input.LA(1);
            
            if ( (LA33_0==IDENT) ) {
                alt33=1;
            }
            else if ( (LA33_0==FETCH) ) {
                alt33=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("459:7: (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )", 33, 0, input);
            
                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // JPQL.g3:459:9: n= joinAssociationPathExpression ( AS )? i= IDENT
                    {
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2319);
                    n=joinAssociationPathExpression();
                    _fsp--;
                    if (failed) return node;
                    // JPQL.g3:459:43: ( AS )?
                    int alt32=2;
                    int LA32_0 = input.LA(1);
                    
                    if ( (LA32_0==AS) ) {
                        alt32=1;
                    }
                    switch (alt32) {
                        case 1 :
                            // JPQL.g3:459:44: AS
                            {
                            match(input,AS,FOLLOW_AS_in_join2322); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_join2328); if (failed) return node;
                    if ( backtracking==0 ) {
                      
                                  node = factory.newJoinVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                     outerJoin, n, i.getText()); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:464:9: t= FETCH n= joinAssociationPathExpression
                    {
                    t=(Token)input.LT(1);
                    match(input,FETCH,FOLLOW_FETCH_in_join2350); if (failed) return node;
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2356);
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
    // JPQL.g3:471:1: joinSpec returns [boolean outer] : ( LEFT ( OUTER )? | INNER )? JOIN ;
    public final boolean joinSpec() throws RecognitionException {

        boolean outer = false;
    
         outer = false; 
        try {
            // JPQL.g3:473:7: ( ( LEFT ( OUTER )? | INNER )? JOIN )
            // JPQL.g3:473:7: ( LEFT ( OUTER )? | INNER )? JOIN
            {
            // JPQL.g3:473:7: ( LEFT ( OUTER )? | INNER )?
            int alt35=3;
            int LA35_0 = input.LA(1);
            
            if ( (LA35_0==LEFT) ) {
                alt35=1;
            }
            else if ( (LA35_0==INNER) ) {
                alt35=2;
            }
            switch (alt35) {
                case 1 :
                    // JPQL.g3:473:8: LEFT ( OUTER )?
                    {
                    match(input,LEFT,FOLLOW_LEFT_in_joinSpec2402); if (failed) return outer;
                    // JPQL.g3:473:13: ( OUTER )?
                    int alt34=2;
                    int LA34_0 = input.LA(1);
                    
                    if ( (LA34_0==OUTER) ) {
                        alt34=1;
                    }
                    switch (alt34) {
                        case 1 :
                            // JPQL.g3:473:14: OUTER
                            {
                            match(input,OUTER,FOLLOW_OUTER_in_joinSpec2405); if (failed) return outer;
                            
                            }
                            break;
                    
                    }

                    if ( backtracking==0 ) {
                       outer = true; 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:473:44: INNER
                    {
                    match(input,INNER,FOLLOW_INNER_in_joinSpec2414); if (failed) return outer;
                    
                    }
                    break;
            
            }

            match(input,JOIN,FOLLOW_JOIN_in_joinSpec2420); if (failed) return outer;
            
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
    // JPQL.g3:476:1: collectionMemberDeclaration returns [Object node] : t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT ;
    public final Object collectionMemberDeclaration() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Token i=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:478:7: (t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT )
            // JPQL.g3:478:7: t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT
            {
            t=(Token)input.LT(1);
            match(input,IN,FOLLOW_IN_in_collectionMemberDeclaration2448); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2450); if (failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2456);
            n=collectionValuedPathExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2458); if (failed) return node;
            // JPQL.g3:479:7: ( AS )?
            int alt36=2;
            int LA36_0 = input.LA(1);
            
            if ( (LA36_0==AS) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // JPQL.g3:479:8: AS
                    {
                    match(input,AS,FOLLOW_AS_in_collectionMemberDeclaration2468); if (failed) return node;
                    
                    }
                    break;
            
            }

            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_collectionMemberDeclaration2474); if (failed) return node;
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
    // JPQL.g3:486:1: collectionValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object collectionValuedPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:488:7: (n= pathExpression )
            // JPQL.g3:488:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_collectionValuedPathExpression2512);
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
    // JPQL.g3:491:1: associationPathExpression returns [Object node] : n= pathExpression ;
    public final Object associationPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:493:7: (n= pathExpression )
            // JPQL.g3:493:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_associationPathExpression2544);
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
    // JPQL.g3:496:1: joinAssociationPathExpression returns [Object node] : left= variableAccess d= DOT right= attribute ;
    public final Object joinAssociationPathExpression() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object left = null;

        Object right = null;
        
    
        
            node = null; 
    
        try {
            // JPQL.g3:500:7: (left= variableAccess d= DOT right= attribute )
            // JPQL.g3:500:7: left= variableAccess d= DOT right= attribute
            {
            pushFollow(FOLLOW_variableAccess_in_joinAssociationPathExpression2576);
            left=variableAccess();
            _fsp--;
            if (failed) return node;
            d=(Token)input.LT(1);
            match(input,DOT,FOLLOW_DOT_in_joinAssociationPathExpression2580); if (failed) return node;
            pushFollow(FOLLOW_attribute_in_joinAssociationPathExpression2586);
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
    // JPQL.g3:504:1: singleValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object singleValuedPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:506:7: (n= pathExpression )
            // JPQL.g3:506:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_singleValuedPathExpression2626);
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
    // JPQL.g3:509:1: stateFieldPathExpression returns [Object node] : n= pathExpression ;
    public final Object stateFieldPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:511:7: (n= pathExpression )
            // JPQL.g3:511:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_stateFieldPathExpression2658);
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
    // JPQL.g3:514:1: pathExpression returns [Object node] : n= variableAccess (d= DOT right= attribute )+ ;
    public final Object pathExpression() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g3:518:7: (n= variableAccess (d= DOT right= attribute )+ )
            // JPQL.g3:518:7: n= variableAccess (d= DOT right= attribute )+
            {
            pushFollow(FOLLOW_variableAccess_in_pathExpression2690);
            n=variableAccess();
            _fsp--;
            if (failed) return node;
            // JPQL.g3:519:9: (d= DOT right= attribute )+
            int cnt37=0;
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);
                
                if ( (LA37_0==DOT) ) {
                    alt37=1;
                }
                
            
                switch (alt37) {
            	case 1 :
            	    // JPQL.g3:519:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pathExpression2703); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_pathExpression2709);
            	    right=attribute();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      
            	                      node = factory.newDot(d.getLine(), d.getCharPositionInLine(), n, right); 
            	                  
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt37 >= 1 ) break loop37;
            	    if (backtracking>0) {failed=true; return node;}
                        EarlyExitException eee =
                            new EarlyExitException(37, input);
                        throw eee;
                }
                cnt37++;
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
    // JPQL.g3:530:1: attribute returns [Object node] : i= . ;
    public final Object attribute() throws RecognitionException {

        Object node = null;
    
        Token i=null;
    
         node = null; 
        try {
            // JPQL.g3:533:7: (i= . )
            // JPQL.g3:533:7: i= .
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
    // JPQL.g3:540:1: variableAccess returns [Object node] : i= IDENT ;
    public final Object variableAccess() throws RecognitionException {

        Object node = null;
    
        Token i=null;
    
         node = null; 
        try {
            // JPQL.g3:542:7: (i= IDENT )
            // JPQL.g3:542:7: i= IDENT
            {
            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_variableAccess2805); if (failed) return node;
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
    // JPQL.g3:546:1: whereClause returns [Object node] : t= WHERE n= conditionalExpression ;
    public final Object whereClause() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:548:7: (t= WHERE n= conditionalExpression )
            // JPQL.g3:548:7: t= WHERE n= conditionalExpression
            {
            t=(Token)input.LT(1);
            match(input,WHERE,FOLLOW_WHERE_in_whereClause2843); if (failed) return node;
            pushFollow(FOLLOW_conditionalExpression_in_whereClause2849);
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
    // JPQL.g3:554:1: conditionalExpression returns [Object node] : n= conditionalTerm (t= OR right= conditionalTerm )* ;
    public final Object conditionalExpression() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g3:558:7: (n= conditionalTerm (t= OR right= conditionalTerm )* )
            // JPQL.g3:558:7: n= conditionalTerm (t= OR right= conditionalTerm )*
            {
            pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression2891);
            n=conditionalTerm();
            _fsp--;
            if (failed) return node;
            // JPQL.g3:559:9: (t= OR right= conditionalTerm )*
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);
                
                if ( (LA38_0==OR) ) {
                    alt38=1;
                }
                
            
                switch (alt38) {
            	case 1 :
            	    // JPQL.g3:559:10: t= OR right= conditionalTerm
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,OR,FOLLOW_OR_in_conditionalExpression2905); if (failed) return node;
            	    pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression2911);
            	    right=conditionalTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newOr(t.getLine(), t.getCharPositionInLine(), n, right); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop38;
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
    // JPQL.g3:564:1: conditionalTerm returns [Object node] : n= conditionalFactor (t= AND right= conditionalFactor )* ;
    public final Object conditionalTerm() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g3:568:7: (n= conditionalFactor (t= AND right= conditionalFactor )* )
            // JPQL.g3:568:7: n= conditionalFactor (t= AND right= conditionalFactor )*
            {
            pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm2966);
            n=conditionalFactor();
            _fsp--;
            if (failed) return node;
            // JPQL.g3:569:9: (t= AND right= conditionalFactor )*
            loop39:
            do {
                int alt39=2;
                int LA39_0 = input.LA(1);
                
                if ( (LA39_0==AND) ) {
                    alt39=1;
                }
                
            
                switch (alt39) {
            	case 1 :
            	    // JPQL.g3:569:10: t= AND right= conditionalFactor
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,AND,FOLLOW_AND_in_conditionalTerm2980); if (failed) return node;
            	    pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm2986);
            	    right=conditionalFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newAnd(t.getLine(), t.getCharPositionInLine(), n, right); 
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
    // $ANTLR end conditionalTerm

    
    // $ANTLR start conditionalFactor
    // JPQL.g3:574:1: conditionalFactor returns [Object node] : (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] ) ;
    public final Object conditionalFactor() throws RecognitionException {

        Object node = null;
    
        Token n=null;
        Object n1 = null;
        
    
         node = null; 
        try {
            // JPQL.g3:576:7: ( (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] ) )
            // JPQL.g3:576:7: (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )
            {
            // JPQL.g3:576:7: (n= NOT )?
            int alt40=2;
            int LA40_0 = input.LA(1);
            
            if ( (LA40_0==NOT) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // JPQL.g3:576:8: n= NOT
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_conditionalFactor3040); if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g3:577:9: (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )
            int alt41=2;
            int LA41_0 = input.LA(1);
            
            if ( (LA41_0==ABS||LA41_0==AVG||(LA41_0>=CONCAT && LA41_0<=CURRENT_TIMESTAMP)||LA41_0==FALSE||LA41_0==LENGTH||(LA41_0>=LOCATE && LA41_0<=MAX)||(LA41_0>=MIN && LA41_0<=MOD)||(LA41_0>=SIZE && LA41_0<=SQRT)||(LA41_0>=SUBSTRING && LA41_0<=SUM)||(LA41_0>=TRIM && LA41_0<=TRUE)||LA41_0==UPPER||LA41_0==IDENT||LA41_0==LEFT_ROUND_BRACKET||(LA41_0>=PLUS && LA41_0<=MINUS)||(LA41_0>=NUM_INT && LA41_0<=NAMED_PARAM)) ) {
                alt41=1;
            }
            else if ( (LA41_0==EXISTS) ) {
                alt41=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("577:9: (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )", 41, 0, input);
            
                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // JPQL.g3:577:11: n1= conditionalPrimary
                    {
                    pushFollow(FOLLOW_conditionalPrimary_in_conditionalFactor3059);
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
                    // JPQL.g3:584:11: n1= existsExpression[(n!=null)]
                    {
                    pushFollow(FOLLOW_existsExpression_in_conditionalFactor3088);
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
    // JPQL.g3:588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );
    public final Object conditionalPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:590:7: ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression )
            int alt42=2;
            int LA42_0 = input.LA(1);
            
            if ( (LA42_0==LEFT_ROUND_BRACKET) ) {
                int LA42_1 = input.LA(2);
                
                if ( (LA42_1==NOT) && (synpred1())) {
                    alt42=1;
                }
                else if ( (LA42_1==LEFT_ROUND_BRACKET) ) {
                    int LA42_35 = input.LA(3);
                    
                    if ( (LA42_35==SELECT) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_35==NOT) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_35==LEFT_ROUND_BRACKET) ) {
                        int LA42_72 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 72, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==PLUS) ) {
                        int LA42_73 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 73, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==MINUS) ) {
                        int LA42_74 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 74, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==AVG) ) {
                        int LA42_75 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 75, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==MAX) ) {
                        int LA42_76 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 76, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==MIN) ) {
                        int LA42_77 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 77, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==SUM) ) {
                        int LA42_78 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 78, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==COUNT) ) {
                        int LA42_79 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 79, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==IDENT) ) {
                        int LA42_80 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 80, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==ABS) ) {
                        int LA42_81 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 81, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==LENGTH) ) {
                        int LA42_82 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 82, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==MOD) ) {
                        int LA42_83 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 83, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==SQRT) ) {
                        int LA42_84 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 84, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==LOCATE) ) {
                        int LA42_85 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 85, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==SIZE) ) {
                        int LA42_86 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 86, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==CURRENT_DATE) ) {
                        int LA42_87 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 87, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==CURRENT_TIME) ) {
                        int LA42_88 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 88, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==CURRENT_TIMESTAMP) ) {
                        int LA42_89 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 89, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==CONCAT) ) {
                        int LA42_90 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 90, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==SUBSTRING) ) {
                        int LA42_91 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 91, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==TRIM) ) {
                        int LA42_92 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 92, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==UPPER) ) {
                        int LA42_93 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 93, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==LOWER) ) {
                        int LA42_94 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 94, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==POSITIONAL_PARAM) ) {
                        int LA42_95 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 95, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==NAMED_PARAM) ) {
                        int LA42_96 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 96, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==NUM_INT) ) {
                        int LA42_97 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 97, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==NUM_LONG) ) {
                        int LA42_98 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 98, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==NUM_FLOAT) ) {
                        int LA42_99 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 99, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==NUM_DOUBLE) ) {
                        int LA42_100 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 100, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==STRING_LITERAL_DOUBLE_QUOTED) ) {
                        int LA42_101 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 101, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==STRING_LITERAL_SINGLE_QUOTED) ) {
                        int LA42_102 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 102, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==TRUE) ) {
                        int LA42_103 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 103, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==FALSE) ) {
                        int LA42_104 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 104, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_35==EXISTS) && (synpred1())) {
                        alt42=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 35, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==PLUS) ) {
                    switch ( input.LA(3) ) {
                    case AVG:
                        {
                        int LA42_106 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 106, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MAX:
                        {
                        int LA42_107 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 107, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MIN:
                        {
                        int LA42_108 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 108, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUM:
                        {
                        int LA42_109 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 109, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COUNT:
                        {
                        int LA42_110 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 110, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA42_111 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 111, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case ABS:
                        {
                        int LA42_112 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 112, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LENGTH:
                        {
                        int LA42_113 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 113, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MOD:
                        {
                        int LA42_114 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 114, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SQRT:
                        {
                        int LA42_115 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 115, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOCATE:
                        {
                        int LA42_116 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 116, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SIZE:
                        {
                        int LA42_117 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 117, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_DATE:
                        {
                        int LA42_118 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 118, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_TIME:
                        {
                        int LA42_119 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 119, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_TIMESTAMP:
                        {
                        int LA42_120 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 120, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CONCAT:
                        {
                        int LA42_121 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 121, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUBSTRING:
                        {
                        int LA42_122 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 122, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TRIM:
                        {
                        int LA42_123 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 123, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case UPPER:
                        {
                        int LA42_124 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 124, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOWER:
                        {
                        int LA42_125 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 125, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case POSITIONAL_PARAM:
                        {
                        int LA42_126 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 126, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NAMED_PARAM:
                        {
                        int LA42_127 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 127, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NUM_INT:
                        {
                        int LA42_128 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 128, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NUM_LONG:
                        {
                        int LA42_129 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 129, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NUM_FLOAT:
                        {
                        int LA42_130 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 130, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NUM_DOUBLE:
                        {
                        int LA42_131 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 131, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case STRING_LITERAL_DOUBLE_QUOTED:
                        {
                        int LA42_132 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 132, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case STRING_LITERAL_SINGLE_QUOTED:
                        {
                        int LA42_133 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 133, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TRUE:
                        {
                        int LA42_134 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 134, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FALSE:
                        {
                        int LA42_135 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 135, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_ROUND_BRACKET:
                        {
                        int LA42_136 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 136, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 36, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA42_1==MINUS) ) {
                    switch ( input.LA(3) ) {
                    case AVG:
                        {
                        int LA42_137 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 137, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MAX:
                        {
                        int LA42_138 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 138, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MIN:
                        {
                        int LA42_139 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 139, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUM:
                        {
                        int LA42_140 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 140, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COUNT:
                        {
                        int LA42_141 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 141, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
                        {
                        int LA42_142 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 142, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case ABS:
                        {
                        int LA42_143 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 143, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LENGTH:
                        {
                        int LA42_144 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 144, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MOD:
                        {
                        int LA42_145 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 145, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SQRT:
                        {
                        int LA42_146 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 146, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOCATE:
                        {
                        int LA42_147 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 147, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SIZE:
                        {
                        int LA42_148 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 148, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_DATE:
                        {
                        int LA42_149 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 149, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_TIME:
                        {
                        int LA42_150 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 150, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CURRENT_TIMESTAMP:
                        {
                        int LA42_151 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 151, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CONCAT:
                        {
                        int LA42_152 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 152, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUBSTRING:
                        {
                        int LA42_153 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 153, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TRIM:
                        {
                        int LA42_154 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 154, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case UPPER:
                        {
                        int LA42_155 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 155, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOWER:
                        {
                        int LA42_156 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 156, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case POSITIONAL_PARAM:
                        {
                        int LA42_157 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 157, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NAMED_PARAM:
                        {
                        int LA42_158 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 158, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NUM_INT:
                        {
                        int LA42_159 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 159, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NUM_LONG:
                        {
                        int LA42_160 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 160, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NUM_FLOAT:
                        {
                        int LA42_161 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 161, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NUM_DOUBLE:
                        {
                        int LA42_162 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 162, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case STRING_LITERAL_DOUBLE_QUOTED:
                        {
                        int LA42_163 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 163, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case STRING_LITERAL_SINGLE_QUOTED:
                        {
                        int LA42_164 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 164, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TRUE:
                        {
                        int LA42_165 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 165, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FALSE:
                        {
                        int LA42_166 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 166, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_ROUND_BRACKET:
                        {
                        int LA42_167 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 167, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 37, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA42_1==AVG) ) {
                    int LA42_38 = input.LA(3);
                    
                    if ( (LA42_38==LEFT_ROUND_BRACKET) ) {
                        int LA42_168 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 168, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 38, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==MAX) ) {
                    int LA42_39 = input.LA(3);
                    
                    if ( (LA42_39==LEFT_ROUND_BRACKET) ) {
                        int LA42_169 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 169, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 39, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==MIN) ) {
                    int LA42_40 = input.LA(3);
                    
                    if ( (LA42_40==LEFT_ROUND_BRACKET) ) {
                        int LA42_170 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 170, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 40, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==SUM) ) {
                    int LA42_41 = input.LA(3);
                    
                    if ( (LA42_41==LEFT_ROUND_BRACKET) ) {
                        int LA42_171 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 171, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 41, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==COUNT) ) {
                    int LA42_42 = input.LA(3);
                    
                    if ( (LA42_42==LEFT_ROUND_BRACKET) ) {
                        int LA42_172 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 172, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 42, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==IDENT) ) {
                    int LA42_43 = input.LA(3);
                    
                    if ( (LA42_43==DOT) ) {
                        int LA42_173 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 173, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_43==MULTIPLY) ) {
                        int LA42_174 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 174, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_43==DIVIDE) ) {
                        int LA42_175 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 175, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_43==PLUS) ) {
                        int LA42_176 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 176, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_43==MINUS) ) {
                        int LA42_177 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 177, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_43==EQUALS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_43==NOT_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_43==GREATER_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_43==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_43==LESS_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_43==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_43==NOT) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_43==BETWEEN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_43==LIKE) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_43==IN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_43==MEMBER) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_43==IS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_43==RIGHT_ROUND_BRACKET) ) {
                        alt42=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 43, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==ABS) ) {
                    int LA42_44 = input.LA(3);
                    
                    if ( (LA42_44==LEFT_ROUND_BRACKET) ) {
                        int LA42_191 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 191, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 44, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==LENGTH) ) {
                    int LA42_45 = input.LA(3);
                    
                    if ( (LA42_45==LEFT_ROUND_BRACKET) ) {
                        int LA42_192 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 192, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 45, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==MOD) ) {
                    int LA42_46 = input.LA(3);
                    
                    if ( (LA42_46==LEFT_ROUND_BRACKET) ) {
                        int LA42_193 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 193, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 46, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==SQRT) ) {
                    int LA42_47 = input.LA(3);
                    
                    if ( (LA42_47==LEFT_ROUND_BRACKET) ) {
                        int LA42_194 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 194, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 47, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==LOCATE) ) {
                    int LA42_48 = input.LA(3);
                    
                    if ( (LA42_48==LEFT_ROUND_BRACKET) ) {
                        int LA42_195 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 195, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 48, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==SIZE) ) {
                    int LA42_49 = input.LA(3);
                    
                    if ( (LA42_49==LEFT_ROUND_BRACKET) ) {
                        int LA42_196 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 196, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 49, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==CURRENT_DATE) ) {
                    int LA42_50 = input.LA(3);
                    
                    if ( (LA42_50==MULTIPLY) ) {
                        int LA42_197 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 197, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_50==DIVIDE) ) {
                        int LA42_198 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 198, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_50==PLUS) ) {
                        int LA42_199 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 199, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_50==MINUS) ) {
                        int LA42_200 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 200, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_50==RIGHT_ROUND_BRACKET) ) {
                        alt42=2;
                    }
                    else if ( (LA42_50==EQUALS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_50==NOT_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_50==GREATER_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_50==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_50==LESS_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_50==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_50==NOT) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_50==BETWEEN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_50==LIKE) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_50==IN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_50==MEMBER) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_50==IS) && (synpred1())) {
                        alt42=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 50, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==CURRENT_TIME) ) {
                    int LA42_51 = input.LA(3);
                    
                    if ( (LA42_51==MULTIPLY) ) {
                        int LA42_214 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 214, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_51==DIVIDE) ) {
                        int LA42_215 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 215, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_51==PLUS) ) {
                        int LA42_216 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 216, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_51==MINUS) ) {
                        int LA42_217 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 217, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_51==EQUALS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_51==NOT_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_51==GREATER_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_51==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_51==LESS_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_51==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_51==NOT) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_51==BETWEEN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_51==LIKE) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_51==IN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_51==MEMBER) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_51==IS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_51==RIGHT_ROUND_BRACKET) ) {
                        alt42=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 51, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==CURRENT_TIMESTAMP) ) {
                    int LA42_52 = input.LA(3);
                    
                    if ( (LA42_52==MULTIPLY) ) {
                        int LA42_231 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 231, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_52==DIVIDE) ) {
                        int LA42_232 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 232, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_52==PLUS) ) {
                        int LA42_233 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 233, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_52==MINUS) ) {
                        int LA42_234 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 234, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_52==EQUALS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_52==NOT_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_52==GREATER_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_52==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_52==LESS_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_52==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_52==NOT) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_52==BETWEEN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_52==LIKE) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_52==IN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_52==MEMBER) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_52==IS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_52==RIGHT_ROUND_BRACKET) ) {
                        alt42=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 52, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==CONCAT) ) {
                    int LA42_53 = input.LA(3);
                    
                    if ( (LA42_53==LEFT_ROUND_BRACKET) ) {
                        int LA42_248 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 248, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 53, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==SUBSTRING) ) {
                    int LA42_54 = input.LA(3);
                    
                    if ( (LA42_54==LEFT_ROUND_BRACKET) ) {
                        int LA42_249 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 249, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 54, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==TRIM) ) {
                    int LA42_55 = input.LA(3);
                    
                    if ( (LA42_55==LEFT_ROUND_BRACKET) ) {
                        int LA42_250 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 250, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 55, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==UPPER) ) {
                    int LA42_56 = input.LA(3);
                    
                    if ( (LA42_56==LEFT_ROUND_BRACKET) ) {
                        int LA42_251 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 251, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 56, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==LOWER) ) {
                    int LA42_57 = input.LA(3);
                    
                    if ( (LA42_57==LEFT_ROUND_BRACKET) ) {
                        int LA42_252 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 252, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 57, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==POSITIONAL_PARAM) ) {
                    int LA42_58 = input.LA(3);
                    
                    if ( (LA42_58==MULTIPLY) ) {
                        int LA42_253 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 253, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_58==DIVIDE) ) {
                        int LA42_254 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 254, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_58==PLUS) ) {
                        int LA42_255 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 255, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_58==MINUS) ) {
                        int LA42_256 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 256, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_58==RIGHT_ROUND_BRACKET) ) {
                        alt42=2;
                    }
                    else if ( (LA42_58==EQUALS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_58==NOT_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_58==GREATER_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_58==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_58==LESS_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_58==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_58==NOT) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_58==BETWEEN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_58==LIKE) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_58==IN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_58==MEMBER) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_58==IS) && (synpred1())) {
                        alt42=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 58, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==NAMED_PARAM) ) {
                    int LA42_59 = input.LA(3);
                    
                    if ( (LA42_59==MULTIPLY) ) {
                        int LA42_270 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 270, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_59==DIVIDE) ) {
                        int LA42_271 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 271, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_59==PLUS) ) {
                        int LA42_272 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 272, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_59==MINUS) ) {
                        int LA42_273 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 273, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_59==RIGHT_ROUND_BRACKET) ) {
                        alt42=2;
                    }
                    else if ( (LA42_59==EQUALS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_59==NOT_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_59==GREATER_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_59==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_59==LESS_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_59==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_59==NOT) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_59==BETWEEN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_59==LIKE) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_59==IN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_59==MEMBER) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_59==IS) && (synpred1())) {
                        alt42=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 59, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==NUM_INT) ) {
                    int LA42_60 = input.LA(3);
                    
                    if ( (LA42_60==MULTIPLY) ) {
                        int LA42_287 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 287, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_60==DIVIDE) ) {
                        int LA42_288 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 288, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_60==PLUS) ) {
                        int LA42_289 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 289, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_60==MINUS) ) {
                        int LA42_290 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 290, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_60==EQUALS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_60==NOT_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_60==GREATER_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_60==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_60==LESS_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_60==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_60==NOT) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_60==BETWEEN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_60==LIKE) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_60==IN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_60==MEMBER) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_60==IS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_60==RIGHT_ROUND_BRACKET) ) {
                        alt42=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 60, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==NUM_LONG) ) {
                    int LA42_61 = input.LA(3);
                    
                    if ( (LA42_61==MULTIPLY) ) {
                        int LA42_304 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 304, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_61==DIVIDE) ) {
                        int LA42_305 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 305, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_61==PLUS) ) {
                        int LA42_306 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 306, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_61==MINUS) ) {
                        int LA42_307 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 307, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_61==RIGHT_ROUND_BRACKET) ) {
                        alt42=2;
                    }
                    else if ( (LA42_61==EQUALS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_61==NOT_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_61==GREATER_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_61==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_61==LESS_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_61==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_61==NOT) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_61==BETWEEN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_61==LIKE) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_61==IN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_61==MEMBER) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_61==IS) && (synpred1())) {
                        alt42=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 61, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==NUM_FLOAT) ) {
                    int LA42_62 = input.LA(3);
                    
                    if ( (LA42_62==MULTIPLY) ) {
                        int LA42_321 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 321, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_62==DIVIDE) ) {
                        int LA42_322 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 322, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_62==PLUS) ) {
                        int LA42_323 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 323, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_62==MINUS) ) {
                        int LA42_324 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 324, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_62==RIGHT_ROUND_BRACKET) ) {
                        alt42=2;
                    }
                    else if ( (LA42_62==EQUALS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_62==NOT_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_62==GREATER_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_62==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_62==LESS_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_62==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_62==NOT) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_62==BETWEEN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_62==LIKE) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_62==IN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_62==MEMBER) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_62==IS) && (synpred1())) {
                        alt42=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 62, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==NUM_DOUBLE) ) {
                    int LA42_63 = input.LA(3);
                    
                    if ( (LA42_63==MULTIPLY) ) {
                        int LA42_338 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 338, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_63==DIVIDE) ) {
                        int LA42_339 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 339, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_63==PLUS) ) {
                        int LA42_340 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 340, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_63==MINUS) ) {
                        int LA42_341 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 341, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_63==EQUALS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_63==NOT_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_63==GREATER_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_63==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_63==LESS_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_63==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_63==NOT) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_63==BETWEEN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_63==LIKE) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_63==IN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_63==MEMBER) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_63==IS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_63==RIGHT_ROUND_BRACKET) ) {
                        alt42=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 63, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==STRING_LITERAL_DOUBLE_QUOTED) ) {
                    int LA42_64 = input.LA(3);
                    
                    if ( (LA42_64==MULTIPLY) ) {
                        int LA42_355 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 355, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_64==DIVIDE) ) {
                        int LA42_356 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 356, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_64==PLUS) ) {
                        int LA42_357 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 357, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_64==MINUS) ) {
                        int LA42_358 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 358, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_64==EQUALS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_64==NOT_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_64==GREATER_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_64==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_64==LESS_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_64==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_64==NOT) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_64==BETWEEN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_64==LIKE) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_64==IN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_64==MEMBER) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_64==IS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_64==RIGHT_ROUND_BRACKET) ) {
                        alt42=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 64, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==STRING_LITERAL_SINGLE_QUOTED) ) {
                    int LA42_65 = input.LA(3);
                    
                    if ( (LA42_65==MULTIPLY) ) {
                        int LA42_372 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 372, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_65==DIVIDE) ) {
                        int LA42_373 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 373, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_65==PLUS) ) {
                        int LA42_374 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 374, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_65==MINUS) ) {
                        int LA42_375 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 375, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_65==RIGHT_ROUND_BRACKET) ) {
                        alt42=2;
                    }
                    else if ( (LA42_65==EQUALS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_65==NOT_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_65==GREATER_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_65==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_65==LESS_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_65==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_65==NOT) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_65==BETWEEN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_65==LIKE) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_65==IN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_65==MEMBER) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_65==IS) && (synpred1())) {
                        alt42=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 65, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==TRUE) ) {
                    int LA42_66 = input.LA(3);
                    
                    if ( (LA42_66==MULTIPLY) ) {
                        int LA42_389 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 389, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_66==DIVIDE) ) {
                        int LA42_390 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 390, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_66==PLUS) ) {
                        int LA42_391 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 391, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_66==MINUS) ) {
                        int LA42_392 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 392, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_66==EQUALS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_66==NOT_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_66==GREATER_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_66==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_66==LESS_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_66==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_66==NOT) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_66==BETWEEN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_66==LIKE) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_66==IN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_66==MEMBER) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_66==IS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_66==RIGHT_ROUND_BRACKET) ) {
                        alt42=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 66, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==FALSE) ) {
                    int LA42_67 = input.LA(3);
                    
                    if ( (LA42_67==MULTIPLY) ) {
                        int LA42_406 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 406, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_67==DIVIDE) ) {
                        int LA42_407 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 407, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_67==PLUS) ) {
                        int LA42_408 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 408, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_67==MINUS) ) {
                        int LA42_409 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt42=1;
                        }
                        else if ( (true) ) {
                            alt42=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 409, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA42_67==EQUALS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_67==NOT_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_67==GREATER_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_67==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_67==LESS_THAN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_67==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_67==NOT) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_67==BETWEEN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_67==LIKE) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_67==IN) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_67==MEMBER) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_67==IS) && (synpred1())) {
                        alt42=1;
                    }
                    else if ( (LA42_67==RIGHT_ROUND_BRACKET) ) {
                        alt42=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 67, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA42_1==EXISTS) && (synpred1())) {
                    alt42=1;
                }
                else if ( (LA42_1==SELECT) ) {
                    alt42=2;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA42_0==ABS||LA42_0==AVG||(LA42_0>=CONCAT && LA42_0<=CURRENT_TIMESTAMP)||LA42_0==FALSE||LA42_0==LENGTH||(LA42_0>=LOCATE && LA42_0<=MAX)||(LA42_0>=MIN && LA42_0<=MOD)||(LA42_0>=SIZE && LA42_0<=SQRT)||(LA42_0>=SUBSTRING && LA42_0<=SUM)||(LA42_0>=TRIM && LA42_0<=TRUE)||LA42_0==UPPER||LA42_0==IDENT||(LA42_0>=PLUS && LA42_0<=MINUS)||(LA42_0>=NUM_INT && LA42_0<=NAMED_PARAM)) ) {
                alt42=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("588:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 42, 0, input);
            
                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // JPQL.g3:590:7: ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3145); if (failed) return node;
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalPrimary3151);
                    n=conditionalExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3153); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:592:7: n= simpleConditionalExpression
                    {
                    pushFollow(FOLLOW_simpleConditionalExpression_in_conditionalPrimary3167);
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
    // JPQL.g3:595:1: simpleConditionalExpression returns [Object node] : left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] ;
    public final Object simpleConditionalExpression() throws RecognitionException {

        Object node = null;
    
        Object left = null;

        Object n = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g3:599:7: (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] )
            // JPQL.g3:599:7: left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node]
            {
            pushFollow(FOLLOW_arithmeticExpression_in_simpleConditionalExpression3199);
            left=arithmeticExpression();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3214);
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
    // JPQL.g3:603:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );
    public final Object simpleConditionalExpressionRemainder(Object left) throws RecognitionException {

        Object node = null;
    
        Token n1=null;
        Token n2=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:605:7: (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] )
            int alt45=3;
            switch ( input.LA(1) ) {
            case EQUALS:
            case NOT_EQUAL_TO:
            case GREATER_THAN:
            case GREATER_THAN_EQUAL_TO:
            case LESS_THAN:
            case LESS_THAN_EQUAL_TO:
                {
                alt45=1;
                }
                break;
            case BETWEEN:
            case IN:
            case LIKE:
            case MEMBER:
            case NOT:
                {
                alt45=2;
                }
                break;
            case IS:
                {
                alt45=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("603:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );", 45, 0, input);
            
                throw nvae;
            }
            
            switch (alt45) {
                case 1 :
                    // JPQL.g3:605:7: n= comparisonExpression[left]
                    {
                    pushFollow(FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3249);
                    n=comparisonExpression(left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:606:7: (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left]
                    {
                    // JPQL.g3:606:7: (n1= NOT )?
                    int alt43=2;
                    int LA43_0 = input.LA(1);
                    
                    if ( (LA43_0==NOT) ) {
                        alt43=1;
                    }
                    switch (alt43) {
                        case 1 :
                            // JPQL.g3:606:8: n1= NOT
                            {
                            n1=(Token)input.LT(1);
                            match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3263); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3271);
                    n=conditionWithNotExpression((n1!=null),  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:607:7: IS (n2= NOT )? n= isExpression[(n2!=null), left]
                    {
                    match(input,IS,FOLLOW_IS_in_simpleConditionalExpressionRemainder3282); if (failed) return node;
                    // JPQL.g3:607:10: (n2= NOT )?
                    int alt44=2;
                    int LA44_0 = input.LA(1);
                    
                    if ( (LA44_0==NOT) ) {
                        alt44=1;
                    }
                    switch (alt44) {
                        case 1 :
                            // JPQL.g3:607:11: n2= NOT
                            {
                            n2=(Token)input.LT(1);
                            match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3287); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3295);
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
    // JPQL.g3:610:1: conditionWithNotExpression[boolean not, Object left] returns [Object node] : (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] );
    public final Object conditionWithNotExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:612:7: (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] )
            int alt46=4;
            switch ( input.LA(1) ) {
            case BETWEEN:
                {
                alt46=1;
                }
                break;
            case LIKE:
                {
                alt46=2;
                }
                break;
            case IN:
                {
                alt46=3;
                }
                break;
            case MEMBER:
                {
                alt46=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("610:1: conditionWithNotExpression[boolean not, Object left] returns [Object node] : (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] );", 46, 0, input);
            
                throw nvae;
            }
            
            switch (alt46) {
                case 1 :
                    // JPQL.g3:612:7: n= betweenExpression[not, left]
                    {
                    pushFollow(FOLLOW_betweenExpression_in_conditionWithNotExpression3330);
                    n=betweenExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:613:7: n= likeExpression[not, left]
                    {
                    pushFollow(FOLLOW_likeExpression_in_conditionWithNotExpression3345);
                    n=likeExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:614:7: n= inExpression[not, left]
                    {
                    pushFollow(FOLLOW_inExpression_in_conditionWithNotExpression3359);
                    n=inExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g3:615:7: n= collectionMemberExpression[not, left]
                    {
                    pushFollow(FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3373);
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
    // JPQL.g3:618:1: isExpression[boolean not, Object left] returns [Object node] : (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] );
    public final Object isExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:620:7: (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] )
            int alt47=2;
            int LA47_0 = input.LA(1);
            
            if ( (LA47_0==NULL) ) {
                alt47=1;
            }
            else if ( (LA47_0==EMPTY) ) {
                alt47=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("618:1: isExpression[boolean not, Object left] returns [Object node] : (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] );", 47, 0, input);
            
                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // JPQL.g3:620:7: n= nullComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_nullComparisonExpression_in_isExpression3408);
                    n=nullComparisonExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:621:7: n= emptyCollectionComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_emptyCollectionComparisonExpression_in_isExpression3423);
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
    // JPQL.g3:624:1: betweenExpression[boolean not, Object left] returns [Object node] : t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression ;
    public final Object betweenExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object lowerBound = null;

        Object upperBound = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g3:628:7: (t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression )
            // JPQL.g3:628:7: t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression
            {
            t=(Token)input.LT(1);
            match(input,BETWEEN,FOLLOW_BETWEEN_in_betweenExpression3456); if (failed) return node;
            pushFollow(FOLLOW_arithmeticExpression_in_betweenExpression3470);
            lowerBound=arithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,AND,FOLLOW_AND_in_betweenExpression3472); if (failed) return node;
            pushFollow(FOLLOW_arithmeticExpression_in_betweenExpression3478);
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
    // JPQL.g3:636:1: inExpression[boolean not, Object left] returns [Object node] : t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET ;
    public final Object inExpression(boolean not, Object left) throws RecognitionException {
        inExpression_stack.push(new inExpression_scope());

        Object node = null;
    
        Token t=null;
        Object itemNode = null;

        Object subqueryNode = null;
        
    
        
            node = null;
            ((inExpression_scope)inExpression_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g3:644:7: (t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET )
            // JPQL.g3:644:7: t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,IN,FOLLOW_IN_in_inExpression3521); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_inExpression3531); if (failed) return node;
            // JPQL.g3:646:9: (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery )
            int alt49=2;
            int LA49_0 = input.LA(1);
            
            if ( ((LA49_0>=NUM_INT && LA49_0<=NAMED_PARAM)) ) {
                alt49=1;
            }
            else if ( (LA49_0==SELECT) ) {
                alt49=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("646:9: (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery )", 49, 0, input);
            
                throw nvae;
            }
            switch (alt49) {
                case 1 :
                    // JPQL.g3:646:11: itemNode= inItem ( COMMA itemNode= inItem )*
                    {
                    pushFollow(FOLLOW_inItem_in_inExpression3547);
                    itemNode=inItem();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       ((inExpression_scope)inExpression_stack.peek()).items.add(itemNode); 
                    }
                    // JPQL.g3:647:13: ( COMMA itemNode= inItem )*
                    loop48:
                    do {
                        int alt48=2;
                        int LA48_0 = input.LA(1);
                        
                        if ( (LA48_0==COMMA) ) {
                            alt48=1;
                        }
                        
                    
                        switch (alt48) {
                    	case 1 :
                    	    // JPQL.g3:647:15: COMMA itemNode= inItem
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_inExpression3565); if (failed) return node;
                    	    pushFollow(FOLLOW_inItem_in_inExpression3571);
                    	    itemNode=inItem();
                    	    _fsp--;
                    	    if (failed) return node;
                    	    if ( backtracking==0 ) {
                    	       ((inExpression_scope)inExpression_stack.peek()).items.add(itemNode); 
                    	    }
                    	    
                    	    }
                    	    break;
                    
                    	default :
                    	    break loop48;
                        }
                    } while (true);

                    if ( backtracking==0 ) {
                      
                                      node = factory.newIn(t.getLine(), t.getCharPositionInLine(),
                                                           not, left, ((inExpression_scope)inExpression_stack.peek()).items);
                                  
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:652:11: subqueryNode= subquery
                    {
                    pushFollow(FOLLOW_subquery_in_inExpression3606);
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

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression3640); if (failed) return node;
            
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
    // JPQL.g3:661:1: inItem returns [Object node] : (n= literalString | n= literalNumeric | n= inputParameter );
    public final Object inItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:663:7: (n= literalString | n= literalNumeric | n= inputParameter )
            int alt50=3;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt50=1;
                }
                break;
            case NUM_INT:
            case NUM_LONG:
            case NUM_FLOAT:
            case NUM_DOUBLE:
                {
                alt50=2;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt50=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("661:1: inItem returns [Object node] : (n= literalString | n= literalNumeric | n= inputParameter );", 50, 0, input);
            
                throw nvae;
            }
            
            switch (alt50) {
                case 1 :
                    // JPQL.g3:663:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_inItem3670);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:664:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_inItem3684);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:665:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_inItem3698);
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
    // JPQL.g3:668:1: likeExpression[boolean not, Object left] returns [Object node] : t= LIKE pattern= likeValue (escapeChars= escape )? ;
    public final Object likeExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object pattern = null;

        Object escapeChars = null;
        
    
        
            node = null;
    
        try {
            // JPQL.g3:672:7: (t= LIKE pattern= likeValue (escapeChars= escape )? )
            // JPQL.g3:672:7: t= LIKE pattern= likeValue (escapeChars= escape )?
            {
            t=(Token)input.LT(1);
            match(input,LIKE,FOLLOW_LIKE_in_likeExpression3730); if (failed) return node;
            pushFollow(FOLLOW_likeValue_in_likeExpression3736);
            pattern=likeValue();
            _fsp--;
            if (failed) return node;
            // JPQL.g3:673:9: (escapeChars= escape )?
            int alt51=2;
            int LA51_0 = input.LA(1);
            
            if ( (LA51_0==ESCAPE) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // JPQL.g3:673:10: escapeChars= escape
                    {
                    pushFollow(FOLLOW_escape_in_likeExpression3751);
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
    // JPQL.g3:680:1: escape returns [Object node] : t= ESCAPE escapeClause= likeValue ;
    public final Object escape() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object escapeClause = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g3:684:7: (t= ESCAPE escapeClause= likeValue )
            // JPQL.g3:684:7: t= ESCAPE escapeClause= likeValue
            {
            t=(Token)input.LT(1);
            match(input,ESCAPE,FOLLOW_ESCAPE_in_escape3791); if (failed) return node;
            pushFollow(FOLLOW_likeValue_in_escape3797);
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
    // JPQL.g3:688:1: likeValue returns [Object node] : (n= literalString | n= inputParameter );
    public final Object likeValue() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:690:7: (n= literalString | n= inputParameter )
            int alt52=2;
            int LA52_0 = input.LA(1);
            
            if ( ((LA52_0>=STRING_LITERAL_DOUBLE_QUOTED && LA52_0<=STRING_LITERAL_SINGLE_QUOTED)) ) {
                alt52=1;
            }
            else if ( ((LA52_0>=POSITIONAL_PARAM && LA52_0<=NAMED_PARAM)) ) {
                alt52=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("688:1: likeValue returns [Object node] : (n= literalString | n= inputParameter );", 52, 0, input);
            
                throw nvae;
            }
            switch (alt52) {
                case 1 :
                    // JPQL.g3:690:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_likeValue3837);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:691:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_likeValue3851);
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
    // JPQL.g3:694:1: nullComparisonExpression[boolean not, Object left] returns [Object node] : t= NULL ;
    public final Object nullComparisonExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
    
         node = null; 
        try {
            // JPQL.g3:696:7: (t= NULL )
            // JPQL.g3:696:7: t= NULL
            {
            t=(Token)input.LT(1);
            match(input,NULL,FOLLOW_NULL_in_nullComparisonExpression3884); if (failed) return node;
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
    // JPQL.g3:700:1: emptyCollectionComparisonExpression[boolean not, Object left] returns [Object node] : t= EMPTY ;
    public final Object emptyCollectionComparisonExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
    
         node = null; 
        try {
            // JPQL.g3:702:7: (t= EMPTY )
            // JPQL.g3:702:7: t= EMPTY
            {
            t=(Token)input.LT(1);
            match(input,EMPTY,FOLLOW_EMPTY_in_emptyCollectionComparisonExpression3925); if (failed) return node;
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
    // JPQL.g3:706:1: collectionMemberExpression[boolean not, Object left] returns [Object node] : t= MEMBER ( OF )? n= collectionValuedPathExpression ;
    public final Object collectionMemberExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:708:7: (t= MEMBER ( OF )? n= collectionValuedPathExpression )
            // JPQL.g3:708:7: t= MEMBER ( OF )? n= collectionValuedPathExpression
            {
            t=(Token)input.LT(1);
            match(input,MEMBER,FOLLOW_MEMBER_in_collectionMemberExpression3966); if (failed) return node;
            // JPQL.g3:708:17: ( OF )?
            int alt53=2;
            int LA53_0 = input.LA(1);
            
            if ( (LA53_0==OF) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // JPQL.g3:708:18: OF
                    {
                    match(input,OF,FOLLOW_OF_in_collectionMemberExpression3969); if (failed) return node;
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression3977);
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
    // JPQL.g3:715:1: existsExpression[boolean not] returns [Object node] : t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET ;
    public final Object existsExpression(boolean not) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object subqueryNode = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g3:719:7: (t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET )
            // JPQL.g3:719:7: t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_existsExpression4017); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4019); if (failed) return node;
            pushFollow(FOLLOW_subquery_in_existsExpression4025);
            subqueryNode=subquery();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4027); if (failed) return node;
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
    // JPQL.g3:726:1: comparisonExpression[Object left] returns [Object node] : (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand );
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
            // JPQL.g3:728:7: (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand )
            int alt54=6;
            switch ( input.LA(1) ) {
            case EQUALS:
                {
                alt54=1;
                }
                break;
            case NOT_EQUAL_TO:
                {
                alt54=2;
                }
                break;
            case GREATER_THAN:
                {
                alt54=3;
                }
                break;
            case GREATER_THAN_EQUAL_TO:
                {
                alt54=4;
                }
                break;
            case LESS_THAN:
                {
                alt54=5;
                }
                break;
            case LESS_THAN_EQUAL_TO:
                {
                alt54=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("726:1: comparisonExpression[Object left] returns [Object node] : (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand );", 54, 0, input);
            
                throw nvae;
            }
            
            switch (alt54) {
                case 1 :
                    // JPQL.g3:728:7: t1= EQUALS n= comparisonExpressionRightOperand
                    {
                    t1=(Token)input.LT(1);
                    match(input,EQUALS,FOLLOW_EQUALS_in_comparisonExpression4067); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4073);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newEquals(t1.getLine(), t1.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:730:7: t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t2=(Token)input.LT(1);
                    match(input,NOT_EQUAL_TO,FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4094); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4100);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newNotEquals(t2.getLine(), t2.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:732:7: t3= GREATER_THAN n= comparisonExpressionRightOperand
                    {
                    t3=(Token)input.LT(1);
                    match(input,GREATER_THAN,FOLLOW_GREATER_THAN_in_comparisonExpression4121); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4127);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newGreaterThan(t3.getLine(), t3.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g3:734:7: t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t4=(Token)input.LT(1);
                    match(input,GREATER_THAN_EQUAL_TO,FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4148); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4154);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newGreaterThanEqual(t4.getLine(), t4.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g3:736:7: t5= LESS_THAN n= comparisonExpressionRightOperand
                    {
                    t5=(Token)input.LT(1);
                    match(input,LESS_THAN,FOLLOW_LESS_THAN_in_comparisonExpression4175); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4181);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newLessThan(t5.getLine(), t5.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g3:738:7: t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t6=(Token)input.LT(1);
                    match(input,LESS_THAN_EQUAL_TO,FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4202); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4208);
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
    // JPQL.g3:742:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression | n= anyOrAllExpression );
    public final Object comparisonExpressionRightOperand() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:744:7: (n= arithmeticExpression | n= anyOrAllExpression )
            int alt55=2;
            int LA55_0 = input.LA(1);
            
            if ( (LA55_0==ABS||LA55_0==AVG||(LA55_0>=CONCAT && LA55_0<=CURRENT_TIMESTAMP)||LA55_0==FALSE||LA55_0==LENGTH||(LA55_0>=LOCATE && LA55_0<=MAX)||(LA55_0>=MIN && LA55_0<=MOD)||(LA55_0>=SIZE && LA55_0<=SQRT)||(LA55_0>=SUBSTRING && LA55_0<=SUM)||(LA55_0>=TRIM && LA55_0<=TRUE)||LA55_0==UPPER||LA55_0==IDENT||LA55_0==LEFT_ROUND_BRACKET||(LA55_0>=PLUS && LA55_0<=MINUS)||(LA55_0>=NUM_INT && LA55_0<=NAMED_PARAM)) ) {
                alt55=1;
            }
            else if ( (LA55_0==ALL||LA55_0==ANY||LA55_0==SOME) ) {
                alt55=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("742:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression | n= anyOrAllExpression );", 55, 0, input);
            
                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // JPQL.g3:744:7: n= arithmeticExpression
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4249);
                    n=arithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:745:7: n= anyOrAllExpression
                    {
                    pushFollow(FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4263);
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
    // JPQL.g3:748:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object arithmeticExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:750:7: (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
            int alt56=2;
            int LA56_0 = input.LA(1);
            
            if ( (LA56_0==ABS||LA56_0==AVG||(LA56_0>=CONCAT && LA56_0<=CURRENT_TIMESTAMP)||LA56_0==FALSE||LA56_0==LENGTH||(LA56_0>=LOCATE && LA56_0<=MAX)||(LA56_0>=MIN && LA56_0<=MOD)||(LA56_0>=SIZE && LA56_0<=SQRT)||(LA56_0>=SUBSTRING && LA56_0<=SUM)||(LA56_0>=TRIM && LA56_0<=TRUE)||LA56_0==UPPER||LA56_0==IDENT||(LA56_0>=PLUS && LA56_0<=MINUS)||(LA56_0>=NUM_INT && LA56_0<=NAMED_PARAM)) ) {
                alt56=1;
            }
            else if ( (LA56_0==LEFT_ROUND_BRACKET) ) {
                int LA56_33 = input.LA(2);
                
                if ( (LA56_33==SELECT) ) {
                    alt56=2;
                }
                else if ( (LA56_33==ABS||LA56_33==AVG||(LA56_33>=CONCAT && LA56_33<=CURRENT_TIMESTAMP)||LA56_33==FALSE||LA56_33==LENGTH||(LA56_33>=LOCATE && LA56_33<=MAX)||(LA56_33>=MIN && LA56_33<=MOD)||(LA56_33>=SIZE && LA56_33<=SQRT)||(LA56_33>=SUBSTRING && LA56_33<=SUM)||(LA56_33>=TRIM && LA56_33<=TRUE)||LA56_33==UPPER||LA56_33==IDENT||LA56_33==LEFT_ROUND_BRACKET||(LA56_33>=PLUS && LA56_33<=MINUS)||(LA56_33>=NUM_INT && LA56_33<=NAMED_PARAM)) ) {
                    alt56=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("748:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 56, 33, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("748:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 56, 0, input);
            
                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // JPQL.g3:750:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4295);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:751:7: LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4305); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_arithmeticExpression4311);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4313); if (failed) return node;
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
    // JPQL.g3:754:1: simpleArithmeticExpression returns [Object node] : n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )* ;
    public final Object simpleArithmeticExpression() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token m=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g3:758:7: (n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )* )
            // JPQL.g3:758:7: n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*
            {
            pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4345);
            n=arithmeticTerm();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g3:759:9: (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*
            loop57:
            do {
                int alt57=3;
                int LA57_0 = input.LA(1);
                
                if ( (LA57_0==PLUS) ) {
                    alt57=1;
                }
                else if ( (LA57_0==MINUS) ) {
                    alt57=2;
                }
                
            
                switch (alt57) {
            	case 1 :
            	    // JPQL.g3:759:11: p= PLUS right= arithmeticTerm
            	    {
            	    p=(Token)input.LT(1);
            	    match(input,PLUS,FOLLOW_PLUS_in_simpleArithmeticExpression4361); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4367);
            	    right=arithmeticTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newPlus(p.getLine(), p.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g3:761:11: m= MINUS right= arithmeticTerm
            	    {
            	    m=(Token)input.LT(1);
            	    match(input,MINUS,FOLLOW_MINUS_in_simpleArithmeticExpression4396); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4402);
            	    right=arithmeticTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newMinus(m.getLine(), m.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop57;
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
    // JPQL.g3:766:1: arithmeticTerm returns [Object node] : n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )* ;
    public final Object arithmeticTerm() throws RecognitionException {

        Object node = null;
    
        Token m=null;
        Token d=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g3:770:7: (n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )* )
            // JPQL.g3:770:7: n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*
            {
            pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4459);
            n=arithmeticFactor();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // JPQL.g3:771:9: (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*
            loop58:
            do {
                int alt58=3;
                int LA58_0 = input.LA(1);
                
                if ( (LA58_0==MULTIPLY) ) {
                    alt58=1;
                }
                else if ( (LA58_0==DIVIDE) ) {
                    alt58=2;
                }
                
            
                switch (alt58) {
            	case 1 :
            	    // JPQL.g3:771:11: m= MULTIPLY right= arithmeticFactor
            	    {
            	    m=(Token)input.LT(1);
            	    match(input,MULTIPLY,FOLLOW_MULTIPLY_in_arithmeticTerm4475); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4481);
            	    right=arithmeticFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newMultiply(m.getLine(), m.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g3:773:11: d= DIVIDE right= arithmeticFactor
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DIVIDE,FOLLOW_DIVIDE_in_arithmeticTerm4510); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4516);
            	    right=arithmeticFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newDivide(d.getLine(), d.getCharPositionInLine(), node, right); 
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
    // $ANTLR end arithmeticTerm

    
    // $ANTLR start arithmeticFactor
    // JPQL.g3:778:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary );
    public final Object arithmeticFactor() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token m=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:780:7: (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary )
            int alt59=3;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt59=1;
                }
                break;
            case MINUS:
                {
                alt59=2;
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
            case IDENT:
            case LEFT_ROUND_BRACKET:
            case NUM_INT:
            case NUM_LONG:
            case NUM_FLOAT:
            case NUM_DOUBLE:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt59=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("778:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary );", 59, 0, input);
            
                throw nvae;
            }
            
            switch (alt59) {
                case 1 :
                    // JPQL.g3:780:7: p= PLUS n= arithmeticPrimary
                    {
                    p=(Token)input.LT(1);
                    match(input,PLUS,FOLLOW_PLUS_in_arithmeticFactor4570); if (failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor4577);
                    n=arithmeticPrimary();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = factory.newUnaryPlus(p.getLine(), p.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:782:7: m= MINUS n= arithmeticPrimary
                    {
                    m=(Token)input.LT(1);
                    match(input,MINUS,FOLLOW_MINUS_in_arithmeticFactor4599); if (failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor4605);
                    n=arithmeticPrimary();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newUnaryMinus(m.getLine(), m.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:784:7: n= arithmeticPrimary
                    {
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor4629);
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
    // JPQL.g3:787:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= variableAccess | n= stateFieldPathExpression | n= functionsReturningNumerics | n= functionsReturningDatetime | n= functionsReturningStrings | n= inputParameter | n= literalNumeric | n= literalString | n= literalBoolean | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET );
    public final Object arithmeticPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:789:7: ({...}?n= aggregateExpression | n= variableAccess | n= stateFieldPathExpression | n= functionsReturningNumerics | n= functionsReturningDatetime | n= functionsReturningStrings | n= inputParameter | n= literalNumeric | n= literalString | n= literalBoolean | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            int alt60=11;
            switch ( input.LA(1) ) {
            case AVG:
            case COUNT:
            case MAX:
            case MIN:
            case SUM:
                {
                alt60=1;
                }
                break;
            case IDENT:
                {
                int LA60_6 = input.LA(2);
                
                if ( (LA60_6==DOT) ) {
                    alt60=3;
                }
                else if ( (LA60_6==EOF||LA60_6==AND||LA60_6==BETWEEN||(LA60_6>=GROUP && LA60_6<=IN)||LA60_6==IS||LA60_6==LIKE||LA60_6==MEMBER||LA60_6==NOT||(LA60_6>=OR && LA60_6<=ORDER)||LA60_6==WHERE||(LA60_6>=COMMA && LA60_6<=EQUALS)||LA60_6==RIGHT_ROUND_BRACKET||(LA60_6>=NOT_EQUAL_TO && LA60_6<=DIVIDE)) ) {
                    alt60=2;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("787:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= variableAccess | n= stateFieldPathExpression | n= functionsReturningNumerics | n= functionsReturningDatetime | n= functionsReturningStrings | n= inputParameter | n= literalNumeric | n= literalString | n= literalBoolean | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET );", 60, 6, input);
                
                    throw nvae;
                }
                }
                break;
            case ABS:
            case LENGTH:
            case LOCATE:
            case MOD:
            case SIZE:
            case SQRT:
                {
                alt60=4;
                }
                break;
            case CURRENT_DATE:
            case CURRENT_TIME:
            case CURRENT_TIMESTAMP:
                {
                alt60=5;
                }
                break;
            case CONCAT:
            case LOWER:
            case SUBSTRING:
            case TRIM:
            case UPPER:
                {
                alt60=6;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt60=7;
                }
                break;
            case NUM_INT:
            case NUM_LONG:
            case NUM_FLOAT:
            case NUM_DOUBLE:
                {
                alt60=8;
                }
                break;
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt60=9;
                }
                break;
            case FALSE:
            case TRUE:
                {
                alt60=10;
                }
                break;
            case LEFT_ROUND_BRACKET:
                {
                alt60=11;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("787:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= variableAccess | n= stateFieldPathExpression | n= functionsReturningNumerics | n= functionsReturningDatetime | n= functionsReturningStrings | n= inputParameter | n= literalNumeric | n= literalString | n= literalBoolean | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET );", 60, 0, input);
            
                throw nvae;
            }
            
            switch (alt60) {
                case 1 :
                    // JPQL.g3:789:7: {...}?n= aggregateExpression
                    {
                    if ( !( aggregatesAllowed() ) ) {
                        if (backtracking>0) {failed=true; return node;}
                        throw new FailedPredicateException(input, "arithmeticPrimary", " aggregatesAllowed() ");
                    }
                    pushFollow(FOLLOW_aggregateExpression_in_arithmeticPrimary4663);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:790:7: n= variableAccess
                    {
                    pushFollow(FOLLOW_variableAccess_in_arithmeticPrimary4677);
                    n=variableAccess();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:791:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_arithmeticPrimary4691);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g3:792:7: n= functionsReturningNumerics
                    {
                    pushFollow(FOLLOW_functionsReturningNumerics_in_arithmeticPrimary4705);
                    n=functionsReturningNumerics();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g3:793:7: n= functionsReturningDatetime
                    {
                    pushFollow(FOLLOW_functionsReturningDatetime_in_arithmeticPrimary4719);
                    n=functionsReturningDatetime();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g3:794:7: n= functionsReturningStrings
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_arithmeticPrimary4733);
                    n=functionsReturningStrings();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 7 :
                    // JPQL.g3:795:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_arithmeticPrimary4747);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 8 :
                    // JPQL.g3:796:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_arithmeticPrimary4761);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 9 :
                    // JPQL.g3:797:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_arithmeticPrimary4775);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 10 :
                    // JPQL.g3:798:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_arithmeticPrimary4789);
                    n=literalBoolean();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 11 :
                    // JPQL.g3:799:7: LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary4799); if (failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary4805);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary4807); if (failed) return node;
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
    // JPQL.g3:802:1: anyOrAllExpression returns [Object node] : (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object anyOrAllExpression() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token y=null;
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:804:7: (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
            int alt61=3;
            switch ( input.LA(1) ) {
            case ALL:
                {
                alt61=1;
                }
                break;
            case ANY:
                {
                alt61=2;
                }
                break;
            case SOME:
                {
                alt61=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("802:1: anyOrAllExpression returns [Object node] : (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 61, 0, input);
            
                throw nvae;
            }
            
            switch (alt61) {
                case 1 :
                    // JPQL.g3:804:7: a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    a=(Token)input.LT(1);
                    match(input,ALL,FOLLOW_ALL_in_anyOrAllExpression4837); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression4839); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression4845);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression4847); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAll(a.getLine(), a.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:806:7: y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    y=(Token)input.LT(1);
                    match(input,ANY,FOLLOW_ANY_in_anyOrAllExpression4867); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression4869); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression4875);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression4877); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAny(y.getLine(), y.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:808:7: s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    s=(Token)input.LT(1);
                    match(input,SOME,FOLLOW_SOME_in_anyOrAllExpression4897); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression4899); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression4905);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression4907); if (failed) return node;
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
    // JPQL.g3:812:1: stringPrimary returns [Object node] : (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression );
    public final Object stringPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:814:7: (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression )
            int alt62=4;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt62=1;
                }
                break;
            case CONCAT:
            case LOWER:
            case SUBSTRING:
            case TRIM:
            case UPPER:
                {
                alt62=2;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt62=3;
                }
                break;
            case IDENT:
                {
                alt62=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("812:1: stringPrimary returns [Object node] : (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression );", 62, 0, input);
            
                throw nvae;
            }
            
            switch (alt62) {
                case 1 :
                    // JPQL.g3:814:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_stringPrimary4947);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:815:7: n= functionsReturningStrings
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_stringPrimary4961);
                    n=functionsReturningStrings();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:816:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_stringPrimary4975);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g3:817:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_stringPrimary4989);
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
    // JPQL.g3:822:1: literal returns [Object node] : (n= literalNumeric | n= literalBoolean | n= literalString );
    public final Object literal() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:824:7: (n= literalNumeric | n= literalBoolean | n= literalString )
            int alt63=3;
            switch ( input.LA(1) ) {
            case NUM_INT:
            case NUM_LONG:
            case NUM_FLOAT:
            case NUM_DOUBLE:
                {
                alt63=1;
                }
                break;
            case FALSE:
            case TRUE:
                {
                alt63=2;
                }
                break;
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt63=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("822:1: literal returns [Object node] : (n= literalNumeric | n= literalBoolean | n= literalString );", 63, 0, input);
            
                throw nvae;
            }
            
            switch (alt63) {
                case 1 :
                    // JPQL.g3:824:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_literal5023);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:825:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_literal5037);
                    n=literalBoolean();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:826:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_literal5051);
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
    // JPQL.g3:829:1: literalNumeric returns [Object node] : (i= NUM_INT | l= NUM_LONG | f= NUM_FLOAT | d= NUM_DOUBLE );
    public final Object literalNumeric() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        Token l=null;
        Token f=null;
        Token d=null;
    
         node = null; 
        try {
            // JPQL.g3:831:7: (i= NUM_INT | l= NUM_LONG | f= NUM_FLOAT | d= NUM_DOUBLE )
            int alt64=4;
            switch ( input.LA(1) ) {
            case NUM_INT:
                {
                alt64=1;
                }
                break;
            case NUM_LONG:
                {
                alt64=2;
                }
                break;
            case NUM_FLOAT:
                {
                alt64=3;
                }
                break;
            case NUM_DOUBLE:
                {
                alt64=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("829:1: literalNumeric returns [Object node] : (i= NUM_INT | l= NUM_LONG | f= NUM_FLOAT | d= NUM_DOUBLE );", 64, 0, input);
            
                throw nvae;
            }
            
            switch (alt64) {
                case 1 :
                    // JPQL.g3:831:7: i= NUM_INT
                    {
                    i=(Token)input.LT(1);
                    match(input,NUM_INT,FOLLOW_NUM_INT_in_literalNumeric5081); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newIntegerLiteral(i.getLine(), i.getCharPositionInLine(), 
                                                                   Integer.valueOf(i.getText())); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:836:7: l= NUM_LONG
                    {
                    l=(Token)input.LT(1);
                    match(input,NUM_LONG,FOLLOW_NUM_LONG_in_literalNumeric5102); if (failed) return node;
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
                    // JPQL.g3:844:7: f= NUM_FLOAT
                    {
                    f=(Token)input.LT(1);
                    match(input,NUM_FLOAT,FOLLOW_NUM_FLOAT_in_literalNumeric5123); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newFloatLiteral(f.getLine(), f.getCharPositionInLine(),
                                                                 Float.valueOf(f.getText()));
                              
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g3:849:7: d= NUM_DOUBLE
                    {
                    d=(Token)input.LT(1);
                    match(input,NUM_DOUBLE,FOLLOW_NUM_DOUBLE_in_literalNumeric5143); if (failed) return node;
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
    // JPQL.g3:856:1: literalBoolean returns [Object node] : (t= TRUE | f= FALSE );
    public final Object literalBoolean() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Token f=null;
    
         node = null; 
        try {
            // JPQL.g3:858:7: (t= TRUE | f= FALSE )
            int alt65=2;
            int LA65_0 = input.LA(1);
            
            if ( (LA65_0==TRUE) ) {
                alt65=1;
            }
            else if ( (LA65_0==FALSE) ) {
                alt65=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("856:1: literalBoolean returns [Object node] : (t= TRUE | f= FALSE );", 65, 0, input);
            
                throw nvae;
            }
            switch (alt65) {
                case 1 :
                    // JPQL.g3:858:7: t= TRUE
                    {
                    t=(Token)input.LT(1);
                    match(input,TRUE,FOLLOW_TRUE_in_literalBoolean5181); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newBooleanLiteral(t.getLine(), t.getCharPositionInLine(), Boolean.TRUE); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:860:7: f= FALSE
                    {
                    f=(Token)input.LT(1);
                    match(input,FALSE,FOLLOW_FALSE_in_literalBoolean5203); if (failed) return node;
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
    // JPQL.g3:864:1: literalString returns [Object node] : (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED );
    public final Object literalString() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Token s=null;
    
         node = null; 
        try {
            // JPQL.g3:866:7: (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED )
            int alt66=2;
            int LA66_0 = input.LA(1);
            
            if ( (LA66_0==STRING_LITERAL_DOUBLE_QUOTED) ) {
                alt66=1;
            }
            else if ( (LA66_0==STRING_LITERAL_SINGLE_QUOTED) ) {
                alt66=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("864:1: literalString returns [Object node] : (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED );", 66, 0, input);
            
                throw nvae;
            }
            switch (alt66) {
                case 1 :
                    // JPQL.g3:866:7: d= STRING_LITERAL_DOUBLE_QUOTED
                    {
                    d=(Token)input.LT(1);
                    match(input,STRING_LITERAL_DOUBLE_QUOTED,FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString5242); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newStringLiteral(d.getLine(), d.getCharPositionInLine(), 
                                                                  convertStringLiteral(d.getText())); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:871:7: s= STRING_LITERAL_SINGLE_QUOTED
                    {
                    s=(Token)input.LT(1);
                    match(input,STRING_LITERAL_SINGLE_QUOTED,FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString5263); if (failed) return node;
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
    // JPQL.g3:878:1: inputParameter returns [Object node] : (p= POSITIONAL_PARAM | n= NAMED_PARAM );
    public final Object inputParameter() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token n=null;
    
         node = null; 
        try {
            // JPQL.g3:880:7: (p= POSITIONAL_PARAM | n= NAMED_PARAM )
            int alt67=2;
            int LA67_0 = input.LA(1);
            
            if ( (LA67_0==POSITIONAL_PARAM) ) {
                alt67=1;
            }
            else if ( (LA67_0==NAMED_PARAM) ) {
                alt67=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("878:1: inputParameter returns [Object node] : (p= POSITIONAL_PARAM | n= NAMED_PARAM );", 67, 0, input);
            
                throw nvae;
            }
            switch (alt67) {
                case 1 :
                    // JPQL.g3:880:7: p= POSITIONAL_PARAM
                    {
                    p=(Token)input.LT(1);
                    match(input,POSITIONAL_PARAM,FOLLOW_POSITIONAL_PARAM_in_inputParameter5301); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  // skip the leading ?
                                  String text = p.getText().substring(1);
                                  node = factory.newPositionalParameter(p.getLine(), p.getCharPositionInLine(), text); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:886:7: n= NAMED_PARAM
                    {
                    n=(Token)input.LT(1);
                    match(input,NAMED_PARAM,FOLLOW_NAMED_PARAM_in_inputParameter5321); if (failed) return node;
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
    // JPQL.g3:894:1: functionsReturningNumerics returns [Object node] : (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size );
    public final Object functionsReturningNumerics() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:896:7: (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size )
            int alt68=6;
            switch ( input.LA(1) ) {
            case ABS:
                {
                alt68=1;
                }
                break;
            case LENGTH:
                {
                alt68=2;
                }
                break;
            case MOD:
                {
                alt68=3;
                }
                break;
            case SQRT:
                {
                alt68=4;
                }
                break;
            case LOCATE:
                {
                alt68=5;
                }
                break;
            case SIZE:
                {
                alt68=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("894:1: functionsReturningNumerics returns [Object node] : (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size );", 68, 0, input);
            
                throw nvae;
            }
            
            switch (alt68) {
                case 1 :
                    // JPQL.g3:896:7: n= abs
                    {
                    pushFollow(FOLLOW_abs_in_functionsReturningNumerics5361);
                    n=abs();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:897:7: n= length
                    {
                    pushFollow(FOLLOW_length_in_functionsReturningNumerics5375);
                    n=length();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:898:7: n= mod
                    {
                    pushFollow(FOLLOW_mod_in_functionsReturningNumerics5389);
                    n=mod();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g3:899:7: n= sqrt
                    {
                    pushFollow(FOLLOW_sqrt_in_functionsReturningNumerics5403);
                    n=sqrt();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g3:900:7: n= locate
                    {
                    pushFollow(FOLLOW_locate_in_functionsReturningNumerics5417);
                    n=locate();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // JPQL.g3:901:7: n= size
                    {
                    pushFollow(FOLLOW_size_in_functionsReturningNumerics5431);
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
    // JPQL.g3:904:1: functionsReturningDatetime returns [Object node] : (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP );
    public final Object functionsReturningDatetime() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Token t=null;
        Token ts=null;
    
         node = null; 
        try {
            // JPQL.g3:906:7: (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP )
            int alt69=3;
            switch ( input.LA(1) ) {
            case CURRENT_DATE:
                {
                alt69=1;
                }
                break;
            case CURRENT_TIME:
                {
                alt69=2;
                }
                break;
            case CURRENT_TIMESTAMP:
                {
                alt69=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("904:1: functionsReturningDatetime returns [Object node] : (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP );", 69, 0, input);
            
                throw nvae;
            }
            
            switch (alt69) {
                case 1 :
                    // JPQL.g3:906:7: d= CURRENT_DATE
                    {
                    d=(Token)input.LT(1);
                    match(input,CURRENT_DATE,FOLLOW_CURRENT_DATE_in_functionsReturningDatetime5461); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newCurrentDate(d.getLine(), d.getCharPositionInLine()); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:908:7: t= CURRENT_TIME
                    {
                    t=(Token)input.LT(1);
                    match(input,CURRENT_TIME,FOLLOW_CURRENT_TIME_in_functionsReturningDatetime5482); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newCurrentTime(t.getLine(), t.getCharPositionInLine()); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:910:7: ts= CURRENT_TIMESTAMP
                    {
                    ts=(Token)input.LT(1);
                    match(input,CURRENT_TIMESTAMP,FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime5502); if (failed) return node;
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
    // JPQL.g3:914:1: functionsReturningStrings returns [Object node] : (n= concat | n= substring | n= trim | n= upper | n= lower );
    public final Object functionsReturningStrings() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:916:7: (n= concat | n= substring | n= trim | n= upper | n= lower )
            int alt70=5;
            switch ( input.LA(1) ) {
            case CONCAT:
                {
                alt70=1;
                }
                break;
            case SUBSTRING:
                {
                alt70=2;
                }
                break;
            case TRIM:
                {
                alt70=3;
                }
                break;
            case UPPER:
                {
                alt70=4;
                }
                break;
            case LOWER:
                {
                alt70=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("914:1: functionsReturningStrings returns [Object node] : (n= concat | n= substring | n= trim | n= upper | n= lower );", 70, 0, input);
            
                throw nvae;
            }
            
            switch (alt70) {
                case 1 :
                    // JPQL.g3:916:7: n= concat
                    {
                    pushFollow(FOLLOW_concat_in_functionsReturningStrings5542);
                    n=concat();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:917:7: n= substring
                    {
                    pushFollow(FOLLOW_substring_in_functionsReturningStrings5556);
                    n=substring();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:918:7: n= trim
                    {
                    pushFollow(FOLLOW_trim_in_functionsReturningStrings5570);
                    n=trim();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g3:919:7: n= upper
                    {
                    pushFollow(FOLLOW_upper_in_functionsReturningStrings5584);
                    n=upper();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // JPQL.g3:920:7: n= lower
                    {
                    pushFollow(FOLLOW_lower_in_functionsReturningStrings5598);
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
    // JPQL.g3:924:1: concat returns [Object node] : c= CONCAT LEFT_ROUND_BRACKET firstArg= stringPrimary COMMA secondArg= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object concat() throws RecognitionException {

        Object node = null;
    
        Token c=null;
        Object firstArg = null;

        Object secondArg = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g3:928:7: (c= CONCAT LEFT_ROUND_BRACKET firstArg= stringPrimary COMMA secondArg= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g3:928:7: c= CONCAT LEFT_ROUND_BRACKET firstArg= stringPrimary COMMA secondArg= stringPrimary RIGHT_ROUND_BRACKET
            {
            c=(Token)input.LT(1);
            match(input,CONCAT,FOLLOW_CONCAT_in_concat5629); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_concat5640); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_concat5655);
            firstArg=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_concat5657); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_concat5663);
            secondArg=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_concat5673); if (failed) return node;
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
    // JPQL.g3:935:1: substring returns [Object node] : s= SUBSTRING LEFT_ROUND_BRACKET string= stringPrimary COMMA start= simpleArithmeticExpression COMMA lengthNode= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object substring() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object string = null;

        Object start = null;

        Object lengthNode = null;
        
    
         
            node = null;
    
        try {
            // JPQL.g3:939:7: (s= SUBSTRING LEFT_ROUND_BRACKET string= stringPrimary COMMA start= simpleArithmeticExpression COMMA lengthNode= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g3:939:7: s= SUBSTRING LEFT_ROUND_BRACKET string= stringPrimary COMMA start= simpleArithmeticExpression COMMA lengthNode= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SUBSTRING,FOLLOW_SUBSTRING_in_substring5711); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_substring5724); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_substring5738);
            string=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_substring5740); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_substring5754);
            start=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_substring5756); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_substring5770);
            lengthNode=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_substring5780); if (failed) return node;
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
    // JPQL.g3:951:1: trim returns [Object node] : t= TRIM LEFT_ROUND_BRACKET ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object trim() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        TrimSpecification trimSpecIndicator = null;

        Object trimCharNode = null;

        Object n = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g3:955:7: (t= TRIM LEFT_ROUND_BRACKET ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g3:955:7: t= TRIM LEFT_ROUND_BRACKET ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,TRIM,FOLLOW_TRIM_in_trim5818); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_trim5828); if (failed) return node;
            // JPQL.g3:957:9: ( ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )?
            int alt71=2;
            int LA71_0 = input.LA(1);
            
            if ( (LA71_0==LEADING) && (synpred2())) {
                alt71=1;
            }
            else if ( (LA71_0==TRAILING) && (synpred2())) {
                alt71=1;
            }
            else if ( (LA71_0==BOTH) && (synpred2())) {
                alt71=1;
            }
            else if ( (LA71_0==STRING_LITERAL_DOUBLE_QUOTED) ) {
                int LA71_4 = input.LA(2);
                
                if ( (LA71_4==FROM) && (synpred2())) {
                    alt71=1;
                }
            }
            else if ( (LA71_0==STRING_LITERAL_SINGLE_QUOTED) ) {
                int LA71_5 = input.LA(2);
                
                if ( (LA71_5==FROM) && (synpred2())) {
                    alt71=1;
                }
            }
            else if ( (LA71_0==POSITIONAL_PARAM) ) {
                int LA71_6 = input.LA(2);
                
                if ( (LA71_6==FROM) && (synpred2())) {
                    alt71=1;
                }
            }
            else if ( (LA71_0==NAMED_PARAM) ) {
                int LA71_7 = input.LA(2);
                
                if ( (LA71_7==FROM) && (synpred2())) {
                    alt71=1;
                }
            }
            else if ( (LA71_0==FROM) && (synpred2())) {
                alt71=1;
            }
            switch (alt71) {
                case 1 :
                    // JPQL.g3:957:11: ( trimSpec trimChar FROM )=>trimSpecIndicator= trimSpec trimCharNode= trimChar FROM
                    {
                    pushFollow(FOLLOW_trimSpec_in_trim5856);
                    trimSpecIndicator=trimSpec();
                    _fsp--;
                    if (failed) return node;
                    pushFollow(FOLLOW_trimChar_in_trim5862);
                    trimCharNode=trimChar();
                    _fsp--;
                    if (failed) return node;
                    match(input,FROM,FOLLOW_FROM_in_trim5864); if (failed) return node;
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_stringPrimary_in_trim5882);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_trim5892); if (failed) return node;
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
    // JPQL.g3:966:1: trimSpec returns [TrimSpecification trimSpec] : ( LEADING | TRAILING | BOTH | );
    public final TrimSpecification trimSpec() throws RecognitionException {

        TrimSpecification trimSpec = null;
    
         trimSpec = TrimSpecification.BOTH; 
        try {
            // JPQL.g3:968:7: ( LEADING | TRAILING | BOTH | )
            int alt72=4;
            switch ( input.LA(1) ) {
            case LEADING:
                {
                alt72=1;
                }
                break;
            case TRAILING:
                {
                alt72=2;
                }
                break;
            case BOTH:
                {
                alt72=3;
                }
                break;
            case FROM:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt72=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return trimSpec;}
                NoViableAltException nvae =
                    new NoViableAltException("966:1: trimSpec returns [TrimSpecification trimSpec] : ( LEADING | TRAILING | BOTH | );", 72, 0, input);
            
                throw nvae;
            }
            
            switch (alt72) {
                case 1 :
                    // JPQL.g3:968:7: LEADING
                    {
                    match(input,LEADING,FOLLOW_LEADING_in_trimSpec5928); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.LEADING; 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:970:7: TRAILING
                    {
                    match(input,TRAILING,FOLLOW_TRAILING_in_trimSpec5946); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.TRAILING; 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:972:7: BOTH
                    {
                    match(input,BOTH,FOLLOW_BOTH_in_trimSpec5964); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.BOTH; 
                    }
                    
                    }
                    break;
                case 4 :
                    // JPQL.g3:975:5: 
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
    // JPQL.g3:977:1: trimChar returns [Object node] : (n= literalString | n= inputParameter | );
    public final Object trimChar() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:979:7: (n= literalString | n= inputParameter | )
            int alt73=3;
            switch ( input.LA(1) ) {
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
                {
                alt73=1;
                }
                break;
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
                {
                alt73=2;
                }
                break;
            case FROM:
                {
                alt73=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("977:1: trimChar returns [Object node] : (n= literalString | n= inputParameter | );", 73, 0, input);
            
                throw nvae;
            }
            
            switch (alt73) {
                case 1 :
                    // JPQL.g3:979:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_trimChar6011);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:980:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_trimChar6025);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:982:5: 
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
    // JPQL.g3:984:1: upper returns [Object node] : u= UPPER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object upper() throws RecognitionException {

        Object node = null;
    
        Token u=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:986:7: (u= UPPER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g3:986:7: u= UPPER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET
            {
            u=(Token)input.LT(1);
            match(input,UPPER,FOLLOW_UPPER_in_upper6062); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_upper6064); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_upper6070);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_upper6072); if (failed) return node;
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
    // JPQL.g3:990:1: lower returns [Object node] : l= LOWER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object lower() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:992:7: (l= LOWER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g3:992:7: l= LOWER LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LOWER,FOLLOW_LOWER_in_lower6110); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_lower6112); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_lower6118);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_lower6120); if (failed) return node;
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
    // JPQL.g3:997:1: abs returns [Object node] : a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object abs() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:999:7: (a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g3:999:7: a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            a=(Token)input.LT(1);
            match(input,ABS,FOLLOW_ABS_in_abs6159); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_abs6161); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_abs6167);
            n=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_abs6169); if (failed) return node;
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
    // JPQL.g3:1003:1: length returns [Object node] : l= LENGTH LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object length() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:1005:7: (l= LENGTH LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET )
            // JPQL.g3:1005:7: l= LENGTH LEFT_ROUND_BRACKET n= stringPrimary RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LENGTH,FOLLOW_LENGTH_in_length6207); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_length6209); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_length6215);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_length6217); if (failed) return node;
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
    // JPQL.g3:1009:1: locate returns [Object node] : l= LOCATE LEFT_ROUND_BRACKET pattern= stringPrimary COMMA n= stringPrimary ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET ;
    public final Object locate() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object pattern = null;

        Object n = null;

        Object startPos = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g3:1013:7: (l= LOCATE LEFT_ROUND_BRACKET pattern= stringPrimary COMMA n= stringPrimary ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET )
            // JPQL.g3:1013:7: l= LOCATE LEFT_ROUND_BRACKET pattern= stringPrimary COMMA n= stringPrimary ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LOCATE,FOLLOW_LOCATE_in_locate6255); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_locate6265); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_locate6280);
            pattern=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_locate6282); if (failed) return node;
            pushFollow(FOLLOW_stringPrimary_in_locate6288);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            // JPQL.g3:1016:9: ( COMMA startPos= simpleArithmeticExpression )?
            int alt74=2;
            int LA74_0 = input.LA(1);
            
            if ( (LA74_0==COMMA) ) {
                alt74=1;
            }
            switch (alt74) {
                case 1 :
                    // JPQL.g3:1016:11: COMMA startPos= simpleArithmeticExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_locate6300); if (failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_locate6306);
                    startPos=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_locate6319); if (failed) return node;
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
    // JPQL.g3:1024:1: size returns [Object node] : s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ;
    public final Object size() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:1026:7: (s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET )
            // JPQL.g3:1026:7: s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SIZE,FOLLOW_SIZE_in_size6357); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_size6368); if (failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_size6374);
            n=collectionValuedPathExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_size6376); if (failed) return node;
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
    // JPQL.g3:1031:1: mod returns [Object node] : m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object mod() throws RecognitionException {

        Object node = null;
    
        Token m=null;
        Object left = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g3:1035:7: (m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g3:1035:7: m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            m=(Token)input.LT(1);
            match(input,MOD,FOLLOW_MOD_in_mod6414); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mod6416); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_mod6430);
            left=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_mod6432); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_mod6447);
            right=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mod6457); if (failed) return node;
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
    // JPQL.g3:1042:1: sqrt returns [Object node] : s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object sqrt() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:1044:7: (s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // JPQL.g3:1044:7: s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SQRT,FOLLOW_SQRT_in_sqrt6495); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_sqrt6506); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_sqrt6512);
            n=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt6514); if (failed) return node;
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
    // JPQL.g3:1049:1: subquery returns [Object node] : select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? ;
    public final Object subquery() throws RecognitionException {

        Object node = null;
    
        Object select = null;

        Object from = null;

        Object where = null;

        Object groupBy = null;

        Object having = null;
        
    
         
            node = null; 
    
        try {
            // JPQL.g3:1053:7: (select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? )
            // JPQL.g3:1053:7: select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )?
            {
            pushFollow(FOLLOW_simpleSelectClause_in_subquery6555);
            select=simpleSelectClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_subqueryFromClause_in_subquery6570);
            from=subqueryFromClause();
            _fsp--;
            if (failed) return node;
            // JPQL.g3:1055:7: (where= whereClause )?
            int alt75=2;
            int LA75_0 = input.LA(1);
            
            if ( (LA75_0==WHERE) ) {
                alt75=1;
            }
            switch (alt75) {
                case 1 :
                    // JPQL.g3:1055:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_subquery6585);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g3:1056:7: (groupBy= groupByClause )?
            int alt76=2;
            int LA76_0 = input.LA(1);
            
            if ( (LA76_0==GROUP) ) {
                alt76=1;
            }
            switch (alt76) {
                case 1 :
                    // JPQL.g3:1056:8: groupBy= groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_subquery6600);
                    groupBy=groupByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // JPQL.g3:1057:7: (having= havingClause )?
            int alt77=2;
            int LA77_0 = input.LA(1);
            
            if ( (LA77_0==HAVING) ) {
                alt77=1;
            }
            switch (alt77) {
                case 1 :
                    // JPQL.g3:1057:8: having= havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_subquery6616);
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
    // JPQL.g3:1064:1: simpleSelectClause returns [Object node] : s= SELECT ( DISTINCT )? n= simpleSelectExpression ;
    public final Object simpleSelectClause() throws RecognitionException {
        simpleSelectClause_stack.push(new simpleSelectClause_scope());

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         
            node = null; 
            ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct = false;
    
        try {
            // JPQL.g3:1072:7: (s= SELECT ( DISTINCT )? n= simpleSelectExpression )
            // JPQL.g3:1072:7: s= SELECT ( DISTINCT )? n= simpleSelectExpression
            {
            s=(Token)input.LT(1);
            match(input,SELECT,FOLLOW_SELECT_in_simpleSelectClause6659); if (failed) return node;
            // JPQL.g3:1072:16: ( DISTINCT )?
            int alt78=2;
            int LA78_0 = input.LA(1);
            
            if ( (LA78_0==DISTINCT) ) {
                alt78=1;
            }
            switch (alt78) {
                case 1 :
                    // JPQL.g3:1072:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_simpleSelectClause6662); if (failed) return node;
                    if ( backtracking==0 ) {
                       ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct = true; 
                    }
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_simpleSelectExpression_in_simpleSelectClause6678);
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
    // JPQL.g3:1082:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccess );
    public final Object simpleSelectExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:1084:7: (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccess )
            int alt79=3;
            int LA79_0 = input.LA(1);
            
            if ( (LA79_0==IDENT) ) {
                int LA79_1 = input.LA(2);
                
                if ( (LA79_1==DOT) ) {
                    alt79=1;
                }
                else if ( (LA79_1==FROM) ) {
                    alt79=3;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1082:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccess );", 79, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA79_0==AVG||LA79_0==COUNT||LA79_0==MAX||LA79_0==MIN||LA79_0==SUM) ) {
                alt79=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1082:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccess );", 79, 0, input);
            
                throw nvae;
            }
            switch (alt79) {
                case 1 :
                    // JPQL.g3:1084:7: n= singleValuedPathExpression
                    {
                    pushFollow(FOLLOW_singleValuedPathExpression_in_simpleSelectExpression6718);
                    n=singleValuedPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:1085:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_simpleSelectExpression6733);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:1086:7: n= variableAccess
                    {
                    pushFollow(FOLLOW_variableAccess_in_simpleSelectExpression6748);
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
    // JPQL.g3:1090:1: subqueryFromClause returns [Object node] : f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] )* ;
    public final Object subqueryFromClause() throws RecognitionException {
        subqueryFromClause_stack.push(new subqueryFromClause_scope());

        Object node = null;
    
        Token f=null;
    
         
            node = null; 
            ((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls = new ArrayList();
    
        try {
            // JPQL.g3:1098:7: (f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] )* )
            // JPQL.g3:1098:7: f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] )*
            {
            f=(Token)input.LT(1);
            match(input,FROM,FOLLOW_FROM_in_subqueryFromClause6783); if (failed) return node;
            pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause6785);
            subselectIdentificationVariableDeclaration(((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls);
            _fsp--;
            if (failed) return node;
            // JPQL.g3:1099:9: ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] )*
            loop80:
            do {
                int alt80=2;
                int LA80_0 = input.LA(1);
                
                if ( (LA80_0==COMMA) ) {
                    alt80=1;
                }
                
            
                switch (alt80) {
            	case 1 :
            	    // JPQL.g3:1099:11: COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_subqueryFromClause6799); if (failed) return node;
            	    pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause6801);
            	    subselectIdentificationVariableDeclaration(((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls);
            	    _fsp--;
            	    if (failed) return node;
            	    
            	    }
            	    break;
            
            	default :
            	    break loop80;
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
    // JPQL.g3:1103:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration );
    public final void subselectIdentificationVariableDeclaration(List varDecls) throws RecognitionException {
        Token i=null;
        Object n = null;
        
    
         Object node; 
        try {
            // JPQL.g3:1105:7: ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration )
            int alt82=3;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA82_1 = input.LA(2);
                
                if ( (LA82_1==AS||LA82_1==IDENT) ) {
                    alt82=1;
                }
                else if ( (LA82_1==DOT) ) {
                    alt82=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1103:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration );", 82, 1, input);
                
                    throw nvae;
                }
                }
                break;
            case IN:
                {
                int LA82_2 = input.LA(2);
                
                if ( (LA82_2==LEFT_ROUND_BRACKET) ) {
                    alt82=3;
                }
                else if ( (LA82_2==AS||LA82_2==IDENT) ) {
                    alt82=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1103:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration );", 82, 2, input);
                
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
            case NUM_INT:
            case NUM_LONG:
            case NUM_FLOAT:
            case NUM_DOUBLE:
            case STRING_LITERAL_DOUBLE_QUOTED:
            case STRING_LITERAL_SINGLE_QUOTED:
            case POSITIONAL_PARAM:
            case NAMED_PARAM:
            case HEX_DIGIT:
            case WS:
            case TEXTCHAR:
            case EXPONENT:
            case FLOAT_SUFFIX:
                {
                alt82=1;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1103:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT | n= collectionMemberDeclaration );", 82, 0, input);
            
                throw nvae;
            }
            
            switch (alt82) {
                case 1 :
                    // JPQL.g3:1105:7: identificationVariableDeclaration[varDecls]
                    {
                    pushFollow(FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration6839);
                    identificationVariableDeclaration(varDecls);
                    _fsp--;
                    if (failed) return ;
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:1106:7: n= associationPathExpression ( AS )? i= IDENT
                    {
                    pushFollow(FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration6852);
                    n=associationPathExpression();
                    _fsp--;
                    if (failed) return ;
                    // JPQL.g3:1106:37: ( AS )?
                    int alt81=2;
                    int LA81_0 = input.LA(1);
                    
                    if ( (LA81_0==AS) ) {
                        alt81=1;
                    }
                    switch (alt81) {
                        case 1 :
                            // JPQL.g3:1106:38: AS
                            {
                            match(input,AS,FOLLOW_AS_in_subselectIdentificationVariableDeclaration6855); if (failed) return ;
                            
                            }
                            break;
                    
                    }

                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration6861); if (failed) return ;
                    if ( backtracking==0 ) {
                       
                                  varDecls.add(factory.newVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                       n, i.getText())); 
                              
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:1111:7: n= collectionMemberDeclaration
                    {
                    pushFollow(FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration6883);
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
    // JPQL.g3:1114:1: orderByClause returns [Object node] : o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* ;
    public final Object orderByClause() throws RecognitionException {
        orderByClause_stack.push(new orderByClause_scope());

        Object node = null;
    
        Token o=null;
        Object n = null;
        
    
         
            node = null; 
            ((orderByClause_scope)orderByClause_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g3:1122:7: (o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* )
            // JPQL.g3:1122:7: o= ORDER BY n= orderByItem ( COMMA n= orderByItem )*
            {
            o=(Token)input.LT(1);
            match(input,ORDER,FOLLOW_ORDER_in_orderByClause6916); if (failed) return node;
            match(input,BY,FOLLOW_BY_in_orderByClause6918); if (failed) return node;
            pushFollow(FOLLOW_orderByItem_in_orderByClause6932);
            n=orderByItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((orderByClause_scope)orderByClause_stack.peek()).items.add(n); 
            }
            // JPQL.g3:1124:9: ( COMMA n= orderByItem )*
            loop83:
            do {
                int alt83=2;
                int LA83_0 = input.LA(1);
                
                if ( (LA83_0==COMMA) ) {
                    alt83=1;
                }
                
            
                switch (alt83) {
            	case 1 :
            	    // JPQL.g3:1124:10: COMMA n= orderByItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_orderByClause6947); if (failed) return node;
            	    pushFollow(FOLLOW_orderByItem_in_orderByClause6953);
            	    n=orderByItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((orderByClause_scope)orderByClause_stack.peek()).items.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop83;
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
    // JPQL.g3:1128:1: orderByItem returns [Object node] : n= stateFieldPathExpression (a= ASC | d= DESC | ) ;
    public final Object orderByItem() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token d=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:1130:7: (n= stateFieldPathExpression (a= ASC | d= DESC | ) )
            // JPQL.g3:1130:7: n= stateFieldPathExpression (a= ASC | d= DESC | )
            {
            pushFollow(FOLLOW_stateFieldPathExpression_in_orderByItem6999);
            n=stateFieldPathExpression();
            _fsp--;
            if (failed) return node;
            // JPQL.g3:1131:9: (a= ASC | d= DESC | )
            int alt84=3;
            switch ( input.LA(1) ) {
            case ASC:
                {
                alt84=1;
                }
                break;
            case DESC:
                {
                alt84=2;
                }
                break;
            case EOF:
            case COMMA:
                {
                alt84=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1131:9: (a= ASC | d= DESC | )", 84, 0, input);
            
                throw nvae;
            }
            
            switch (alt84) {
                case 1 :
                    // JPQL.g3:1131:11: a= ASC
                    {
                    a=(Token)input.LT(1);
                    match(input,ASC,FOLLOW_ASC_in_orderByItem7013); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAscOrdering(a.getLine(), a.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:1133:11: d= DESC
                    {
                    d=(Token)input.LT(1);
                    match(input,DESC,FOLLOW_DESC_in_orderByItem7042); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newDescOrdering(d.getLine(), d.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:1136:13: 
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
    // JPQL.g3:1140:1: groupByClause returns [Object node] : g= GROUP BY n= groupByItem ( COMMA n= groupByItem )* ;
    public final Object groupByClause() throws RecognitionException {
        groupByClause_stack.push(new groupByClause_scope());

        Object node = null;
    
        Token g=null;
        Object n = null;
        
    
         
            node = null; 
            ((groupByClause_scope)groupByClause_stack.peek()).items = new ArrayList();
    
        try {
            // JPQL.g3:1148:7: (g= GROUP BY n= groupByItem ( COMMA n= groupByItem )* )
            // JPQL.g3:1148:7: g= GROUP BY n= groupByItem ( COMMA n= groupByItem )*
            {
            g=(Token)input.LT(1);
            match(input,GROUP,FOLLOW_GROUP_in_groupByClause7122); if (failed) return node;
            match(input,BY,FOLLOW_BY_in_groupByClause7124); if (failed) return node;
            pushFollow(FOLLOW_groupByItem_in_groupByClause7138);
            n=groupByItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((groupByClause_scope)groupByClause_stack.peek()).items.add(n); 
            }
            // JPQL.g3:1150:9: ( COMMA n= groupByItem )*
            loop85:
            do {
                int alt85=2;
                int LA85_0 = input.LA(1);
                
                if ( (LA85_0==COMMA) ) {
                    alt85=1;
                }
                
            
                switch (alt85) {
            	case 1 :
            	    // JPQL.g3:1150:10: COMMA n= groupByItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_groupByClause7151); if (failed) return node;
            	    pushFollow(FOLLOW_groupByItem_in_groupByClause7157);
            	    n=groupByItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((groupByClause_scope)groupByClause_stack.peek()).items.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop85;
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
    // JPQL.g3:1154:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccess );
    public final Object groupByItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:1156:7: (n= stateFieldPathExpression | n= variableAccess )
            int alt86=2;
            int LA86_0 = input.LA(1);
            
            if ( (LA86_0==IDENT) ) {
                int LA86_1 = input.LA(2);
                
                if ( (LA86_1==EOF||LA86_1==HAVING||LA86_1==ORDER||LA86_1==COMMA||LA86_1==RIGHT_ROUND_BRACKET) ) {
                    alt86=2;
                }
                else if ( (LA86_1==DOT) ) {
                    alt86=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1154:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccess );", 86, 1, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1154:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccess );", 86, 0, input);
            
                throw nvae;
            }
            switch (alt86) {
                case 1 :
                    // JPQL.g3:1156:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_groupByItem7203);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:1157:7: n= variableAccess
                    {
                    pushFollow(FOLLOW_variableAccess_in_groupByItem7217);
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
    // JPQL.g3:1160:1: havingClause returns [Object node] : h= HAVING n= conditionalExpression ;
    public final Object havingClause() throws RecognitionException {

        Object node = null;
    
        Token h=null;
        Object n = null;
        
    
         node = null; 
        try {
            // JPQL.g3:1162:7: (h= HAVING n= conditionalExpression )
            // JPQL.g3:1162:7: h= HAVING n= conditionalExpression
            {
            h=(Token)input.LT(1);
            match(input,HAVING,FOLLOW_HAVING_in_havingClause7247); if (failed) return node;
            if ( backtracking==0 ) {
               setAggregatesAllowed(true); 
            }
            pushFollow(FOLLOW_conditionalExpression_in_havingClause7264);
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
        // JPQL.g3:590:7: ( LEFT_ROUND_BRACKET conditionalExpression )
        // JPQL.g3:590:8: LEFT_ROUND_BRACKET conditionalExpression
        {
        match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_synpred13130); if (failed) return ;
        pushFollow(FOLLOW_conditionalExpression_in_synpred13132);
        conditionalExpression();
        _fsp--;
        if (failed) return ;
        
        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred2
    public final void synpred2_fragment() throws RecognitionException {   
        // JPQL.g3:957:11: ( trimSpec trimChar FROM )
        // JPQL.g3:957:13: trimSpec trimChar FROM
        {
        pushFollow(FOLLOW_trimSpec_in_synpred25843);
        trimSpec();
        _fsp--;
        if (failed) return ;
        pushFollow(FOLLOW_trimChar_in_synpred25845);
        trimChar();
        _fsp--;
        if (failed) return ;
        match(input,FROM,FOLLOW_FROM_in_synpred25847); if (failed) return ;
        
        }
    }
    // $ANTLR end synpred2

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


 

    public static final BitSet FOLLOW_selectStatement_in_document650 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_updateStatement_in_document662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_deleteStatement_in_document674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selectClause_in_selectStatement705 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_fromClause_in_selectStatement720 = new BitSet(new long[]{0x0004000030000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_whereClause_in_selectStatement735 = new BitSet(new long[]{0x0004000030000000L});
    public static final BitSet FOLLOW_groupByClause_in_selectStatement750 = new BitSet(new long[]{0x0004000020000000L});
    public static final BitSet FOLLOW_havingClause_in_selectStatement766 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_orderByClause_in_selectStatement781 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_selectStatement791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_updateClause_in_updateStatement834 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_setClause_in_updateStatement849 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_whereClause_in_updateStatement863 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_updateStatement873 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPDATE_in_updateClause905 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000003FFFFFFFL});
    public static final BitSet FOLLOW_abstractSchemaName_in_updateClause911 = new BitSet(new long[]{0x0000000000000102L,0x0000000000000004L});
    public static final BitSet FOLLOW_AS_in_updateClause924 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_updateClause932 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_in_setClause981 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000003FFFFFFFL});
    public static final BitSet FOLLOW_setAssignmentClause_in_setClause987 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_COMMA_in_setClause1000 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000003FFFFFFFL});
    public static final BitSet FOLLOW_setAssignmentClause_in_setClause1006 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_setAssignmentTarget_in_setAssignmentClause1064 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_EQUALS_in_setAssignmentClause1068 = new BitSet(new long[]{0x36C04DD00207C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_newValue_in_setAssignmentClause1074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_in_setAssignmentTarget1104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_setAssignmentTarget1119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_newValue1151 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_newValue1165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_deleteClause_in_deleteStatement1209 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_whereClause_in_deleteStatement1222 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_deleteStatement1232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DELETE_in_deleteClause1265 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_FROM_in_deleteClause1267 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000003FFFFFFFL});
    public static final BitSet FOLLOW_abstractSchemaName_in_deleteClause1273 = new BitSet(new long[]{0x0000000000000102L,0x0000000000000004L});
    public static final BitSet FOLLOW_AS_in_deleteClause1286 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_deleteClause1292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_selectClause1339 = new BitSet(new long[]{0x0400950000208400L,0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_selectClause1342 = new BitSet(new long[]{0x0400950000008400L,0x0000000000000004L});
    public static final BitSet FOLLOW_selectExpression_in_selectClause1358 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_COMMA_in_selectClause1370 = new BitSet(new long[]{0x0400950000008400L,0x0000000000000004L});
    public static final BitSet FOLLOW_selectExpression_in_selectClause1376 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_selectExpression1422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_selectExpression1436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OBJECT_in_selectExpression1446 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1448 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_variableAccess_in_selectExpression1454 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constructorExpression_in_selectExpression1471 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccess_in_pathExprOrVariableAccess1504 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_DOT_in_pathExprOrVariableAccess1519 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000003FFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExprOrVariableAccess1525 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_AVG_in_aggregateExpression1582 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1584 = new BitSet(new long[]{0x0000000000200000L,0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1587 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression1605 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MAX_in_aggregateExpression1628 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1630 = new BitSet(new long[]{0x0000000000200000L,0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1633 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression1652 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MIN_in_aggregateExpression1674 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1676 = new BitSet(new long[]{0x0000000000200000L,0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1679 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression1697 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1699 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUM_in_aggregateExpression1719 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1721 = new BitSet(new long[]{0x0000000000200000L,0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1724 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression1742 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1744 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COUNT_in_aggregateExpression1764 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1766 = new BitSet(new long[]{0x0000000000200000L,0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1769 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_aggregateExpression1787 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_constructorExpression1832 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_constructorName_in_constructorExpression1838 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression1848 = new BitSet(new long[]{0x0400050000008400L,0x0000000000000004L});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression1863 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000048L});
    public static final BitSet FOLLOW_COMMA_in_constructorExpression1878 = new BitSet(new long[]{0x0400050000008400L,0x0000000000000004L});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression1884 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000048L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression1899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_constructorName1940 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_DOT_in_constructorName1954 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_constructorName1958 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_constructorItem2002 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_constructorItem2016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_fromClause2049 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000003FFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2051 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_COMMA_in_fromClause2063 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000003FFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2068 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_fromClause2093 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2159 = new BitSet(new long[]{0x0000000A80000002L});
    public static final BitSet FOLLOW_join_in_identificationVariableDeclaration2178 = new BitSet(new long[]{0x0000000A80000002L});
    public static final BitSet FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2213 = new BitSet(new long[]{0x0000000000000100L,0x0000000000000004L});
    public static final BitSet FOLLOW_AS_in_rangeVariableDeclaration2216 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_rangeVariableDeclaration2222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_joinSpec_in_join2305 = new BitSet(new long[]{0x0000000004000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2319 = new BitSet(new long[]{0x0000000000000100L,0x0000000000000004L});
    public static final BitSet FOLLOW_AS_in_join2322 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_join2328 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FETCH_in_join2350 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_in_joinSpec2402 = new BitSet(new long[]{0x0008000200000000L});
    public static final BitSet FOLLOW_OUTER_in_joinSpec2405 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_INNER_in_joinSpec2414 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_JOIN_in_joinSpec2420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_collectionMemberDeclaration2448 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2450 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2456 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2458 = new BitSet(new long[]{0x0000000000000100L,0x0000000000000004L});
    public static final BitSet FOLLOW_AS_in_collectionMemberDeclaration2468 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_collectionMemberDeclaration2474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_collectionValuedPathExpression2512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_associationPathExpression2544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccess_in_joinAssociationPathExpression2576 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_DOT_in_joinAssociationPathExpression2580 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000003FFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_joinAssociationPathExpression2586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_singleValuedPathExpression2626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_stateFieldPathExpression2658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccess_in_pathExpression2690 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_DOT_in_pathExpression2703 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000003FFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExpression2709 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_IDENT_in_variableAccess2805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_whereClause2843 = new BitSet(new long[]{0x36C02DD00307C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_conditionalExpression_in_whereClause2849 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression2891 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_OR_in_conditionalExpression2905 = new BitSet(new long[]{0x36C02DD00307C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression2911 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm2966 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_AND_in_conditionalTerm2980 = new BitSet(new long[]{0x36C02DD00307C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm2986 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_NOT_in_conditionalFactor3040 = new BitSet(new long[]{0x36C00DD00307C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_conditionalPrimary_in_conditionalFactor3059 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_existsExpression_in_conditionalFactor3088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3145 = new BitSet(new long[]{0x36C02DD00307C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalPrimary3151 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleConditionalExpression_in_conditionalPrimary3167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_simpleConditionalExpression3199 = new BitSet(new long[]{0x0000222140000800L,0x0000000000001F10L});
    public static final BitSet FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3263 = new BitSet(new long[]{0x0000022040000800L});
    public static final BitSet FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IS_in_simpleConditionalExpressionRemainder3282 = new BitSet(new long[]{0x0000600000400000L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3287 = new BitSet(new long[]{0x0000400000400000L});
    public static final BitSet FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_betweenExpression_in_conditionWithNotExpression3330 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_likeExpression_in_conditionWithNotExpression3345 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inExpression_in_conditionWithNotExpression3359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullComparisonExpression_in_isExpression3408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_emptyCollectionComparisonExpression_in_isExpression3423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BETWEEN_in_betweenExpression3456 = new BitSet(new long[]{0x36C00DD00207C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_arithmeticExpression_in_betweenExpression3470 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_AND_in_betweenExpression3472 = new BitSet(new long[]{0x36C00DD00207C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_arithmeticExpression_in_betweenExpression3478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_inExpression3521 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_inExpression3531 = new BitSet(new long[]{0x0010000000000000L,0x0000000001FE0000L});
    public static final BitSet FOLLOW_inItem_in_inExpression3547 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000048L});
    public static final BitSet FOLLOW_COMMA_in_inExpression3565 = new BitSet(new long[]{0x0000000000000000L,0x0000000001FE0000L});
    public static final BitSet FOLLOW_inItem_in_inExpression3571 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000048L});
    public static final BitSet FOLLOW_subquery_in_inExpression3606 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression3640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_inItem3670 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_inItem3684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_inItem3698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LIKE_in_likeExpression3730 = new BitSet(new long[]{0x0000000000000000L,0x0000000001E00000L});
    public static final BitSet FOLLOW_likeValue_in_likeExpression3736 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_escape_in_likeExpression3751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ESCAPE_in_escape3791 = new BitSet(new long[]{0x0000000000000000L,0x0000000001E00000L});
    public static final BitSet FOLLOW_likeValue_in_escape3797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_likeValue3837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_likeValue3851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_nullComparisonExpression3884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EMPTY_in_emptyCollectionComparisonExpression3925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MEMBER_in_collectionMemberExpression3966 = new BitSet(new long[]{0x0001000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_OF_in_collectionMemberExpression3969 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression3977 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_existsExpression4017 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4019 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_subquery_in_existsExpression4025 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_comparisonExpression4067 = new BitSet(new long[]{0x37C00DD00207C4B0L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4094 = new BitSet(new long[]{0x37C00DD00207C4B0L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_in_comparisonExpression4121 = new BitSet(new long[]{0x37C00DD00207C4B0L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4148 = new BitSet(new long[]{0x37C00DD00207C4B0L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_in_comparisonExpression4175 = new BitSet(new long[]{0x37C00DD00207C4B0L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4202 = new BitSet(new long[]{0x37C00DD00207C4B0L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4305 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_subquery_in_arithmeticExpression4311 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4345 = new BitSet(new long[]{0x0000000000000002L,0x0000000000006000L});
    public static final BitSet FOLLOW_PLUS_in_simpleArithmeticExpression4361 = new BitSet(new long[]{0x36C00DD00207C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4367 = new BitSet(new long[]{0x0000000000000002L,0x0000000000006000L});
    public static final BitSet FOLLOW_MINUS_in_simpleArithmeticExpression4396 = new BitSet(new long[]{0x36C00DD00207C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4402 = new BitSet(new long[]{0x0000000000000002L,0x0000000000006000L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4459 = new BitSet(new long[]{0x0000000000000002L,0x0000000000018000L});
    public static final BitSet FOLLOW_MULTIPLY_in_arithmeticTerm4475 = new BitSet(new long[]{0x36C00DD00207C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4481 = new BitSet(new long[]{0x0000000000000002L,0x0000000000018000L});
    public static final BitSet FOLLOW_DIVIDE_in_arithmeticTerm4510 = new BitSet(new long[]{0x36C00DD00207C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4516 = new BitSet(new long[]{0x0000000000000002L,0x0000000000018000L});
    public static final BitSet FOLLOW_PLUS_in_arithmeticFactor4570 = new BitSet(new long[]{0x36C00DD00207C410L,0x0000000001FE0025L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor4577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_arithmeticFactor4599 = new BitSet(new long[]{0x36C00DD00207C410L,0x0000000001FE0025L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor4605 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor4629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_arithmeticPrimary4663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccess_in_arithmeticPrimary4677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_arithmeticPrimary4691 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningNumerics_in_arithmeticPrimary4705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningDatetime_in_arithmeticPrimary4719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_arithmeticPrimary4733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_arithmeticPrimary4747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_arithmeticPrimary4761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_arithmeticPrimary4775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_arithmeticPrimary4789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary4799 = new BitSet(new long[]{0x36C00DD00207C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary4805 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary4807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALL_in_anyOrAllExpression4837 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression4839 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression4845 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression4847 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_anyOrAllExpression4867 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression4869 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression4875 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression4877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SOME_in_anyOrAllExpression4897 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression4899 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression4905 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression4907 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_stringPrimary4947 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_stringPrimary4961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_stringPrimary4975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_stringPrimary4989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_literal5023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_literal5037 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_literal5051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUM_INT_in_literalNumeric5081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUM_LONG_in_literalNumeric5102 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUM_FLOAT_in_literalNumeric5123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUM_DOUBLE_in_literalNumeric5143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_literalBoolean5181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_literalBoolean5203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString5242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString5263 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_POSITIONAL_PARAM_in_inputParameter5301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_PARAM_in_inputParameter5321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_abs_in_functionsReturningNumerics5361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_length_in_functionsReturningNumerics5375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mod_in_functionsReturningNumerics5389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sqrt_in_functionsReturningNumerics5403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_locate_in_functionsReturningNumerics5417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_size_in_functionsReturningNumerics5431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_DATE_in_functionsReturningDatetime5461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIME_in_functionsReturningDatetime5482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime5502 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_concat_in_functionsReturningStrings5542 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_substring_in_functionsReturningStrings5556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trim_in_functionsReturningStrings5570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_upper_in_functionsReturningStrings5584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lower_in_functionsReturningStrings5598 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONCAT_in_concat5629 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_concat5640 = new BitSet(new long[]{0x1200008000004000L,0x0000000001E00005L});
    public static final BitSet FOLLOW_stringPrimary_in_concat5655 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_COMMA_in_concat5657 = new BitSet(new long[]{0x1200008000004000L,0x0000000001E00005L});
    public static final BitSet FOLLOW_stringPrimary_in_concat5663 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_concat5673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBSTRING_in_substring5711 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_substring5724 = new BitSet(new long[]{0x1200008000004000L,0x0000000001E00005L});
    public static final BitSet FOLLOW_stringPrimary_in_substring5738 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_COMMA_in_substring5740 = new BitSet(new long[]{0x36C00DD00207C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_substring5754 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_COMMA_in_substring5756 = new BitSet(new long[]{0x36C00DD00207C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_substring5770 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_substring5780 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRIM_in_trim5818 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_trim5828 = new BitSet(new long[]{0x1A00008408005000L,0x0000000001E00005L});
    public static final BitSet FOLLOW_trimSpec_in_trim5856 = new BitSet(new long[]{0x0000000008000000L,0x0000000001E00000L});
    public static final BitSet FOLLOW_trimChar_in_trim5862 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_FROM_in_trim5864 = new BitSet(new long[]{0x1200008000004000L,0x0000000001E00005L});
    public static final BitSet FOLLOW_stringPrimary_in_trim5882 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_trim5892 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEADING_in_trimSpec5928 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRAILING_in_trimSpec5946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOTH_in_trimSpec5964 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_trimChar6011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_trimChar6025 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPPER_in_upper6062 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_upper6064 = new BitSet(new long[]{0x1200008000004000L,0x0000000001E00005L});
    public static final BitSet FOLLOW_stringPrimary_in_upper6070 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_upper6072 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOWER_in_lower6110 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_lower6112 = new BitSet(new long[]{0x1200008000004000L,0x0000000001E00005L});
    public static final BitSet FOLLOW_stringPrimary_in_lower6118 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_lower6120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ABS_in_abs6159 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_abs6161 = new BitSet(new long[]{0x36C00DD00207C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_abs6167 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_abs6169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LENGTH_in_length6207 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_length6209 = new BitSet(new long[]{0x1200008000004000L,0x0000000001E00005L});
    public static final BitSet FOLLOW_stringPrimary_in_length6215 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_length6217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCATE_in_locate6255 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_locate6265 = new BitSet(new long[]{0x1200008000004000L,0x0000000001E00005L});
    public static final BitSet FOLLOW_stringPrimary_in_locate6280 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_COMMA_in_locate6282 = new BitSet(new long[]{0x1200008000004000L,0x0000000001E00005L});
    public static final BitSet FOLLOW_stringPrimary_in_locate6288 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000048L});
    public static final BitSet FOLLOW_COMMA_in_locate6300 = new BitSet(new long[]{0x36C00DD00207C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_locate6306 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_locate6319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIZE_in_size6357 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_size6368 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_size6374 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_size6376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_mod6414 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mod6416 = new BitSet(new long[]{0x36C00DD00207C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_mod6430 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_COMMA_in_mod6432 = new BitSet(new long[]{0x36C00DD00207C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_mod6447 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mod6457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQRT_in_sqrt6495 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_sqrt6506 = new BitSet(new long[]{0x36C00DD00207C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_sqrt6512 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt6514 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleSelectClause_in_subquery6555 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_subqueryFromClause_in_subquery6570 = new BitSet(new long[]{0x0000000030000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_whereClause_in_subquery6585 = new BitSet(new long[]{0x0000000030000002L});
    public static final BitSet FOLLOW_groupByClause_in_subquery6600 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_havingClause_in_subquery6616 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_simpleSelectClause6659 = new BitSet(new long[]{0x0400050000208400L,0x0000000000000004L});
    public static final BitSet FOLLOW_DISTINCT_in_simpleSelectClause6662 = new BitSet(new long[]{0x0400050000008400L,0x0000000000000004L});
    public static final BitSet FOLLOW_simpleSelectExpression_in_simpleSelectClause6678 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleValuedPathExpression_in_simpleSelectExpression6718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_simpleSelectExpression6733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccess_in_simpleSelectExpression6748 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_subqueryFromClause6783 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000003FFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause6785 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_COMMA_in_subqueryFromClause6799 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000003FFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause6801 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration6839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration6852 = new BitSet(new long[]{0x0000000000000100L,0x0000000000000004L});
    public static final BitSet FOLLOW_AS_in_subselectIdentificationVariableDeclaration6855 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration6861 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration6883 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_in_orderByClause6916 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_orderByClause6918 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause6932 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_COMMA_in_orderByClause6947 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause6953 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_orderByItem6999 = new BitSet(new long[]{0x0000000000080202L});
    public static final BitSet FOLLOW_ASC_in_orderByItem7013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_in_orderByItem7042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_groupByClause7122 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_groupByClause7124 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_groupByItem_in_groupByClause7138 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_COMMA_in_groupByClause7151 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_groupByItem_in_groupByClause7157 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000008L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_groupByItem7203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccess_in_groupByItem7217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HAVING_in_havingClause7247 = new BitSet(new long[]{0x36C02DD00307C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_conditionalExpression_in_havingClause7264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_synpred13130 = new BitSet(new long[]{0x36C02DD00307C410L,0x0000000001FE6025L});
    public static final BitSet FOLLOW_conditionalExpression_in_synpred13132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trimSpec_in_synpred25843 = new BitSet(new long[]{0x0000000008000000L,0x0000000001E00000L});
    public static final BitSet FOLLOW_trimChar_in_synpred25845 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_FROM_in_synpred25847 = new BitSet(new long[]{0x0000000000000002L});

}