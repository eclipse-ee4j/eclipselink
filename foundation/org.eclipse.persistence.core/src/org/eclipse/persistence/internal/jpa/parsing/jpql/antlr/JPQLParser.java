// $ANTLR 3.0 C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g 2010-03-03 14:13:44

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ABS", "ALL", "AND", "ANY", "AS", "ASC", "AVG", "BETWEEN", "BOTH", "BY", "CASE", "COALESCE", "CONCAT", "COUNT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "DESC", "DELETE", "DISTINCT", "ELSE", "EMPTY", "END", "ENTRY", "ESCAPE", "EXISTS", "FALSE", "FETCH", "FUNC", "FROM", "GROUP", "HAVING", "IN", "INDEX", "INNER", "IS", "JOIN", "KEY", "LEADING", "LEFT", "LENGTH", "LIKE", "LOCATE", "LOWER", "MAX", "MEMBER", "MIN", "MOD", "NEW", "NOT", "NULL", "NULLIF", "OBJECT", "OF", "OR", "ORDER", "OUTER", "SELECT", "SET", "SIZE", "SQRT", "SOME", "SUBSTRING", "SUM", "THEN", "TRAILING", "TRIM", "TRUE", "TYPE", "UNKNOWN", "UPDATE", "UPPER", "VALUE", "WHEN", "WHERE", "IDENT", "COMMA", "EQUALS", "LEFT_ROUND_BRACKET", "RIGHT_ROUND_BRACKET", "DOT", "NOT_EQUAL_TO", "GREATER_THAN", "GREATER_THAN_EQUAL_TO", "LESS_THAN", "LESS_THAN_EQUAL_TO", "PLUS", "MINUS", "MULTIPLY", "DIVIDE", "INTEGER_LITERAL", "LONG_LITERAL", "FLOAT_LITERAL", "DOUBLE_LITERAL", "STRING_LITERAL_DOUBLE_QUOTED", "STRING_LITERAL_SINGLE_QUOTED", "DATE_LITERAL", "TIME_LITERAL", "TIMESTAMP_LITERAL", "POSITIONAL_PARAM", "NAMED_PARAM", "WS", "LEFT_CURLY_BRACKET", "RIGHT_CURLY_BRACKET", "TEXTCHAR", "HEX_DIGIT", "HEX_LITERAL", "INTEGER_SUFFIX", "OCTAL_LITERAL", "NUMERIC_DIGITS", "DOUBLE_SUFFIX", "EXPONENT", "FLOAT_SUFFIX", "DATE_STRING", "TIME_STRING"
    };
    public static final int EXPONENT=115;
    public static final int DATE_STRING=117;
    public static final int FLOAT_SUFFIX=116;
    public static final int MOD=51;
    public static final int CURRENT_TIME=19;
    public static final int CASE=14;
    public static final int NEW=52;
    public static final int LEFT_ROUND_BRACKET=82;
    public static final int DOUBLE_LITERAL=97;
    public static final int TIME_LITERAL=101;
    public static final int COUNT=17;
    public static final int EQUALS=81;
    public static final int NOT=53;
    public static final int EOF=-1;
    public static final int TIME_STRING=118;
    public static final int TYPE=72;
    public static final int LEFT_CURLY_BRACKET=106;
    public static final int GREATER_THAN_EQUAL_TO=87;
    public static final int ESCAPE=28;
    public static final int NAMED_PARAM=104;
    public static final int BOTH=12;
    public static final int TIMESTAMP_LITERAL=102;
    public static final int NUMERIC_DIGITS=113;
    public static final int SELECT=61;
    public static final int DIVIDE=93;
    public static final int COALESCE=15;
    public static final int ASC=9;
    public static final int CONCAT=16;
    public static final int KEY=41;
    public static final int NULL=54;
    public static final int ELSE=24;
    public static final int TRAILING=69;
    public static final int DELETE=22;
    public static final int VALUE=76;
    public static final int DATE_LITERAL=100;
    public static final int OF=57;
    public static final int RIGHT_CURLY_BRACKET=107;
    public static final int LEADING=42;
    public static final int INTEGER_SUFFIX=111;
    public static final int EMPTY=25;
    public static final int ABS=4;
    public static final int GROUP=34;
    public static final int NOT_EQUAL_TO=85;
    public static final int WS=105;
    public static final int FETCH=31;
    public static final int STRING_LITERAL_SINGLE_QUOTED=99;
    public static final int INTEGER_LITERAL=94;
    public static final int FUNC=32;
    public static final int OR=58;
    public static final int TRIM=70;
    public static final int LESS_THAN=88;
    public static final int RIGHT_ROUND_BRACKET=83;
    public static final int POSITIONAL_PARAM=103;
    public static final int LOWER=47;
    public static final int FROM=33;
    public static final int END=26;
    public static final int FALSE=30;
    public static final int LESS_THAN_EQUAL_TO=89;
    public static final int DISTINCT=23;
    public static final int CURRENT_DATE=18;
    public static final int SIZE=63;
    public static final int UPPER=75;
    public static final int WHERE=78;
    public static final int NULLIF=55;
    public static final int MEMBER=49;
    public static final int INNER=38;
    public static final int ORDER=59;
    public static final int TEXTCHAR=108;
    public static final int MAX=48;
    public static final int UPDATE=74;
    public static final int AND=6;
    public static final int SUM=67;
    public static final int STRING_LITERAL_DOUBLE_QUOTED=98;
    public static final int LENGTH=44;
    public static final int INDEX=37;
    public static final int AS=8;
    public static final int IN=36;
    public static final int THEN=68;
    public static final int UNKNOWN=73;
    public static final int MULTIPLY=92;
    public static final int OBJECT=56;
    public static final int COMMA=80;
    public static final int IS=39;
    public static final int LEFT=43;
    public static final int AVG=10;
    public static final int SOME=65;
    public static final int ALL=5;
    public static final int IDENT=79;
    public static final int PLUS=90;
    public static final int HEX_LITERAL=110;
    public static final int EXISTS=29;
    public static final int DOT=84;
    public static final int CURRENT_TIMESTAMP=20;
    public static final int LIKE=45;
    public static final int OUTER=60;
    public static final int BY=13;
    public static final int GREATER_THAN=86;
    public static final int OCTAL_LITERAL=112;
    public static final int HEX_DIGIT=109;
    public static final int SET=62;
    public static final int HAVING=35;
    public static final int ENTRY=27;
    public static final int MIN=50;
    public static final int SQRT=64;
    public static final int MINUS=91;
    public static final int LONG_LITERAL=95;
    public static final int TRUE=71;
    public static final int JOIN=40;
    public static final int SUBSTRING=66;
    public static final int DOUBLE_SUFFIX=114;
    public static final int FLOAT_LITERAL=96;
    public static final int ANY=7;
    public static final int LOCATE=46;
    public static final int WHEN=77;
    public static final int DESC=21;
    public static final int BETWEEN=11;
    
        public JPQLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[111+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g"; }

    
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:198:1: document : (root= selectStatement | root= updateStatement | root= deleteStatement );
    public final void document() throws RecognitionException {
        Object root = null;
        
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:199:7: (root= selectStatement | root= updateStatement | root= deleteStatement )
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
                    new NoViableAltException("198:1: document : (root= selectStatement | root= updateStatement | root= deleteStatement );", 1, 0, input);
            
                throw nvae;
            }
            
            switch (alt1) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:199:7: root= selectStatement
                    {
                    pushFollow(FOLLOW_selectStatement_in_document754);
                    root=selectStatement();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      queryRoot = root;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:200:7: root= updateStatement
                    {
                    pushFollow(FOLLOW_updateStatement_in_document768);
                    root=updateStatement();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      queryRoot = root;
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:201:7: root= deleteStatement
                    {
                    pushFollow(FOLLOW_deleteStatement_in_document782);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:204:1: selectStatement returns [Object node] : select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF ;
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
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:208:7: (select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:208:7: select= selectClause from= fromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? (orderBy= orderByClause )? EOF
            {
            pushFollow(FOLLOW_selectClause_in_selectStatement815);
            select=selectClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_fromClause_in_selectStatement830);
            from=fromClause();
            _fsp--;
            if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:210:7: (where= whereClause )?
            int alt2=2;
            int LA2_0 = input.LA(1);
            
            if ( (LA2_0==WHERE) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:210:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_selectStatement845);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:211:7: (groupBy= groupByClause )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            
            if ( (LA3_0==GROUP) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:211:8: groupBy= groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_selectStatement860);
                    groupBy=groupByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:212:7: (having= havingClause )?
            int alt4=2;
            int LA4_0 = input.LA(1);
            
            if ( (LA4_0==HAVING) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:212:8: having= havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_selectStatement876);
                    having=havingClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:213:7: (orderBy= orderByClause )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            
            if ( (LA5_0==ORDER) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:213:8: orderBy= orderByClause
                    {
                    pushFollow(FOLLOW_orderByClause_in_selectStatement891);
                    orderBy=orderByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_selectStatement901); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:223:1: updateStatement returns [Object node] : update= updateClause set= setClause (where= whereClause )? EOF ;
    public final Object updateStatement() throws RecognitionException {

        Object node = null;
    
        Object update = null;

        Object set = null;

        Object where = null;
        
    
         
            node = null; 
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:227:7: (update= updateClause set= setClause (where= whereClause )? EOF )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:227:7: update= updateClause set= setClause (where= whereClause )? EOF
            {
            pushFollow(FOLLOW_updateClause_in_updateStatement944);
            update=updateClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_setClause_in_updateStatement959);
            set=setClause();
            _fsp--;
            if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:229:7: (where= whereClause )?
            int alt6=2;
            int LA6_0 = input.LA(1);
            
            if ( (LA6_0==WHERE) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:229:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_updateStatement973);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_updateStatement983); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:233:1: updateClause returns [Object node] : u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )? ;
    public final Object updateClause() throws RecognitionException {

        Object node = null;
    
        Token u=null;
        Token ident=null;
        String schema = null;
        
    
         
            node = null; 
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:237:7: (u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )? )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:237:7: u= UPDATE schema= abstractSchemaName ( ( AS )? ident= IDENT )?
            {
            u=(Token)input.LT(1);
            match(input,UPDATE,FOLLOW_UPDATE_in_updateClause1015); if (failed) return node;
            pushFollow(FOLLOW_abstractSchemaName_in_updateClause1021);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:238:9: ( ( AS )? ident= IDENT )?
            int alt8=2;
            int LA8_0 = input.LA(1);
            
            if ( (LA8_0==AS||LA8_0==IDENT) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:238:10: ( AS )? ident= IDENT
                    {
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:238:10: ( AS )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);
                    
                    if ( (LA7_0==AS) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:238:11: AS
                            {
                            match(input,AS,FOLLOW_AS_in_updateClause1034); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    ident=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_updateClause1042); if (failed) return node;
                    
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:249:1: setClause returns [Object node] : t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )* ;
    public final Object setClause() throws RecognitionException {
        setClause_stack.push(new setClause_scope());

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         
            node = null; 
            ((setClause_scope)setClause_stack.peek()).assignments = new ArrayList();
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:257:7: (t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )* )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:257:7: t= SET n= setAssignmentClause ( COMMA n= setAssignmentClause )*
            {
            t=(Token)input.LT(1);
            match(input,SET,FOLLOW_SET_in_setClause1091); if (failed) return node;
            pushFollow(FOLLOW_setAssignmentClause_in_setClause1097);
            n=setAssignmentClause();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((setClause_scope)setClause_stack.peek()).assignments.add(n); 
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:258:9: ( COMMA n= setAssignmentClause )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);
                
                if ( (LA9_0==COMMA) ) {
                    alt9=1;
                }
                
            
                switch (alt9) {
            	case 1 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:258:10: COMMA n= setAssignmentClause
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_setClause1110); if (failed) return node;
            	    pushFollow(FOLLOW_setAssignmentClause_in_setClause1116);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:262:1: setAssignmentClause returns [Object node] : target= setAssignmentTarget t= EQUALS value= newValue ;
    public final Object setAssignmentClause() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object target = null;

        Object value = null;
        
    
         
            node = null;
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:270:7: (target= setAssignmentTarget t= EQUALS value= newValue )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:270:7: target= setAssignmentTarget t= EQUALS value= newValue
            {
            pushFollow(FOLLOW_setAssignmentTarget_in_setAssignmentClause1174);
            target=setAssignmentTarget();
            _fsp--;
            if (failed) return node;
            t=(Token)input.LT(1);
            match(input,EQUALS,FOLLOW_EQUALS_in_setAssignmentClause1178); if (failed) return node;
            pushFollow(FOLLOW_newValue_in_setAssignmentClause1184);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:273:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );
    public final Object setAssignmentTarget() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         
            node = null;
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:277:7: (n= attribute | n= pathExpression )
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
                        new NoViableAltException("273:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 1, input);
                
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
                        new NoViableAltException("273:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 2, input);
                
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
                        new NoViableAltException("273:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 3, input);
                
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
                    new NoViableAltException("273:1: setAssignmentTarget returns [Object node] : (n= attribute | n= pathExpression );", 10, 0, input);
            
                throw nvae;
            }
            
            switch (alt10) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:277:7: n= attribute
                    {
                    pushFollow(FOLLOW_attribute_in_setAssignmentTarget1214);
                    n=attribute();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:278:7: n= pathExpression
                    {
                    pushFollow(FOLLOW_pathExpression_in_setAssignmentTarget1229);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:281:1: newValue returns [Object node] : (n= scalarExpression | n1= NULL );
    public final Object newValue() throws RecognitionException {

        Object node = null;
    
        Token n1=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:283:7: (n= scalarExpression | n1= NULL )
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
                    new NoViableAltException("281:1: newValue returns [Object node] : (n= scalarExpression | n1= NULL );", 11, 0, input);
            
                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:283:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_newValue1261);
                    n=scalarExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:284:7: n1= NULL
                    {
                    n1=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_newValue1275); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:290:1: deleteStatement returns [Object node] : delete= deleteClause (where= whereClause )? EOF ;
    public final Object deleteStatement() throws RecognitionException {

        Object node = null;
    
        Object delete = null;

        Object where = null;
        
    
         
            node = null; 
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:294:7: (delete= deleteClause (where= whereClause )? EOF )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:294:7: delete= deleteClause (where= whereClause )? EOF
            {
            pushFollow(FOLLOW_deleteClause_in_deleteStatement1319);
            delete=deleteClause();
            _fsp--;
            if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:295:7: (where= whereClause )?
            int alt12=2;
            int LA12_0 = input.LA(1);
            
            if ( (LA12_0==WHERE) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:295:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_deleteStatement1332);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,EOF,FOLLOW_EOF_in_deleteStatement1342); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:299:1: deleteClause returns [Object node] : t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )? ;
    public final Object deleteClause() throws RecognitionException {
        deleteClause_stack.push(new deleteClause_scope());

        Object node = null;
    
        Token t=null;
        Token ident=null;
        String schema = null;
        
    
         
            node = null; 
            ((deleteClause_scope)deleteClause_stack.peek()).variable = null;
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:307:7: (t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )? )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:307:7: t= DELETE FROM schema= abstractSchemaName ( ( AS )? ident= IDENT )?
            {
            t=(Token)input.LT(1);
            match(input,DELETE,FOLLOW_DELETE_in_deleteClause1375); if (failed) return node;
            match(input,FROM,FOLLOW_FROM_in_deleteClause1377); if (failed) return node;
            pushFollow(FOLLOW_abstractSchemaName_in_deleteClause1383);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:308:9: ( ( AS )? ident= IDENT )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            
            if ( (LA14_0==AS||LA14_0==IDENT) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:308:10: ( AS )? ident= IDENT
                    {
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:308:10: ( AS )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);
                    
                    if ( (LA13_0==AS) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:308:11: AS
                            {
                            match(input,AS,FOLLOW_AS_in_deleteClause1396); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    ident=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_deleteClause1402); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:317:1: selectClause returns [Object node] : t= SELECT ( DISTINCT )? n= selectItem ( COMMA n= selectItem )* ;
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
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:329:7: (t= SELECT ( DISTINCT )? n= selectItem ( COMMA n= selectItem )* )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:329:7: t= SELECT ( DISTINCT )? n= selectItem ( COMMA n= selectItem )*
            {
            t=(Token)input.LT(1);
            match(input,SELECT,FOLLOW_SELECT_in_selectClause1449); if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:329:16: ( DISTINCT )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            
            if ( (LA15_0==DISTINCT) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:329:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_selectClause1452); if (failed) return node;
                    if ( backtracking==0 ) {
                       ((selectClause_scope)selectClause_stack.peek()).distinct = true; 
                    }
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_selectItem_in_selectClause1468);
            n=selectItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              
                            ((selectClause_scope)selectClause_stack.peek()).exprs.add(n.expr);
                            ((selectClause_scope)selectClause_stack.peek()).idents.add(n.ident);
                        
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:335:11: ( COMMA n= selectItem )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);
                
                if ( (LA16_0==COMMA) ) {
                    alt16=1;
                }
                
            
                switch (alt16) {
            	case 1 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:335:13: COMMA n= selectItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_selectClause1496); if (failed) return node;
            	    pushFollow(FOLLOW_selectItem_in_selectClause1502);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:348:1: selectItem returns [Object expr, Object ident] : e= selectExpression ( ( AS )? identifier= IDENT )? ;
    public final selectItem_return selectItem() throws RecognitionException {
        selectItem_return retval = new selectItem_return();
        retval.start = input.LT(1);
    
        Token identifier=null;
        Object e = null;
        
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:349:7: (e= selectExpression ( ( AS )? identifier= IDENT )? )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:349:7: e= selectExpression ( ( AS )? identifier= IDENT )?
            {
            pushFollow(FOLLOW_selectExpression_in_selectItem1598);
            e=selectExpression();
            _fsp--;
            if (failed) return retval;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:349:28: ( ( AS )? identifier= IDENT )?
            int alt18=2;
            int LA18_0 = input.LA(1);
            
            if ( (LA18_0==AS||LA18_0==IDENT) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:349:29: ( AS )? identifier= IDENT
                    {
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:349:29: ( AS )?
                    int alt17=2;
                    int LA17_0 = input.LA(1);
                    
                    if ( (LA17_0==AS) ) {
                        alt17=1;
                    }
                    switch (alt17) {
                        case 1 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:349:30: AS
                            {
                            match(input,AS,FOLLOW_AS_in_selectItem1602); if (failed) return retval;
                            
                            }
                            break;
                    
                    }

                    identifier=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_selectItem1610); if (failed) return retval;
                    
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );
    public final Object selectExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:364:7: (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression )
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 53, input);
                        
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 54, input);
                        
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 55, input);
                        
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 56, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 48, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 1, input);
                
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 57, input);
                        
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 58, input);
                        
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 59, input);
                        
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 60, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 49, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 2, input);
                
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 61, input);
                        
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 62, input);
                        
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 63, input);
                        
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 64, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 50, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 3, input);
                
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 65, input);
                        
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 66, input);
                        
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 67, input);
                        
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 68, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 51, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 4, input);
                
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 69, input);
                        
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 70, input);
                        
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 71, input);
                        
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
                                new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 72, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 52, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 5, input);
                
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
                    new NoViableAltException("362:1: selectExpression returns [Object node] : (n= aggregateExpression | n= scalarExpression | OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | n= constructorExpression | n= mapEntryExpression );", 19, 0, input);
            
                throw nvae;
            }
            
            switch (alt19) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:364:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_selectExpression1654);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:365:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_selectExpression1668);
                    n=scalarExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:366:7: OBJECT LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    match(input,OBJECT,FOLLOW_OBJECT_in_selectExpression1678); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1680); if (failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_selectExpression1686);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1688); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:367:7: n= constructorExpression
                    {
                    pushFollow(FOLLOW_constructorExpression_in_selectExpression1703);
                    n=constructorExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:368:7: n= mapEntryExpression
                    {
                    pushFollow(FOLLOW_mapEntryExpression_in_selectExpression1718);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:371:1: mapEntryExpression returns [Object node] : l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET ;
    public final Object mapEntryExpression() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:373:7: (l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:373:7: l= ENTRY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,ENTRY,FOLLOW_ENTRY_in_mapEntryExpression1750); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mapEntryExpression1752); if (failed) return node;
            pushFollow(FOLLOW_variableAccessOrTypeConstant_in_mapEntryExpression1758);
            n=variableAccessOrTypeConstant();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mapEntryExpression1760); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:376:1: pathExprOrVariableAccess returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )* ;
    public final Object pathExprOrVariableAccess() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
        
            node = null;
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:380:7: (n= qualifiedIdentificationVariable (d= DOT right= attribute )* )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:380:7: n= qualifiedIdentificationVariable (d= DOT right= attribute )*
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_pathExprOrVariableAccess1792);
            n=qualifiedIdentificationVariable();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:381:9: (d= DOT right= attribute )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);
                
                if ( (LA20_0==DOT) ) {
                    alt20=1;
                }
                
            
                switch (alt20) {
            	case 1 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:381:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pathExprOrVariableAccess1807); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_pathExprOrVariableAccess1813);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:386:1: qualifiedIdentificationVariable returns [Object node] : (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET );
    public final Object qualifiedIdentificationVariable() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:388:7: (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
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
                    new NoViableAltException("386:1: qualifiedIdentificationVariable returns [Object node] : (n= variableAccessOrTypeConstant | l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET | l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET );", 21, 0, input);
            
                throw nvae;
            }
            
            switch (alt21) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:388:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1869);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:389:7: l= KEY LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    l=(Token)input.LT(1);
                    match(input,KEY,FOLLOW_KEY_in_qualifiedIdentificationVariable1883); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1885); if (failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1891);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1893); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newKey(l.getLine(), l.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:390:7: l= VALUE LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
                    {
                    l=(Token)input.LT(1);
                    match(input,VALUE,FOLLOW_VALUE_in_qualifiedIdentificationVariable1908); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1910); if (failed) return node;
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1916);
                    n=variableAccessOrTypeConstant();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1918); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:393:1: aggregateExpression returns [Object node] : (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET );
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
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:401:7: (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET )
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
                    new NoViableAltException("393:1: aggregateExpression returns [Object node] : (t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET | t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET );", 27, 0, input);
            
                throw nvae;
            }
            
            switch (alt27) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:401:7: t1= AVG LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t1=(Token)input.LT(1);
                    match(input,AVG,FOLLOW_AVG_in_aggregateExpression1951); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1953); if (failed) return node;
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:401:33: ( DISTINCT )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);
                    
                    if ( (LA22_0==DISTINCT) ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:401:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression1956); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression1974);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1976); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAvg(t1.getLine(), t1.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:404:7: t2= MAX LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t2=(Token)input.LT(1);
                    match(input,MAX,FOLLOW_MAX_in_aggregateExpression1997); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1999); if (failed) return node;
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:404:33: ( DISTINCT )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);
                    
                    if ( (LA23_0==DISTINCT) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:404:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2002); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression2021);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2023); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newMax(t2.getLine(), t2.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:407:7: t3= MIN LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t3=(Token)input.LT(1);
                    match(input,MIN,FOLLOW_MIN_in_aggregateExpression2043); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2045); if (failed) return node;
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:407:33: ( DISTINCT )?
                    int alt24=2;
                    int LA24_0 = input.LA(1);
                    
                    if ( (LA24_0==DISTINCT) ) {
                        alt24=1;
                    }
                    switch (alt24) {
                        case 1 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:407:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2048); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression2066);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2068); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newMin(t3.getLine(), t3.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 4 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:410:7: t4= SUM LEFT_ROUND_BRACKET ( DISTINCT )? n= stateFieldPathExpression RIGHT_ROUND_BRACKET
                    {
                    t4=(Token)input.LT(1);
                    match(input,SUM,FOLLOW_SUM_in_aggregateExpression2088); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2090); if (failed) return node;
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:410:33: ( DISTINCT )?
                    int alt25=2;
                    int LA25_0 = input.LA(1);
                    
                    if ( (LA25_0==DISTINCT) ) {
                        alt25=1;
                    }
                    switch (alt25) {
                        case 1 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:410:34: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2093); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_stateFieldPathExpression_in_aggregateExpression2111);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2113); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newSum(t4.getLine(), t4.getCharPositionInLine(), ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct, n); 
                    }
                    
                    }
                    break;
                case 5 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:413:7: t5= COUNT LEFT_ROUND_BRACKET ( DISTINCT )? n= pathExprOrVariableAccess RIGHT_ROUND_BRACKET
                    {
                    t5=(Token)input.LT(1);
                    match(input,COUNT,FOLLOW_COUNT_in_aggregateExpression2133); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2135); if (failed) return node;
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:413:35: ( DISTINCT )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);
                    
                    if ( (LA26_0==DISTINCT) ) {
                        alt26=1;
                    }
                    switch (alt26) {
                        case 1 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:413:36: DISTINCT
                            {
                            match(input,DISTINCT,FOLLOW_DISTINCT_in_aggregateExpression2138); if (failed) return node;
                            if ( backtracking==0 ) {
                               ((aggregateExpression_scope)aggregateExpression_stack.peek()).distinct = true; 
                            }
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_aggregateExpression2156);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2158); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:418:1: constructorExpression returns [Object node] : t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET ;
    public final Object constructorExpression() throws RecognitionException {
        constructorExpression_stack.push(new constructorExpression_scope());

        Object node = null;
    
        Token t=null;
        String className = null;

        Object n = null;
        
    
         
            node = null;
            ((constructorExpression_scope)constructorExpression_stack.peek()).args = new ArrayList();
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:426:7: (t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:426:7: t= NEW className= constructorName LEFT_ROUND_BRACKET n= constructorItem ( COMMA n= constructorItem )* RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,NEW,FOLLOW_NEW_in_constructorExpression2201); if (failed) return node;
            pushFollow(FOLLOW_constructorName_in_constructorExpression2207);
            className=constructorName();
            _fsp--;
            if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression2217); if (failed) return node;
            pushFollow(FOLLOW_constructorItem_in_constructorExpression2232);
            n=constructorItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((constructorExpression_scope)constructorExpression_stack.peek()).args.add(n); 
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:429:9: ( COMMA n= constructorItem )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);
                
                if ( (LA28_0==COMMA) ) {
                    alt28=1;
                }
                
            
                switch (alt28) {
            	case 1 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:429:11: COMMA n= constructorItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_constructorExpression2247); if (failed) return node;
            	    pushFollow(FOLLOW_constructorItem_in_constructorExpression2253);
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

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression2268); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:437:1: constructorName returns [String className] : i1= IDENT ( DOT i2= IDENT )* ;
    public final String constructorName() throws RecognitionException {
        constructorName_stack.push(new constructorName_scope());

        String className = null;
    
        Token i1=null;
        Token i2=null;
    
         
            className = null;
            ((constructorName_scope)constructorName_stack.peek()).buf = new StringBuffer(); 
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:445:7: (i1= IDENT ( DOT i2= IDENT )* )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:445:7: i1= IDENT ( DOT i2= IDENT )*
            {
            i1=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_constructorName2309); if (failed) return className;
            if ( backtracking==0 ) {
               ((constructorName_scope)constructorName_stack.peek()).buf.append(i1.getText()); 
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:446:9: ( DOT i2= IDENT )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);
                
                if ( (LA29_0==DOT) ) {
                    alt29=1;
                }
                
            
                switch (alt29) {
            	case 1 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:446:11: DOT i2= IDENT
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_constructorName2323); if (failed) return className;
            	    i2=(Token)input.LT(1);
            	    match(input,IDENT,FOLLOW_IDENT_in_constructorName2327); if (failed) return className;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );
    public final Object constructorItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:452:7: (n= scalarExpression | n= aggregateExpression )
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 50, input);
                        
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 51, input);
                        
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 52, input);
                        
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 53, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 45, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 3, input);
                
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 54, input);
                        
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 55, input);
                        
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 56, input);
                        
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 57, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 46, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 4, input);
                
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 58, input);
                        
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 59, input);
                        
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 60, input);
                        
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 61, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 47, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 5, input);
                
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 62, input);
                        
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 63, input);
                        
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 64, input);
                        
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 65, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 48, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 6, input);
                
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 66, input);
                        
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 67, input);
                        
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 68, input);
                        
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
                                new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 69, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 49, input);
                    
                        throw nvae;
                    }
                
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 7, input);
                
                    throw nvae;
                }
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("450:1: constructorItem returns [Object node] : (n= scalarExpression | n= aggregateExpression );", 30, 0, input);
            
                throw nvae;
            }
            
            switch (alt30) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:452:7: n= scalarExpression
                    {
                    pushFollow(FOLLOW_scalarExpression_in_constructorItem2371);
                    n=scalarExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:453:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_constructorItem2385);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:457:1: fromClause returns [Object node] : t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )* ;
    public final Object fromClause() throws RecognitionException {
        fromClause_stack.push(new fromClause_scope());

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         
            node = null; 
            ((fromClause_scope)fromClause_stack.peek()).varDecls = new ArrayList();
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:465:7: (t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )* )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:465:7: t= FROM identificationVariableDeclaration[$fromClause::varDecls] ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )*
            {
            t=(Token)input.LT(1);
            match(input,FROM,FOLLOW_FROM_in_fromClause2419); if (failed) return node;
            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2421);
            identificationVariableDeclaration(((fromClause_scope)fromClause_stack.peek()).varDecls);
            _fsp--;
            if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:466:9: ( COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration ) )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);
                
                if ( (LA32_0==COMMA) ) {
                    alt32=1;
                }
                
            
                switch (alt32) {
            	case 1 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:466:10: COMMA ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_fromClause2433); if (failed) return node;
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:466:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )
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
            	                new NoViableAltException("466:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )", 31, 1, input);
            	        
            	            throw nvae;
            	        }
            	    }
            	    else if ( ((LA31_0>=ABS && LA31_0<=HAVING)||(LA31_0>=INDEX && LA31_0<=TIME_STRING)) ) {
            	        alt31=1;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return node;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("466:17: ( identificationVariableDeclaration[$fromClause::varDecls] | n= collectionMemberDeclaration )", 31, 0, input);
            	    
            	        throw nvae;
            	    }
            	    switch (alt31) {
            	        case 1 :
            	            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:466:19: identificationVariableDeclaration[$fromClause::varDecls]
            	            {
            	            pushFollow(FOLLOW_identificationVariableDeclaration_in_fromClause2438);
            	            identificationVariableDeclaration(((fromClause_scope)fromClause_stack.peek()).varDecls);
            	            _fsp--;
            	            if (failed) return node;
            	            
            	            }
            	            break;
            	        case 2 :
            	            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:467:19: n= collectionMemberDeclaration
            	            {
            	            pushFollow(FOLLOW_collectionMemberDeclaration_in_fromClause2463);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:473:1: identificationVariableDeclaration[List varDecls] : node= rangeVariableDeclaration (node= join )* ;
    public final void identificationVariableDeclaration(List varDecls) throws RecognitionException {
        Object node = null;
        
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:474:7: (node= rangeVariableDeclaration (node= join )* )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:474:7: node= rangeVariableDeclaration (node= join )*
            {
            pushFollow(FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2529);
            node=rangeVariableDeclaration();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               varDecls.add(node); 
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:475:9: (node= join )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);
                
                if ( (LA33_0==INNER||LA33_0==JOIN||LA33_0==LEFT) ) {
                    alt33=1;
                }
                
            
                switch (alt33) {
            	case 1 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:475:11: node= join
            	    {
            	    pushFollow(FOLLOW_join_in_identificationVariableDeclaration2548);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:478:1: rangeVariableDeclaration returns [Object node] : schema= abstractSchemaName ( AS )? i= IDENT ;
    public final Object rangeVariableDeclaration() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        String schema = null;
        
    
         
            node = null; 
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:482:7: (schema= abstractSchemaName ( AS )? i= IDENT )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:482:7: schema= abstractSchemaName ( AS )? i= IDENT
            {
            pushFollow(FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2583);
            schema=abstractSchemaName();
            _fsp--;
            if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:482:35: ( AS )?
            int alt34=2;
            int LA34_0 = input.LA(1);
            
            if ( (LA34_0==AS) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:482:36: AS
                    {
                    match(input,AS,FOLLOW_AS_in_rangeVariableDeclaration2586); if (failed) return node;
                    
                    }
                    break;
            
            }

            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_rangeVariableDeclaration2592); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:493:1: abstractSchemaName returns [String schema] : ident= . ;
    public final String abstractSchemaName() throws RecognitionException {

        String schema = null;
    
        Token ident=null;
    
         schema = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:495:7: (ident= . )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:495:7: ident= .
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:502:1: join returns [Object node] : outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression ) ;
    public final Object join() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        Token t=null;
        boolean outerJoin = false;

        Object n = null;
        
    
         
            node = null;
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:506:7: (outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression ) )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:506:7: outerJoin= joinSpec (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )
            {
            pushFollow(FOLLOW_joinSpec_in_join2675);
            outerJoin=joinSpec();
            _fsp--;
            if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:507:7: (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )
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
                    new NoViableAltException("507:7: (n= joinAssociationPathExpression ( AS )? i= IDENT | t= FETCH n= joinAssociationPathExpression )", 36, 0, input);
            
                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:507:9: n= joinAssociationPathExpression ( AS )? i= IDENT
                    {
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2689);
                    n=joinAssociationPathExpression();
                    _fsp--;
                    if (failed) return node;
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:507:43: ( AS )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);
                    
                    if ( (LA35_0==AS) ) {
                        alt35=1;
                    }
                    switch (alt35) {
                        case 1 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:507:44: AS
                            {
                            match(input,AS,FOLLOW_AS_in_join2692); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_join2698); if (failed) return node;
                    if ( backtracking==0 ) {
                      
                                  node = factory.newJoinVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                     outerJoin, n, i.getText()); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:512:9: t= FETCH n= joinAssociationPathExpression
                    {
                    t=(Token)input.LT(1);
                    match(input,FETCH,FOLLOW_FETCH_in_join2720); if (failed) return node;
                    pushFollow(FOLLOW_joinAssociationPathExpression_in_join2726);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:519:1: joinSpec returns [boolean outer] : ( LEFT ( OUTER )? | INNER )? JOIN ;
    public final boolean joinSpec() throws RecognitionException {

        boolean outer = false;
    
         outer = false; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:521:7: ( ( LEFT ( OUTER )? | INNER )? JOIN )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:521:7: ( LEFT ( OUTER )? | INNER )? JOIN
            {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:521:7: ( LEFT ( OUTER )? | INNER )?
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
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:521:8: LEFT ( OUTER )?
                    {
                    match(input,LEFT,FOLLOW_LEFT_in_joinSpec2772); if (failed) return outer;
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:521:13: ( OUTER )?
                    int alt37=2;
                    int LA37_0 = input.LA(1);
                    
                    if ( (LA37_0==OUTER) ) {
                        alt37=1;
                    }
                    switch (alt37) {
                        case 1 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:521:14: OUTER
                            {
                            match(input,OUTER,FOLLOW_OUTER_in_joinSpec2775); if (failed) return outer;
                            
                            }
                            break;
                    
                    }

                    if ( backtracking==0 ) {
                       outer = true; 
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:521:44: INNER
                    {
                    match(input,INNER,FOLLOW_INNER_in_joinSpec2784); if (failed) return outer;
                    
                    }
                    break;
            
            }

            match(input,JOIN,FOLLOW_JOIN_in_joinSpec2790); if (failed) return outer;
            
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:524:1: collectionMemberDeclaration returns [Object node] : t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT ;
    public final Object collectionMemberDeclaration() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Token i=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:526:7: (t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:526:7: t= IN LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ( AS )? i= IDENT
            {
            t=(Token)input.LT(1);
            match(input,IN,FOLLOW_IN_in_collectionMemberDeclaration2818); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2820); if (failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2826);
            n=collectionValuedPathExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2828); if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:527:7: ( AS )?
            int alt39=2;
            int LA39_0 = input.LA(1);
            
            if ( (LA39_0==AS) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:527:8: AS
                    {
                    match(input,AS,FOLLOW_AS_in_collectionMemberDeclaration2838); if (failed) return node;
                    
                    }
                    break;
            
            }

            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_collectionMemberDeclaration2844); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:534:1: collectionValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object collectionValuedPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:536:7: (n= pathExpression )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:536:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_collectionValuedPathExpression2882);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:539:1: associationPathExpression returns [Object node] : n= pathExpression ;
    public final Object associationPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:541:7: (n= pathExpression )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:541:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_associationPathExpression2914);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:544:1: joinAssociationPathExpression returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )+ ;
    public final Object joinAssociationPathExpression() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
        
            node = null; 
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:548:8: (n= qualifiedIdentificationVariable (d= DOT right= attribute )+ )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:548:8: n= qualifiedIdentificationVariable (d= DOT right= attribute )+
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_joinAssociationPathExpression2947);
            n=qualifiedIdentificationVariable();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:549:9: (d= DOT right= attribute )+
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
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:549:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_joinAssociationPathExpression2962); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_joinAssociationPathExpression2968);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:554:1: singleValuedPathExpression returns [Object node] : n= pathExpression ;
    public final Object singleValuedPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:556:7: (n= pathExpression )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:556:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_singleValuedPathExpression3024);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:559:1: stateFieldPathExpression returns [Object node] : n= pathExpression ;
    public final Object stateFieldPathExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:561:7: (n= pathExpression )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:561:7: n= pathExpression
            {
            pushFollow(FOLLOW_pathExpression_in_stateFieldPathExpression3056);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:564:1: pathExpression returns [Object node] : n= qualifiedIdentificationVariable (d= DOT right= attribute )+ ;
    public final Object pathExpression() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:568:7: (n= qualifiedIdentificationVariable (d= DOT right= attribute )+ )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:568:7: n= qualifiedIdentificationVariable (d= DOT right= attribute )+
            {
            pushFollow(FOLLOW_qualifiedIdentificationVariable_in_pathExpression3088);
            n=qualifiedIdentificationVariable();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:569:9: (d= DOT right= attribute )+
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
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:569:10: d= DOT right= attribute
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_pathExpression3103); if (failed) return node;
            	    pushFollow(FOLLOW_attribute_in_pathExpression3109);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:580:1: attribute returns [Object node] : i= . ;
    public final Object attribute() throws RecognitionException {

        Object node = null;
    
        Token i=null;
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:583:7: (i= . )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:583:7: i= .
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:590:1: variableAccessOrTypeConstant returns [Object node] : i= IDENT ;
    public final Object variableAccessOrTypeConstant() throws RecognitionException {

        Object node = null;
    
        Token i=null;
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:592:7: (i= IDENT )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:592:7: i= IDENT
            {
            i=(Token)input.LT(1);
            match(input,IDENT,FOLLOW_IDENT_in_variableAccessOrTypeConstant3205); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:596:1: whereClause returns [Object node] : t= WHERE n= conditionalExpression ;
    public final Object whereClause() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:598:7: (t= WHERE n= conditionalExpression )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:598:7: t= WHERE n= conditionalExpression
            {
            t=(Token)input.LT(1);
            match(input,WHERE,FOLLOW_WHERE_in_whereClause3243); if (failed) return node;
            pushFollow(FOLLOW_conditionalExpression_in_whereClause3249);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:604:1: conditionalExpression returns [Object node] : n= conditionalTerm (t= OR right= conditionalTerm )* ;
    public final Object conditionalExpression() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:608:7: (n= conditionalTerm (t= OR right= conditionalTerm )* )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:608:7: n= conditionalTerm (t= OR right= conditionalTerm )*
            {
            pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression3291);
            n=conditionalTerm();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:609:9: (t= OR right= conditionalTerm )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);
                
                if ( (LA42_0==OR) ) {
                    alt42=1;
                }
                
            
                switch (alt42) {
            	case 1 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:609:10: t= OR right= conditionalTerm
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,OR,FOLLOW_OR_in_conditionalExpression3306); if (failed) return node;
            	    pushFollow(FOLLOW_conditionalTerm_in_conditionalExpression3312);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:614:1: conditionalTerm returns [Object node] : n= conditionalFactor (t= AND right= conditionalFactor )* ;
    public final Object conditionalTerm() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:618:7: (n= conditionalFactor (t= AND right= conditionalFactor )* )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:618:7: n= conditionalFactor (t= AND right= conditionalFactor )*
            {
            pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm3367);
            n=conditionalFactor();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:619:9: (t= AND right= conditionalFactor )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);
                
                if ( (LA43_0==AND) ) {
                    alt43=1;
                }
                
            
                switch (alt43) {
            	case 1 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:619:10: t= AND right= conditionalFactor
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,AND,FOLLOW_AND_in_conditionalTerm3382); if (failed) return node;
            	    pushFollow(FOLLOW_conditionalFactor_in_conditionalTerm3388);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:624:1: conditionalFactor returns [Object node] : (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] ) ;
    public final Object conditionalFactor() throws RecognitionException {

        Object node = null;
    
        Token n=null;
        Object n1 = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:626:7: ( (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] ) )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:626:7: (n= NOT )? (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )
            {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:626:7: (n= NOT )?
            int alt44=2;
            int LA44_0 = input.LA(1);
            
            if ( (LA44_0==NOT) ) {
                alt44=1;
            }
            switch (alt44) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:626:8: n= NOT
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_conditionalFactor3443); if (failed) return node;
                    
                    }
                    break;
            
            }

            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:627:9: (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )
            int alt45=2;
            int LA45_0 = input.LA(1);
            
            if ( (LA45_0==ABS||LA45_0==AVG||(LA45_0>=CASE && LA45_0<=CURRENT_TIMESTAMP)||LA45_0==FALSE||LA45_0==FUNC||LA45_0==INDEX||LA45_0==KEY||LA45_0==LENGTH||(LA45_0>=LOCATE && LA45_0<=MAX)||(LA45_0>=MIN && LA45_0<=MOD)||LA45_0==NULLIF||(LA45_0>=SIZE && LA45_0<=SQRT)||(LA45_0>=SUBSTRING && LA45_0<=SUM)||(LA45_0>=TRIM && LA45_0<=TYPE)||(LA45_0>=UPPER && LA45_0<=VALUE)||LA45_0==IDENT||LA45_0==LEFT_ROUND_BRACKET||(LA45_0>=PLUS && LA45_0<=MINUS)||(LA45_0>=INTEGER_LITERAL && LA45_0<=NAMED_PARAM)) ) {
                alt45=1;
            }
            else if ( (LA45_0==EXISTS) ) {
                alt45=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("627:9: (n1= conditionalPrimary | n1= existsExpression[(n!=null)] )", 45, 0, input);
            
                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:627:11: n1= conditionalPrimary
                    {
                    pushFollow(FOLLOW_conditionalPrimary_in_conditionalFactor3462);
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
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:634:11: n1= existsExpression[(n!=null)]
                    {
                    pushFollow(FOLLOW_existsExpression_in_conditionalFactor3491);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );
    public final Object conditionalPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:640:7: ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression )
            int alt46=2;
            int LA46_0 = input.LA(1);
            
            if ( (LA46_0==LEFT_ROUND_BRACKET) ) {
                int LA46_1 = input.LA(2);
                
                if ( (LA46_1==NOT) && (synpred1())) {
                    alt46=1;
                }
                else if ( (LA46_1==LEFT_ROUND_BRACKET) ) {
                    int LA46_46 = input.LA(3);
                    
                    if ( (LA46_46==PLUS) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 92, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==MINUS) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 93, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==AVG) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 94, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==MAX) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 95, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==MIN) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 96, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==SUM) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 97, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==COUNT) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 98, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==IDENT) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 99, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==KEY) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 100, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==VALUE) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 101, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==POSITIONAL_PARAM) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 102, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==NAMED_PARAM) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 103, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==CASE) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 104, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==COALESCE) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 105, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==NULLIF) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 106, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==ABS) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 107, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==LENGTH) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 108, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==MOD) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 109, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==SQRT) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 110, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==LOCATE) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 111, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==SIZE) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 112, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==INDEX) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 113, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==FUNC) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 114, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 115, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==INTEGER_LITERAL) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 116, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==LONG_LITERAL) ) {
                        int LA46_117 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 117, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==FLOAT_LITERAL) ) {
                        int LA46_118 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 118, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==DOUBLE_LITERAL) ) {
                        int LA46_119 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 119, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_46==NOT) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==CURRENT_DATE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==CURRENT_TIME) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==CURRENT_TIMESTAMP) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==CONCAT) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==SUBSTRING) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==TRIM) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==UPPER) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==LOWER) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==STRING_LITERAL_DOUBLE_QUOTED) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==STRING_LITERAL_SINGLE_QUOTED) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==TRUE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==FALSE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==DATE_LITERAL) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==TIME_LITERAL) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==TIMESTAMP_LITERAL) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==TYPE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==EXISTS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_46==SELECT) && (synpred1())) {
                        alt46=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 46, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==PLUS) ) {
                    switch ( input.LA(3) ) {
                    case AVG:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 139, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MAX:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 140, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MIN:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 141, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUM:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 142, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COUNT:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 143, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 144, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 145, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 146, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case POSITIONAL_PARAM:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 147, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NAMED_PARAM:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 148, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CASE:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 149, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COALESCE:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 150, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NULLIF:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 151, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case ABS:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 152, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LENGTH:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 153, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MOD:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 154, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SQRT:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 155, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOCATE:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 156, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SIZE:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 157, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INDEX:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 158, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FUNC:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 159, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_ROUND_BRACKET:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 160, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INTEGER_LITERAL:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 161, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LONG_LITERAL:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 162, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FLOAT_LITERAL:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 163, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case DOUBLE_LITERAL:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 164, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 47, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA46_1==MINUS) ) {
                    switch ( input.LA(3) ) {
                    case AVG:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 165, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MAX:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 166, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MIN:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 167, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SUM:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 168, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COUNT:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 169, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case IDENT:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 170, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 171, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 172, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case POSITIONAL_PARAM:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 173, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NAMED_PARAM:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 174, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case CASE:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 175, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case COALESCE:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 176, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case NULLIF:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 177, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case ABS:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 178, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LENGTH:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 179, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case MOD:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 180, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SQRT:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 181, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LOCATE:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 182, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case SIZE:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 183, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INDEX:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 184, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FUNC:
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 185, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LEFT_ROUND_BRACKET:
                        {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 186, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case INTEGER_LITERAL:
                        {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 187, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case LONG_LITERAL:
                        {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 188, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case FLOAT_LITERAL:
                        {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 189, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case DOUBLE_LITERAL:
                        {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 190, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 48, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA46_1==AVG) ) {
                    int LA46_49 = input.LA(3);
                    
                    if ( (LA46_49==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 191, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 49, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==MAX) ) {
                    int LA46_50 = input.LA(3);
                    
                    if ( (LA46_50==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 192, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 50, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==MIN) ) {
                    int LA46_51 = input.LA(3);
                    
                    if ( (LA46_51==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 193, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 51, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==SUM) ) {
                    int LA46_52 = input.LA(3);
                    
                    if ( (LA46_52==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 194, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 52, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==COUNT) ) {
                    int LA46_53 = input.LA(3);
                    
                    if ( (LA46_53==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 195, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 53, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==IDENT) ) {
                    int LA46_54 = input.LA(3);
                    
                    if ( (LA46_54==DOT) ) {
                        int LA46_196 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 196, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_54==MULTIPLY) ) {
                        int LA46_197 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 197, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_54==DIVIDE) ) {
                        int LA46_198 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 198, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_54==PLUS) ) {
                        int LA46_199 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 199, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_54==MINUS) ) {
                        int LA46_200 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 200, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_54==RIGHT_ROUND_BRACKET) ) {
                        alt46=2;
                    }
                    else if ( (LA46_54==EQUALS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_54==NOT_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_54==GREATER_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_54==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_54==LESS_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_54==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_54==NOT) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_54==BETWEEN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_54==LIKE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_54==IN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_54==MEMBER) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_54==IS) && (synpred1())) {
                        alt46=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 54, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==KEY) ) {
                    int LA46_55 = input.LA(3);
                    
                    if ( (LA46_55==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 214, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 55, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==VALUE) ) {
                    int LA46_56 = input.LA(3);
                    
                    if ( (LA46_56==LEFT_ROUND_BRACKET) ) {
                        int LA46_215 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 215, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 56, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==POSITIONAL_PARAM) ) {
                    int LA46_57 = input.LA(3);
                    
                    if ( (LA46_57==MULTIPLY) ) {
                        int LA46_216 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 216, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_57==DIVIDE) ) {
                        int LA46_217 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 217, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_57==PLUS) ) {
                        int LA46_218 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 218, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_57==MINUS) ) {
                        int LA46_219 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 219, input);
                        
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
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 57, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==NAMED_PARAM) ) {
                    int LA46_58 = input.LA(3);
                    
                    if ( (LA46_58==MULTIPLY) ) {
                        int LA46_233 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 233, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_58==DIVIDE) ) {
                        int LA46_234 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 234, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_58==PLUS) ) {
                        int LA46_235 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 235, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_58==MINUS) ) {
                        int LA46_236 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 236, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_58==EQUALS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_58==NOT_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_58==GREATER_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_58==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_58==LESS_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_58==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_58==NOT) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_58==BETWEEN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_58==LIKE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_58==IN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_58==MEMBER) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_58==IS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_58==RIGHT_ROUND_BRACKET) ) {
                        alt46=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 58, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==CASE) ) {
                    switch ( input.LA(3) ) {
                    case IDENT:
                        {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 250, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case KEY:
                        {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 251, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case VALUE:
                        {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 252, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case TYPE:
                        {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 253, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    case WHEN:
                        {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 254, input);
                        
                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 59, input);
                    
                        throw nvae;
                    }
                
                }
                else if ( (LA46_1==COALESCE) ) {
                    int LA46_60 = input.LA(3);
                    
                    if ( (LA46_60==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 255, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 60, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==NULLIF) ) {
                    int LA46_61 = input.LA(3);
                    
                    if ( (LA46_61==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 256, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 61, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==ABS) ) {
                    int LA46_62 = input.LA(3);
                    
                    if ( (LA46_62==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 257, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 62, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==LENGTH) ) {
                    int LA46_63 = input.LA(3);
                    
                    if ( (LA46_63==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 258, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 63, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==MOD) ) {
                    int LA46_64 = input.LA(3);
                    
                    if ( (LA46_64==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 259, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 64, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==SQRT) ) {
                    int LA46_65 = input.LA(3);
                    
                    if ( (LA46_65==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 260, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 65, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==LOCATE) ) {
                    int LA46_66 = input.LA(3);
                    
                    if ( (LA46_66==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 261, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 66, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==SIZE) ) {
                    int LA46_67 = input.LA(3);
                    
                    if ( (LA46_67==LEFT_ROUND_BRACKET) ) {
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
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 262, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 67, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==INDEX) ) {
                    int LA46_68 = input.LA(3);
                    
                    if ( (LA46_68==LEFT_ROUND_BRACKET) ) {
                        int LA46_263 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 263, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 68, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==FUNC) ) {
                    int LA46_69 = input.LA(3);
                    
                    if ( (LA46_69==LEFT_ROUND_BRACKET) ) {
                        int LA46_264 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 264, input);
                        
                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 69, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==INTEGER_LITERAL) ) {
                    int LA46_70 = input.LA(3);
                    
                    if ( (LA46_70==MULTIPLY) ) {
                        int LA46_265 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 265, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_70==DIVIDE) ) {
                        int LA46_266 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 266, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_70==PLUS) ) {
                        int LA46_267 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 267, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_70==MINUS) ) {
                        int LA46_268 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 268, input);
                        
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
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 70, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==LONG_LITERAL) ) {
                    int LA46_71 = input.LA(3);
                    
                    if ( (LA46_71==MULTIPLY) ) {
                        int LA46_282 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 282, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_71==DIVIDE) ) {
                        int LA46_283 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 283, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_71==PLUS) ) {
                        int LA46_284 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 284, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_71==MINUS) ) {
                        int LA46_285 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 285, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_71==RIGHT_ROUND_BRACKET) ) {
                        alt46=2;
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
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 71, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==FLOAT_LITERAL) ) {
                    int LA46_72 = input.LA(3);
                    
                    if ( (LA46_72==MULTIPLY) ) {
                        int LA46_299 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 299, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_72==DIVIDE) ) {
                        int LA46_300 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 300, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_72==PLUS) ) {
                        int LA46_301 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 301, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_72==MINUS) ) {
                        int LA46_302 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 302, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_72==RIGHT_ROUND_BRACKET) ) {
                        alt46=2;
                    }
                    else if ( (LA46_72==EQUALS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_72==NOT_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_72==GREATER_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_72==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_72==LESS_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_72==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_72==NOT) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_72==BETWEEN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_72==LIKE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_72==IN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_72==MEMBER) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_72==IS) && (synpred1())) {
                        alt46=1;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 72, input);
                    
                        throw nvae;
                    }
                }
                else if ( (LA46_1==DOUBLE_LITERAL) ) {
                    int LA46_73 = input.LA(3);
                    
                    if ( (LA46_73==MULTIPLY) ) {
                        int LA46_316 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 316, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_73==DIVIDE) ) {
                        int LA46_317 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 317, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_73==PLUS) ) {
                        int LA46_318 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 318, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_73==MINUS) ) {
                        int LA46_319 = input.LA(4);
                        
                        if ( (synpred1()) ) {
                            alt46=1;
                        }
                        else if ( (true) ) {
                            alt46=2;
                        }
                        else {
                            if (backtracking>0) {failed=true; return node;}
                            NoViableAltException nvae =
                                new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 319, input);
                        
                            throw nvae;
                        }
                    }
                    else if ( (LA46_73==EQUALS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_73==NOT_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_73==GREATER_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_73==GREATER_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_73==LESS_THAN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_73==LESS_THAN_EQUAL_TO) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_73==NOT) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_73==BETWEEN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_73==LIKE) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_73==IN) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_73==MEMBER) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_73==IS) && (synpred1())) {
                        alt46=1;
                    }
                    else if ( (LA46_73==RIGHT_ROUND_BRACKET) ) {
                        alt46=2;
                    }
                    else {
                        if (backtracking>0) {failed=true; return node;}
                        NoViableAltException nvae =
                            new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 73, input);
                    
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
                        new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA46_0==ABS||LA46_0==AVG||(LA46_0>=CASE && LA46_0<=CURRENT_TIMESTAMP)||LA46_0==FALSE||LA46_0==FUNC||LA46_0==INDEX||LA46_0==KEY||LA46_0==LENGTH||(LA46_0>=LOCATE && LA46_0<=MAX)||(LA46_0>=MIN && LA46_0<=MOD)||LA46_0==NULLIF||(LA46_0>=SIZE && LA46_0<=SQRT)||(LA46_0>=SUBSTRING && LA46_0<=SUM)||(LA46_0>=TRIM && LA46_0<=TYPE)||(LA46_0>=UPPER && LA46_0<=VALUE)||LA46_0==IDENT||(LA46_0>=PLUS && LA46_0<=MINUS)||(LA46_0>=INTEGER_LITERAL && LA46_0<=NAMED_PARAM)) ) {
                alt46=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("638:1: conditionalPrimary returns [Object node] : ( ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET | n= simpleConditionalExpression );", 46, 0, input);
            
                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:640:7: ( LEFT_ROUND_BRACKET conditionalExpression )=> LEFT_ROUND_BRACKET n= conditionalExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3548); if (failed) return node;
                    pushFollow(FOLLOW_conditionalExpression_in_conditionalPrimary3554);
                    n=conditionalExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3556); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:642:7: n= simpleConditionalExpression
                    {
                    pushFollow(FOLLOW_simpleConditionalExpression_in_conditionalPrimary3570);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:645:1: simpleConditionalExpression returns [Object node] : (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] | left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] );
    public final Object simpleConditionalExpression() throws RecognitionException {

        Object node = null;
    
        Object left = null;

        Object n = null;
        
    
         
            node = null; 
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:649:7: (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] | left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] )
            int alt47=2;
            int LA47_0 = input.LA(1);
            
            if ( (LA47_0==ABS||LA47_0==AVG||(LA47_0>=CASE && LA47_0<=COALESCE)||LA47_0==COUNT||LA47_0==FUNC||LA47_0==INDEX||LA47_0==KEY||LA47_0==LENGTH||LA47_0==LOCATE||LA47_0==MAX||(LA47_0>=MIN && LA47_0<=MOD)||LA47_0==NULLIF||(LA47_0>=SIZE && LA47_0<=SQRT)||LA47_0==SUM||LA47_0==VALUE||LA47_0==IDENT||LA47_0==LEFT_ROUND_BRACKET||(LA47_0>=PLUS && LA47_0<=MINUS)||(LA47_0>=INTEGER_LITERAL && LA47_0<=DOUBLE_LITERAL)||(LA47_0>=POSITIONAL_PARAM && LA47_0<=NAMED_PARAM)) ) {
                alt47=1;
            }
            else if ( (LA47_0==CONCAT||(LA47_0>=CURRENT_DATE && LA47_0<=CURRENT_TIMESTAMP)||LA47_0==FALSE||LA47_0==LOWER||LA47_0==SUBSTRING||(LA47_0>=TRIM && LA47_0<=TYPE)||LA47_0==UPPER||(LA47_0>=STRING_LITERAL_DOUBLE_QUOTED && LA47_0<=TIMESTAMP_LITERAL)) ) {
                alt47=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("645:1: simpleConditionalExpression returns [Object node] : (left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node] | left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node] );", 47, 0, input);
            
                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:649:7: left= arithmeticExpression n= simpleConditionalExpressionRemainder[$left.node]
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_simpleConditionalExpression3602);
                    left=arithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    pushFollow(FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3608);
                    n=simpleConditionalExpressionRemainder(left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:650:7: left= nonArithmeticScalarExpression n= simpleConditionalExpressionRemainder[$left.node]
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_simpleConditionalExpression3623);
                    left=nonArithmeticScalarExpression();
                    _fsp--;
                    if (failed) return node;
                    pushFollow(FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3629);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:653:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );
    public final Object simpleConditionalExpressionRemainder(Object left) throws RecognitionException {

        Object node = null;
    
        Token n1=null;
        Token n2=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:655:7: (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] )
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
                    new NoViableAltException("653:1: simpleConditionalExpressionRemainder[Object left] returns [Object node] : (n= comparisonExpression[left] | (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left] | IS (n2= NOT )? n= isExpression[(n2!=null), left] );", 50, 0, input);
            
                throw nvae;
            }
            
            switch (alt50) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:655:7: n= comparisonExpression[left]
                    {
                    pushFollow(FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3664);
                    n=comparisonExpression(left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:656:7: (n1= NOT )? n= conditionWithNotExpression[(n1!=null), left]
                    {
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:656:7: (n1= NOT )?
                    int alt48=2;
                    int LA48_0 = input.LA(1);
                    
                    if ( (LA48_0==NOT) ) {
                        alt48=1;
                    }
                    switch (alt48) {
                        case 1 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:656:8: n1= NOT
                            {
                            n1=(Token)input.LT(1);
                            match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3678); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3686);
                    n=conditionWithNotExpression((n1!=null),  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:657:7: IS (n2= NOT )? n= isExpression[(n2!=null), left]
                    {
                    match(input,IS,FOLLOW_IS_in_simpleConditionalExpressionRemainder3697); if (failed) return node;
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:657:10: (n2= NOT )?
                    int alt49=2;
                    int LA49_0 = input.LA(1);
                    
                    if ( (LA49_0==NOT) ) {
                        alt49=1;
                    }
                    switch (alt49) {
                        case 1 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:657:11: n2= NOT
                            {
                            n2=(Token)input.LT(1);
                            match(input,NOT,FOLLOW_NOT_in_simpleConditionalExpressionRemainder3702); if (failed) return node;
                            
                            }
                            break;
                    
                    }

                    pushFollow(FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3710);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:660:1: conditionWithNotExpression[boolean not, Object left] returns [Object node] : (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] );
    public final Object conditionWithNotExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:662:7: (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] )
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
                    new NoViableAltException("660:1: conditionWithNotExpression[boolean not, Object left] returns [Object node] : (n= betweenExpression[not, left] | n= likeExpression[not, left] | n= inExpression[not, left] | n= collectionMemberExpression[not, left] );", 51, 0, input);
            
                throw nvae;
            }
            
            switch (alt51) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:662:7: n= betweenExpression[not, left]
                    {
                    pushFollow(FOLLOW_betweenExpression_in_conditionWithNotExpression3745);
                    n=betweenExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:663:7: n= likeExpression[not, left]
                    {
                    pushFollow(FOLLOW_likeExpression_in_conditionWithNotExpression3760);
                    n=likeExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:664:7: n= inExpression[not, left]
                    {
                    pushFollow(FOLLOW_inExpression_in_conditionWithNotExpression3774);
                    n=inExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:665:7: n= collectionMemberExpression[not, left]
                    {
                    pushFollow(FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3788);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:668:1: isExpression[boolean not, Object left] returns [Object node] : (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] );
    public final Object isExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:670:7: (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] )
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
                    new NoViableAltException("668:1: isExpression[boolean not, Object left] returns [Object node] : (n= nullComparisonExpression[not, left] | n= emptyCollectionComparisonExpression[not, left] );", 52, 0, input);
            
                throw nvae;
            }
            switch (alt52) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:670:7: n= nullComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_nullComparisonExpression_in_isExpression3823);
                    n=nullComparisonExpression(not,  left);
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:671:7: n= emptyCollectionComparisonExpression[not, left]
                    {
                    pushFollow(FOLLOW_emptyCollectionComparisonExpression_in_isExpression3838);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:674:1: betweenExpression[boolean not, Object left] returns [Object node] : t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression ;
    public final Object betweenExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object lowerBound = null;

        Object upperBound = null;
        
    
        
            node = null;
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:678:7: (t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:678:7: t= BETWEEN lowerBound= arithmeticExpression AND upperBound= arithmeticExpression
            {
            t=(Token)input.LT(1);
            match(input,BETWEEN,FOLLOW_BETWEEN_in_betweenExpression3871); if (failed) return node;
            pushFollow(FOLLOW_arithmeticExpression_in_betweenExpression3885);
            lowerBound=arithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,AND,FOLLOW_AND_in_betweenExpression3887); if (failed) return node;
            pushFollow(FOLLOW_arithmeticExpression_in_betweenExpression3893);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:686:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET );
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
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:694:8: (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET )
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
                        new NoViableAltException("686:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET );", 55, 1, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("686:1: inExpression[boolean not, Object left] returns [Object node] : (t= IN n= inputParameter | t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET );", 55, 0, input);
            
                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:694:8: t= IN n= inputParameter
                    {
                    t=(Token)input.LT(1);
                    match(input,IN,FOLLOW_IN_in_inExpression3939); if (failed) return node;
                    pushFollow(FOLLOW_inputParameter_in_inExpression3945);
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
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:699:9: t= IN LEFT_ROUND_BRACKET (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery ) RIGHT_ROUND_BRACKET
                    {
                    t=(Token)input.LT(1);
                    match(input,IN,FOLLOW_IN_in_inExpression3972); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_inExpression3982); if (failed) return node;
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:701:9: (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery )
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
                            new NoViableAltException("701:9: (itemNode= inItem ( COMMA itemNode= inItem )* | subqueryNode= subquery )", 54, 0, input);
                    
                        throw nvae;
                    }
                    switch (alt54) {
                        case 1 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:701:11: itemNode= inItem ( COMMA itemNode= inItem )*
                            {
                            pushFollow(FOLLOW_inItem_in_inExpression3998);
                            itemNode=inItem();
                            _fsp--;
                            if (failed) return node;
                            if ( backtracking==0 ) {
                               ((inExpression_scope)inExpression_stack.peek()).items.add(itemNode); 
                            }
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:702:13: ( COMMA itemNode= inItem )*
                            loop53:
                            do {
                                int alt53=2;
                                int LA53_0 = input.LA(1);
                                
                                if ( (LA53_0==COMMA) ) {
                                    alt53=1;
                                }
                                
                            
                                switch (alt53) {
                            	case 1 :
                            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:702:15: COMMA itemNode= inItem
                            	    {
                            	    match(input,COMMA,FOLLOW_COMMA_in_inExpression4016); if (failed) return node;
                            	    pushFollow(FOLLOW_inItem_in_inExpression4022);
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
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:707:11: subqueryNode= subquery
                            {
                            pushFollow(FOLLOW_subquery_in_inExpression4057);
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

                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression4091); if (failed) return node;
                    
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:716:1: inItem returns [Object node] : (n= literalString | n= literalNumeric | n= inputParameter | n= variableAccessOrTypeConstant );
    public final Object inItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:718:7: (n= literalString | n= literalNumeric | n= inputParameter | n= variableAccessOrTypeConstant )
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
                    new NoViableAltException("716:1: inItem returns [Object node] : (n= literalString | n= literalNumeric | n= inputParameter | n= variableAccessOrTypeConstant );", 56, 0, input);
            
                throw nvae;
            }
            
            switch (alt56) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:718:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_inItem4121);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:719:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_inItem4135);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:720:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_inItem4149);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:721:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_inItem4163);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:724:1: likeExpression[boolean not, Object left] returns [Object node] : t= LIKE pattern= likeValue (escapeChars= escape )? ;
    public final Object likeExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object pattern = null;

        Object escapeChars = null;
        
    
        
            node = null;
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:728:7: (t= LIKE pattern= likeValue (escapeChars= escape )? )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:728:7: t= LIKE pattern= likeValue (escapeChars= escape )?
            {
            t=(Token)input.LT(1);
            match(input,LIKE,FOLLOW_LIKE_in_likeExpression4195); if (failed) return node;
            pushFollow(FOLLOW_likeValue_in_likeExpression4201);
            pattern=likeValue();
            _fsp--;
            if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:729:9: (escapeChars= escape )?
            int alt57=2;
            int LA57_0 = input.LA(1);
            
            if ( (LA57_0==ESCAPE) ) {
                alt57=1;
            }
            switch (alt57) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:729:10: escapeChars= escape
                    {
                    pushFollow(FOLLOW_escape_in_likeExpression4216);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:736:1: escape returns [Object node] : t= ESCAPE escapeClause= likeValue ;
    public final Object escape() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object escapeClause = null;
        
    
         
            node = null; 
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:740:7: (t= ESCAPE escapeClause= likeValue )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:740:7: t= ESCAPE escapeClause= likeValue
            {
            t=(Token)input.LT(1);
            match(input,ESCAPE,FOLLOW_ESCAPE_in_escape4256); if (failed) return node;
            pushFollow(FOLLOW_likeValue_in_escape4262);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:744:1: likeValue returns [Object node] : (n= literalString | n= inputParameter );
    public final Object likeValue() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:746:7: (n= literalString | n= inputParameter )
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
                    new NoViableAltException("744:1: likeValue returns [Object node] : (n= literalString | n= inputParameter );", 58, 0, input);
            
                throw nvae;
            }
            switch (alt58) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:746:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_likeValue4302);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:747:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_likeValue4316);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:750:1: nullComparisonExpression[boolean not, Object left] returns [Object node] : t= NULL ;
    public final Object nullComparisonExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:752:7: (t= NULL )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:752:7: t= NULL
            {
            t=(Token)input.LT(1);
            match(input,NULL,FOLLOW_NULL_in_nullComparisonExpression4349); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:756:1: emptyCollectionComparisonExpression[boolean not, Object left] returns [Object node] : t= EMPTY ;
    public final Object emptyCollectionComparisonExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:758:7: (t= EMPTY )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:758:7: t= EMPTY
            {
            t=(Token)input.LT(1);
            match(input,EMPTY,FOLLOW_EMPTY_in_emptyCollectionComparisonExpression4390); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:762:1: collectionMemberExpression[boolean not, Object left] returns [Object node] : t= MEMBER ( OF )? n= collectionValuedPathExpression ;
    public final Object collectionMemberExpression(boolean not, Object left) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:764:7: (t= MEMBER ( OF )? n= collectionValuedPathExpression )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:764:7: t= MEMBER ( OF )? n= collectionValuedPathExpression
            {
            t=(Token)input.LT(1);
            match(input,MEMBER,FOLLOW_MEMBER_in_collectionMemberExpression4431); if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:764:17: ( OF )?
            int alt59=2;
            int LA59_0 = input.LA(1);
            
            if ( (LA59_0==OF) ) {
                alt59=1;
            }
            switch (alt59) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:764:18: OF
                    {
                    match(input,OF,FOLLOW_OF_in_collectionMemberExpression4434); if (failed) return node;
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression4442);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:771:1: existsExpression[boolean not] returns [Object node] : t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET ;
    public final Object existsExpression(boolean not) throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Object subqueryNode = null;
        
    
         
            node = null;
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:775:7: (t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:775:7: t= EXISTS LEFT_ROUND_BRACKET subqueryNode= subquery RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_existsExpression4482); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4484); if (failed) return node;
            pushFollow(FOLLOW_subquery_in_existsExpression4490);
            subqueryNode=subquery();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4492); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:782:1: comparisonExpression[Object left] returns [Object node] : (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand );
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
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:784:7: (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand )
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
                    new NoViableAltException("782:1: comparisonExpression[Object left] returns [Object node] : (t1= EQUALS n= comparisonExpressionRightOperand | t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand | t3= GREATER_THAN n= comparisonExpressionRightOperand | t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand | t5= LESS_THAN n= comparisonExpressionRightOperand | t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand );", 60, 0, input);
            
                throw nvae;
            }
            
            switch (alt60) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:784:7: t1= EQUALS n= comparisonExpressionRightOperand
                    {
                    t1=(Token)input.LT(1);
                    match(input,EQUALS,FOLLOW_EQUALS_in_comparisonExpression4532); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4538);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newEquals(t1.getLine(), t1.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:786:7: t2= NOT_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t2=(Token)input.LT(1);
                    match(input,NOT_EQUAL_TO,FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4559); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4565);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newNotEquals(t2.getLine(), t2.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:788:7: t3= GREATER_THAN n= comparisonExpressionRightOperand
                    {
                    t3=(Token)input.LT(1);
                    match(input,GREATER_THAN,FOLLOW_GREATER_THAN_in_comparisonExpression4586); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4592);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newGreaterThan(t3.getLine(), t3.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 4 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:790:7: t4= GREATER_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t4=(Token)input.LT(1);
                    match(input,GREATER_THAN_EQUAL_TO,FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4613); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4619);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newGreaterThanEqual(t4.getLine(), t4.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 5 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:792:7: t5= LESS_THAN n= comparisonExpressionRightOperand
                    {
                    t5=(Token)input.LT(1);
                    match(input,LESS_THAN,FOLLOW_LESS_THAN_in_comparisonExpression4640); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4646);
                    n=comparisonExpressionRightOperand();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newLessThan(t5.getLine(), t5.getCharPositionInLine(), left, n); 
                    }
                    
                    }
                    break;
                case 6 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:794:7: t6= LESS_THAN_EQUAL_TO n= comparisonExpressionRightOperand
                    {
                    t6=(Token)input.LT(1);
                    match(input,LESS_THAN_EQUAL_TO,FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4667); if (failed) return node;
                    pushFollow(FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4673);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:798:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression | n= nonArithmeticScalarExpression | n= anyOrAllExpression );
    public final Object comparisonExpressionRightOperand() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:800:7: (n= arithmeticExpression | n= nonArithmeticScalarExpression | n= anyOrAllExpression )
            int alt61=3;
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
                    new NoViableAltException("798:1: comparisonExpressionRightOperand returns [Object node] : (n= arithmeticExpression | n= nonArithmeticScalarExpression | n= anyOrAllExpression );", 61, 0, input);
            
                throw nvae;
            }
            
            switch (alt61) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:800:7: n= arithmeticExpression
                    {
                    pushFollow(FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4714);
                    n=arithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:801:7: n= nonArithmeticScalarExpression
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_comparisonExpressionRightOperand4728);
                    n=nonArithmeticScalarExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:802:7: n= anyOrAllExpression
                    {
                    pushFollow(FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4742);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:805:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object arithmeticExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:807:7: (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
            int alt62=2;
            int LA62_0 = input.LA(1);
            
            if ( (LA62_0==ABS||LA62_0==AVG||(LA62_0>=CASE && LA62_0<=COALESCE)||LA62_0==COUNT||LA62_0==FUNC||LA62_0==INDEX||LA62_0==KEY||LA62_0==LENGTH||LA62_0==LOCATE||LA62_0==MAX||(LA62_0>=MIN && LA62_0<=MOD)||LA62_0==NULLIF||(LA62_0>=SIZE && LA62_0<=SQRT)||LA62_0==SUM||LA62_0==VALUE||LA62_0==IDENT||(LA62_0>=PLUS && LA62_0<=MINUS)||(LA62_0>=INTEGER_LITERAL && LA62_0<=DOUBLE_LITERAL)||(LA62_0>=POSITIONAL_PARAM && LA62_0<=NAMED_PARAM)) ) {
                alt62=1;
            }
            else if ( (LA62_0==LEFT_ROUND_BRACKET) ) {
                int LA62_24 = input.LA(2);
                
                if ( (LA62_24==ABS||LA62_24==AVG||(LA62_24>=CASE && LA62_24<=COALESCE)||LA62_24==COUNT||LA62_24==FUNC||LA62_24==INDEX||LA62_24==KEY||LA62_24==LENGTH||LA62_24==LOCATE||LA62_24==MAX||(LA62_24>=MIN && LA62_24<=MOD)||LA62_24==NULLIF||(LA62_24>=SIZE && LA62_24<=SQRT)||LA62_24==SUM||LA62_24==VALUE||LA62_24==IDENT||LA62_24==LEFT_ROUND_BRACKET||(LA62_24>=PLUS && LA62_24<=MINUS)||(LA62_24>=INTEGER_LITERAL && LA62_24<=DOUBLE_LITERAL)||(LA62_24>=POSITIONAL_PARAM && LA62_24<=NAMED_PARAM)) ) {
                    alt62=1;
                }
                else if ( (LA62_24==SELECT) ) {
                    alt62=2;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("805:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 62, 24, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("805:1: arithmeticExpression returns [Object node] : (n= simpleArithmeticExpression | LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 62, 0, input);
            
                throw nvae;
            }
            switch (alt62) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:807:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4774);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:808:7: LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4784); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_arithmeticExpression4790);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4792); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:811:1: simpleArithmeticExpression returns [Object node] : n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )* ;
    public final Object simpleArithmeticExpression() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token m=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:815:7: (n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )* )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:815:7: n= arithmeticTerm (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*
            {
            pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4824);
            n=arithmeticTerm();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:816:9: (p= PLUS right= arithmeticTerm | m= MINUS right= arithmeticTerm )*
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
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:816:11: p= PLUS right= arithmeticTerm
            	    {
            	    p=(Token)input.LT(1);
            	    match(input,PLUS,FOLLOW_PLUS_in_simpleArithmeticExpression4840); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4846);
            	    right=arithmeticTerm();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newPlus(p.getLine(), p.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            	case 2 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:818:11: m= MINUS right= arithmeticTerm
            	    {
            	    m=(Token)input.LT(1);
            	    match(input,MINUS,FOLLOW_MINUS_in_simpleArithmeticExpression4875); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4881);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:823:1: arithmeticTerm returns [Object node] : n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )* ;
    public final Object arithmeticTerm() throws RecognitionException {

        Object node = null;
    
        Token m=null;
        Token d=null;
        Object n = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:827:7: (n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )* )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:827:7: n= arithmeticFactor (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*
            {
            pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4938);
            n=arithmeticFactor();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              node = n;
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:828:9: (m= MULTIPLY right= arithmeticFactor | d= DIVIDE right= arithmeticFactor )*
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
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:828:11: m= MULTIPLY right= arithmeticFactor
            	    {
            	    m=(Token)input.LT(1);
            	    match(input,MULTIPLY,FOLLOW_MULTIPLY_in_arithmeticTerm4954); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4960);
            	    right=arithmeticFactor();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       node = factory.newMultiply(m.getLine(), m.getCharPositionInLine(), node, right); 
            	    }
            	    
            	    }
            	    break;
            	case 2 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:830:11: d= DIVIDE right= arithmeticFactor
            	    {
            	    d=(Token)input.LT(1);
            	    match(input,DIVIDE,FOLLOW_DIVIDE_in_arithmeticTerm4989); if (failed) return node;
            	    pushFollow(FOLLOW_arithmeticFactor_in_arithmeticTerm4995);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:835:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary );
    public final Object arithmeticFactor() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token m=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:837:7: (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary )
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
                alt65=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("835:1: arithmeticFactor returns [Object node] : (p= PLUS n= arithmeticPrimary | m= MINUS n= arithmeticPrimary | n= arithmeticPrimary );", 65, 0, input);
            
                throw nvae;
            }
            
            switch (alt65) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:837:7: p= PLUS n= arithmeticPrimary
                    {
                    p=(Token)input.LT(1);
                    match(input,PLUS,FOLLOW_PLUS_in_arithmeticFactor5049); if (failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor5056);
                    n=arithmeticPrimary();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = factory.newUnaryPlus(p.getLine(), p.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:839:7: m= MINUS n= arithmeticPrimary
                    {
                    m=(Token)input.LT(1);
                    match(input,MINUS,FOLLOW_MINUS_in_arithmeticFactor5078); if (failed) return node;
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor5084);
                    n=arithmeticPrimary();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newUnaryMinus(m.getLine(), m.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:841:7: n= arithmeticPrimary
                    {
                    pushFollow(FOLLOW_arithmeticPrimary_in_arithmeticFactor5108);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:844:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= inputParameter | n= caseExpression | n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET | n= literalNumeric );
    public final Object arithmeticPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:846:7: ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= inputParameter | n= caseExpression | n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET | n= literalNumeric )
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
            case FUNC:
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
                    new NoViableAltException("844:1: arithmeticPrimary returns [Object node] : ({...}?n= aggregateExpression | n= pathExprOrVariableAccess | n= inputParameter | n= caseExpression | n= functionsReturningNumerics | LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET | n= literalNumeric );", 66, 0, input);
            
                throw nvae;
            }
            
            switch (alt66) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:846:7: {...}?n= aggregateExpression
                    {
                    if ( !( aggregatesAllowed() ) ) {
                        if (backtracking>0) {failed=true; return node;}
                        throw new FailedPredicateException(input, "arithmeticPrimary", " aggregatesAllowed() ");
                    }
                    pushFollow(FOLLOW_aggregateExpression_in_arithmeticPrimary5142);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:847:7: n= pathExprOrVariableAccess
                    {
                    pushFollow(FOLLOW_pathExprOrVariableAccess_in_arithmeticPrimary5156);
                    n=pathExprOrVariableAccess();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:848:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_arithmeticPrimary5170);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:849:7: n= caseExpression
                    {
                    pushFollow(FOLLOW_caseExpression_in_arithmeticPrimary5184);
                    n=caseExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:850:7: n= functionsReturningNumerics
                    {
                    pushFollow(FOLLOW_functionsReturningNumerics_in_arithmeticPrimary5198);
                    n=functionsReturningNumerics();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:851:7: LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
                    {
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary5208); if (failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary5214);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary5216); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 7 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:852:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_arithmeticPrimary5230);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:855:1: scalarExpression returns [Object node] : (n= simpleArithmeticExpression | n= nonArithmeticScalarExpression );
    public final Object scalarExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:857:7: (n= simpleArithmeticExpression | n= nonArithmeticScalarExpression )
            int alt67=2;
            int LA67_0 = input.LA(1);
            
            if ( (LA67_0==ABS||LA67_0==AVG||(LA67_0>=CASE && LA67_0<=COALESCE)||LA67_0==COUNT||LA67_0==FUNC||LA67_0==INDEX||LA67_0==KEY||LA67_0==LENGTH||LA67_0==LOCATE||LA67_0==MAX||(LA67_0>=MIN && LA67_0<=MOD)||LA67_0==NULLIF||(LA67_0>=SIZE && LA67_0<=SQRT)||LA67_0==SUM||LA67_0==VALUE||LA67_0==IDENT||LA67_0==LEFT_ROUND_BRACKET||(LA67_0>=PLUS && LA67_0<=MINUS)||(LA67_0>=INTEGER_LITERAL && LA67_0<=DOUBLE_LITERAL)||(LA67_0>=POSITIONAL_PARAM && LA67_0<=NAMED_PARAM)) ) {
                alt67=1;
            }
            else if ( (LA67_0==CONCAT||(LA67_0>=CURRENT_DATE && LA67_0<=CURRENT_TIMESTAMP)||LA67_0==FALSE||LA67_0==LOWER||LA67_0==SUBSTRING||(LA67_0>=TRIM && LA67_0<=TYPE)||LA67_0==UPPER||(LA67_0>=STRING_LITERAL_DOUBLE_QUOTED && LA67_0<=TIMESTAMP_LITERAL)) ) {
                alt67=2;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("855:1: scalarExpression returns [Object node] : (n= simpleArithmeticExpression | n= nonArithmeticScalarExpression );", 67, 0, input);
            
                throw nvae;
            }
            switch (alt67) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:857:7: n= simpleArithmeticExpression
                    {
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_scalarExpression5262);
                    n=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:858:7: n= nonArithmeticScalarExpression
                    {
                    pushFollow(FOLLOW_nonArithmeticScalarExpression_in_scalarExpression5277);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:861:1: nonArithmeticScalarExpression returns [Object node] : (n= functionsReturningDatetime | n= functionsReturningStrings | n= literalString | n= literalBoolean | n= literalTemporal | n= entityTypeExpression );
    public final Object nonArithmeticScalarExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:863:7: (n= functionsReturningDatetime | n= functionsReturningStrings | n= literalString | n= literalBoolean | n= literalTemporal | n= entityTypeExpression )
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
                    new NoViableAltException("861:1: nonArithmeticScalarExpression returns [Object node] : (n= functionsReturningDatetime | n= functionsReturningStrings | n= literalString | n= literalBoolean | n= literalTemporal | n= entityTypeExpression );", 68, 0, input);
            
                throw nvae;
            }
            
            switch (alt68) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:863:7: n= functionsReturningDatetime
                    {
                    pushFollow(FOLLOW_functionsReturningDatetime_in_nonArithmeticScalarExpression5309);
                    n=functionsReturningDatetime();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:864:7: n= functionsReturningStrings
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_nonArithmeticScalarExpression5323);
                    n=functionsReturningStrings();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:865:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_nonArithmeticScalarExpression5337);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:866:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_nonArithmeticScalarExpression5351);
                    n=literalBoolean();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:867:7: n= literalTemporal
                    {
                    pushFollow(FOLLOW_literalTemporal_in_nonArithmeticScalarExpression5365);
                    n=literalTemporal();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:868:7: n= entityTypeExpression
                    {
                    pushFollow(FOLLOW_entityTypeExpression_in_nonArithmeticScalarExpression5379);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:871:1: anyOrAllExpression returns [Object node] : (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );
    public final Object anyOrAllExpression() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token y=null;
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:873:7: (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET )
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
                    new NoViableAltException("871:1: anyOrAllExpression returns [Object node] : (a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET | s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET );", 69, 0, input);
            
                throw nvae;
            }
            
            switch (alt69) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:873:7: a= ALL LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    a=(Token)input.LT(1);
                    match(input,ALL,FOLLOW_ALL_in_anyOrAllExpression5409); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5411); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5417);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5419); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAll(a.getLine(), a.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:875:7: y= ANY LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    y=(Token)input.LT(1);
                    match(input,ANY,FOLLOW_ANY_in_anyOrAllExpression5439); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5441); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5447);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5449); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newAny(y.getLine(), y.getCharPositionInLine(), n); 
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:877:7: s= SOME LEFT_ROUND_BRACKET n= subquery RIGHT_ROUND_BRACKET
                    {
                    s=(Token)input.LT(1);
                    match(input,SOME,FOLLOW_SOME_in_anyOrAllExpression5469); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5471); if (failed) return node;
                    pushFollow(FOLLOW_subquery_in_anyOrAllExpression5477);
                    n=subquery();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5479); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:881:1: entityTypeExpression returns [Object node] : n= typeDiscriminator ;
    public final Object entityTypeExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:883:7: (n= typeDiscriminator )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:883:7: n= typeDiscriminator
            {
            pushFollow(FOLLOW_typeDiscriminator_in_entityTypeExpression5519);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:886:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );
    public final Object typeDiscriminator() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token c=null;
        Object n = null;
        
    
        node = null;
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:888:7: (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET )
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
                            new NoViableAltException("886:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );", 70, 2, input);
                    
                        throw nvae;
                    }
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("886:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );", 70, 1, input);
                
                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("886:1: typeDiscriminator returns [Object node] : (a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET | c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET );", 70, 0, input);
            
                throw nvae;
            }
            switch (alt70) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:888:7: a= TYPE LEFT_ROUND_BRACKET n= variableOrSingleValuedPath RIGHT_ROUND_BRACKET
                    {
                    a=(Token)input.LT(1);
                    match(input,TYPE,FOLLOW_TYPE_in_typeDiscriminator5552); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5554); if (failed) return node;
                    pushFollow(FOLLOW_variableOrSingleValuedPath_in_typeDiscriminator5560);
                    n=variableOrSingleValuedPath();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5562); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newType(a.getLine(), a.getCharPositionInLine(), n);
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:889:7: c= TYPE LEFT_ROUND_BRACKET n= inputParameter RIGHT_ROUND_BRACKET
                    {
                    c=(Token)input.LT(1);
                    match(input,TYPE,FOLLOW_TYPE_in_typeDiscriminator5577); if (failed) return node;
                    match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5579); if (failed) return node;
                    pushFollow(FOLLOW_inputParameter_in_typeDiscriminator5585);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5587); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:892:1: caseExpression returns [Object node] : (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression );
    public final Object caseExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:894:6: (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression )
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
                        new NoViableAltException("892:1: caseExpression returns [Object node] : (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression );", 71, 1, input);
                
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
                    new NoViableAltException("892:1: caseExpression returns [Object node] : (n= simpleCaseExpression | n= generalCaseExpression | n= coalesceExpression | n= nullIfExpression );", 71, 0, input);
            
                throw nvae;
            }
            
            switch (alt71) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:894:6: n= simpleCaseExpression
                    {
                    pushFollow(FOLLOW_simpleCaseExpression_in_caseExpression5622);
                    n=simpleCaseExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:895:6: n= generalCaseExpression
                    {
                    pushFollow(FOLLOW_generalCaseExpression_in_caseExpression5635);
                    n=generalCaseExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:896:6: n= coalesceExpression
                    {
                    pushFollow(FOLLOW_coalesceExpression_in_caseExpression5648);
                    n=coalesceExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:897:6: n= nullIfExpression
                    {
                    pushFollow(FOLLOW_nullIfExpression_in_caseExpression5661);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:900:1: simpleCaseExpression returns [Object node] : a= CASE c= caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END ;
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
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:908:6: (a= CASE c= caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:908:6: a= CASE c= caseOperand w= simpleWhenClause (w= simpleWhenClause )* ELSE e= scalarExpression END
            {
            a=(Token)input.LT(1);
            match(input,CASE,FOLLOW_CASE_in_simpleCaseExpression5699); if (failed) return node;
            pushFollow(FOLLOW_caseOperand_in_simpleCaseExpression5705);
            c=caseOperand();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_simpleWhenClause_in_simpleCaseExpression5711);
            w=simpleWhenClause();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((simpleCaseExpression_scope)simpleCaseExpression_stack.peek()).whens.add(w);
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:908:97: (w= simpleWhenClause )*
            loop72:
            do {
                int alt72=2;
                int LA72_0 = input.LA(1);
                
                if ( (LA72_0==WHEN) ) {
                    alt72=1;
                }
                
            
                switch (alt72) {
            	case 1 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:908:98: w= simpleWhenClause
            	    {
            	    pushFollow(FOLLOW_simpleWhenClause_in_simpleCaseExpression5720);
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

            match(input,ELSE,FOLLOW_ELSE_in_simpleCaseExpression5726); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleCaseExpression5732);
            e=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,END,FOLLOW_END_in_simpleCaseExpression5734); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:915:1: generalCaseExpression returns [Object node] : a= CASE w= whenClause (w= whenClause )* ELSE e= scalarExpression END ;
    public final Object generalCaseExpression() throws RecognitionException {
        generalCaseExpression_stack.push(new generalCaseExpression_scope());

        Object node = null;
    
        Token a=null;
        Object w = null;

        Object e = null;
        
    
        
            node = null;
            ((generalCaseExpression_scope)generalCaseExpression_stack.peek()).whens = new ArrayList();
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:923:6: (a= CASE w= whenClause (w= whenClause )* ELSE e= scalarExpression END )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:923:6: a= CASE w= whenClause (w= whenClause )* ELSE e= scalarExpression END
            {
            a=(Token)input.LT(1);
            match(input,CASE,FOLLOW_CASE_in_generalCaseExpression5778); if (failed) return node;
            pushFollow(FOLLOW_whenClause_in_generalCaseExpression5784);
            w=whenClause();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((generalCaseExpression_scope)generalCaseExpression_stack.peek()).whens.add(w);
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:923:76: (w= whenClause )*
            loop73:
            do {
                int alt73=2;
                int LA73_0 = input.LA(1);
                
                if ( (LA73_0==WHEN) ) {
                    alt73=1;
                }
                
            
                switch (alt73) {
            	case 1 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:923:77: w= whenClause
            	    {
            	    pushFollow(FOLLOW_whenClause_in_generalCaseExpression5793);
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

            match(input,ELSE,FOLLOW_ELSE_in_generalCaseExpression5799); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_generalCaseExpression5805);
            e=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,END,FOLLOW_END_in_generalCaseExpression5807); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:930:1: coalesceExpression returns [Object node] : c= COALESCE LEFT_ROUND_BRACKET p= scalarExpression ( COMMA s= scalarExpression )+ RIGHT_ROUND_BRACKET ;
    public final Object coalesceExpression() throws RecognitionException {
        coalesceExpression_stack.push(new coalesceExpression_scope());

        Object node = null;
    
        Token c=null;
        Object p = null;

        Object s = null;
        
    
        
            node = null;
            ((coalesceExpression_scope)coalesceExpression_stack.peek()).primaries = new ArrayList();
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:938:6: (c= COALESCE LEFT_ROUND_BRACKET p= scalarExpression ( COMMA s= scalarExpression )+ RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:938:6: c= COALESCE LEFT_ROUND_BRACKET p= scalarExpression ( COMMA s= scalarExpression )+ RIGHT_ROUND_BRACKET
            {
            c=(Token)input.LT(1);
            match(input,COALESCE,FOLLOW_COALESCE_in_coalesceExpression5851); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_coalesceExpression5853); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_coalesceExpression5859);
            p=scalarExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((coalesceExpression_scope)coalesceExpression_stack.peek()).primaries.add(p);
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:938:106: ( COMMA s= scalarExpression )+
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
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:938:107: COMMA s= scalarExpression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_coalesceExpression5864); if (failed) return node;
            	    pushFollow(FOLLOW_scalarExpression_in_coalesceExpression5870);
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

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_coalesceExpression5876); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:945:1: nullIfExpression returns [Object node] : n= NULLIF LEFT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object nullIfExpression() throws RecognitionException {

        Object node = null;
    
        Token n=null;
        Object l = null;

        Object r = null;
        
    
        node = null;
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:947:6: (n= NULLIF LEFT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:947:6: n= NULLIF LEFT_ROUND_BRACKET l= scalarExpression COMMA r= scalarExpression RIGHT_ROUND_BRACKET
            {
            n=(Token)input.LT(1);
            match(input,NULLIF,FOLLOW_NULLIF_in_nullIfExpression5917); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_nullIfExpression5919); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_nullIfExpression5925);
            l=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_nullIfExpression5927); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_nullIfExpression5933);
            r=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_nullIfExpression5935); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:955:1: caseOperand returns [Object node] : (n= stateFieldPathExpression | n= typeDiscriminator );
    public final Object caseOperand() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:957:6: (n= stateFieldPathExpression | n= typeDiscriminator )
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
                    new NoViableAltException("955:1: caseOperand returns [Object node] : (n= stateFieldPathExpression | n= typeDiscriminator );", 75, 0, input);
            
                throw nvae;
            }
            switch (alt75) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:957:6: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_caseOperand5982);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:958:6: n= typeDiscriminator
                    {
                    pushFollow(FOLLOW_typeDiscriminator_in_caseOperand5996);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:961:1: whenClause returns [Object node] : w= WHEN c= conditionalExpression THEN a= scalarExpression ;
    public final Object whenClause() throws RecognitionException {

        Object node = null;
    
        Token w=null;
        Object c = null;

        Object a = null;
        
    
        node = null;
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:963:6: (w= WHEN c= conditionalExpression THEN a= scalarExpression )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:963:6: w= WHEN c= conditionalExpression THEN a= scalarExpression
            {
            w=(Token)input.LT(1);
            match(input,WHEN,FOLLOW_WHEN_in_whenClause6031); if (failed) return node;
            pushFollow(FOLLOW_conditionalExpression_in_whenClause6037);
            c=conditionalExpression();
            _fsp--;
            if (failed) return node;
            match(input,THEN,FOLLOW_THEN_in_whenClause6039); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_whenClause6045);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:970:1: simpleWhenClause returns [Object node] : w= WHEN c= scalarExpression THEN a= scalarExpression ;
    public final Object simpleWhenClause() throws RecognitionException {

        Object node = null;
    
        Token w=null;
        Object c = null;

        Object a = null;
        
    
        node = null;
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:972:6: (w= WHEN c= scalarExpression THEN a= scalarExpression )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:972:6: w= WHEN c= scalarExpression THEN a= scalarExpression
            {
            w=(Token)input.LT(1);
            match(input,WHEN,FOLLOW_WHEN_in_simpleWhenClause6087); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleWhenClause6093);
            c=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,THEN,FOLLOW_THEN_in_simpleWhenClause6095); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_simpleWhenClause6101);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:979:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression | n= variableAccessOrTypeConstant );
    public final Object variableOrSingleValuedPath() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
        node = null;
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:981:7: (n= singleValuedPathExpression | n= variableAccessOrTypeConstant )
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
                        new NoViableAltException("979:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression | n= variableAccessOrTypeConstant );", 76, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA76_0==KEY||LA76_0==VALUE) ) {
                alt76=1;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("979:1: variableOrSingleValuedPath returns [Object node] : (n= singleValuedPathExpression | n= variableAccessOrTypeConstant );", 76, 0, input);
            
                throw nvae;
            }
            switch (alt76) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:981:7: n= singleValuedPathExpression
                    {
                    pushFollow(FOLLOW_singleValuedPathExpression_in_variableOrSingleValuedPath6138);
                    n=singleValuedPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:982:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_variableOrSingleValuedPath6152);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:985:1: stringPrimary returns [Object node] : (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression );
    public final Object stringPrimary() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:987:7: (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression )
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
                    new NoViableAltException("985:1: stringPrimary returns [Object node] : (n= literalString | n= functionsReturningStrings | n= inputParameter | n= stateFieldPathExpression );", 77, 0, input);
            
                throw nvae;
            }
            
            switch (alt77) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:987:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_stringPrimary6184);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:988:7: n= functionsReturningStrings
                    {
                    pushFollow(FOLLOW_functionsReturningStrings_in_stringPrimary6198);
                    n=functionsReturningStrings();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:989:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_stringPrimary6212);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:990:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_stringPrimary6226);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:995:1: literal returns [Object node] : (n= literalNumeric | n= literalBoolean | n= literalString );
    public final Object literal() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:997:7: (n= literalNumeric | n= literalBoolean | n= literalString )
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
                    new NoViableAltException("995:1: literal returns [Object node] : (n= literalNumeric | n= literalBoolean | n= literalString );", 78, 0, input);
            
                throw nvae;
            }
            
            switch (alt78) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:997:7: n= literalNumeric
                    {
                    pushFollow(FOLLOW_literalNumeric_in_literal6260);
                    n=literalNumeric();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:998:7: n= literalBoolean
                    {
                    pushFollow(FOLLOW_literalBoolean_in_literal6274);
                    n=literalBoolean();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:999:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_literal6288);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1002:1: literalNumeric returns [Object node] : (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL );
    public final Object literalNumeric() throws RecognitionException {

        Object node = null;
    
        Token i=null;
        Token l=null;
        Token f=null;
        Token d=null;
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1004:7: (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL )
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
                    new NoViableAltException("1002:1: literalNumeric returns [Object node] : (i= INTEGER_LITERAL | l= LONG_LITERAL | f= FLOAT_LITERAL | d= DOUBLE_LITERAL );", 79, 0, input);
            
                throw nvae;
            }
            
            switch (alt79) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1004:7: i= INTEGER_LITERAL
                    {
                    i=(Token)input.LT(1);
                    match(input,INTEGER_LITERAL,FOLLOW_INTEGER_LITERAL_in_literalNumeric6318); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newIntegerLiteral(i.getLine(), i.getCharPositionInLine(), 
                                                                   Integer.valueOf(i.getText())); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1009:7: l= LONG_LITERAL
                    {
                    l=(Token)input.LT(1);
                    match(input,LONG_LITERAL,FOLLOW_LONG_LITERAL_in_literalNumeric6334); if (failed) return node;
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
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1017:7: f= FLOAT_LITERAL
                    {
                    f=(Token)input.LT(1);
                    match(input,FLOAT_LITERAL,FOLLOW_FLOAT_LITERAL_in_literalNumeric6355); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newFloatLiteral(f.getLine(), f.getCharPositionInLine(),
                                                                 Float.valueOf(f.getText()));
                              
                    }
                    
                    }
                    break;
                case 4 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1022:7: d= DOUBLE_LITERAL
                    {
                    d=(Token)input.LT(1);
                    match(input,DOUBLE_LITERAL,FOLLOW_DOUBLE_LITERAL_in_literalNumeric6375); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1029:1: literalBoolean returns [Object node] : (t= TRUE | f= FALSE );
    public final Object literalBoolean() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        Token f=null;
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1031:7: (t= TRUE | f= FALSE )
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
                    new NoViableAltException("1029:1: literalBoolean returns [Object node] : (t= TRUE | f= FALSE );", 80, 0, input);
            
                throw nvae;
            }
            switch (alt80) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1031:7: t= TRUE
                    {
                    t=(Token)input.LT(1);
                    match(input,TRUE,FOLLOW_TRUE_in_literalBoolean6413); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newBooleanLiteral(t.getLine(), t.getCharPositionInLine(), Boolean.TRUE); 
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1033:7: f= FALSE
                    {
                    f=(Token)input.LT(1);
                    match(input,FALSE,FOLLOW_FALSE_in_literalBoolean6435); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1037:1: literalString returns [Object node] : (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED );
    public final Object literalString() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Token s=null;
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1039:7: (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED )
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
                    new NoViableAltException("1037:1: literalString returns [Object node] : (d= STRING_LITERAL_DOUBLE_QUOTED | s= STRING_LITERAL_SINGLE_QUOTED );", 81, 0, input);
            
                throw nvae;
            }
            switch (alt81) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1039:7: d= STRING_LITERAL_DOUBLE_QUOTED
                    {
                    d=(Token)input.LT(1);
                    match(input,STRING_LITERAL_DOUBLE_QUOTED,FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString6474); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  node = factory.newStringLiteral(d.getLine(), d.getCharPositionInLine(), 
                                                                  convertStringLiteral(d.getText())); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1044:7: s= STRING_LITERAL_SINGLE_QUOTED
                    {
                    s=(Token)input.LT(1);
                    match(input,STRING_LITERAL_SINGLE_QUOTED,FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString6495); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1051:1: literalTemporal returns [Object node] : (d= DATE_LITERAL | d= TIME_LITERAL | d= TIMESTAMP_LITERAL );
    public final Object literalTemporal() throws RecognitionException {

        Object node = null;
    
        Token d=null;
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1053:7: (d= DATE_LITERAL | d= TIME_LITERAL | d= TIMESTAMP_LITERAL )
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
                    new NoViableAltException("1051:1: literalTemporal returns [Object node] : (d= DATE_LITERAL | d= TIME_LITERAL | d= TIMESTAMP_LITERAL );", 82, 0, input);
            
                throw nvae;
            }
            
            switch (alt82) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1053:7: d= DATE_LITERAL
                    {
                    d=(Token)input.LT(1);
                    match(input,DATE_LITERAL,FOLLOW_DATE_LITERAL_in_literalTemporal6535); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = factory.newDateLiteral(d.getLine(), d.getCharPositionInLine(), d.getText()); 
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1054:7: d= TIME_LITERAL
                    {
                    d=(Token)input.LT(1);
                    match(input,TIME_LITERAL,FOLLOW_TIME_LITERAL_in_literalTemporal6549); if (failed) return node;
                    if ( backtracking==0 ) {
                      node = factory.newTimeLiteral(d.getLine(), d.getCharPositionInLine(), d.getText()); 
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1055:7: d= TIMESTAMP_LITERAL
                    {
                    d=(Token)input.LT(1);
                    match(input,TIMESTAMP_LITERAL,FOLLOW_TIMESTAMP_LITERAL_in_literalTemporal6563); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1058:1: inputParameter returns [Object node] : (p= POSITIONAL_PARAM | n= NAMED_PARAM );
    public final Object inputParameter() throws RecognitionException {

        Object node = null;
    
        Token p=null;
        Token n=null;
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1060:7: (p= POSITIONAL_PARAM | n= NAMED_PARAM )
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
                    new NoViableAltException("1058:1: inputParameter returns [Object node] : (p= POSITIONAL_PARAM | n= NAMED_PARAM );", 83, 0, input);
            
                throw nvae;
            }
            switch (alt83) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1060:7: p= POSITIONAL_PARAM
                    {
                    p=(Token)input.LT(1);
                    match(input,POSITIONAL_PARAM,FOLLOW_POSITIONAL_PARAM_in_inputParameter6593); if (failed) return node;
                    if ( backtracking==0 ) {
                       
                                  // skip the leading ?
                                  String text = p.getText().substring(1);
                                  node = factory.newPositionalParameter(p.getLine(), p.getCharPositionInLine(), text); 
                              
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1066:7: n= NAMED_PARAM
                    {
                    n=(Token)input.LT(1);
                    match(input,NAMED_PARAM,FOLLOW_NAMED_PARAM_in_inputParameter6613); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1074:1: functionsReturningNumerics returns [Object node] : (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size | n= index | n= func );
    public final Object functionsReturningNumerics() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1076:7: (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size | n= index | n= func )
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1074:1: functionsReturningNumerics returns [Object node] : (n= abs | n= length | n= mod | n= sqrt | n= locate | n= size | n= index | n= func );", 84, 0, input);
            
                throw nvae;
            }
            
            switch (alt84) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1076:7: n= abs
                    {
                    pushFollow(FOLLOW_abs_in_functionsReturningNumerics6653);
                    n=abs();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1077:7: n= length
                    {
                    pushFollow(FOLLOW_length_in_functionsReturningNumerics6667);
                    n=length();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1078:7: n= mod
                    {
                    pushFollow(FOLLOW_mod_in_functionsReturningNumerics6681);
                    n=mod();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1079:7: n= sqrt
                    {
                    pushFollow(FOLLOW_sqrt_in_functionsReturningNumerics6695);
                    n=sqrt();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1080:7: n= locate
                    {
                    pushFollow(FOLLOW_locate_in_functionsReturningNumerics6709);
                    n=locate();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 6 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1081:7: n= size
                    {
                    pushFollow(FOLLOW_size_in_functionsReturningNumerics6723);
                    n=size();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 7 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1082:7: n= index
                    {
                    pushFollow(FOLLOW_index_in_functionsReturningNumerics6737);
                    n=index();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 8 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1083:7: n= func
                    {
                    pushFollow(FOLLOW_func_in_functionsReturningNumerics6751);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1086:1: functionsReturningDatetime returns [Object node] : (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP );
    public final Object functionsReturningDatetime() throws RecognitionException {

        Object node = null;
    
        Token d=null;
        Token t=null;
        Token ts=null;
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1088:7: (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP )
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
                    new NoViableAltException("1086:1: functionsReturningDatetime returns [Object node] : (d= CURRENT_DATE | t= CURRENT_TIME | ts= CURRENT_TIMESTAMP );", 85, 0, input);
            
                throw nvae;
            }
            
            switch (alt85) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1088:7: d= CURRENT_DATE
                    {
                    d=(Token)input.LT(1);
                    match(input,CURRENT_DATE,FOLLOW_CURRENT_DATE_in_functionsReturningDatetime6781); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newCurrentDate(d.getLine(), d.getCharPositionInLine()); 
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1090:7: t= CURRENT_TIME
                    {
                    t=(Token)input.LT(1);
                    match(input,CURRENT_TIME,FOLLOW_CURRENT_TIME_in_functionsReturningDatetime6802); if (failed) return node;
                    if ( backtracking==0 ) {
                       node = factory.newCurrentTime(t.getLine(), t.getCharPositionInLine()); 
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1092:7: ts= CURRENT_TIMESTAMP
                    {
                    ts=(Token)input.LT(1);
                    match(input,CURRENT_TIMESTAMP,FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime6822); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1096:1: functionsReturningStrings returns [Object node] : (n= concat | n= substring | n= trim | n= upper | n= lower );
    public final Object functionsReturningStrings() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1098:7: (n= concat | n= substring | n= trim | n= upper | n= lower )
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
                    new NoViableAltException("1096:1: functionsReturningStrings returns [Object node] : (n= concat | n= substring | n= trim | n= upper | n= lower );", 86, 0, input);
            
                throw nvae;
            }
            
            switch (alt86) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1098:7: n= concat
                    {
                    pushFollow(FOLLOW_concat_in_functionsReturningStrings6862);
                    n=concat();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1099:7: n= substring
                    {
                    pushFollow(FOLLOW_substring_in_functionsReturningStrings6876);
                    n=substring();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1100:7: n= trim
                    {
                    pushFollow(FOLLOW_trim_in_functionsReturningStrings6890);
                    n=trim();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 4 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1101:7: n= upper
                    {
                    pushFollow(FOLLOW_upper_in_functionsReturningStrings6904);
                    n=upper();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 5 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1102:7: n= lower
                    {
                    pushFollow(FOLLOW_lower_in_functionsReturningStrings6918);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1106:1: concat returns [Object node] : c= CONCAT LEFT_ROUND_BRACKET firstArg= scalarExpression ( COMMA arg= scalarExpression )+ RIGHT_ROUND_BRACKET ;
    public final Object concat() throws RecognitionException {
        concat_stack.push(new concat_scope());

        Object node = null;
    
        Token c=null;
        Object firstArg = null;

        Object arg = null;
        
    
         
            node = null;
            ((concat_scope)concat_stack.peek()).items = new ArrayList();
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1114:7: (c= CONCAT LEFT_ROUND_BRACKET firstArg= scalarExpression ( COMMA arg= scalarExpression )+ RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1114:7: c= CONCAT LEFT_ROUND_BRACKET firstArg= scalarExpression ( COMMA arg= scalarExpression )+ RIGHT_ROUND_BRACKET
            {
            c=(Token)input.LT(1);
            match(input,CONCAT,FOLLOW_CONCAT_in_concat6953); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_concat6964); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_concat6979);
            firstArg=scalarExpression();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
              ((concat_scope)concat_stack.peek()).items.add(firstArg);
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1116:75: ( COMMA arg= scalarExpression )+
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
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1116:76: COMMA arg= scalarExpression
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_concat6984); if (failed) return node;
            	    pushFollow(FOLLOW_scalarExpression_in_concat6990);
            	    arg=scalarExpression();
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

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_concat7004); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1121:1: substring returns [Object node] : s= SUBSTRING LEFT_ROUND_BRACKET string= scalarExpression COMMA start= simpleArithmeticExpression ( COMMA lengthNode= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET ;
    public final Object substring() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object string = null;

        Object start = null;

        Object lengthNode = null;
        
    
         
            node = null;
            lengthNode = null;
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1126:7: (s= SUBSTRING LEFT_ROUND_BRACKET string= scalarExpression COMMA start= simpleArithmeticExpression ( COMMA lengthNode= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1126:7: s= SUBSTRING LEFT_ROUND_BRACKET string= scalarExpression COMMA start= simpleArithmeticExpression ( COMMA lengthNode= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SUBSTRING,FOLLOW_SUBSTRING_in_substring7042); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_substring7055); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_substring7069);
            string=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_substring7071); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_substring7085);
            start=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1130:9: ( COMMA lengthNode= simpleArithmeticExpression )?
            int alt88=2;
            int LA88_0 = input.LA(1);
            
            if ( (LA88_0==COMMA) ) {
                alt88=1;
            }
            switch (alt88) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1130:10: COMMA lengthNode= simpleArithmeticExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_substring7096); if (failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_substring7102);
                    lengthNode=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_substring7114); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1143:1: trim returns [Object node] : t= TRIM LEFT_ROUND_BRACKET (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET ;
    public final Object trim() throws RecognitionException {

        Object node = null;
    
        Token t=null;
        TrimSpecification trimSpecIndicator = null;

        Object trimCharNode = null;

        Object n = null;
        
    
         
            node = null;
            trimSpecIndicator = TrimSpecification.BOTH;
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1148:7: (t= TRIM LEFT_ROUND_BRACKET (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1148:7: t= TRIM LEFT_ROUND_BRACKET (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )? n= stringPrimary RIGHT_ROUND_BRACKET
            {
            t=(Token)input.LT(1);
            match(input,TRIM,FOLLOW_TRIM_in_trim7152); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_trim7162); if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1150:9: (trimSpecIndicator= trimSpec trimCharNode= trimChar FROM )?
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
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1150:10: trimSpecIndicator= trimSpec trimCharNode= trimChar FROM
                    {
                    pushFollow(FOLLOW_trimSpec_in_trim7178);
                    trimSpecIndicator=trimSpec();
                    _fsp--;
                    if (failed) return node;
                    pushFollow(FOLLOW_trimChar_in_trim7184);
                    trimCharNode=trimChar();
                    _fsp--;
                    if (failed) return node;
                    match(input,FROM,FOLLOW_FROM_in_trim7186); if (failed) return node;
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_stringPrimary_in_trim7202);
            n=stringPrimary();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_trim7212); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1159:1: trimSpec returns [TrimSpecification trimSpec] : ( LEADING | TRAILING | BOTH | );
    public final TrimSpecification trimSpec() throws RecognitionException {

        TrimSpecification trimSpec = null;
    
         trimSpec = TrimSpecification.BOTH; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1161:7: ( LEADING | TRAILING | BOTH | )
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
                    new NoViableAltException("1159:1: trimSpec returns [TrimSpecification trimSpec] : ( LEADING | TRAILING | BOTH | );", 90, 0, input);
            
                throw nvae;
            }
            
            switch (alt90) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1161:7: LEADING
                    {
                    match(input,LEADING,FOLLOW_LEADING_in_trimSpec7248); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.LEADING; 
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1163:7: TRAILING
                    {
                    match(input,TRAILING,FOLLOW_TRAILING_in_trimSpec7266); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.TRAILING; 
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1165:7: BOTH
                    {
                    match(input,BOTH,FOLLOW_BOTH_in_trimSpec7284); if (failed) return trimSpec;
                    if ( backtracking==0 ) {
                       trimSpec = TrimSpecification.BOTH; 
                    }
                    
                    }
                    break;
                case 4 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1168:5: 
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1170:1: trimChar returns [Object node] : (n= literalString | n= inputParameter | );
    public final Object trimChar() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1172:7: (n= literalString | n= inputParameter | )
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
                    new NoViableAltException("1170:1: trimChar returns [Object node] : (n= literalString | n= inputParameter | );", 91, 0, input);
            
                throw nvae;
            }
            
            switch (alt91) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1172:7: n= literalString
                    {
                    pushFollow(FOLLOW_literalString_in_trimChar7331);
                    n=literalString();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1173:7: n= inputParameter
                    {
                    pushFollow(FOLLOW_inputParameter_in_trimChar7345);
                    n=inputParameter();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1175:5: 
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1177:1: upper returns [Object node] : u= UPPER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object upper() throws RecognitionException {

        Object node = null;
    
        Token u=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1179:7: (u= UPPER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1179:7: u= UPPER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET
            {
            u=(Token)input.LT(1);
            match(input,UPPER,FOLLOW_UPPER_in_upper7382); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_upper7384); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_upper7390);
            n=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_upper7392); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1183:1: lower returns [Object node] : l= LOWER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object lower() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1185:7: (l= LOWER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1185:7: l= LOWER LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LOWER,FOLLOW_LOWER_in_lower7430); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_lower7432); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_lower7438);
            n=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_lower7440); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1190:1: abs returns [Object node] : a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object abs() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1192:7: (a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1192:7: a= ABS LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            a=(Token)input.LT(1);
            match(input,ABS,FOLLOW_ABS_in_abs7479); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_abs7481); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_abs7487);
            n=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_abs7489); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1196:1: length returns [Object node] : l= LENGTH LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET ;
    public final Object length() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1198:7: (l= LENGTH LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1198:7: l= LENGTH LEFT_ROUND_BRACKET n= scalarExpression RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LENGTH,FOLLOW_LENGTH_in_length7527); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_length7529); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_length7535);
            n=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_length7537); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1202:1: locate returns [Object node] : l= LOCATE LEFT_ROUND_BRACKET pattern= scalarExpression COMMA n= scalarExpression ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET ;
    public final Object locate() throws RecognitionException {

        Object node = null;
    
        Token l=null;
        Object pattern = null;

        Object n = null;

        Object startPos = null;
        
    
         
            node = null; 
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1206:7: (l= LOCATE LEFT_ROUND_BRACKET pattern= scalarExpression COMMA n= scalarExpression ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1206:7: l= LOCATE LEFT_ROUND_BRACKET pattern= scalarExpression COMMA n= scalarExpression ( COMMA startPos= simpleArithmeticExpression )? RIGHT_ROUND_BRACKET
            {
            l=(Token)input.LT(1);
            match(input,LOCATE,FOLLOW_LOCATE_in_locate7575); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_locate7585); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_locate7600);
            pattern=scalarExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_locate7602); if (failed) return node;
            pushFollow(FOLLOW_scalarExpression_in_locate7608);
            n=scalarExpression();
            _fsp--;
            if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1209:9: ( COMMA startPos= simpleArithmeticExpression )?
            int alt92=2;
            int LA92_0 = input.LA(1);
            
            if ( (LA92_0==COMMA) ) {
                alt92=1;
            }
            switch (alt92) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1209:11: COMMA startPos= simpleArithmeticExpression
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_locate7620); if (failed) return node;
                    pushFollow(FOLLOW_simpleArithmeticExpression_in_locate7626);
                    startPos=simpleArithmeticExpression();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_locate7639); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1217:1: size returns [Object node] : s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET ;
    public final Object size() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1219:7: (s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1219:7: s= SIZE LEFT_ROUND_BRACKET n= collectionValuedPathExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SIZE,FOLLOW_SIZE_in_size7677); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_size7688); if (failed) return node;
            pushFollow(FOLLOW_collectionValuedPathExpression_in_size7694);
            n=collectionValuedPathExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_size7696); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1224:1: mod returns [Object node] : m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object mod() throws RecognitionException {

        Object node = null;
    
        Token m=null;
        Object left = null;

        Object right = null;
        
    
         
            node = null; 
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1228:7: (m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1228:7: m= MOD LEFT_ROUND_BRACKET left= simpleArithmeticExpression COMMA right= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            m=(Token)input.LT(1);
            match(input,MOD,FOLLOW_MOD_in_mod7734); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_mod7736); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_mod7750);
            left=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,COMMA,FOLLOW_COMMA_in_mod7752); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_mod7767);
            right=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_mod7777); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1235:1: sqrt returns [Object node] : s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET ;
    public final Object sqrt() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1237:7: (s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1237:7: s= SQRT LEFT_ROUND_BRACKET n= simpleArithmeticExpression RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,SQRT,FOLLOW_SQRT_in_sqrt7815); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_sqrt7826); if (failed) return node;
            pushFollow(FOLLOW_simpleArithmeticExpression_in_sqrt7832);
            n=simpleArithmeticExpression();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt7834); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1242:1: index returns [Object node] : s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET ;
    public final Object index() throws RecognitionException {

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1244:7: (s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1244:7: s= INDEX LEFT_ROUND_BRACKET n= variableAccessOrTypeConstant RIGHT_ROUND_BRACKET
            {
            s=(Token)input.LT(1);
            match(input,INDEX,FOLLOW_INDEX_in_index7876); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_index7878); if (failed) return node;
            pushFollow(FOLLOW_variableAccessOrTypeConstant_in_index7884);
            n=variableAccessOrTypeConstant();
            _fsp--;
            if (failed) return node;
            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_index7886); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1249:1: func returns [Object node] : f= FUNC LEFT_ROUND_BRACKET name= STRING_LITERAL_SINGLE_QUOTED ( COMMA n= newValue )* RIGHT_ROUND_BRACKET ;
    public final Object func() throws RecognitionException {
        func_stack.push(new func_scope());

        Object node = null;
    
        Token f=null;
        Token name=null;
        Object n = null;
        
    
         
            node = null; 
            ((func_scope)func_stack.peek()).exprs = new ArrayList();
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1257:7: (f= FUNC LEFT_ROUND_BRACKET name= STRING_LITERAL_SINGLE_QUOTED ( COMMA n= newValue )* RIGHT_ROUND_BRACKET )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1257:7: f= FUNC LEFT_ROUND_BRACKET name= STRING_LITERAL_SINGLE_QUOTED ( COMMA n= newValue )* RIGHT_ROUND_BRACKET
            {
            f=(Token)input.LT(1);
            match(input,FUNC,FOLLOW_FUNC_in_func7928); if (failed) return node;
            match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_func7930); if (failed) return node;
            name=(Token)input.LT(1);
            match(input,STRING_LITERAL_SINGLE_QUOTED,FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_func7942); if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1259:7: ( COMMA n= newValue )*
            loop93:
            do {
                int alt93=2;
                int LA93_0 = input.LA(1);
                
                if ( (LA93_0==COMMA) ) {
                    alt93=1;
                }
                
            
                switch (alt93) {
            	case 1 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1259:8: COMMA n= newValue
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_func7951); if (failed) return node;
            	    pushFollow(FOLLOW_newValue_in_func7957);
            	    n=newValue();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      
            	                  ((func_scope)func_stack.peek()).exprs.add(n);
            	                
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop93;
                }
            } while (true);

            match(input,RIGHT_ROUND_BRACKET,FOLLOW_RIGHT_ROUND_BRACKET_in_func7989); if (failed) return node;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1268:1: subquery returns [Object node] : select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? ;
    public final Object subquery() throws RecognitionException {

        Object node = null;
    
        Object select = null;

        Object from = null;

        Object where = null;

        Object groupBy = null;

        Object having = null;
        
    
         
            node = null; 
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1272:7: (select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )? )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1272:7: select= simpleSelectClause from= subqueryFromClause (where= whereClause )? (groupBy= groupByClause )? (having= havingClause )?
            {
            pushFollow(FOLLOW_simpleSelectClause_in_subquery8027);
            select=simpleSelectClause();
            _fsp--;
            if (failed) return node;
            pushFollow(FOLLOW_subqueryFromClause_in_subquery8042);
            from=subqueryFromClause();
            _fsp--;
            if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1274:7: (where= whereClause )?
            int alt94=2;
            int LA94_0 = input.LA(1);
            
            if ( (LA94_0==WHERE) ) {
                alt94=1;
            }
            switch (alt94) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1274:8: where= whereClause
                    {
                    pushFollow(FOLLOW_whereClause_in_subquery8057);
                    where=whereClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1275:7: (groupBy= groupByClause )?
            int alt95=2;
            int LA95_0 = input.LA(1);
            
            if ( (LA95_0==GROUP) ) {
                alt95=1;
            }
            switch (alt95) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1275:8: groupBy= groupByClause
                    {
                    pushFollow(FOLLOW_groupByClause_in_subquery8072);
                    groupBy=groupByClause();
                    _fsp--;
                    if (failed) return node;
                    
                    }
                    break;
            
            }

            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1276:7: (having= havingClause )?
            int alt96=2;
            int LA96_0 = input.LA(1);
            
            if ( (LA96_0==HAVING) ) {
                alt96=1;
            }
            switch (alt96) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1276:8: having= havingClause
                    {
                    pushFollow(FOLLOW_havingClause_in_subquery8088);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1283:1: simpleSelectClause returns [Object node] : s= SELECT ( DISTINCT )? n= simpleSelectExpression ;
    public final Object simpleSelectClause() throws RecognitionException {
        simpleSelectClause_stack.push(new simpleSelectClause_scope());

        Object node = null;
    
        Token s=null;
        Object n = null;
        
    
         
            node = null; 
            ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct = false;
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1291:7: (s= SELECT ( DISTINCT )? n= simpleSelectExpression )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1291:7: s= SELECT ( DISTINCT )? n= simpleSelectExpression
            {
            s=(Token)input.LT(1);
            match(input,SELECT,FOLLOW_SELECT_in_simpleSelectClause8131); if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1291:16: ( DISTINCT )?
            int alt97=2;
            int LA97_0 = input.LA(1);
            
            if ( (LA97_0==DISTINCT) ) {
                alt97=1;
            }
            switch (alt97) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1291:17: DISTINCT
                    {
                    match(input,DISTINCT,FOLLOW_DISTINCT_in_simpleSelectClause8134); if (failed) return node;
                    if ( backtracking==0 ) {
                       ((simpleSelectClause_scope)simpleSelectClause_stack.peek()).distinct = true; 
                    }
                    
                    }
                    break;
            
            }

            pushFollow(FOLLOW_simpleSelectExpression_in_simpleSelectClause8150);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1301:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );
    public final Object simpleSelectExpression() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1303:7: (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant )
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
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1301:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );", 98, 1, input);
                
                    throw nvae;
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
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1301:1: simpleSelectExpression returns [Object node] : (n= singleValuedPathExpression | n= aggregateExpression | n= variableAccessOrTypeConstant );", 98, 0, input);
            
                throw nvae;
            }
            
            switch (alt98) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1303:7: n= singleValuedPathExpression
                    {
                    pushFollow(FOLLOW_singleValuedPathExpression_in_simpleSelectExpression8190);
                    n=singleValuedPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1304:7: n= aggregateExpression
                    {
                    pushFollow(FOLLOW_aggregateExpression_in_simpleSelectExpression8205);
                    n=aggregateExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1305:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_simpleSelectExpression8220);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1309:1: subqueryFromClause returns [Object node] : f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )* ;
    public final Object subqueryFromClause() throws RecognitionException {
        subqueryFromClause_stack.push(new subqueryFromClause_scope());

        Object node = null;
    
        Token f=null;
        Object c = null;
        
    
         
            node = null; 
            ((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls = new ArrayList();
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1317:7: (f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )* )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1317:7: f= FROM subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )*
            {
            f=(Token)input.LT(1);
            match(input,FROM,FOLLOW_FROM_in_subqueryFromClause8255); if (failed) return node;
            pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8257);
            subselectIdentificationVariableDeclaration(((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls);
            _fsp--;
            if (failed) return node;
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1318:9: ( COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls] | c= collectionMemberDeclaration )*
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
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1319:13: COMMA subselectIdentificationVariableDeclaration[$subqueryFromClause::varDecls]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_subqueryFromClause8284); if (failed) return node;
            	    pushFollow(FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8303);
            	    subselectIdentificationVariableDeclaration(((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls);
            	    _fsp--;
            	    if (failed) return node;
            	    
            	    }
            	    break;
            	case 2 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1321:19: c= collectionMemberDeclaration
            	    {
            	    pushFollow(FOLLOW_collectionMemberDeclaration_in_subqueryFromClause8329);
            	    c=collectionMemberDeclaration();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	      ((subqueryFromClause_scope)subqueryFromClause_stack.peek()).varDecls.add(c);
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop99;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1326:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );
    public final void subselectIdentificationVariableDeclaration(List varDecls) throws RecognitionException {
        Token i=null;
        Object n = null;
        
    
         Object node; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1328:7: ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration )
            int alt102=3;
            switch ( input.LA(1) ) {
            case IDENT:
                {
                int LA102_1 = input.LA(2);
                
                if ( (LA102_1==DOT) ) {
                    alt102=2;
                }
                else if ( (LA102_1==AS||LA102_1==IDENT) ) {
                    alt102=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1326:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 102, 1, input);
                
                    throw nvae;
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
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1326:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 102, 2, input);
                
                    throw nvae;
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
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1326:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 102, 3, input);
                
                    throw nvae;
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
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1326:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 102, 4, input);
                
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
                alt102=1;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1326:1: subselectIdentificationVariableDeclaration[List varDecls] : ( identificationVariableDeclaration[varDecls] | n= associationPathExpression ( AS )? i= IDENT ( join )* | n= collectionMemberDeclaration );", 102, 0, input);
            
                throw nvae;
            }
            
            switch (alt102) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1328:7: identificationVariableDeclaration[varDecls]
                    {
                    pushFollow(FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration8376);
                    identificationVariableDeclaration(varDecls);
                    _fsp--;
                    if (failed) return ;
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1329:7: n= associationPathExpression ( AS )? i= IDENT ( join )*
                    {
                    pushFollow(FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration8389);
                    n=associationPathExpression();
                    _fsp--;
                    if (failed) return ;
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1329:37: ( AS )?
                    int alt100=2;
                    int LA100_0 = input.LA(1);
                    
                    if ( (LA100_0==AS) ) {
                        alt100=1;
                    }
                    switch (alt100) {
                        case 1 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1329:38: AS
                            {
                            match(input,AS,FOLLOW_AS_in_subselectIdentificationVariableDeclaration8392); if (failed) return ;
                            
                            }
                            break;
                    
                    }

                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration8398); if (failed) return ;
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1329:51: ( join )*
                    loop101:
                    do {
                        int alt101=2;
                        int LA101_0 = input.LA(1);
                        
                        if ( (LA101_0==INNER||LA101_0==JOIN||LA101_0==LEFT) ) {
                            alt101=1;
                        }
                        
                    
                        switch (alt101) {
                    	case 1 :
                    	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1329:52: join
                    	    {
                    	    pushFollow(FOLLOW_join_in_subselectIdentificationVariableDeclaration8401);
                    	    join();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    if ( backtracking==0 ) {
                    	       varDecls.add(n); 
                    	    }
                    	    
                    	    }
                    	    break;
                    
                    	default :
                    	    break loop101;
                        }
                    } while (true);

                    if ( backtracking==0 ) {
                       
                                  varDecls.add(factory.newVariableDecl(i.getLine(), i.getCharPositionInLine(), 
                                                                       n, i.getText())); 
                              
                    }
                    
                    }
                    break;
                case 3 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1334:7: n= collectionMemberDeclaration
                    {
                    pushFollow(FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration8428);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1337:1: orderByClause returns [Object node] : o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* ;
    public final Object orderByClause() throws RecognitionException {
        orderByClause_stack.push(new orderByClause_scope());

        Object node = null;
    
        Token o=null;
        Object n = null;
        
    
         
            node = null; 
            ((orderByClause_scope)orderByClause_stack.peek()).items = new ArrayList();
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1345:7: (o= ORDER BY n= orderByItem ( COMMA n= orderByItem )* )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1345:7: o= ORDER BY n= orderByItem ( COMMA n= orderByItem )*
            {
            o=(Token)input.LT(1);
            match(input,ORDER,FOLLOW_ORDER_in_orderByClause8461); if (failed) return node;
            match(input,BY,FOLLOW_BY_in_orderByClause8463); if (failed) return node;
            pushFollow(FOLLOW_orderByItem_in_orderByClause8477);
            n=orderByItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((orderByClause_scope)orderByClause_stack.peek()).items.add(n); 
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1347:9: ( COMMA n= orderByItem )*
            loop103:
            do {
                int alt103=2;
                int LA103_0 = input.LA(1);
                
                if ( (LA103_0==COMMA) ) {
                    alt103=1;
                }
                
            
                switch (alt103) {
            	case 1 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1347:10: COMMA n= orderByItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_orderByClause8492); if (failed) return node;
            	    pushFollow(FOLLOW_orderByItem_in_orderByClause8498);
            	    n=orderByItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((orderByClause_scope)orderByClause_stack.peek()).items.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop103;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1351:1: orderByItem returns [Object node] : (n= stateFieldPathExpression (a= ASC | d= DESC | ) | i= IDENT (a= ASC | d= DESC | ) );
    public final Object orderByItem() throws RecognitionException {

        Object node = null;
    
        Token a=null;
        Token d=null;
        Token i=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1353:7: (n= stateFieldPathExpression (a= ASC | d= DESC | ) | i= IDENT (a= ASC | d= DESC | ) )
            int alt106=2;
            int LA106_0 = input.LA(1);
            
            if ( (LA106_0==IDENT) ) {
                int LA106_1 = input.LA(2);
                
                if ( (LA106_1==EOF||LA106_1==ASC||LA106_1==DESC||LA106_1==COMMA) ) {
                    alt106=2;
                }
                else if ( (LA106_1==DOT) ) {
                    alt106=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1351:1: orderByItem returns [Object node] : (n= stateFieldPathExpression (a= ASC | d= DESC | ) | i= IDENT (a= ASC | d= DESC | ) );", 106, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA106_0==KEY||LA106_0==VALUE) ) {
                alt106=1;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1351:1: orderByItem returns [Object node] : (n= stateFieldPathExpression (a= ASC | d= DESC | ) | i= IDENT (a= ASC | d= DESC | ) );", 106, 0, input);
            
                throw nvae;
            }
            switch (alt106) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1353:7: n= stateFieldPathExpression (a= ASC | d= DESC | )
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_orderByItem8544);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1354:9: (a= ASC | d= DESC | )
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
                            new NoViableAltException("1354:9: (a= ASC | d= DESC | )", 104, 0, input);
                    
                        throw nvae;
                    }
                    
                    switch (alt104) {
                        case 1 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1354:11: a= ASC
                            {
                            a=(Token)input.LT(1);
                            match(input,ASC,FOLLOW_ASC_in_orderByItem8558); if (failed) return node;
                            if ( backtracking==0 ) {
                               node = factory.newAscOrdering(a.getLine(), a.getCharPositionInLine(), n); 
                            }
                            
                            }
                            break;
                        case 2 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1356:11: d= DESC
                            {
                            d=(Token)input.LT(1);
                            match(input,DESC,FOLLOW_DESC_in_orderByItem8587); if (failed) return node;
                            if ( backtracking==0 ) {
                               node = factory.newDescOrdering(d.getLine(), d.getCharPositionInLine(), n); 
                            }
                            
                            }
                            break;
                        case 3 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1359:13: 
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
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1361:8: i= IDENT (a= ASC | d= DESC | )
                    {
                    i=(Token)input.LT(1);
                    match(input,IDENT,FOLLOW_IDENT_in_orderByItem8649); if (failed) return node;
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1362:9: (a= ASC | d= DESC | )
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
                            new NoViableAltException("1362:9: (a= ASC | d= DESC | )", 105, 0, input);
                    
                        throw nvae;
                    }
                    
                    switch (alt105) {
                        case 1 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1362:11: a= ASC
                            {
                            a=(Token)input.LT(1);
                            match(input,ASC,FOLLOW_ASC_in_orderByItem8663); if (failed) return node;
                            if ( backtracking==0 ) {
                               node = factory.newAscOrdering(a.getLine(), a.getCharPositionInLine(), i.getText()); 
                            }
                            
                            }
                            break;
                        case 2 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1364:11: d= DESC
                            {
                            d=(Token)input.LT(1);
                            match(input,DESC,FOLLOW_DESC_in_orderByItem8692); if (failed) return node;
                            if ( backtracking==0 ) {
                               node = factory.newDescOrdering(d.getLine(), d.getCharPositionInLine(), i.getText()); 
                            }
                            
                            }
                            break;
                        case 3 :
                            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1367:13: 
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1371:1: groupByClause returns [Object node] : g= GROUP BY n= groupByItem ( COMMA n= groupByItem )* ;
    public final Object groupByClause() throws RecognitionException {
        groupByClause_stack.push(new groupByClause_scope());

        Object node = null;
    
        Token g=null;
        Object n = null;
        
    
         
            node = null; 
            ((groupByClause_scope)groupByClause_stack.peek()).items = new ArrayList();
    
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1379:7: (g= GROUP BY n= groupByItem ( COMMA n= groupByItem )* )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1379:7: g= GROUP BY n= groupByItem ( COMMA n= groupByItem )*
            {
            g=(Token)input.LT(1);
            match(input,GROUP,FOLLOW_GROUP_in_groupByClause8773); if (failed) return node;
            match(input,BY,FOLLOW_BY_in_groupByClause8775); if (failed) return node;
            pushFollow(FOLLOW_groupByItem_in_groupByClause8789);
            n=groupByItem();
            _fsp--;
            if (failed) return node;
            if ( backtracking==0 ) {
               ((groupByClause_scope)groupByClause_stack.peek()).items.add(n); 
            }
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1381:9: ( COMMA n= groupByItem )*
            loop107:
            do {
                int alt107=2;
                int LA107_0 = input.LA(1);
                
                if ( (LA107_0==COMMA) ) {
                    alt107=1;
                }
                
            
                switch (alt107) {
            	case 1 :
            	    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1381:10: COMMA n= groupByItem
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_groupByClause8802); if (failed) return node;
            	    pushFollow(FOLLOW_groupByItem_in_groupByClause8808);
            	    n=groupByItem();
            	    _fsp--;
            	    if (failed) return node;
            	    if ( backtracking==0 ) {
            	       ((groupByClause_scope)groupByClause_stack.peek()).items.add(n); 
            	    }
            	    
            	    }
            	    break;
            
            	default :
            	    break loop107;
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1385:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccessOrTypeConstant );
    public final Object groupByItem() throws RecognitionException {

        Object node = null;
    
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1387:7: (n= stateFieldPathExpression | n= variableAccessOrTypeConstant )
            int alt108=2;
            int LA108_0 = input.LA(1);
            
            if ( (LA108_0==IDENT) ) {
                int LA108_1 = input.LA(2);
                
                if ( (LA108_1==EOF||LA108_1==HAVING||LA108_1==ORDER||LA108_1==COMMA||LA108_1==RIGHT_ROUND_BRACKET) ) {
                    alt108=2;
                }
                else if ( (LA108_1==DOT) ) {
                    alt108=1;
                }
                else {
                    if (backtracking>0) {failed=true; return node;}
                    NoViableAltException nvae =
                        new NoViableAltException("1385:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccessOrTypeConstant );", 108, 1, input);
                
                    throw nvae;
                }
            }
            else if ( (LA108_0==KEY||LA108_0==VALUE) ) {
                alt108=1;
            }
            else {
                if (backtracking>0) {failed=true; return node;}
                NoViableAltException nvae =
                    new NoViableAltException("1385:1: groupByItem returns [Object node] : (n= stateFieldPathExpression | n= variableAccessOrTypeConstant );", 108, 0, input);
            
                throw nvae;
            }
            switch (alt108) {
                case 1 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1387:7: n= stateFieldPathExpression
                    {
                    pushFollow(FOLLOW_stateFieldPathExpression_in_groupByItem8854);
                    n=stateFieldPathExpression();
                    _fsp--;
                    if (failed) return node;
                    if ( backtracking==0 ) {
                      node = n;
                    }
                    
                    }
                    break;
                case 2 :
                    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1388:7: n= variableAccessOrTypeConstant
                    {
                    pushFollow(FOLLOW_variableAccessOrTypeConstant_in_groupByItem8868);
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
    // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1391:1: havingClause returns [Object node] : h= HAVING n= conditionalExpression ;
    public final Object havingClause() throws RecognitionException {

        Object node = null;
    
        Token h=null;
        Object n = null;
        
    
         node = null; 
        try {
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1393:7: (h= HAVING n= conditionalExpression )
            // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:1393:7: h= HAVING n= conditionalExpression
            {
            h=(Token)input.LT(1);
            match(input,HAVING,FOLLOW_HAVING_in_havingClause8898); if (failed) return node;
            if ( backtracking==0 ) {
               setAggregatesAllowed(true); 
            }
            pushFollow(FOLLOW_conditionalExpression_in_havingClause8915);
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
        // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:640:7: ( LEFT_ROUND_BRACKET conditionalExpression )
        // C:/Dev-eclipselink-rt/trunk/foundation/org.eclipse.persistence.core/resource/org/eclipse/persistence/internal/jpa/parsing/jpql/antlr/JPQL.g:640:8: LEFT_ROUND_BRACKET conditionalExpression
        {
        match(input,LEFT_ROUND_BRACKET,FOLLOW_LEFT_ROUND_BRACKET_in_synpred13533); if (failed) return ;
        pushFollow(FOLLOW_conditionalExpression_in_synpred13535);
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


 

    public static final BitSet FOLLOW_selectStatement_in_document754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_updateStatement_in_document768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_deleteStatement_in_document782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selectClause_in_selectStatement815 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_fromClause_in_selectStatement830 = new BitSet(new long[]{0x0800000C00000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_whereClause_in_selectStatement845 = new BitSet(new long[]{0x0800000C00000000L});
    public static final BitSet FOLLOW_groupByClause_in_selectStatement860 = new BitSet(new long[]{0x0800000800000000L});
    public static final BitSet FOLLOW_havingClause_in_selectStatement876 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_orderByClause_in_selectStatement891 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_selectStatement901 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_updateClause_in_updateStatement944 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_setClause_in_updateStatement959 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_whereClause_in_updateStatement973 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_updateStatement983 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPDATE_in_updateClause1015 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x007FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_abstractSchemaName_in_updateClause1021 = new BitSet(new long[]{0x0000000000000102L,0x0000000000008000L});
    public static final BitSet FOLLOW_AS_in_updateClause1034 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_IDENT_in_updateClause1042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_in_setClause1091 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x007FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_setAssignmentClause_in_setClause1097 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_setClause1110 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x007FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_setAssignmentClause_in_setClause1116 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_setAssignmentTarget_in_setAssignmentClause1174 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_EQUALS_in_setAssignmentClause1178 = new BitSet(new long[]{0x80CDD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_newValue_in_setAssignmentClause1184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attribute_in_setAssignmentTarget1214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_setAssignmentTarget1229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scalarExpression_in_newValue1261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_newValue1275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_deleteClause_in_deleteStatement1319 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_whereClause_in_deleteStatement1332 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_deleteStatement1342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DELETE_in_deleteClause1375 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_FROM_in_deleteClause1377 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x007FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_abstractSchemaName_in_deleteClause1383 = new BitSet(new long[]{0x0000000000000102L,0x0000000000008000L});
    public static final BitSet FOLLOW_AS_in_deleteClause1396 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_IDENT_in_deleteClause1402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_selectClause1449 = new BitSet(new long[]{0x819DD221489FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_DISTINCT_in_selectClause1452 = new BitSet(new long[]{0x819DD221481FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_selectItem_in_selectClause1468 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_selectClause1496 = new BitSet(new long[]{0x819DD221481FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_selectItem_in_selectClause1502 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_selectExpression_in_selectItem1598 = new BitSet(new long[]{0x0000000000000102L,0x0000000000008000L});
    public static final BitSet FOLLOW_AS_in_selectItem1602 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_IDENT_in_selectItem1610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_selectExpression1654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_scalarExpression_in_selectExpression1668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OBJECT_in_selectExpression1678 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_selectExpression1680 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_selectExpression1686 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_selectExpression1688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constructorExpression_in_selectExpression1703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mapEntryExpression_in_selectExpression1718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENTRY_in_mapEntryExpression1750 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mapEntryExpression1752 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_mapEntryExpression1758 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mapEntryExpression1760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_pathExprOrVariableAccess1792 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_DOT_in_pathExprOrVariableAccess1807 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x007FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExprOrVariableAccess1813 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_KEY_in_qualifiedIdentificationVariable1883 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1885 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1891 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VALUE_in_qualifiedIdentificationVariable1908 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_qualifiedIdentificationVariable1910 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_qualifiedIdentificationVariable1916 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_qualifiedIdentificationVariable1918 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AVG_in_aggregateExpression1951 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1953 = new BitSet(new long[]{0x0000020000800000L,0x0000000000009000L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression1956 = new BitSet(new long[]{0x0000020000000000L,0x0000000000009000L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression1974 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression1976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MAX_in_aggregateExpression1997 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression1999 = new BitSet(new long[]{0x0000020000800000L,0x0000000000009000L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2002 = new BitSet(new long[]{0x0000020000000000L,0x0000000000009000L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression2021 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MIN_in_aggregateExpression2043 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2045 = new BitSet(new long[]{0x0000020000800000L,0x0000000000009000L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2048 = new BitSet(new long[]{0x0000020000000000L,0x0000000000009000L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression2066 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2068 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUM_in_aggregateExpression2088 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2090 = new BitSet(new long[]{0x0000020000800000L,0x0000000000009000L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2093 = new BitSet(new long[]{0x0000020000000000L,0x0000000000009000L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_aggregateExpression2111 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COUNT_in_aggregateExpression2133 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_aggregateExpression2135 = new BitSet(new long[]{0x0000020000800000L,0x0000000000009000L});
    public static final BitSet FOLLOW_DISTINCT_in_aggregateExpression2138 = new BitSet(new long[]{0x0000020000000000L,0x0000000000009000L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_aggregateExpression2156 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_aggregateExpression2158 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEW_in_constructorExpression2201 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_constructorName_in_constructorExpression2207 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_constructorExpression2217 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression2232 = new BitSet(new long[]{0x0000000000000000L,0x0000000000090000L});
    public static final BitSet FOLLOW_COMMA_in_constructorExpression2247 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_constructorItem_in_constructorExpression2253 = new BitSet(new long[]{0x0000000000000000L,0x0000000000090000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_constructorExpression2268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_constructorName2309 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_DOT_in_constructorName2323 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_IDENT_in_constructorName2327 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_scalarExpression_in_constructorItem2371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_constructorItem2385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_fromClause2419 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x007FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2421 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_fromClause2433 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x007FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_fromClause2438 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_fromClause2463 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_rangeVariableDeclaration_in_identificationVariableDeclaration2529 = new BitSet(new long[]{0x0000094000000002L});
    public static final BitSet FOLLOW_join_in_identificationVariableDeclaration2548 = new BitSet(new long[]{0x0000094000000002L});
    public static final BitSet FOLLOW_abstractSchemaName_in_rangeVariableDeclaration2583 = new BitSet(new long[]{0x0000000000000100L,0x0000000000008000L});
    public static final BitSet FOLLOW_AS_in_rangeVariableDeclaration2586 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_IDENT_in_rangeVariableDeclaration2592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_joinSpec_in_join2675 = new BitSet(new long[]{0x0000020080000000L,0x0000000000009000L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2689 = new BitSet(new long[]{0x0000000000000100L,0x0000000000008000L});
    public static final BitSet FOLLOW_AS_in_join2692 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_IDENT_in_join2698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FETCH_in_join2720 = new BitSet(new long[]{0x0000020000000000L,0x0000000000009000L});
    public static final BitSet FOLLOW_joinAssociationPathExpression_in_join2726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_in_joinSpec2772 = new BitSet(new long[]{0x1000010000000000L});
    public static final BitSet FOLLOW_OUTER_in_joinSpec2775 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_INNER_in_joinSpec2784 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_JOIN_in_joinSpec2790 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_collectionMemberDeclaration2818 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_collectionMemberDeclaration2820 = new BitSet(new long[]{0x0000020000000000L,0x0000000000009000L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberDeclaration2826 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_collectionMemberDeclaration2828 = new BitSet(new long[]{0x0000000000000100L,0x0000000000008000L});
    public static final BitSet FOLLOW_AS_in_collectionMemberDeclaration2838 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_IDENT_in_collectionMemberDeclaration2844 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_collectionValuedPathExpression2882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_associationPathExpression2914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_joinAssociationPathExpression2947 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_DOT_in_joinAssociationPathExpression2962 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x007FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_joinAssociationPathExpression2968 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_pathExpression_in_singleValuedPathExpression3024 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExpression_in_stateFieldPathExpression3056 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualifiedIdentificationVariable_in_pathExpression3088 = new BitSet(new long[]{0x0000000000000000L,0x0000000000100000L});
    public static final BitSet FOLLOW_DOT_in_pathExpression3103 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x007FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_attribute_in_pathExpression3109 = new BitSet(new long[]{0x0000000000000002L,0x0000000000100000L});
    public static final BitSet FOLLOW_IDENT_in_variableAccessOrTypeConstant3205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHERE_in_whereClause3243 = new BitSet(new long[]{0x80ADD221601FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_conditionalExpression_in_whereClause3249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression3291 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_OR_in_conditionalExpression3306 = new BitSet(new long[]{0x80ADD221601FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_conditionalTerm_in_conditionalExpression3312 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm3367 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_AND_in_conditionalTerm3382 = new BitSet(new long[]{0x80ADD221601FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_conditionalFactor_in_conditionalTerm3388 = new BitSet(new long[]{0x0000000000000042L});
    public static final BitSet FOLLOW_NOT_in_conditionalFactor3443 = new BitSet(new long[]{0x808DD221601FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_conditionalPrimary_in_conditionalFactor3462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_existsExpression_in_conditionalFactor3491 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_conditionalPrimary3548 = new BitSet(new long[]{0x80ADD221601FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_conditionalExpression_in_conditionalPrimary3554 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_conditionalPrimary3556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleConditionalExpression_in_conditionalPrimary3570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_simpleConditionalExpression3602 = new BitSet(new long[]{0x0022209000000800L,0x0000000003E20000L});
    public static final BitSet FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_simpleConditionalExpression3623 = new BitSet(new long[]{0x0022209000000800L,0x0000000003E20000L});
    public static final BitSet FOLLOW_simpleConditionalExpressionRemainder_in_simpleConditionalExpression3629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comparisonExpression_in_simpleConditionalExpressionRemainder3664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3678 = new BitSet(new long[]{0x0002201000000800L});
    public static final BitSet FOLLOW_conditionWithNotExpression_in_simpleConditionalExpressionRemainder3686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IS_in_simpleConditionalExpressionRemainder3697 = new BitSet(new long[]{0x0060000002000000L});
    public static final BitSet FOLLOW_NOT_in_simpleConditionalExpressionRemainder3702 = new BitSet(new long[]{0x0040000002000000L});
    public static final BitSet FOLLOW_isExpression_in_simpleConditionalExpressionRemainder3710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_betweenExpression_in_conditionWithNotExpression3745 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_likeExpression_in_conditionWithNotExpression3760 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inExpression_in_conditionWithNotExpression3774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collectionMemberExpression_in_conditionWithNotExpression3788 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullComparisonExpression_in_isExpression3823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_emptyCollectionComparisonExpression_in_isExpression3838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BETWEEN_in_betweenExpression3871 = new BitSet(new long[]{0x808D52210002C410L,0x00000183CC049009L});
    public static final BitSet FOLLOW_arithmeticExpression_in_betweenExpression3885 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_AND_in_betweenExpression3887 = new BitSet(new long[]{0x808D52210002C410L,0x00000183CC049009L});
    public static final BitSet FOLLOW_arithmeticExpression_in_betweenExpression3893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_inExpression3939 = new BitSet(new long[]{0x0000000000000000L,0x0000018000000000L});
    public static final BitSet FOLLOW_inputParameter_in_inExpression3945 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_inExpression3972 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_inExpression3982 = new BitSet(new long[]{0x2000000000000000L,0x0000018FC0008000L});
    public static final BitSet FOLLOW_inItem_in_inExpression3998 = new BitSet(new long[]{0x0000000000000000L,0x0000000000090000L});
    public static final BitSet FOLLOW_COMMA_in_inExpression4016 = new BitSet(new long[]{0x0000000000000000L,0x0000018FC0008000L});
    public static final BitSet FOLLOW_inItem_in_inExpression4022 = new BitSet(new long[]{0x0000000000000000L,0x0000000000090000L});
    public static final BitSet FOLLOW_subquery_in_inExpression4057 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_inExpression4091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_inItem4121 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_inItem4135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_inItem4149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_inItem4163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LIKE_in_likeExpression4195 = new BitSet(new long[]{0x0000000000000000L,0x0000018C00000000L});
    public static final BitSet FOLLOW_likeValue_in_likeExpression4201 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_escape_in_likeExpression4216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ESCAPE_in_escape4256 = new BitSet(new long[]{0x0000000000000000L,0x0000018C00000000L});
    public static final BitSet FOLLOW_likeValue_in_escape4262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_likeValue4302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_likeValue4316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_nullComparisonExpression4349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EMPTY_in_emptyCollectionComparisonExpression4390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MEMBER_in_collectionMemberExpression4431 = new BitSet(new long[]{0x0200020000000000L,0x0000000000009000L});
    public static final BitSet FOLLOW_OF_in_collectionMemberExpression4434 = new BitSet(new long[]{0x0000020000000000L,0x0000000000009000L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_collectionMemberExpression4442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_existsExpression4482 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_existsExpression4484 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_subquery_in_existsExpression4490 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_existsExpression4492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_comparisonExpression4532 = new BitSet(new long[]{0x808DD221401FC4B0L,0x000001FFCC0499CFL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_EQUAL_TO_in_comparisonExpression4559 = new BitSet(new long[]{0x808DD221401FC4B0L,0x000001FFCC0499CFL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_in_comparisonExpression4586 = new BitSet(new long[]{0x808DD221401FC4B0L,0x000001FFCC0499CFL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_THAN_EQUAL_TO_in_comparisonExpression4613 = new BitSet(new long[]{0x808DD221401FC4B0L,0x000001FFCC0499CFL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_in_comparisonExpression4640 = new BitSet(new long[]{0x808DD221401FC4B0L,0x000001FFCC0499CFL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LESS_THAN_EQUAL_TO_in_comparisonExpression4667 = new BitSet(new long[]{0x808DD221401FC4B0L,0x000001FFCC0499CFL});
    public static final BitSet FOLLOW_comparisonExpressionRightOperand_in_comparisonExpression4673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticExpression_in_comparisonExpressionRightOperand4714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_comparisonExpressionRightOperand4728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_anyOrAllExpression_in_comparisonExpressionRightOperand4742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticExpression4774 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticExpression4784 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_subquery_in_arithmeticExpression4790 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticExpression4792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4824 = new BitSet(new long[]{0x0000000000000002L,0x000000000C000000L});
    public static final BitSet FOLLOW_PLUS_in_simpleArithmeticExpression4840 = new BitSet(new long[]{0x808D52210002C410L,0x00000183CC049009L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4846 = new BitSet(new long[]{0x0000000000000002L,0x000000000C000000L});
    public static final BitSet FOLLOW_MINUS_in_simpleArithmeticExpression4875 = new BitSet(new long[]{0x808D52210002C410L,0x00000183CC049009L});
    public static final BitSet FOLLOW_arithmeticTerm_in_simpleArithmeticExpression4881 = new BitSet(new long[]{0x0000000000000002L,0x000000000C000000L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4938 = new BitSet(new long[]{0x0000000000000002L,0x0000000030000000L});
    public static final BitSet FOLLOW_MULTIPLY_in_arithmeticTerm4954 = new BitSet(new long[]{0x808D52210002C410L,0x00000183CC049009L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4960 = new BitSet(new long[]{0x0000000000000002L,0x0000000030000000L});
    public static final BitSet FOLLOW_DIVIDE_in_arithmeticTerm4989 = new BitSet(new long[]{0x808D52210002C410L,0x00000183CC049009L});
    public static final BitSet FOLLOW_arithmeticFactor_in_arithmeticTerm4995 = new BitSet(new long[]{0x0000000000000002L,0x0000000030000000L});
    public static final BitSet FOLLOW_PLUS_in_arithmeticFactor5049 = new BitSet(new long[]{0x808D52210002C410L,0x00000183C0049009L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor5056 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_arithmeticFactor5078 = new BitSet(new long[]{0x808D52210002C410L,0x00000183C0049009L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor5084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_arithmeticPrimary_in_arithmeticFactor5108 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_arithmeticPrimary5142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pathExprOrVariableAccess_in_arithmeticPrimary5156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_arithmeticPrimary5170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_caseExpression_in_arithmeticPrimary5184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningNumerics_in_arithmeticPrimary5198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_arithmeticPrimary5208 = new BitSet(new long[]{0x808D52210002C410L,0x00000183CC049009L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_arithmeticPrimary5214 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_arithmeticPrimary5216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_arithmeticPrimary5230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_scalarExpression5262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nonArithmeticScalarExpression_in_scalarExpression5277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningDatetime_in_nonArithmeticScalarExpression5309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_nonArithmeticScalarExpression5323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_nonArithmeticScalarExpression5337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_nonArithmeticScalarExpression5351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalTemporal_in_nonArithmeticScalarExpression5365 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_entityTypeExpression_in_nonArithmeticScalarExpression5379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ALL_in_anyOrAllExpression5409 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5411 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5417 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5419 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ANY_in_anyOrAllExpression5439 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5441 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5447 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SOME_in_anyOrAllExpression5469 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_anyOrAllExpression5471 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_subquery_in_anyOrAllExpression5477 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_anyOrAllExpression5479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDiscriminator_in_entityTypeExpression5519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_typeDiscriminator5552 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5554 = new BitSet(new long[]{0x0000020000000000L,0x0000000000009000L});
    public static final BitSet FOLLOW_variableOrSingleValuedPath_in_typeDiscriminator5560 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5562 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TYPE_in_typeDiscriminator5577 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_typeDiscriminator5579 = new BitSet(new long[]{0x0000000000000000L,0x0000018000000000L});
    public static final BitSet FOLLOW_inputParameter_in_typeDiscriminator5585 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_typeDiscriminator5587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleCaseExpression_in_caseExpression5622 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_generalCaseExpression_in_caseExpression5635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_coalesceExpression_in_caseExpression5648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_nullIfExpression_in_caseExpression5661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_simpleCaseExpression5699 = new BitSet(new long[]{0x0000020000000000L,0x0000000000009100L});
    public static final BitSet FOLLOW_caseOperand_in_simpleCaseExpression5705 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_simpleWhenClause_in_simpleCaseExpression5711 = new BitSet(new long[]{0x0000000001000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_simpleWhenClause_in_simpleCaseExpression5720 = new BitSet(new long[]{0x0000000001000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_ELSE_in_simpleCaseExpression5726 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_simpleCaseExpression5732 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_END_in_simpleCaseExpression5734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_in_generalCaseExpression5778 = new BitSet(new long[]{0x0000000000000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_whenClause_in_generalCaseExpression5784 = new BitSet(new long[]{0x0000000001000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_whenClause_in_generalCaseExpression5793 = new BitSet(new long[]{0x0000000001000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_ELSE_in_generalCaseExpression5799 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_generalCaseExpression5805 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_END_in_generalCaseExpression5807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COALESCE_in_coalesceExpression5851 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_coalesceExpression5853 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_coalesceExpression5859 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_coalesceExpression5864 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_coalesceExpression5870 = new BitSet(new long[]{0x0000000000000000L,0x0000000000090000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_coalesceExpression5876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULLIF_in_nullIfExpression5917 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_nullIfExpression5919 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_nullIfExpression5925 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_nullIfExpression5927 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_nullIfExpression5933 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_nullIfExpression5935 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_caseOperand5982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_typeDiscriminator_in_caseOperand5996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_whenClause6031 = new BitSet(new long[]{0x80ADD221601FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_conditionalExpression_in_whenClause6037 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_THEN_in_whenClause6039 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_whenClause6045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_simpleWhenClause6087 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_simpleWhenClause6093 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_THEN_in_simpleWhenClause6095 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_simpleWhenClause6101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleValuedPathExpression_in_variableOrSingleValuedPath6138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_variableOrSingleValuedPath6152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_stringPrimary6184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_functionsReturningStrings_in_stringPrimary6198 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_stringPrimary6212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_stringPrimary6226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalNumeric_in_literal6260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalBoolean_in_literal6274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_literal6288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_LITERAL_in_literalNumeric6318 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LONG_LITERAL_in_literalNumeric6334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_LITERAL_in_literalNumeric6355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_LITERAL_in_literalNumeric6375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_literalBoolean6413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_literalBoolean6435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_DOUBLE_QUOTED_in_literalString6474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_literalString6495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_LITERAL_in_literalTemporal6535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TIME_LITERAL_in_literalTemporal6549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TIMESTAMP_LITERAL_in_literalTemporal6563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_POSITIONAL_PARAM_in_inputParameter6593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAMED_PARAM_in_inputParameter6613 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_abs_in_functionsReturningNumerics6653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_length_in_functionsReturningNumerics6667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mod_in_functionsReturningNumerics6681 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sqrt_in_functionsReturningNumerics6695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_locate_in_functionsReturningNumerics6709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_size_in_functionsReturningNumerics6723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_index_in_functionsReturningNumerics6737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_func_in_functionsReturningNumerics6751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_DATE_in_functionsReturningDatetime6781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIME_in_functionsReturningDatetime6802 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURRENT_TIMESTAMP_in_functionsReturningDatetime6822 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_concat_in_functionsReturningStrings6862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_substring_in_functionsReturningStrings6876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_trim_in_functionsReturningStrings6890 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_upper_in_functionsReturningStrings6904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lower_in_functionsReturningStrings6918 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONCAT_in_concat6953 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_concat6964 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_concat6979 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_concat6984 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_concat6990 = new BitSet(new long[]{0x0000000000000000L,0x0000000000090000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_concat7004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUBSTRING_in_substring7042 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_substring7055 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_substring7069 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_substring7071 = new BitSet(new long[]{0x808D52210002C410L,0x00000183CC049009L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_substring7085 = new BitSet(new long[]{0x0000000000000000L,0x0000000000090000L});
    public static final BitSet FOLLOW_COMMA_in_substring7096 = new BitSet(new long[]{0x808D52210002C410L,0x00000183CC049009L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_substring7102 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_substring7114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRIM_in_trim7152 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_trim7162 = new BitSet(new long[]{0x0000860200011000L,0x0000018C00009864L});
    public static final BitSet FOLLOW_trimSpec_in_trim7178 = new BitSet(new long[]{0x0000000200000000L,0x0000018C00000000L});
    public static final BitSet FOLLOW_trimChar_in_trim7184 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_FROM_in_trim7186 = new BitSet(new long[]{0x0000820000010000L,0x0000018C00009844L});
    public static final BitSet FOLLOW_stringPrimary_in_trim7202 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_trim7212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEADING_in_trimSpec7248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRAILING_in_trimSpec7266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOTH_in_trimSpec7284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literalString_in_trimChar7331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inputParameter_in_trimChar7345 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_UPPER_in_upper7382 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_upper7384 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_upper7390 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_upper7392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOWER_in_lower7430 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_lower7432 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_lower7438 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_lower7440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ABS_in_abs7479 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_abs7481 = new BitSet(new long[]{0x808D52210002C410L,0x00000183CC049009L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_abs7487 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_abs7489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LENGTH_in_length7527 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_length7529 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_length7535 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_length7537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCATE_in_locate7575 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_locate7585 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_locate7600 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_locate7602 = new BitSet(new long[]{0x808DD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_scalarExpression_in_locate7608 = new BitSet(new long[]{0x0000000000000000L,0x0000000000090000L});
    public static final BitSet FOLLOW_COMMA_in_locate7620 = new BitSet(new long[]{0x808D52210002C410L,0x00000183CC049009L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_locate7626 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_locate7639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SIZE_in_size7677 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_size7688 = new BitSet(new long[]{0x0000020000000000L,0x0000000000009000L});
    public static final BitSet FOLLOW_collectionValuedPathExpression_in_size7694 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_size7696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_mod7734 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_mod7736 = new BitSet(new long[]{0x808D52210002C410L,0x00000183CC049009L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_mod7750 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_mod7752 = new BitSet(new long[]{0x808D52210002C410L,0x00000183CC049009L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_mod7767 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_mod7777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SQRT_in_sqrt7815 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_sqrt7826 = new BitSet(new long[]{0x808D52210002C410L,0x00000183CC049009L});
    public static final BitSet FOLLOW_simpleArithmeticExpression_in_sqrt7832 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_sqrt7834 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INDEX_in_index7876 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_index7878 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_index7884 = new BitSet(new long[]{0x0000000000000000L,0x0000000000080000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_index7886 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNC_in_func7928 = new BitSet(new long[]{0x0000000000000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_func7930 = new BitSet(new long[]{0x0000000000000000L,0x0000000800000000L});
    public static final BitSet FOLLOW_STRING_LITERAL_SINGLE_QUOTED_in_func7942 = new BitSet(new long[]{0x0000000000000000L,0x0000000000090000L});
    public static final BitSet FOLLOW_COMMA_in_func7951 = new BitSet(new long[]{0x80CDD221401FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_newValue_in_func7957 = new BitSet(new long[]{0x0000000000000000L,0x0000000000090000L});
    public static final BitSet FOLLOW_RIGHT_ROUND_BRACKET_in_func7989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleSelectClause_in_subquery8027 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_subqueryFromClause_in_subquery8042 = new BitSet(new long[]{0x0000000C00000002L,0x0000000000004000L});
    public static final BitSet FOLLOW_whereClause_in_subquery8057 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_groupByClause_in_subquery8072 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_havingClause_in_subquery8088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SELECT_in_simpleSelectClause8131 = new BitSet(new long[]{0x0005020000820400L,0x0000000000009008L});
    public static final BitSet FOLLOW_DISTINCT_in_simpleSelectClause8134 = new BitSet(new long[]{0x0005020000020400L,0x0000000000009008L});
    public static final BitSet FOLLOW_simpleSelectExpression_in_simpleSelectClause8150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_singleValuedPathExpression_in_simpleSelectExpression8190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_aggregateExpression_in_simpleSelectExpression8205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_simpleSelectExpression8220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_subqueryFromClause8255 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x007FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8257 = new BitSet(new long[]{0x0000001000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_subqueryFromClause8284 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x007FFFFFFFFFFFFFL});
    public static final BitSet FOLLOW_subselectIdentificationVariableDeclaration_in_subqueryFromClause8303 = new BitSet(new long[]{0x0000001000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_subqueryFromClause8329 = new BitSet(new long[]{0x0000001000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_identificationVariableDeclaration_in_subselectIdentificationVariableDeclaration8376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_associationPathExpression_in_subselectIdentificationVariableDeclaration8389 = new BitSet(new long[]{0x0000000000000100L,0x0000000000008000L});
    public static final BitSet FOLLOW_AS_in_subselectIdentificationVariableDeclaration8392 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_IDENT_in_subselectIdentificationVariableDeclaration8398 = new BitSet(new long[]{0x0000094000000002L});
    public static final BitSet FOLLOW_join_in_subselectIdentificationVariableDeclaration8401 = new BitSet(new long[]{0x0000094000000002L});
    public static final BitSet FOLLOW_collectionMemberDeclaration_in_subselectIdentificationVariableDeclaration8428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ORDER_in_orderByClause8461 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_orderByClause8463 = new BitSet(new long[]{0x0000020000000000L,0x0000000000009000L});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause8477 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_orderByClause8492 = new BitSet(new long[]{0x0000020000000000L,0x0000000000009000L});
    public static final BitSet FOLLOW_orderByItem_in_orderByClause8498 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_orderByItem8544 = new BitSet(new long[]{0x0000000000200202L});
    public static final BitSet FOLLOW_ASC_in_orderByItem8558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_in_orderByItem8587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_orderByItem8649 = new BitSet(new long[]{0x0000000000200202L});
    public static final BitSet FOLLOW_ASC_in_orderByItem8663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DESC_in_orderByItem8692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GROUP_in_groupByClause8773 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BY_in_groupByClause8775 = new BitSet(new long[]{0x0000020000000000L,0x0000000000009000L});
    public static final BitSet FOLLOW_groupByItem_in_groupByClause8789 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_COMMA_in_groupByClause8802 = new BitSet(new long[]{0x0000020000000000L,0x0000000000009000L});
    public static final BitSet FOLLOW_groupByItem_in_groupByClause8808 = new BitSet(new long[]{0x0000000000000002L,0x0000000000010000L});
    public static final BitSet FOLLOW_stateFieldPathExpression_in_groupByItem8854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variableAccessOrTypeConstant_in_groupByItem8868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HAVING_in_havingClause8898 = new BitSet(new long[]{0x80ADD221601FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_conditionalExpression_in_havingClause8915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_ROUND_BRACKET_in_synpred13533 = new BitSet(new long[]{0x80ADD221601FC410L,0x000001FFCC0499CDL});
    public static final BitSet FOLLOW_conditionalExpression_in_synpred13535 = new BitSet(new long[]{0x0000000000000002L});

}