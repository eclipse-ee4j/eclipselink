// $ANTLR 3.0 JPQL.g 2009-05-12 10:01:56

    package org.eclipse.persistence.internal.jpa.parsing.jpql.antlr;

    import org.eclipse.persistence.internal.jpa.parsing.jpql.InvalidIdentifierStartException;


import org.eclipse.persistence.internal.libraries.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class JPQLLexer extends Lexer {
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
    public static final int EMPTY=22;
    public static final int INTEGER_SUFFIX=96;
    public static final int ABS=4;
    public static final int NOT_EQUAL_TO=75;
    public static final int GROUP=29;
    public static final int WS=92;
    public static final int FETCH=27;
    public static final int STRING_LITERAL_SINGLE_QUOTED=89;
    public static final int INTEGER_LITERAL=84;
    public static final int OR=51;
    public static final int LESS_THAN=78;
    public static final int TRIM=62;
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
    public static final int Tokens=102;
    public static final int LONG_LITERAL=85;
    public static final int TRUE=63;
    public static final int JOIN=34;
    public static final int SUBSTRING=59;
    public static final int FLOAT_LITERAL=86;
    public static final int DOUBLE_SUFFIX=99;
    public static final int ANY=7;
    public static final int LOCATE=40;
    public static final int DESC=19;
    public static final int BETWEEN=11;
    public JPQLLexer() {;} 
    public JPQLLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "JPQL.g"; }

    // $ANTLR start ABS
    public final void mABS() throws RecognitionException {
        try {
            int _type = ABS;
            // JPQL.g:8:7: ( 'abs' )
            // JPQL.g:8:7: 'abs'
            {
            match("abs"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ABS

    // $ANTLR start ALL
    public final void mALL() throws RecognitionException {
        try {
            int _type = ALL;
            // JPQL.g:9:7: ( 'all' )
            // JPQL.g:9:7: 'all'
            {
            match("all"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ALL

    // $ANTLR start AND
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            // JPQL.g:10:7: ( 'and' )
            // JPQL.g:10:7: 'and'
            {
            match("and"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AND

    // $ANTLR start ANY
    public final void mANY() throws RecognitionException {
        try {
            int _type = ANY;
            // JPQL.g:11:7: ( 'any' )
            // JPQL.g:11:7: 'any'
            {
            match("any"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ANY

    // $ANTLR start AS
    public final void mAS() throws RecognitionException {
        try {
            int _type = AS;
            // JPQL.g:12:6: ( 'as' )
            // JPQL.g:12:6: 'as'
            {
            match("as"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AS

    // $ANTLR start ASC
    public final void mASC() throws RecognitionException {
        try {
            int _type = ASC;
            // JPQL.g:13:7: ( 'asc' )
            // JPQL.g:13:7: 'asc'
            {
            match("asc"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ASC

    // $ANTLR start AVG
    public final void mAVG() throws RecognitionException {
        try {
            int _type = AVG;
            // JPQL.g:14:7: ( 'avg' )
            // JPQL.g:14:7: 'avg'
            {
            match("avg"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end AVG

    // $ANTLR start BETWEEN
    public final void mBETWEEN() throws RecognitionException {
        try {
            int _type = BETWEEN;
            // JPQL.g:15:11: ( 'between' )
            // JPQL.g:15:11: 'between'
            {
            match("between"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BETWEEN

    // $ANTLR start BOTH
    public final void mBOTH() throws RecognitionException {
        try {
            int _type = BOTH;
            // JPQL.g:16:8: ( 'both' )
            // JPQL.g:16:8: 'both'
            {
            match("both"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BOTH

    // $ANTLR start BY
    public final void mBY() throws RecognitionException {
        try {
            int _type = BY;
            // JPQL.g:17:6: ( 'by' )
            // JPQL.g:17:6: 'by'
            {
            match("by"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end BY

    // $ANTLR start CONCAT
    public final void mCONCAT() throws RecognitionException {
        try {
            int _type = CONCAT;
            // JPQL.g:18:10: ( 'concat' )
            // JPQL.g:18:10: 'concat'
            {
            match("concat"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CONCAT

    // $ANTLR start COUNT
    public final void mCOUNT() throws RecognitionException {
        try {
            int _type = COUNT;
            // JPQL.g:19:9: ( 'count' )
            // JPQL.g:19:9: 'count'
            {
            match("count"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COUNT

    // $ANTLR start CURRENT_DATE
    public final void mCURRENT_DATE() throws RecognitionException {
        try {
            int _type = CURRENT_DATE;
            // JPQL.g:20:16: ( 'current_date' )
            // JPQL.g:20:16: 'current_date'
            {
            match("current_date"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CURRENT_DATE

    // $ANTLR start CURRENT_TIME
    public final void mCURRENT_TIME() throws RecognitionException {
        try {
            int _type = CURRENT_TIME;
            // JPQL.g:21:16: ( 'current_time' )
            // JPQL.g:21:16: 'current_time'
            {
            match("current_time"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CURRENT_TIME

    // $ANTLR start CURRENT_TIMESTAMP
    public final void mCURRENT_TIMESTAMP() throws RecognitionException {
        try {
            int _type = CURRENT_TIMESTAMP;
            // JPQL.g:22:21: ( 'current_timestamp' )
            // JPQL.g:22:21: 'current_timestamp'
            {
            match("current_timestamp"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CURRENT_TIMESTAMP

    // $ANTLR start DESC
    public final void mDESC() throws RecognitionException {
        try {
            int _type = DESC;
            // JPQL.g:23:8: ( 'desc' )
            // JPQL.g:23:8: 'desc'
            {
            match("desc"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DESC

    // $ANTLR start DELETE
    public final void mDELETE() throws RecognitionException {
        try {
            int _type = DELETE;
            // JPQL.g:24:10: ( 'delete' )
            // JPQL.g:24:10: 'delete'
            {
            match("delete"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DELETE

    // $ANTLR start DISTINCT
    public final void mDISTINCT() throws RecognitionException {
        try {
            int _type = DISTINCT;
            // JPQL.g:25:12: ( 'distinct' )
            // JPQL.g:25:12: 'distinct'
            {
            match("distinct"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DISTINCT

    // $ANTLR start EMPTY
    public final void mEMPTY() throws RecognitionException {
        try {
            int _type = EMPTY;
            // JPQL.g:26:9: ( 'empty' )
            // JPQL.g:26:9: 'empty'
            {
            match("empty"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EMPTY

    // $ANTLR start ENTRY
    public final void mENTRY() throws RecognitionException {
        try {
            int _type = ENTRY;
            // JPQL.g:27:9: ( 'entry' )
            // JPQL.g:27:9: 'entry'
            {
            match("entry"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ENTRY

    // $ANTLR start ESCAPE
    public final void mESCAPE() throws RecognitionException {
        try {
            int _type = ESCAPE;
            // JPQL.g:28:10: ( 'escape' )
            // JPQL.g:28:10: 'escape'
            {
            match("escape"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ESCAPE

    // $ANTLR start EXISTS
    public final void mEXISTS() throws RecognitionException {
        try {
            int _type = EXISTS;
            // JPQL.g:29:10: ( 'exists' )
            // JPQL.g:29:10: 'exists'
            {
            match("exists"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EXISTS

    // $ANTLR start FALSE
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            // JPQL.g:30:9: ( 'false' )
            // JPQL.g:30:9: 'false'
            {
            match("false"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FALSE

    // $ANTLR start FETCH
    public final void mFETCH() throws RecognitionException {
        try {
            int _type = FETCH;
            // JPQL.g:31:9: ( 'fetch' )
            // JPQL.g:31:9: 'fetch'
            {
            match("fetch"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FETCH

    // $ANTLR start FROM
    public final void mFROM() throws RecognitionException {
        try {
            int _type = FROM;
            // JPQL.g:32:8: ( 'from' )
            // JPQL.g:32:8: 'from'
            {
            match("from"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FROM

    // $ANTLR start GROUP
    public final void mGROUP() throws RecognitionException {
        try {
            int _type = GROUP;
            // JPQL.g:33:9: ( 'group' )
            // JPQL.g:33:9: 'group'
            {
            match("group"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GROUP

    // $ANTLR start HAVING
    public final void mHAVING() throws RecognitionException {
        try {
            int _type = HAVING;
            // JPQL.g:34:10: ( 'having' )
            // JPQL.g:34:10: 'having'
            {
            match("having"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end HAVING

    // $ANTLR start IN
    public final void mIN() throws RecognitionException {
        try {
            int _type = IN;
            // JPQL.g:35:6: ( 'in' )
            // JPQL.g:35:6: 'in'
            {
            match("in"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IN

    // $ANTLR start INNER
    public final void mINNER() throws RecognitionException {
        try {
            int _type = INNER;
            // JPQL.g:36:9: ( 'inner' )
            // JPQL.g:36:9: 'inner'
            {
            match("inner"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INNER

    // $ANTLR start IS
    public final void mIS() throws RecognitionException {
        try {
            int _type = IS;
            // JPQL.g:37:6: ( 'is' )
            // JPQL.g:37:6: 'is'
            {
            match("is"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IS

    // $ANTLR start JOIN
    public final void mJOIN() throws RecognitionException {
        try {
            int _type = JOIN;
            // JPQL.g:38:8: ( 'join' )
            // JPQL.g:38:8: 'join'
            {
            match("join"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end JOIN

    // $ANTLR start KEY
    public final void mKEY() throws RecognitionException {
        try {
            int _type = KEY;
            // JPQL.g:39:7: ( 'key' )
            // JPQL.g:39:7: 'key'
            {
            match("key"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end KEY

    // $ANTLR start LEADING
    public final void mLEADING() throws RecognitionException {
        try {
            int _type = LEADING;
            // JPQL.g:40:11: ( 'leading' )
            // JPQL.g:40:11: 'leading'
            {
            match("leading"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LEADING

    // $ANTLR start LEFT
    public final void mLEFT() throws RecognitionException {
        try {
            int _type = LEFT;
            // JPQL.g:41:8: ( 'left' )
            // JPQL.g:41:8: 'left'
            {
            match("left"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LEFT

    // $ANTLR start LENGTH
    public final void mLENGTH() throws RecognitionException {
        try {
            int _type = LENGTH;
            // JPQL.g:42:10: ( 'length' )
            // JPQL.g:42:10: 'length'
            {
            match("length"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LENGTH

    // $ANTLR start LIKE
    public final void mLIKE() throws RecognitionException {
        try {
            int _type = LIKE;
            // JPQL.g:43:8: ( 'like' )
            // JPQL.g:43:8: 'like'
            {
            match("like"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LIKE

    // $ANTLR start LOCATE
    public final void mLOCATE() throws RecognitionException {
        try {
            int _type = LOCATE;
            // JPQL.g:44:10: ( 'locate' )
            // JPQL.g:44:10: 'locate'
            {
            match("locate"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LOCATE

    // $ANTLR start LOWER
    public final void mLOWER() throws RecognitionException {
        try {
            int _type = LOWER;
            // JPQL.g:45:9: ( 'lower' )
            // JPQL.g:45:9: 'lower'
            {
            match("lower"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LOWER

    // $ANTLR start MAX
    public final void mMAX() throws RecognitionException {
        try {
            int _type = MAX;
            // JPQL.g:46:7: ( 'max' )
            // JPQL.g:46:7: 'max'
            {
            match("max"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MAX

    // $ANTLR start MEMBER
    public final void mMEMBER() throws RecognitionException {
        try {
            int _type = MEMBER;
            // JPQL.g:47:10: ( 'member' )
            // JPQL.g:47:10: 'member'
            {
            match("member"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MEMBER

    // $ANTLR start MIN
    public final void mMIN() throws RecognitionException {
        try {
            int _type = MIN;
            // JPQL.g:48:7: ( 'min' )
            // JPQL.g:48:7: 'min'
            {
            match("min"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MIN

    // $ANTLR start MOD
    public final void mMOD() throws RecognitionException {
        try {
            int _type = MOD;
            // JPQL.g:49:7: ( 'mod' )
            // JPQL.g:49:7: 'mod'
            {
            match("mod"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MOD

    // $ANTLR start NEW
    public final void mNEW() throws RecognitionException {
        try {
            int _type = NEW;
            // JPQL.g:50:7: ( 'new' )
            // JPQL.g:50:7: 'new'
            {
            match("new"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NEW

    // $ANTLR start NOT
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            // JPQL.g:51:7: ( 'not' )
            // JPQL.g:51:7: 'not'
            {
            match("not"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NOT

    // $ANTLR start NULL
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            // JPQL.g:52:8: ( 'null' )
            // JPQL.g:52:8: 'null'
            {
            match("null"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NULL

    // $ANTLR start OBJECT
    public final void mOBJECT() throws RecognitionException {
        try {
            int _type = OBJECT;
            // JPQL.g:53:10: ( 'object' )
            // JPQL.g:53:10: 'object'
            {
            match("object"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OBJECT

    // $ANTLR start OF
    public final void mOF() throws RecognitionException {
        try {
            int _type = OF;
            // JPQL.g:54:6: ( 'of' )
            // JPQL.g:54:6: 'of'
            {
            match("of"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OF

    // $ANTLR start OR
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            // JPQL.g:55:6: ( 'or' )
            // JPQL.g:55:6: 'or'
            {
            match("or"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OR

    // $ANTLR start ORDER
    public final void mORDER() throws RecognitionException {
        try {
            int _type = ORDER;
            // JPQL.g:56:9: ( 'order' )
            // JPQL.g:56:9: 'order'
            {
            match("order"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ORDER

    // $ANTLR start OUTER
    public final void mOUTER() throws RecognitionException {
        try {
            int _type = OUTER;
            // JPQL.g:57:9: ( 'outer' )
            // JPQL.g:57:9: 'outer'
            {
            match("outer"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OUTER

    // $ANTLR start SELECT
    public final void mSELECT() throws RecognitionException {
        try {
            int _type = SELECT;
            // JPQL.g:58:10: ( 'select' )
            // JPQL.g:58:10: 'select'
            {
            match("select"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SELECT

    // $ANTLR start SET
    public final void mSET() throws RecognitionException {
        try {
            int _type = SET;
            // JPQL.g:59:7: ( 'set' )
            // JPQL.g:59:7: 'set'
            {
            match("set"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SET

    // $ANTLR start SIZE
    public final void mSIZE() throws RecognitionException {
        try {
            int _type = SIZE;
            // JPQL.g:60:8: ( 'size' )
            // JPQL.g:60:8: 'size'
            {
            match("size"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SIZE

    // $ANTLR start SQRT
    public final void mSQRT() throws RecognitionException {
        try {
            int _type = SQRT;
            // JPQL.g:61:8: ( 'sqrt' )
            // JPQL.g:61:8: 'sqrt'
            {
            match("sqrt"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SQRT

    // $ANTLR start SOME
    public final void mSOME() throws RecognitionException {
        try {
            int _type = SOME;
            // JPQL.g:62:8: ( 'some' )
            // JPQL.g:62:8: 'some'
            {
            match("some"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SOME

    // $ANTLR start SUBSTRING
    public final void mSUBSTRING() throws RecognitionException {
        try {
            int _type = SUBSTRING;
            // JPQL.g:63:13: ( 'substring' )
            // JPQL.g:63:13: 'substring'
            {
            match("substring"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SUBSTRING

    // $ANTLR start SUM
    public final void mSUM() throws RecognitionException {
        try {
            int _type = SUM;
            // JPQL.g:64:7: ( 'sum' )
            // JPQL.g:64:7: 'sum'
            {
            match("sum"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SUM

    // $ANTLR start TRAILING
    public final void mTRAILING() throws RecognitionException {
        try {
            int _type = TRAILING;
            // JPQL.g:65:12: ( 'trailing' )
            // JPQL.g:65:12: 'trailing'
            {
            match("trailing"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TRAILING

    // $ANTLR start TRIM
    public final void mTRIM() throws RecognitionException {
        try {
            int _type = TRIM;
            // JPQL.g:66:8: ( 'trim' )
            // JPQL.g:66:8: 'trim'
            {
            match("trim"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TRIM

    // $ANTLR start TRUE
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            // JPQL.g:67:8: ( 'true' )
            // JPQL.g:67:8: 'true'
            {
            match("true"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TRUE

    // $ANTLR start UNKNOWN
    public final void mUNKNOWN() throws RecognitionException {
        try {
            int _type = UNKNOWN;
            // JPQL.g:68:11: ( 'unknown' )
            // JPQL.g:68:11: 'unknown'
            {
            match("unknown"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end UNKNOWN

    // $ANTLR start UPDATE
    public final void mUPDATE() throws RecognitionException {
        try {
            int _type = UPDATE;
            // JPQL.g:69:10: ( 'update' )
            // JPQL.g:69:10: 'update'
            {
            match("update"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end UPDATE

    // $ANTLR start UPPER
    public final void mUPPER() throws RecognitionException {
        try {
            int _type = UPPER;
            // JPQL.g:70:9: ( 'upper' )
            // JPQL.g:70:9: 'upper'
            {
            match("upper"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end UPPER

    // $ANTLR start VALUE
    public final void mVALUE() throws RecognitionException {
        try {
            int _type = VALUE;
            // JPQL.g:71:9: ( 'value' )
            // JPQL.g:71:9: 'value'
            {
            match("value"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end VALUE

    // $ANTLR start WHERE
    public final void mWHERE() throws RecognitionException {
        try {
            int _type = WHERE;
            // JPQL.g:72:9: ( 'where' )
            // JPQL.g:72:9: 'where'
            {
            match("where"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WHERE

    // $ANTLR start DOT
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            // JPQL.g:1182:7: ( '.' )
            // JPQL.g:1182:7: '.'
            {
            match('.'); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOT

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // JPQL.g:1185:7: ( ( ' ' | '\\t' | '\\n' | '\\r' )+ )
            // JPQL.g:1185:7: ( ' ' | '\\t' | '\\n' | '\\r' )+
            {
            // JPQL.g:1185:7: ( ' ' | '\\t' | '\\n' | '\\r' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);
                
                if ( ((LA1_0>='\t' && LA1_0<='\n')||LA1_0=='\r'||LA1_0==' ') ) {
                    alt1=1;
                }
                
            
                switch (alt1) {
            	case 1 :
            	    // JPQL.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();
            	    
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }

            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);

             skip(); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WS

    // $ANTLR start LEFT_ROUND_BRACKET
    public final void mLEFT_ROUND_BRACKET() throws RecognitionException {
        try {
            int _type = LEFT_ROUND_BRACKET;
            // JPQL.g:1189:7: ( '(' )
            // JPQL.g:1189:7: '('
            {
            match('('); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LEFT_ROUND_BRACKET

    // $ANTLR start RIGHT_ROUND_BRACKET
    public final void mRIGHT_ROUND_BRACKET() throws RecognitionException {
        try {
            int _type = RIGHT_ROUND_BRACKET;
            // JPQL.g:1193:7: ( ')' )
            // JPQL.g:1193:7: ')'
            {
            match(')'); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RIGHT_ROUND_BRACKET

    // $ANTLR start COMMA
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            // JPQL.g:1197:7: ( ',' )
            // JPQL.g:1197:7: ','
            {
            match(','); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COMMA

    // $ANTLR start IDENT
    public final void mIDENT() throws RecognitionException {
        try {
            int _type = IDENT;
            // JPQL.g:1201:7: ( TEXTCHAR )
            // JPQL.g:1201:7: TEXTCHAR
            {
            mTEXTCHAR(); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IDENT

    // $ANTLR start TEXTCHAR
    public final void mTEXTCHAR() throws RecognitionException {
        try {
            int c1;
            int c2;
    
            // JPQL.g:1206:7: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | c1= '\\u0080' .. '\\uFFFE' ) ( 'a' .. 'z' | '_' | '$' | '0' .. '9' | c2= '\\u0080' .. '\\uFFFE' )* )
            // JPQL.g:1206:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | c1= '\\u0080' .. '\\uFFFE' ) ( 'a' .. 'z' | '_' | '$' | '0' .. '9' | c2= '\\u0080' .. '\\uFFFE' )*
            {
            // JPQL.g:1206:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | c1= '\\u0080' .. '\\uFFFE' )
            int alt2=5;
            int LA2_0 = input.LA(1);
            
            if ( ((LA2_0>='a' && LA2_0<='z')) ) {
                alt2=1;
            }
            else if ( ((LA2_0>='A' && LA2_0<='Z')) ) {
                alt2=2;
            }
            else if ( (LA2_0=='_') ) {
                alt2=3;
            }
            else if ( (LA2_0=='$') ) {
                alt2=4;
            }
            else if ( ((LA2_0>='\u0080' && LA2_0<='\uFFFE')) ) {
                alt2=5;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1206:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | c1= '\\u0080' .. '\\uFFFE' )", 2, 0, input);
            
                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // JPQL.g:1206:8: 'a' .. 'z'
                    {
                    matchRange('a','z'); 
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1206:19: 'A' .. 'Z'
                    {
                    matchRange('A','Z'); 
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1206:30: '_'
                    {
                    match('_'); 
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:1206:36: '$'
                    {
                    match('$'); 
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:1207:8: c1= '\\u0080' .. '\\uFFFE'
                    {
                    c1 = input.LA(1);
                    matchRange('\u0080','\uFFFE'); 
                    
                               if (!Character.isJavaIdentifierStart(c1)) {
                                    throw new InvalidIdentifierStartException(c1, getLine(), getCharPositionInLine());
                               }
                           
                    
                    }
                    break;
            
            }

            // JPQL.g:1214:7: ( 'a' .. 'z' | '_' | '$' | '0' .. '9' | c2= '\\u0080' .. '\\uFFFE' )*
            loop3:
            do {
                int alt3=6;
                int LA3_0 = input.LA(1);
                
                if ( ((LA3_0>='a' && LA3_0<='z')) ) {
                    alt3=1;
                }
                else if ( (LA3_0=='_') ) {
                    alt3=2;
                }
                else if ( (LA3_0=='$') ) {
                    alt3=3;
                }
                else if ( ((LA3_0>='0' && LA3_0<='9')) ) {
                    alt3=4;
                }
                else if ( ((LA3_0>='\u0080' && LA3_0<='\uFFFE')) ) {
                    alt3=5;
                }
                
            
                switch (alt3) {
            	case 1 :
            	    // JPQL.g:1214:8: 'a' .. 'z'
            	    {
            	    matchRange('a','z'); 
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:1214:19: '_'
            	    {
            	    match('_'); 
            	    
            	    }
            	    break;
            	case 3 :
            	    // JPQL.g:1214:25: '$'
            	    {
            	    match('$'); 
            	    
            	    }
            	    break;
            	case 4 :
            	    // JPQL.g:1214:31: '0' .. '9'
            	    {
            	    matchRange('0','9'); 
            	    
            	    }
            	    break;
            	case 5 :
            	    // JPQL.g:1215:8: c2= '\\u0080' .. '\\uFFFE'
            	    {
            	    c2 = input.LA(1);
            	    matchRange('\u0080','\uFFFE'); 
            	    
            	               if (!Character.isJavaIdentifierPart(c2)) {
            	                    throw new InvalidIdentifierStartException(c2, getLine(), getCharPositionInLine());
            	               }
            	           
            	    
            	    }
            	    break;
            
            	default :
            	    break loop3;
                }
            } while (true);

            
            }

        }
        finally {
        }
    }
    // $ANTLR end TEXTCHAR

    // $ANTLR start HEX_LITERAL
    public final void mHEX_LITERAL() throws RecognitionException {
        try {
            int _type = HEX_LITERAL;
            // JPQL.g:1225:15: ( '0' ( 'x' | 'X' ) ( HEX_DIGIT )+ )
            // JPQL.g:1225:15: '0' ( 'x' | 'X' ) ( HEX_DIGIT )+
            {
            match('0'); 
            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();
            
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // JPQL.g:1225:29: ( HEX_DIGIT )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);
                
                if ( ((LA4_0>='0' && LA4_0<='9')||(LA4_0>='A' && LA4_0<='F')||(LA4_0>='a' && LA4_0<='f')) ) {
                    alt4=1;
                }
                
            
                switch (alt4) {
            	case 1 :
            	    // JPQL.g:1225:29: HEX_DIGIT
            	    {
            	    mHEX_DIGIT(); 
            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end HEX_LITERAL

    // $ANTLR start INTEGER_LITERAL
    public final void mINTEGER_LITERAL() throws RecognitionException {
        try {
            int _type = INTEGER_LITERAL;
            // JPQL.g:1227:19: ( ( '0' | '1' .. '9' ( '0' .. '9' )* ) )
            // JPQL.g:1227:19: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            {
            // JPQL.g:1227:19: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            int alt6=2;
            int LA6_0 = input.LA(1);
            
            if ( (LA6_0=='0') ) {
                alt6=1;
            }
            else if ( ((LA6_0>='1' && LA6_0<='9')) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1227:19: ( '0' | '1' .. '9' ( '0' .. '9' )* )", 6, 0, input);
            
                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // JPQL.g:1227:20: '0'
                    {
                    match('0'); 
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1227:26: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 
                    // JPQL.g:1227:35: ( '0' .. '9' )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);
                        
                        if ( ((LA5_0>='0' && LA5_0<='9')) ) {
                            alt5=1;
                        }
                        
                    
                        switch (alt5) {
                    	case 1 :
                    	    // JPQL.g:1227:35: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 
                    	    
                    	    }
                    	    break;
                    
                    	default :
                    	    break loop5;
                        }
                    } while (true);

                    
                    }
                    break;
            
            }

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INTEGER_LITERAL

    // $ANTLR start LONG_LITERAL
    public final void mLONG_LITERAL() throws RecognitionException {
        try {
            int _type = LONG_LITERAL;
            // JPQL.g:1229:16: ( INTEGER_LITERAL INTEGER_SUFFIX )
            // JPQL.g:1229:16: INTEGER_LITERAL INTEGER_SUFFIX
            {
            mINTEGER_LITERAL(); 
            mINTEGER_SUFFIX(); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LONG_LITERAL

    // $ANTLR start OCTAL_LITERAL
    public final void mOCTAL_LITERAL() throws RecognitionException {
        try {
            int _type = OCTAL_LITERAL;
            // JPQL.g:1231:17: ( '0' ( '0' .. '7' )+ )
            // JPQL.g:1231:17: '0' ( '0' .. '7' )+
            {
            match('0'); 
            // JPQL.g:1231:21: ( '0' .. '7' )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);
                
                if ( ((LA7_0>='0' && LA7_0<='7')) ) {
                    alt7=1;
                }
                
            
                switch (alt7) {
            	case 1 :
            	    // JPQL.g:1231:22: '0' .. '7'
            	    {
            	    matchRange('0','7'); 
            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OCTAL_LITERAL

    // $ANTLR start HEX_DIGIT
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // JPQL.g:1236:9: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // JPQL.g:1236:9: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();
            
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            
            }

        }
        finally {
        }
    }
    // $ANTLR end HEX_DIGIT

    // $ANTLR start INTEGER_SUFFIX
    public final void mINTEGER_SUFFIX() throws RecognitionException {
        try {
            // JPQL.g:1240:18: ( ( 'l' | 'L' ) )
            // JPQL.g:1240:18: ( 'l' | 'L' )
            {
            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();
            
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            
            }

        }
        finally {
        }
    }
    // $ANTLR end INTEGER_SUFFIX

    // $ANTLR start NUMERIC_DIGITS
    public final void mNUMERIC_DIGITS() throws RecognitionException {
        try {
            // JPQL.g:1244:9: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ )
            int alt12=3;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // JPQL.g:1244:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )*
                    {
                    // JPQL.g:1244:9: ( '0' .. '9' )+
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);
                        
                        if ( ((LA8_0>='0' && LA8_0<='9')) ) {
                            alt8=1;
                        }
                        
                    
                        switch (alt8) {
                    	case 1 :
                    	    // JPQL.g:1244:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 
                    	    
                    	    }
                    	    break;
                    
                    	default :
                    	    if ( cnt8 >= 1 ) break loop8;
                                EarlyExitException eee =
                                    new EarlyExitException(8, input);
                                throw eee;
                        }
                        cnt8++;
                    } while (true);

                    match('.'); 
                    // JPQL.g:1244:25: ( '0' .. '9' )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);
                        
                        if ( ((LA9_0>='0' && LA9_0<='9')) ) {
                            alt9=1;
                        }
                        
                    
                        switch (alt9) {
                    	case 1 :
                    	    // JPQL.g:1244:26: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 
                    	    
                    	    }
                    	    break;
                    
                    	default :
                    	    break loop9;
                        }
                    } while (true);

                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1245:9: '.' ( '0' .. '9' )+
                    {
                    match('.'); 
                    // JPQL.g:1245:13: ( '0' .. '9' )+
                    int cnt10=0;
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);
                        
                        if ( ((LA10_0>='0' && LA10_0<='9')) ) {
                            alt10=1;
                        }
                        
                    
                        switch (alt10) {
                    	case 1 :
                    	    // JPQL.g:1245:14: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 
                    	    
                    	    }
                    	    break;
                    
                    	default :
                    	    if ( cnt10 >= 1 ) break loop10;
                                EarlyExitException eee =
                                    new EarlyExitException(10, input);
                                throw eee;
                        }
                        cnt10++;
                    } while (true);

                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1246:9: ( '0' .. '9' )+
                    {
                    // JPQL.g:1246:9: ( '0' .. '9' )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);
                        
                        if ( ((LA11_0>='0' && LA11_0<='9')) ) {
                            alt11=1;
                        }
                        
                    
                        switch (alt11) {
                    	case 1 :
                    	    // JPQL.g:1246:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 
                    	    
                    	    }
                    	    break;
                    
                    	default :
                    	    if ( cnt11 >= 1 ) break loop11;
                                EarlyExitException eee =
                                    new EarlyExitException(11, input);
                                throw eee;
                        }
                        cnt11++;
                    } while (true);

                    
                    }
                    break;
            
            }
        }
        finally {
        }
    }
    // $ANTLR end NUMERIC_DIGITS

    // $ANTLR start DOUBLE_LITERAL
    public final void mDOUBLE_LITERAL() throws RecognitionException {
        try {
            int _type = DOUBLE_LITERAL;
            // JPQL.g:1250:9: ( NUMERIC_DIGITS ( DOUBLE_SUFFIX )? )
            // JPQL.g:1250:9: NUMERIC_DIGITS ( DOUBLE_SUFFIX )?
            {
            mNUMERIC_DIGITS(); 
            // JPQL.g:1250:24: ( DOUBLE_SUFFIX )?
            int alt13=2;
            int LA13_0 = input.LA(1);
            
            if ( (LA13_0=='d') ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // JPQL.g:1250:24: DOUBLE_SUFFIX
                    {
                    mDOUBLE_SUFFIX(); 
                    
                    }
                    break;
            
            }

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOUBLE_LITERAL

    // $ANTLR start FLOAT_LITERAL
    public final void mFLOAT_LITERAL() throws RecognitionException {
        try {
            int _type = FLOAT_LITERAL;
            // JPQL.g:1254:9: ( NUMERIC_DIGITS EXPONENT ( FLOAT_SUFFIX )? | NUMERIC_DIGITS FLOAT_SUFFIX )
            int alt15=2;
            alt15 = dfa15.predict(input);
            switch (alt15) {
                case 1 :
                    // JPQL.g:1254:9: NUMERIC_DIGITS EXPONENT ( FLOAT_SUFFIX )?
                    {
                    mNUMERIC_DIGITS(); 
                    mEXPONENT(); 
                    // JPQL.g:1254:33: ( FLOAT_SUFFIX )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);
                    
                    if ( (LA14_0=='f') ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // JPQL.g:1254:33: FLOAT_SUFFIX
                            {
                            mFLOAT_SUFFIX(); 
                            
                            }
                            break;
                    
                    }

                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1255:9: NUMERIC_DIGITS FLOAT_SUFFIX
                    {
                    mNUMERIC_DIGITS(); 
                    mFLOAT_SUFFIX(); 
                    
                    }
                    break;
            
            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FLOAT_LITERAL

    // $ANTLR start EXPONENT
    public final void mEXPONENT() throws RecognitionException {
        try {
            // JPQL.g:1261:9: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // JPQL.g:1261:9: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // JPQL.g:1261:21: ( '+' | '-' )?
            int alt16=2;
            int LA16_0 = input.LA(1);
            
            if ( (LA16_0=='+'||LA16_0=='-') ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // JPQL.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                    
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recover(mse);    throw mse;
                    }

                    
                    }
                    break;
            
            }

            // JPQL.g:1261:32: ( '0' .. '9' )+
            int cnt17=0;
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);
                
                if ( ((LA17_0>='0' && LA17_0<='9')) ) {
                    alt17=1;
                }
                
            
                switch (alt17) {
            	case 1 :
            	    // JPQL.g:1261:33: '0' .. '9'
            	    {
            	    matchRange('0','9'); 
            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt17 >= 1 ) break loop17;
                        EarlyExitException eee =
                            new EarlyExitException(17, input);
                        throw eee;
                }
                cnt17++;
            } while (true);

            
            }

        }
        finally {
        }
    }
    // $ANTLR end EXPONENT

    // $ANTLR start FLOAT_SUFFIX
    public final void mFLOAT_SUFFIX() throws RecognitionException {
        try {
            // JPQL.g:1267:9: ( 'f' )
            // JPQL.g:1267:9: 'f'
            {
            match('f'); 
            
            }

        }
        finally {
        }
    }
    // $ANTLR end FLOAT_SUFFIX

    // $ANTLR start DOUBLE_SUFFIX
    public final void mDOUBLE_SUFFIX() throws RecognitionException {
        try {
            // JPQL.g:1272:7: ( 'd' )
            // JPQL.g:1272:7: 'd'
            {
            match('d'); 
            
            }

        }
        finally {
        }
    }
    // $ANTLR end DOUBLE_SUFFIX

    // $ANTLR start EQUALS
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            // JPQL.g:1276:7: ( '=' )
            // JPQL.g:1276:7: '='
            {
            match('='); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EQUALS

    // $ANTLR start GREATER_THAN
    public final void mGREATER_THAN() throws RecognitionException {
        try {
            int _type = GREATER_THAN;
            // JPQL.g:1280:7: ( '>' )
            // JPQL.g:1280:7: '>'
            {
            match('>'); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GREATER_THAN

    // $ANTLR start GREATER_THAN_EQUAL_TO
    public final void mGREATER_THAN_EQUAL_TO() throws RecognitionException {
        try {
            int _type = GREATER_THAN_EQUAL_TO;
            // JPQL.g:1284:7: ( '>=' )
            // JPQL.g:1284:7: '>='
            {
            match(">="); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end GREATER_THAN_EQUAL_TO

    // $ANTLR start LESS_THAN
    public final void mLESS_THAN() throws RecognitionException {
        try {
            int _type = LESS_THAN;
            // JPQL.g:1288:7: ( '<' )
            // JPQL.g:1288:7: '<'
            {
            match('<'); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LESS_THAN

    // $ANTLR start LESS_THAN_EQUAL_TO
    public final void mLESS_THAN_EQUAL_TO() throws RecognitionException {
        try {
            int _type = LESS_THAN_EQUAL_TO;
            // JPQL.g:1292:7: ( '<=' )
            // JPQL.g:1292:7: '<='
            {
            match("<="); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LESS_THAN_EQUAL_TO

    // $ANTLR start NOT_EQUAL_TO
    public final void mNOT_EQUAL_TO() throws RecognitionException {
        try {
            int _type = NOT_EQUAL_TO;
            // JPQL.g:1296:7: ( '<>' )
            // JPQL.g:1296:7: '<>'
            {
            match("<>"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NOT_EQUAL_TO

    // $ANTLR start MULTIPLY
    public final void mMULTIPLY() throws RecognitionException {
        try {
            int _type = MULTIPLY;
            // JPQL.g:1300:7: ( '*' )
            // JPQL.g:1300:7: '*'
            {
            match('*'); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MULTIPLY

    // $ANTLR start DIVIDE
    public final void mDIVIDE() throws RecognitionException {
        try {
            int _type = DIVIDE;
            // JPQL.g:1304:7: ( '/' )
            // JPQL.g:1304:7: '/'
            {
            match('/'); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DIVIDE

    // $ANTLR start PLUS
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            // JPQL.g:1308:7: ( '+' )
            // JPQL.g:1308:7: '+'
            {
            match('+'); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end PLUS

    // $ANTLR start MINUS
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            // JPQL.g:1312:7: ( '-' )
            // JPQL.g:1312:7: '-'
            {
            match('-'); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end MINUS

    // $ANTLR start POSITIONAL_PARAM
    public final void mPOSITIONAL_PARAM() throws RecognitionException {
        try {
            int _type = POSITIONAL_PARAM;
            // JPQL.g:1317:7: ( '?' ( '1' .. '9' ) ( '0' .. '9' )* )
            // JPQL.g:1317:7: '?' ( '1' .. '9' ) ( '0' .. '9' )*
            {
            match('?'); 
            // JPQL.g:1317:11: ( '1' .. '9' )
            // JPQL.g:1317:12: '1' .. '9'
            {
            matchRange('1','9'); 
            
            }

            // JPQL.g:1317:22: ( '0' .. '9' )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);
                
                if ( ((LA18_0>='0' && LA18_0<='9')) ) {
                    alt18=1;
                }
                
            
                switch (alt18) {
            	case 1 :
            	    // JPQL.g:1317:23: '0' .. '9'
            	    {
            	    matchRange('0','9'); 
            	    
            	    }
            	    break;
            
            	default :
            	    break loop18;
                }
            } while (true);

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end POSITIONAL_PARAM

    // $ANTLR start NAMED_PARAM
    public final void mNAMED_PARAM() throws RecognitionException {
        try {
            int _type = NAMED_PARAM;
            // JPQL.g:1321:7: ( ':' TEXTCHAR )
            // JPQL.g:1321:7: ':' TEXTCHAR
            {
            match(':'); 
            mTEXTCHAR(); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NAMED_PARAM

    // $ANTLR start STRING_LITERAL_DOUBLE_QUOTED
    public final void mSTRING_LITERAL_DOUBLE_QUOTED() throws RecognitionException {
        try {
            int _type = STRING_LITERAL_DOUBLE_QUOTED;
            // JPQL.g:1327:7: ( '\"' (~ ( '\"' ) )* '\"' )
            // JPQL.g:1327:7: '\"' (~ ( '\"' ) )* '\"'
            {
            match('\"'); 
            // JPQL.g:1327:11: (~ ( '\"' ) )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);
                
                if ( ((LA19_0>='\u0000' && LA19_0<='!')||(LA19_0>='#' && LA19_0<='\uFFFE')) ) {
                    alt19=1;
                }
                
            
                switch (alt19) {
            	case 1 :
            	    // JPQL.g:1327:12: ~ ( '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();
            	    
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }

            	    
            	    }
            	    break;
            
            	default :
            	    break loop19;
                }
            } while (true);

            match('\"'); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STRING_LITERAL_DOUBLE_QUOTED

    // $ANTLR start STRING_LITERAL_SINGLE_QUOTED
    public final void mSTRING_LITERAL_SINGLE_QUOTED() throws RecognitionException {
        try {
            int _type = STRING_LITERAL_SINGLE_QUOTED;
            // JPQL.g:1331:7: ( '\\'' (~ ( '\\'' ) | ( '\\'\\'' ) )* '\\'' )
            // JPQL.g:1331:7: '\\'' (~ ( '\\'' ) | ( '\\'\\'' ) )* '\\''
            {
            match('\''); 
            // JPQL.g:1331:12: (~ ( '\\'' ) | ( '\\'\\'' ) )*
            loop20:
            do {
                int alt20=3;
                int LA20_0 = input.LA(1);
                
                if ( (LA20_0=='\'') ) {
                    int LA20_1 = input.LA(2);
                    
                    if ( (LA20_1=='\'') ) {
                        alt20=2;
                    }
                    
                
                }
                else if ( ((LA20_0>='\u0000' && LA20_0<='&')||(LA20_0>='(' && LA20_0<='\uFFFE')) ) {
                    alt20=1;
                }
                
            
                switch (alt20) {
            	case 1 :
            	    // JPQL.g:1331:13: ~ ( '\\'' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='\uFFFE') ) {
            	        input.consume();
            	    
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }

            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:1331:24: ( '\\'\\'' )
            	    {
            	    // JPQL.g:1331:24: ( '\\'\\'' )
            	    // JPQL.g:1331:25: '\\'\\''
            	    {
            	    match("\'\'"); 

            	    
            	    }

            	    
            	    }
            	    break;
            
            	default :
            	    break loop20;
                }
            } while (true);

            match('\''); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end STRING_LITERAL_SINGLE_QUOTED

    public void mTokens() throws RecognitionException {
        // JPQL.g:1:10: ( ABS | ALL | AND | ANY | AS | ASC | AVG | BETWEEN | BOTH | BY | CONCAT | COUNT | CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | DESC | DELETE | DISTINCT | EMPTY | ENTRY | ESCAPE | EXISTS | FALSE | FETCH | FROM | GROUP | HAVING | IN | INNER | IS | JOIN | KEY | LEADING | LEFT | LENGTH | LIKE | LOCATE | LOWER | MAX | MEMBER | MIN | MOD | NEW | NOT | NULL | OBJECT | OF | OR | ORDER | OUTER | SELECT | SET | SIZE | SQRT | SOME | SUBSTRING | SUM | TRAILING | TRIM | TRUE | UNKNOWN | UPDATE | UPPER | VALUE | WHERE | DOT | WS | LEFT_ROUND_BRACKET | RIGHT_ROUND_BRACKET | COMMA | IDENT | HEX_LITERAL | INTEGER_LITERAL | LONG_LITERAL | OCTAL_LITERAL | DOUBLE_LITERAL | FLOAT_LITERAL | EQUALS | GREATER_THAN | GREATER_THAN_EQUAL_TO | LESS_THAN | LESS_THAN_EQUAL_TO | NOT_EQUAL_TO | MULTIPLY | DIVIDE | PLUS | MINUS | POSITIONAL_PARAM | NAMED_PARAM | STRING_LITERAL_DOUBLE_QUOTED | STRING_LITERAL_SINGLE_QUOTED )
        int alt21=91;
        alt21 = dfa21.predict(input);
        switch (alt21) {
            case 1 :
                // JPQL.g:1:10: ABS
                {
                mABS(); 
                
                }
                break;
            case 2 :
                // JPQL.g:1:14: ALL
                {
                mALL(); 
                
                }
                break;
            case 3 :
                // JPQL.g:1:18: AND
                {
                mAND(); 
                
                }
                break;
            case 4 :
                // JPQL.g:1:22: ANY
                {
                mANY(); 
                
                }
                break;
            case 5 :
                // JPQL.g:1:26: AS
                {
                mAS(); 
                
                }
                break;
            case 6 :
                // JPQL.g:1:29: ASC
                {
                mASC(); 
                
                }
                break;
            case 7 :
                // JPQL.g:1:33: AVG
                {
                mAVG(); 
                
                }
                break;
            case 8 :
                // JPQL.g:1:37: BETWEEN
                {
                mBETWEEN(); 
                
                }
                break;
            case 9 :
                // JPQL.g:1:45: BOTH
                {
                mBOTH(); 
                
                }
                break;
            case 10 :
                // JPQL.g:1:50: BY
                {
                mBY(); 
                
                }
                break;
            case 11 :
                // JPQL.g:1:53: CONCAT
                {
                mCONCAT(); 
                
                }
                break;
            case 12 :
                // JPQL.g:1:60: COUNT
                {
                mCOUNT(); 
                
                }
                break;
            case 13 :
                // JPQL.g:1:66: CURRENT_DATE
                {
                mCURRENT_DATE(); 
                
                }
                break;
            case 14 :
                // JPQL.g:1:79: CURRENT_TIME
                {
                mCURRENT_TIME(); 
                
                }
                break;
            case 15 :
                // JPQL.g:1:92: CURRENT_TIMESTAMP
                {
                mCURRENT_TIMESTAMP(); 
                
                }
                break;
            case 16 :
                // JPQL.g:1:110: DESC
                {
                mDESC(); 
                
                }
                break;
            case 17 :
                // JPQL.g:1:115: DELETE
                {
                mDELETE(); 
                
                }
                break;
            case 18 :
                // JPQL.g:1:122: DISTINCT
                {
                mDISTINCT(); 
                
                }
                break;
            case 19 :
                // JPQL.g:1:131: EMPTY
                {
                mEMPTY(); 
                
                }
                break;
            case 20 :
                // JPQL.g:1:137: ENTRY
                {
                mENTRY(); 
                
                }
                break;
            case 21 :
                // JPQL.g:1:143: ESCAPE
                {
                mESCAPE(); 
                
                }
                break;
            case 22 :
                // JPQL.g:1:150: EXISTS
                {
                mEXISTS(); 
                
                }
                break;
            case 23 :
                // JPQL.g:1:157: FALSE
                {
                mFALSE(); 
                
                }
                break;
            case 24 :
                // JPQL.g:1:163: FETCH
                {
                mFETCH(); 
                
                }
                break;
            case 25 :
                // JPQL.g:1:169: FROM
                {
                mFROM(); 
                
                }
                break;
            case 26 :
                // JPQL.g:1:174: GROUP
                {
                mGROUP(); 
                
                }
                break;
            case 27 :
                // JPQL.g:1:180: HAVING
                {
                mHAVING(); 
                
                }
                break;
            case 28 :
                // JPQL.g:1:187: IN
                {
                mIN(); 
                
                }
                break;
            case 29 :
                // JPQL.g:1:190: INNER
                {
                mINNER(); 
                
                }
                break;
            case 30 :
                // JPQL.g:1:196: IS
                {
                mIS(); 
                
                }
                break;
            case 31 :
                // JPQL.g:1:199: JOIN
                {
                mJOIN(); 
                
                }
                break;
            case 32 :
                // JPQL.g:1:204: KEY
                {
                mKEY(); 
                
                }
                break;
            case 33 :
                // JPQL.g:1:208: LEADING
                {
                mLEADING(); 
                
                }
                break;
            case 34 :
                // JPQL.g:1:216: LEFT
                {
                mLEFT(); 
                
                }
                break;
            case 35 :
                // JPQL.g:1:221: LENGTH
                {
                mLENGTH(); 
                
                }
                break;
            case 36 :
                // JPQL.g:1:228: LIKE
                {
                mLIKE(); 
                
                }
                break;
            case 37 :
                // JPQL.g:1:233: LOCATE
                {
                mLOCATE(); 
                
                }
                break;
            case 38 :
                // JPQL.g:1:240: LOWER
                {
                mLOWER(); 
                
                }
                break;
            case 39 :
                // JPQL.g:1:246: MAX
                {
                mMAX(); 
                
                }
                break;
            case 40 :
                // JPQL.g:1:250: MEMBER
                {
                mMEMBER(); 
                
                }
                break;
            case 41 :
                // JPQL.g:1:257: MIN
                {
                mMIN(); 
                
                }
                break;
            case 42 :
                // JPQL.g:1:261: MOD
                {
                mMOD(); 
                
                }
                break;
            case 43 :
                // JPQL.g:1:265: NEW
                {
                mNEW(); 
                
                }
                break;
            case 44 :
                // JPQL.g:1:269: NOT
                {
                mNOT(); 
                
                }
                break;
            case 45 :
                // JPQL.g:1:273: NULL
                {
                mNULL(); 
                
                }
                break;
            case 46 :
                // JPQL.g:1:278: OBJECT
                {
                mOBJECT(); 
                
                }
                break;
            case 47 :
                // JPQL.g:1:285: OF
                {
                mOF(); 
                
                }
                break;
            case 48 :
                // JPQL.g:1:288: OR
                {
                mOR(); 
                
                }
                break;
            case 49 :
                // JPQL.g:1:291: ORDER
                {
                mORDER(); 
                
                }
                break;
            case 50 :
                // JPQL.g:1:297: OUTER
                {
                mOUTER(); 
                
                }
                break;
            case 51 :
                // JPQL.g:1:303: SELECT
                {
                mSELECT(); 
                
                }
                break;
            case 52 :
                // JPQL.g:1:310: SET
                {
                mSET(); 
                
                }
                break;
            case 53 :
                // JPQL.g:1:314: SIZE
                {
                mSIZE(); 
                
                }
                break;
            case 54 :
                // JPQL.g:1:319: SQRT
                {
                mSQRT(); 
                
                }
                break;
            case 55 :
                // JPQL.g:1:324: SOME
                {
                mSOME(); 
                
                }
                break;
            case 56 :
                // JPQL.g:1:329: SUBSTRING
                {
                mSUBSTRING(); 
                
                }
                break;
            case 57 :
                // JPQL.g:1:339: SUM
                {
                mSUM(); 
                
                }
                break;
            case 58 :
                // JPQL.g:1:343: TRAILING
                {
                mTRAILING(); 
                
                }
                break;
            case 59 :
                // JPQL.g:1:352: TRIM
                {
                mTRIM(); 
                
                }
                break;
            case 60 :
                // JPQL.g:1:357: TRUE
                {
                mTRUE(); 
                
                }
                break;
            case 61 :
                // JPQL.g:1:362: UNKNOWN
                {
                mUNKNOWN(); 
                
                }
                break;
            case 62 :
                // JPQL.g:1:370: UPDATE
                {
                mUPDATE(); 
                
                }
                break;
            case 63 :
                // JPQL.g:1:377: UPPER
                {
                mUPPER(); 
                
                }
                break;
            case 64 :
                // JPQL.g:1:383: VALUE
                {
                mVALUE(); 
                
                }
                break;
            case 65 :
                // JPQL.g:1:389: WHERE
                {
                mWHERE(); 
                
                }
                break;
            case 66 :
                // JPQL.g:1:395: DOT
                {
                mDOT(); 
                
                }
                break;
            case 67 :
                // JPQL.g:1:399: WS
                {
                mWS(); 
                
                }
                break;
            case 68 :
                // JPQL.g:1:402: LEFT_ROUND_BRACKET
                {
                mLEFT_ROUND_BRACKET(); 
                
                }
                break;
            case 69 :
                // JPQL.g:1:421: RIGHT_ROUND_BRACKET
                {
                mRIGHT_ROUND_BRACKET(); 
                
                }
                break;
            case 70 :
                // JPQL.g:1:441: COMMA
                {
                mCOMMA(); 
                
                }
                break;
            case 71 :
                // JPQL.g:1:447: IDENT
                {
                mIDENT(); 
                
                }
                break;
            case 72 :
                // JPQL.g:1:453: HEX_LITERAL
                {
                mHEX_LITERAL(); 
                
                }
                break;
            case 73 :
                // JPQL.g:1:465: INTEGER_LITERAL
                {
                mINTEGER_LITERAL(); 
                
                }
                break;
            case 74 :
                // JPQL.g:1:481: LONG_LITERAL
                {
                mLONG_LITERAL(); 
                
                }
                break;
            case 75 :
                // JPQL.g:1:494: OCTAL_LITERAL
                {
                mOCTAL_LITERAL(); 
                
                }
                break;
            case 76 :
                // JPQL.g:1:508: DOUBLE_LITERAL
                {
                mDOUBLE_LITERAL(); 
                
                }
                break;
            case 77 :
                // JPQL.g:1:523: FLOAT_LITERAL
                {
                mFLOAT_LITERAL(); 
                
                }
                break;
            case 78 :
                // JPQL.g:1:537: EQUALS
                {
                mEQUALS(); 
                
                }
                break;
            case 79 :
                // JPQL.g:1:544: GREATER_THAN
                {
                mGREATER_THAN(); 
                
                }
                break;
            case 80 :
                // JPQL.g:1:557: GREATER_THAN_EQUAL_TO
                {
                mGREATER_THAN_EQUAL_TO(); 
                
                }
                break;
            case 81 :
                // JPQL.g:1:579: LESS_THAN
                {
                mLESS_THAN(); 
                
                }
                break;
            case 82 :
                // JPQL.g:1:589: LESS_THAN_EQUAL_TO
                {
                mLESS_THAN_EQUAL_TO(); 
                
                }
                break;
            case 83 :
                // JPQL.g:1:608: NOT_EQUAL_TO
                {
                mNOT_EQUAL_TO(); 
                
                }
                break;
            case 84 :
                // JPQL.g:1:621: MULTIPLY
                {
                mMULTIPLY(); 
                
                }
                break;
            case 85 :
                // JPQL.g:1:630: DIVIDE
                {
                mDIVIDE(); 
                
                }
                break;
            case 86 :
                // JPQL.g:1:637: PLUS
                {
                mPLUS(); 
                
                }
                break;
            case 87 :
                // JPQL.g:1:642: MINUS
                {
                mMINUS(); 
                
                }
                break;
            case 88 :
                // JPQL.g:1:648: POSITIONAL_PARAM
                {
                mPOSITIONAL_PARAM(); 
                
                }
                break;
            case 89 :
                // JPQL.g:1:665: NAMED_PARAM
                {
                mNAMED_PARAM(); 
                
                }
                break;
            case 90 :
                // JPQL.g:1:677: STRING_LITERAL_DOUBLE_QUOTED
                {
                mSTRING_LITERAL_DOUBLE_QUOTED(); 
                
                }
                break;
            case 91 :
                // JPQL.g:1:706: STRING_LITERAL_SINGLE_QUOTED
                {
                mSTRING_LITERAL_SINGLE_QUOTED(); 
                
                }
                break;
        
        }
    
    }


    protected DFA12 dfa12 = new DFA12(this);
    protected DFA15 dfa15 = new DFA15(this);
    protected DFA21 dfa21 = new DFA21(this);
    static final String DFA12_eotS =
        "\1\uffff\1\3\3\uffff";
    static final String DFA12_eofS =
        "\5\uffff";
    static final String DFA12_minS =
        "\2\56\3\uffff";
    static final String DFA12_maxS =
        "\2\71\3\uffff";
    static final String DFA12_acceptS =
        "\2\uffff\1\2\1\3\1\1";
    static final String DFA12_specialS =
        "\5\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\4\1\uffff\12\1",
            "",
            "",
            ""
    };
    
    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;
    
    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }
    
    class DFA12 extends DFA {
    
        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        public String getDescription() {
            return "1242:1: fragment NUMERIC_DIGITS : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ );";
        }
    }
    static final String DFA15_eotS =
        "\10\uffff";
    static final String DFA15_eofS =
        "\10\uffff";
    static final String DFA15_minS =
        "\2\56\1\60\1\uffff\1\60\1\uffff\2\60";
    static final String DFA15_maxS =
        "\1\71\1\146\1\71\1\uffff\1\146\1\uffff\2\146";
    static final String DFA15_acceptS =
        "\3\uffff\1\2\1\uffff\1\1\2\uffff";
    static final String DFA15_specialS =
        "\10\uffff}>";
    static final String[] DFA15_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\4\1\uffff\12\1\13\uffff\1\5\37\uffff\1\5\1\3",
            "\12\6",
            "",
            "\12\7\13\uffff\1\5\37\uffff\1\5\1\3",
            "",
            "\12\6\13\uffff\1\5\37\uffff\1\5\1\3",
            "\12\7\13\uffff\1\5\37\uffff\1\5\1\3"
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
            return "1253:1: FLOAT_LITERAL : ( NUMERIC_DIGITS EXPONENT ( FLOAT_SUFFIX )? | NUMERIC_DIGITS FLOAT_SUFFIX );";
        }
    }
    static final String DFA21_eotS =
        "\1\uffff\24\32\1\131\5\uffff\2\134\1\uffff\1\145\1\150\10\uffff"+
        "\3\32\1\156\3\32\1\162\15\32\1\u0082\1\u0084\14\32\1\u0095\2\32"+
        "\1\u0098\12\32\1\uffff\1\135\3\uffff\1\u00a8\1\135\2\uffff\1\135"+
        "\1\134\5\uffff\1\u00aa\1\u00ab\1\u00ac\1\u00ad\1\u00ae\1\uffff\1"+
        "\u00af\2\32\1\uffff\17\32\1\uffff\1\32\1\uffff\1\32\1\u00c3\6\32"+
        "\1\u00ca\1\32\1\u00cc\1\u00cd\1\u00ce\1\u00cf\2\32\1\uffff\2\32"+
        "\1\uffff\1\32\1\u00d5\2\32\1\u00d8\12\32\1\uffff\1\135\6\uffff\1"+
        "\32\1\u00e4\3\32\1\u00e8\6\32\1\u00ef\5\32\1\u00f5\1\uffff\3\32"+
        "\1\u00f9\1\32\1\u00fb\1\uffff\1\32\4\uffff\1\u00fd\4\32\1\uffff"+
        "\1\u0102\1\32\1\uffff\1\u0104\1\u0105\1\u0106\1\u0107\7\32\1\uffff"+
        "\2\32\1\u0111\1\uffff\2\32\1\u0114\1\u0115\2\32\1\uffff\1\u0118"+
        "\1\u0119\1\u011a\1\32\1\u011c\1\uffff\1\u011d\2\32\1\uffff\1\32"+
        "\1\uffff\1\32\1\uffff\1\u0122\1\u0123\2\32\1\uffff\1\32\4\uffff"+
        "\2\32\1\u0129\1\32\1\u012b\1\u012c\2\32\1\u012f\1\uffff\1\u0130"+
        "\1\32\2\uffff\1\u0132\1\u0133\3\uffff\1\u0134\2\uffff\1\u0135\1"+
        "\32\1\u0137\1\u0138\2\uffff\1\u0139\1\32\1\u013b\1\32\1\u013d\1"+
        "\uffff\1\32\2\uffff\1\u013f\1\32\2\uffff\1\32\4\uffff\1\u0142\3"+
        "\uffff\1\32\1\uffff\1\32\1\uffff\1\u0145\1\uffff\1\32\1\u0148\1"+
        "\uffff\1\32\1\u014a\1\uffff\2\32\1\uffff\1\u014d\1\uffff\2\32\1"+
        "\uffff\2\32\1\u0153\1\u0154\1\32\2\uffff\3\32\1\u0159\1\uffff";
    static final String DFA21_eofS =
        "\u015a\uffff";
    static final String DFA21_minS =
        "\1\11\1\142\1\145\1\157\1\145\1\155\1\141\1\162\1\141\1\156\1\157"+
        "\2\145\1\141\1\145\1\142\1\145\1\162\1\156\1\141\1\150\1\60\5\uffff"+
        "\2\56\1\uffff\2\75\10\uffff\1\163\1\144\1\154\1\44\1\147\2\164\1"+
        "\44\1\162\1\156\1\154\1\163\1\164\1\160\1\151\1\143\1\157\1\164"+
        "\1\154\1\157\1\166\2\44\1\151\1\171\1\143\1\141\1\153\1\170\1\155"+
        "\1\156\1\144\1\167\1\164\1\154\1\44\1\164\1\152\1\44\1\142\1\172"+
        "\1\154\1\155\1\162\1\141\1\144\1\153\1\154\1\145\1\uffff\1\60\3"+
        "\uffff\1\56\1\60\2\uffff\2\56\5\uffff\5\44\1\uffff\1\44\1\167\1"+
        "\150\1\uffff\1\162\1\143\1\156\1\143\1\145\1\164\1\162\1\164\1\163"+
        "\1\141\1\155\1\143\1\163\1\165\1\151\1\uffff\1\145\1\uffff\1\156"+
        "\1\44\1\145\1\141\1\144\1\164\1\147\1\145\1\44\1\142\4\44\1\154"+
        "\1\145\1\uffff\2\145\1\uffff\1\163\1\44\2\145\1\44\1\145\1\164\1"+
        "\155\1\145\1\151\1\141\1\145\1\156\1\165\1\162\1\uffff\1\60\6\uffff"+
        "\1\145\1\44\1\145\1\141\1\164\1\44\1\164\1\151\2\171\1\164\1\160"+
        "\1\44\1\150\1\145\1\160\1\156\1\162\1\44\1\uffff\1\162\1\164\1\151"+
        "\1\44\1\164\1\44\1\uffff\1\145\4\uffff\1\44\2\162\1\143\1\164\1"+
        "\uffff\1\44\1\143\1\uffff\4\44\1\154\1\164\1\162\1\157\3\145\1\uffff"+
        "\1\156\1\164\1\44\1\uffff\1\145\1\156\2\44\1\163\1\145\1\uffff\3"+
        "\44\1\147\1\44\1\uffff\1\44\1\145\1\156\1\uffff\1\150\1\uffff\1"+
        "\162\1\uffff\2\44\1\164\1\162\1\uffff\1\164\4\uffff\1\151\1\145"+
        "\1\44\1\167\2\44\1\156\1\164\1\44\1\uffff\1\44\1\143\2\uffff\2\44"+
        "\3\uffff\1\44\2\uffff\1\44\1\147\2\44\2\uffff\1\44\1\151\1\44\1"+
        "\156\1\44\1\uffff\1\156\2\uffff\1\44\1\137\2\uffff\1\164\4\uffff"+
        "\1\44\3\uffff\1\156\1\uffff\1\147\1\uffff\1\44\1\uffff\1\144\1\44"+
        "\1\uffff\1\147\1\44\1\uffff\1\151\1\141\1\uffff\1\44\1\uffff\1\155"+
        "\1\164\1\uffff\2\145\2\44\1\164\2\uffff\1\141\1\155\1\160\1\44\1"+
        "\uffff";
    static final String DFA21_maxS =
        "\1\ufffe\1\166\1\171\1\165\1\151\1\170\2\162\1\141\1\163\1\157\1"+
        "\145\2\157\3\165\1\162\1\160\1\141\1\150\1\71\5\uffff\1\170\1\154"+
        "\1\uffff\1\75\1\76\10\uffff\1\163\1\171\1\154\1\ufffe\1\147\2\164"+
        "\1\ufffe\1\162\1\165\2\163\1\164\1\160\1\151\1\143\1\157\1\164\1"+
        "\154\1\157\1\166\2\ufffe\1\151\1\171\1\167\1\156\1\153\1\170\1\155"+
        "\1\156\1\144\1\167\1\164\1\154\1\ufffe\1\164\1\152\1\ufffe\1\155"+
        "\1\172\1\164\1\155\1\162\1\165\1\160\1\153\1\154\1\145\1\uffff\1"+
        "\146\3\uffff\2\146\2\uffff\1\146\1\154\5\uffff\5\ufffe\1\uffff\1"+
        "\ufffe\1\167\1\150\1\uffff\1\162\1\143\1\156\1\143\1\145\1\164\1"+
        "\162\1\164\1\163\1\141\1\155\1\143\1\163\1\165\1\151\1\uffff\1\145"+
        "\1\uffff\1\156\1\ufffe\1\145\1\141\1\144\1\164\1\147\1\145\1\ufffe"+
        "\1\142\4\ufffe\1\154\1\145\1\uffff\2\145\1\uffff\1\163\1\ufffe\2"+
        "\145\1\ufffe\1\145\1\164\1\155\1\145\1\151\1\141\1\145\1\156\1\165"+
        "\1\162\1\uffff\1\146\6\uffff\1\145\1\ufffe\1\145\1\141\1\164\1\ufffe"+
        "\1\164\1\151\2\171\1\164\1\160\1\ufffe\1\150\1\145\1\160\1\156\1"+
        "\162\1\ufffe\1\uffff\1\162\1\164\1\151\1\ufffe\1\164\1\ufffe\1\uffff"+
        "\1\145\4\uffff\1\ufffe\2\162\1\143\1\164\1\uffff\1\ufffe\1\143\1"+
        "\uffff\4\ufffe\1\154\1\164\1\162\1\157\3\145\1\uffff\1\156\1\164"+
        "\1\ufffe\1\uffff\1\145\1\156\2\ufffe\1\163\1\145\1\uffff\3\ufffe"+
        "\1\147\1\ufffe\1\uffff\1\ufffe\1\145\1\156\1\uffff\1\150\1\uffff"+
        "\1\162\1\uffff\2\ufffe\1\164\1\162\1\uffff\1\164\4\uffff\1\151\1"+
        "\145\1\ufffe\1\167\2\ufffe\1\156\1\164\1\ufffe\1\uffff\1\ufffe\1"+
        "\143\2\uffff\2\ufffe\3\uffff\1\ufffe\2\uffff\1\ufffe\1\147\2\ufffe"+
        "\2\uffff\1\ufffe\1\151\1\ufffe\1\156\1\ufffe\1\uffff\1\156\2\uffff"+
        "\1\ufffe\1\137\2\uffff\1\164\4\uffff\1\ufffe\3\uffff\1\156\1\uffff"+
        "\1\147\1\uffff\1\ufffe\1\uffff\1\164\1\ufffe\1\uffff\1\147\1\ufffe"+
        "\1\uffff\1\151\1\141\1\uffff\1\ufffe\1\uffff\1\155\1\164\1\uffff"+
        "\2\145\2\ufffe\1\164\2\uffff\1\141\1\155\1\160\1\ufffe\1\uffff";
    static final String DFA21_acceptS =
        "\26\uffff\1\103\1\104\1\105\1\106\1\107\2\uffff\1\116\2\uffff\1"+
        "\124\1\125\1\126\1\127\1\130\1\131\1\132\1\133\61\uffff\1\102\1"+
        "\uffff\1\110\1\111\1\114\2\uffff\1\112\1\115\2\uffff\1\120\1\117"+
        "\1\123\1\122\1\121\5\uffff\1\5\3\uffff\1\12\17\uffff\1\36\1\uffff"+
        "\1\34\20\uffff\1\60\2\uffff\1\57\17\uffff\1\113\1\uffff\1\1\1\3"+
        "\1\4\1\2\1\6\1\7\23\uffff\1\40\6\uffff\1\47\1\uffff\1\51\1\52\1"+
        "\53\1\54\5\uffff\1\71\2\uffff\1\64\13\uffff\1\11\3\uffff\1\20\6"+
        "\uffff\1\31\5\uffff\1\37\3\uffff\1\42\1\uffff\1\44\1\uffff\1\55"+
        "\4\uffff\1\65\1\uffff\1\67\1\66\1\73\1\74\11\uffff\1\14\2\uffff"+
        "\1\24\1\23\2\uffff\1\30\1\27\1\32\1\uffff\1\35\1\46\4\uffff\1\61"+
        "\1\62\5\uffff\1\77\1\uffff\1\100\1\101\2\uffff\1\13\1\21\1\uffff"+
        "\1\26\1\25\1\33\1\45\1\uffff\1\43\1\50\1\56\1\uffff\1\63\1\uffff"+
        "\1\76\1\uffff\1\10\2\uffff\1\41\2\uffff\1\75\2\uffff\1\22\1\uffff"+
        "\1\72\2\uffff\1\70\5\uffff\1\16\1\15\4\uffff\1\17";
    static final String DFA21_specialS =
        "\u015a\uffff}>";
    static final String[] DFA21_transitionS = {
            "\2\26\2\uffff\1\26\22\uffff\1\26\1\uffff\1\46\1\uffff\1\32\2"+
            "\uffff\1\47\1\27\1\30\1\40\1\42\1\31\1\43\1\25\1\41\1\33\11"+
            "\34\1\45\1\uffff\1\37\1\35\1\36\1\44\1\uffff\32\32\4\uffff\1"+
            "\32\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13"+
            "\1\14\1\15\1\16\1\17\3\32\1\20\1\21\1\22\1\23\1\24\3\32\5\uffff"+
            "\uff7f\32",
            "\1\50\11\uffff\1\52\1\uffff\1\51\4\uffff\1\53\2\uffff\1\54",
            "\1\55\11\uffff\1\56\11\uffff\1\57",
            "\1\61\5\uffff\1\60",
            "\1\62\3\uffff\1\63",
            "\1\65\1\64\4\uffff\1\67\4\uffff\1\66",
            "\1\72\3\uffff\1\71\14\uffff\1\70",
            "\1\73",
            "\1\74",
            "\1\76\4\uffff\1\75",
            "\1\77",
            "\1\100",
            "\1\102\3\uffff\1\103\5\uffff\1\101",
            "\1\104\3\uffff\1\105\3\uffff\1\106\5\uffff\1\107",
            "\1\110\11\uffff\1\111\5\uffff\1\112",
            "\1\115\3\uffff\1\116\13\uffff\1\113\2\uffff\1\114",
            "\1\121\3\uffff\1\120\5\uffff\1\122\1\uffff\1\123\3\uffff\1\117",
            "\1\124",
            "\1\126\1\uffff\1\125",
            "\1\127",
            "\1\130",
            "\12\132",
            "",
            "",
            "",
            "",
            "",
            "\1\137\1\uffff\10\136\2\142\13\uffff\1\141\6\uffff\1\140\13"+
            "\uffff\1\133\13\uffff\1\135\2\141\5\uffff\1\140\13\uffff\1\133",
            "\1\137\1\uffff\12\143\13\uffff\1\141\6\uffff\1\140\27\uffff"+
            "\1\135\2\141\5\uffff\1\140",
            "",
            "\1\144",
            "\1\147\1\146",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\151",
            "\1\152\24\uffff\1\153",
            "\1\154",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\2\32\1\155\27\32"+
            "\5\uffff\uff7f\32",
            "\1\157",
            "\1\160",
            "\1\161",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\163",
            "\1\164\6\uffff\1\165",
            "\1\167\6\uffff\1\166",
            "\1\170",
            "\1\171",
            "\1\172",
            "\1\173",
            "\1\174",
            "\1\175",
            "\1\176",
            "\1\177",
            "\1\u0080",
            "\1\u0081",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\15\32\1\u0083\14"+
            "\32\5\uffff\uff7f\32",
            "\1\u0085",
            "\1\u0086",
            "\1\u0088\23\uffff\1\u0087",
            "\1\u0089\4\uffff\1\u008a\7\uffff\1\u008b",
            "\1\u008c",
            "\1\u008d",
            "\1\u008e",
            "\1\u008f",
            "\1\u0090",
            "\1\u0091",
            "\1\u0092",
            "\1\u0093",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\3\32\1\u0094\26\32"+
            "\5\uffff\uff7f\32",
            "\1\u0096",
            "\1\u0097",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u0099\12\uffff\1\u009a",
            "\1\u009b",
            "\1\u009c\7\uffff\1\u009d",
            "\1\u009e",
            "\1\u009f",
            "\1\u00a2\7\uffff\1\u00a0\13\uffff\1\u00a1",
            "\1\u00a3\13\uffff\1\u00a4",
            "\1\u00a5",
            "\1\u00a6",
            "\1\u00a7",
            "",
            "\12\132\13\uffff\1\141\37\uffff\2\141",
            "",
            "",
            "",
            "\1\137\1\uffff\10\136\2\142\13\uffff\1\141\36\uffff\1\135\2"+
            "\141",
            "\12\u00a9\13\uffff\1\141\37\uffff\2\141",
            "",
            "",
            "\1\137\1\uffff\12\142\13\uffff\1\141\37\uffff\2\141",
            "\1\137\1\uffff\12\143\13\uffff\1\141\6\uffff\1\140\27\uffff"+
            "\1\135\2\141\5\uffff\1\140",
            "",
            "",
            "",
            "",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u00b0",
            "\1\u00b1",
            "",
            "\1\u00b2",
            "\1\u00b3",
            "\1\u00b4",
            "\1\u00b5",
            "\1\u00b6",
            "\1\u00b7",
            "\1\u00b8",
            "\1\u00b9",
            "\1\u00ba",
            "\1\u00bb",
            "\1\u00bc",
            "\1\u00bd",
            "\1\u00be",
            "\1\u00bf",
            "\1\u00c0",
            "",
            "\1\u00c1",
            "",
            "\1\u00c2",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u00c4",
            "\1\u00c5",
            "\1\u00c6",
            "\1\u00c7",
            "\1\u00c8",
            "\1\u00c9",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u00cb",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u00d0",
            "\1\u00d1",
            "",
            "\1\u00d2",
            "\1\u00d3",
            "",
            "\1\u00d4",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u00d6",
            "\1\u00d7",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u00d9",
            "\1\u00da",
            "\1\u00db",
            "\1\u00dc",
            "\1\u00dd",
            "\1\u00de",
            "\1\u00df",
            "\1\u00e0",
            "\1\u00e1",
            "\1\u00e2",
            "",
            "\12\u00a9\13\uffff\1\141\37\uffff\2\141",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u00e3",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u00e5",
            "\1\u00e6",
            "\1\u00e7",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u00e9",
            "\1\u00ea",
            "\1\u00eb",
            "\1\u00ec",
            "\1\u00ed",
            "\1\u00ee",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u00f0",
            "\1\u00f1",
            "\1\u00f2",
            "\1\u00f3",
            "\1\u00f4",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "",
            "\1\u00f6",
            "\1\u00f7",
            "\1\u00f8",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u00fa",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "",
            "\1\u00fc",
            "",
            "",
            "",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u00fe",
            "\1\u00ff",
            "\1\u0100",
            "\1\u0101",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u0103",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u0108",
            "\1\u0109",
            "\1\u010a",
            "\1\u010b",
            "\1\u010c",
            "\1\u010d",
            "\1\u010e",
            "",
            "\1\u010f",
            "\1\u0110",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "",
            "\1\u0112",
            "\1\u0113",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u0116",
            "\1\u0117",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u011b",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u011e",
            "\1\u011f",
            "",
            "\1\u0120",
            "",
            "\1\u0121",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u0124",
            "\1\u0125",
            "",
            "\1\u0126",
            "",
            "",
            "",
            "",
            "\1\u0127",
            "\1\u0128",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u012a",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u012d",
            "\1\u012e",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u0131",
            "",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "",
            "",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u0136",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u013a",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u013c",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "",
            "\1\u013e",
            "",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u0140",
            "",
            "",
            "\1\u0141",
            "",
            "",
            "",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "",
            "",
            "",
            "\1\u0143",
            "",
            "\1\u0144",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "",
            "\1\u0147\17\uffff\1\u0146",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "",
            "\1\u0149",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "",
            "\1\u014b",
            "\1\u014c",
            "",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "",
            "\1\u014e",
            "\1\u014f",
            "",
            "\1\u0150",
            "\1\u0151",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\22\32\1\u0152\7\32"+
            "\5\uffff\uff7f\32",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            "\1\u0155",
            "",
            "",
            "\1\u0156",
            "\1\u0157",
            "\1\u0158",
            "\1\32\13\uffff\12\32\45\uffff\1\32\1\uffff\32\32\5\uffff\uff7f"+
            "\32",
            ""
    };
    
    static final short[] DFA21_eot = DFA.unpackEncodedString(DFA21_eotS);
    static final short[] DFA21_eof = DFA.unpackEncodedString(DFA21_eofS);
    static final char[] DFA21_min = DFA.unpackEncodedStringToUnsignedChars(DFA21_minS);
    static final char[] DFA21_max = DFA.unpackEncodedStringToUnsignedChars(DFA21_maxS);
    static final short[] DFA21_accept = DFA.unpackEncodedString(DFA21_acceptS);
    static final short[] DFA21_special = DFA.unpackEncodedString(DFA21_specialS);
    static final short[][] DFA21_transition;
    
    static {
        int numStates = DFA21_transitionS.length;
        DFA21_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA21_transition[i] = DFA.unpackEncodedString(DFA21_transitionS[i]);
        }
    }
    
    class DFA21 extends DFA {
    
        public DFA21(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 21;
            this.eot = DFA21_eot;
            this.eof = DFA21_eof;
            this.min = DFA21_min;
            this.max = DFA21_max;
            this.accept = DFA21_accept;
            this.special = DFA21_special;
            this.transition = DFA21_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( ABS | ALL | AND | ANY | AS | ASC | AVG | BETWEEN | BOTH | BY | CONCAT | COUNT | CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | DESC | DELETE | DISTINCT | EMPTY | ENTRY | ESCAPE | EXISTS | FALSE | FETCH | FROM | GROUP | HAVING | IN | INNER | IS | JOIN | KEY | LEADING | LEFT | LENGTH | LIKE | LOCATE | LOWER | MAX | MEMBER | MIN | MOD | NEW | NOT | NULL | OBJECT | OF | OR | ORDER | OUTER | SELECT | SET | SIZE | SQRT | SOME | SUBSTRING | SUM | TRAILING | TRIM | TRUE | UNKNOWN | UPDATE | UPPER | VALUE | WHERE | DOT | WS | LEFT_ROUND_BRACKET | RIGHT_ROUND_BRACKET | COMMA | IDENT | HEX_LITERAL | INTEGER_LITERAL | LONG_LITERAL | OCTAL_LITERAL | DOUBLE_LITERAL | FLOAT_LITERAL | EQUALS | GREATER_THAN | GREATER_THAN_EQUAL_TO | LESS_THAN | LESS_THAN_EQUAL_TO | NOT_EQUAL_TO | MULTIPLY | DIVIDE | PLUS | MINUS | POSITIONAL_PARAM | NAMED_PARAM | STRING_LITERAL_DOUBLE_QUOTED | STRING_LITERAL_SINGLE_QUOTED );";
        }
    }
 

}