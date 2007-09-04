// $ANTLR 3.0 JPQL.g3 2007-08-30 14:47:14

    package org.eclipse.persistence.internal.jpa.parsing.jpql.antlr;

    import org.eclipse.persistence.internal.jpa.parsing.jpql.InvalidIdentifierStartException;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class JPQLLexer extends Lexer {
    public static final int COMMA=67;
    public static final int EXISTS=24;
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
    public static final int TEXTCHAR=90;
    public static final int CONCAT=14;
    public static final int SELECT=52;
    public static final int BETWEEN=11;
    public static final int DESC=19;
    public static final int LESS_THAN_EQUAL_TO=76;
    public static final int BOTH=12;
    public static final int PLUS=77;
    public static final int MEMBER=41;
    public static final int INTEGER_LITERAL=81;
    public static final int TRIM=60;
    public static final int MULTIPLY=79;
    public static final int NUMERIC_DIGITS=95;
    public static final int DISTINCT=21;
    public static final int LOCATE=38;
    public static final int IDENT=66;
    public static final int FLOAT_LITERAL=83;
    public static final int WS=89;
    public static final int DOUBLE_LITERAL=84;
    public static final int NEW=44;
    public static final int OF=48;
    public static final int RIGHT_ROUND_BRACKET=70;
    public static final int UPDATE=63;
    public static final int LOWER=39;
    public static final int POSITIONAL_PARAM=87;
    public static final int ANY=7;
    public static final int FLOAT_SUFFIX=98;
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
    public static final int GREATER_THAN_EQUAL_TO=74;
    public static final int INNER=31;
    public static final int MOD=43;
    public static final int OCTAL_LITERAL=94;
    public static final int HEX_LITERAL=92;
    public static final int OR=49;
    public static final int DIVIDE=80;
    public static final int BY=13;
    public static final int GROUP=28;
    public static final int ESCAPE=23;
    public static final int HEX_DIGIT=91;
    public static final int LEFT=35;
    public static final int DOUBLE_SUFFIX=96;
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
    public static final int INTEGER_SUFFIX=93;
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
    public static final int EXPONENT=97;
    public static final int UPPER=64;
    public static final int EOF=-1;
    public static final int Tokens=99;
    public static final int SIZE=54;
    public static final int AVG=10;
    public static final int NOT=45;
    public static final int LONG_LITERAL=82;
    public JPQLLexer() {;} 
    public JPQLLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "JPQL.g3"; }

    // $ANTLR start ABS
    public final void mABS() throws RecognitionException {
        try {
            int _type = ABS;
            // JPQL.g3:8:7: ( 'abs' )
            // JPQL.g3:8:7: 'abs'
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
            // JPQL.g3:9:7: ( 'all' )
            // JPQL.g3:9:7: 'all'
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
            // JPQL.g3:10:7: ( 'and' )
            // JPQL.g3:10:7: 'and'
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
            // JPQL.g3:11:7: ( 'any' )
            // JPQL.g3:11:7: 'any'
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
            // JPQL.g3:12:6: ( 'as' )
            // JPQL.g3:12:6: 'as'
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
            // JPQL.g3:13:7: ( 'asc' )
            // JPQL.g3:13:7: 'asc'
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
            // JPQL.g3:14:7: ( 'avg' )
            // JPQL.g3:14:7: 'avg'
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
            // JPQL.g3:15:11: ( 'between' )
            // JPQL.g3:15:11: 'between'
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
            // JPQL.g3:16:8: ( 'both' )
            // JPQL.g3:16:8: 'both'
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
            // JPQL.g3:17:6: ( 'by' )
            // JPQL.g3:17:6: 'by'
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
            // JPQL.g3:18:10: ( 'concat' )
            // JPQL.g3:18:10: 'concat'
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
            // JPQL.g3:19:9: ( 'count' )
            // JPQL.g3:19:9: 'count'
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
            // JPQL.g3:20:16: ( 'current_date' )
            // JPQL.g3:20:16: 'current_date'
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
            // JPQL.g3:21:16: ( 'current_time' )
            // JPQL.g3:21:16: 'current_time'
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
            // JPQL.g3:22:21: ( 'current_timestamp' )
            // JPQL.g3:22:21: 'current_timestamp'
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
            // JPQL.g3:23:8: ( 'desc' )
            // JPQL.g3:23:8: 'desc'
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
            // JPQL.g3:24:10: ( 'delete' )
            // JPQL.g3:24:10: 'delete'
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
            // JPQL.g3:25:12: ( 'distinct' )
            // JPQL.g3:25:12: 'distinct'
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
            // JPQL.g3:26:9: ( 'empty' )
            // JPQL.g3:26:9: 'empty'
            {
            match("empty"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EMPTY

    // $ANTLR start ESCAPE
    public final void mESCAPE() throws RecognitionException {
        try {
            int _type = ESCAPE;
            // JPQL.g3:27:10: ( 'escape' )
            // JPQL.g3:27:10: 'escape'
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
            // JPQL.g3:28:10: ( 'exists' )
            // JPQL.g3:28:10: 'exists'
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
            // JPQL.g3:29:9: ( 'false' )
            // JPQL.g3:29:9: 'false'
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
            // JPQL.g3:30:9: ( 'fetch' )
            // JPQL.g3:30:9: 'fetch'
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
            // JPQL.g3:31:8: ( 'from' )
            // JPQL.g3:31:8: 'from'
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
            // JPQL.g3:32:9: ( 'group' )
            // JPQL.g3:32:9: 'group'
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
            // JPQL.g3:33:10: ( 'having' )
            // JPQL.g3:33:10: 'having'
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
            // JPQL.g3:34:6: ( 'in' )
            // JPQL.g3:34:6: 'in'
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
            // JPQL.g3:35:9: ( 'inner' )
            // JPQL.g3:35:9: 'inner'
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
            // JPQL.g3:36:6: ( 'is' )
            // JPQL.g3:36:6: 'is'
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
            // JPQL.g3:37:8: ( 'join' )
            // JPQL.g3:37:8: 'join'
            {
            match("join"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end JOIN

    // $ANTLR start LEADING
    public final void mLEADING() throws RecognitionException {
        try {
            int _type = LEADING;
            // JPQL.g3:38:11: ( 'leading' )
            // JPQL.g3:38:11: 'leading'
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
            // JPQL.g3:39:8: ( 'left' )
            // JPQL.g3:39:8: 'left'
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
            // JPQL.g3:40:10: ( 'length' )
            // JPQL.g3:40:10: 'length'
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
            // JPQL.g3:41:8: ( 'like' )
            // JPQL.g3:41:8: 'like'
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
            // JPQL.g3:42:10: ( 'locate' )
            // JPQL.g3:42:10: 'locate'
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
            // JPQL.g3:43:9: ( 'lower' )
            // JPQL.g3:43:9: 'lower'
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
            // JPQL.g3:44:7: ( 'max' )
            // JPQL.g3:44:7: 'max'
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
            // JPQL.g3:45:10: ( 'member' )
            // JPQL.g3:45:10: 'member'
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
            // JPQL.g3:46:7: ( 'min' )
            // JPQL.g3:46:7: 'min'
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
            // JPQL.g3:47:7: ( 'mod' )
            // JPQL.g3:47:7: 'mod'
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
            // JPQL.g3:48:7: ( 'new' )
            // JPQL.g3:48:7: 'new'
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
            // JPQL.g3:49:7: ( 'not' )
            // JPQL.g3:49:7: 'not'
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
            // JPQL.g3:50:8: ( 'null' )
            // JPQL.g3:50:8: 'null'
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
            // JPQL.g3:51:10: ( 'object' )
            // JPQL.g3:51:10: 'object'
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
            // JPQL.g3:52:6: ( 'of' )
            // JPQL.g3:52:6: 'of'
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
            // JPQL.g3:53:6: ( 'or' )
            // JPQL.g3:53:6: 'or'
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
            // JPQL.g3:54:9: ( 'order' )
            // JPQL.g3:54:9: 'order'
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
            // JPQL.g3:55:9: ( 'outer' )
            // JPQL.g3:55:9: 'outer'
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
            // JPQL.g3:56:10: ( 'select' )
            // JPQL.g3:56:10: 'select'
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
            // JPQL.g3:57:7: ( 'set' )
            // JPQL.g3:57:7: 'set'
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
            // JPQL.g3:58:8: ( 'size' )
            // JPQL.g3:58:8: 'size'
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
            // JPQL.g3:59:8: ( 'sqrt' )
            // JPQL.g3:59:8: 'sqrt'
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
            // JPQL.g3:60:8: ( 'some' )
            // JPQL.g3:60:8: 'some'
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
            // JPQL.g3:61:13: ( 'substring' )
            // JPQL.g3:61:13: 'substring'
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
            // JPQL.g3:62:7: ( 'sum' )
            // JPQL.g3:62:7: 'sum'
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
            // JPQL.g3:63:12: ( 'trailing' )
            // JPQL.g3:63:12: 'trailing'
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
            // JPQL.g3:64:8: ( 'trim' )
            // JPQL.g3:64:8: 'trim'
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
            // JPQL.g3:65:8: ( 'true' )
            // JPQL.g3:65:8: 'true'
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
            // JPQL.g3:66:11: ( 'unknown' )
            // JPQL.g3:66:11: 'unknown'
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
            // JPQL.g3:67:10: ( 'update' )
            // JPQL.g3:67:10: 'update'
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
            // JPQL.g3:68:9: ( 'upper' )
            // JPQL.g3:68:9: 'upper'
            {
            match("upper"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end UPPER

    // $ANTLR start WHERE
    public final void mWHERE() throws RecognitionException {
        try {
            int _type = WHERE;
            // JPQL.g3:69:9: ( 'where' )
            // JPQL.g3:69:9: 'where'
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
            // JPQL.g3:1186:7: ( '.' )
            // JPQL.g3:1186:7: '.'
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
            // JPQL.g3:1189:7: ( ( ' ' | '\\t' | '\\n' | '\\r' )+ )
            // JPQL.g3:1189:7: ( ' ' | '\\t' | '\\n' | '\\r' )+
            {
            // JPQL.g3:1189:7: ( ' ' | '\\t' | '\\n' | '\\r' )+
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
            	    // JPQL.g3:
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
            // JPQL.g3:1193:7: ( '(' )
            // JPQL.g3:1193:7: '('
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
            // JPQL.g3:1197:7: ( ')' )
            // JPQL.g3:1197:7: ')'
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
            // JPQL.g3:1201:7: ( ',' )
            // JPQL.g3:1201:7: ','
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
            // JPQL.g3:1205:7: ( TEXTCHAR )
            // JPQL.g3:1205:7: TEXTCHAR
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
    
            // JPQL.g3:1210:7: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | c1= '\\u0080' .. '\\uFFFE' ) ( 'a' .. 'z' | '_' | '$' | '0' .. '9' | c2= '\\u0080' .. '\\uFFFE' )* )
            // JPQL.g3:1210:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | c1= '\\u0080' .. '\\uFFFE' ) ( 'a' .. 'z' | '_' | '$' | '0' .. '9' | c2= '\\u0080' .. '\\uFFFE' )*
            {
            // JPQL.g3:1210:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | c1= '\\u0080' .. '\\uFFFE' )
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
                    new NoViableAltException("1210:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | c1= '\\u0080' .. '\\uFFFE' )", 2, 0, input);
            
                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // JPQL.g3:1210:8: 'a' .. 'z'
                    {
                    matchRange('a','z'); 
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:1210:19: 'A' .. 'Z'
                    {
                    matchRange('A','Z'); 
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:1210:30: '_'
                    {
                    match('_'); 
                    
                    }
                    break;
                case 4 :
                    // JPQL.g3:1210:36: '$'
                    {
                    match('$'); 
                    
                    }
                    break;
                case 5 :
                    // JPQL.g3:1211:8: c1= '\\u0080' .. '\\uFFFE'
                    {
                    c1 = input.LA(1);
                    matchRange('\u0080','\uFFFE'); 
                    
                               if (!Character.isJavaIdentifierStart(c1)) {
                                    throw new InvalidIdentifierStartException(c1, getLine(), getCharPositionInLine());
                               }
                           
                    
                    }
                    break;
            
            }

            // JPQL.g3:1218:7: ( 'a' .. 'z' | '_' | '$' | '0' .. '9' | c2= '\\u0080' .. '\\uFFFE' )*
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
            	    // JPQL.g3:1218:8: 'a' .. 'z'
            	    {
            	    matchRange('a','z'); 
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g3:1218:19: '_'
            	    {
            	    match('_'); 
            	    
            	    }
            	    break;
            	case 3 :
            	    // JPQL.g3:1218:25: '$'
            	    {
            	    match('$'); 
            	    
            	    }
            	    break;
            	case 4 :
            	    // JPQL.g3:1218:31: '0' .. '9'
            	    {
            	    matchRange('0','9'); 
            	    
            	    }
            	    break;
            	case 5 :
            	    // JPQL.g3:1219:8: c2= '\\u0080' .. '\\uFFFE'
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
            // JPQL.g3:1229:15: ( '0' ( 'x' | 'X' ) ( HEX_DIGIT )+ )
            // JPQL.g3:1229:15: '0' ( 'x' | 'X' ) ( HEX_DIGIT )+
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

            // JPQL.g3:1229:29: ( HEX_DIGIT )+
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
            	    // JPQL.g3:1229:29: HEX_DIGIT
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
            // JPQL.g3:1231:19: ( ( '0' | '1' .. '9' ( '0' .. '9' )* ) )
            // JPQL.g3:1231:19: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            {
            // JPQL.g3:1231:19: ( '0' | '1' .. '9' ( '0' .. '9' )* )
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
                    new NoViableAltException("1231:19: ( '0' | '1' .. '9' ( '0' .. '9' )* )", 6, 0, input);
            
                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // JPQL.g3:1231:20: '0'
                    {
                    match('0'); 
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:1231:26: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 
                    // JPQL.g3:1231:35: ( '0' .. '9' )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);
                        
                        if ( ((LA5_0>='0' && LA5_0<='9')) ) {
                            alt5=1;
                        }
                        
                    
                        switch (alt5) {
                    	case 1 :
                    	    // JPQL.g3:1231:35: '0' .. '9'
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
            // JPQL.g3:1233:16: ( INTEGER_LITERAL INTEGER_SUFFIX )
            // JPQL.g3:1233:16: INTEGER_LITERAL INTEGER_SUFFIX
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
            // JPQL.g3:1235:17: ( '0' ( '0' .. '7' )+ )
            // JPQL.g3:1235:17: '0' ( '0' .. '7' )+
            {
            match('0'); 
            // JPQL.g3:1235:21: ( '0' .. '7' )+
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
            	    // JPQL.g3:1235:22: '0' .. '7'
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
            // JPQL.g3:1240:9: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // JPQL.g3:1240:9: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
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
            // JPQL.g3:1244:18: ( ( 'l' | 'L' ) )
            // JPQL.g3:1244:18: ( 'l' | 'L' )
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
            // JPQL.g3:1248:9: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ )
            int alt12=3;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // JPQL.g3:1248:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )*
                    {
                    // JPQL.g3:1248:9: ( '0' .. '9' )+
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
                    	    // JPQL.g3:1248:10: '0' .. '9'
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
                    // JPQL.g3:1248:25: ( '0' .. '9' )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);
                        
                        if ( ((LA9_0>='0' && LA9_0<='9')) ) {
                            alt9=1;
                        }
                        
                    
                        switch (alt9) {
                    	case 1 :
                    	    // JPQL.g3:1248:26: '0' .. '9'
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
                    // JPQL.g3:1249:9: '.' ( '0' .. '9' )+
                    {
                    match('.'); 
                    // JPQL.g3:1249:13: ( '0' .. '9' )+
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
                    	    // JPQL.g3:1249:14: '0' .. '9'
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
                    // JPQL.g3:1250:9: ( '0' .. '9' )+
                    {
                    // JPQL.g3:1250:9: ( '0' .. '9' )+
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
                    	    // JPQL.g3:1250:10: '0' .. '9'
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
            // JPQL.g3:1254:9: ( NUMERIC_DIGITS ( DOUBLE_SUFFIX )? )
            // JPQL.g3:1254:9: NUMERIC_DIGITS ( DOUBLE_SUFFIX )?
            {
            mNUMERIC_DIGITS(); 
            // JPQL.g3:1254:24: ( DOUBLE_SUFFIX )?
            int alt13=2;
            int LA13_0 = input.LA(1);
            
            if ( (LA13_0=='d') ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // JPQL.g3:1254:24: DOUBLE_SUFFIX
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
            // JPQL.g3:1258:9: ( NUMERIC_DIGITS EXPONENT ( FLOAT_SUFFIX )? | NUMERIC_DIGITS FLOAT_SUFFIX )
            int alt15=2;
            alt15 = dfa15.predict(input);
            switch (alt15) {
                case 1 :
                    // JPQL.g3:1258:9: NUMERIC_DIGITS EXPONENT ( FLOAT_SUFFIX )?
                    {
                    mNUMERIC_DIGITS(); 
                    mEXPONENT(); 
                    // JPQL.g3:1258:33: ( FLOAT_SUFFIX )?
                    int alt14=2;
                    int LA14_0 = input.LA(1);
                    
                    if ( (LA14_0=='f') ) {
                        alt14=1;
                    }
                    switch (alt14) {
                        case 1 :
                            // JPQL.g3:1258:33: FLOAT_SUFFIX
                            {
                            mFLOAT_SUFFIX(); 
                            
                            }
                            break;
                    
                    }

                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:1259:9: NUMERIC_DIGITS FLOAT_SUFFIX
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
            // JPQL.g3:1265:9: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // JPQL.g3:1265:9: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // JPQL.g3:1265:21: ( '+' | '-' )?
            int alt16=2;
            int LA16_0 = input.LA(1);
            
            if ( (LA16_0=='+'||LA16_0=='-') ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // JPQL.g3:
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

            // JPQL.g3:1265:32: ( '0' .. '9' )+
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
            	    // JPQL.g3:1265:33: '0' .. '9'
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
            // JPQL.g3:1271:9: ( 'f' )
            // JPQL.g3:1271:9: 'f'
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
            // JPQL.g3:1276:7: ( 'd' )
            // JPQL.g3:1276:7: 'd'
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
            // JPQL.g3:1280:7: ( '=' )
            // JPQL.g3:1280:7: '='
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
            // JPQL.g3:1284:7: ( '>' )
            // JPQL.g3:1284:7: '>'
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
            // JPQL.g3:1288:7: ( '>=' )
            // JPQL.g3:1288:7: '>='
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
            // JPQL.g3:1292:7: ( '<' )
            // JPQL.g3:1292:7: '<'
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
            // JPQL.g3:1296:7: ( '<=' )
            // JPQL.g3:1296:7: '<='
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
            // JPQL.g3:1300:7: ( '<>' )
            // JPQL.g3:1300:7: '<>'
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
            // JPQL.g3:1304:7: ( '*' )
            // JPQL.g3:1304:7: '*'
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
            // JPQL.g3:1308:7: ( '/' )
            // JPQL.g3:1308:7: '/'
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
            // JPQL.g3:1312:7: ( '+' )
            // JPQL.g3:1312:7: '+'
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
            // JPQL.g3:1316:7: ( '-' )
            // JPQL.g3:1316:7: '-'
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
            // JPQL.g3:1321:7: ( '?' ( '1' .. '9' ) ( '0' .. '9' )* )
            // JPQL.g3:1321:7: '?' ( '1' .. '9' ) ( '0' .. '9' )*
            {
            match('?'); 
            // JPQL.g3:1321:11: ( '1' .. '9' )
            // JPQL.g3:1321:12: '1' .. '9'
            {
            matchRange('1','9'); 
            
            }

            // JPQL.g3:1321:22: ( '0' .. '9' )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);
                
                if ( ((LA18_0>='0' && LA18_0<='9')) ) {
                    alt18=1;
                }
                
            
                switch (alt18) {
            	case 1 :
            	    // JPQL.g3:1321:23: '0' .. '9'
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
            // JPQL.g3:1325:7: ( ':' TEXTCHAR )
            // JPQL.g3:1325:7: ':' TEXTCHAR
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
            // JPQL.g3:1331:7: ( '\"' (~ ( '\"' ) )* '\"' )
            // JPQL.g3:1331:7: '\"' (~ ( '\"' ) )* '\"'
            {
            match('\"'); 
            // JPQL.g3:1331:11: (~ ( '\"' ) )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);
                
                if ( ((LA19_0>='\u0000' && LA19_0<='!')||(LA19_0>='#' && LA19_0<='\uFFFE')) ) {
                    alt19=1;
                }
                
            
                switch (alt19) {
            	case 1 :
            	    // JPQL.g3:1331:12: ~ ( '\"' )
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
            // JPQL.g3:1335:7: ( '\\'' (~ ( '\\'' ) | ( '\\'\\'' ) )* '\\'' )
            // JPQL.g3:1335:7: '\\'' (~ ( '\\'' ) | ( '\\'\\'' ) )* '\\''
            {
            match('\''); 
            // JPQL.g3:1335:12: (~ ( '\\'' ) | ( '\\'\\'' ) )*
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
            	    // JPQL.g3:1335:13: ~ ( '\\'' )
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
            	    // JPQL.g3:1335:24: ( '\\'\\'' )
            	    {
            	    // JPQL.g3:1335:24: ( '\\'\\'' )
            	    // JPQL.g3:1335:25: '\\'\\''
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
        // JPQL.g3:1:10: ( ABS | ALL | AND | ANY | AS | ASC | AVG | BETWEEN | BOTH | BY | CONCAT | COUNT | CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | DESC | DELETE | DISTINCT | EMPTY | ESCAPE | EXISTS | FALSE | FETCH | FROM | GROUP | HAVING | IN | INNER | IS | JOIN | LEADING | LEFT | LENGTH | LIKE | LOCATE | LOWER | MAX | MEMBER | MIN | MOD | NEW | NOT | NULL | OBJECT | OF | OR | ORDER | OUTER | SELECT | SET | SIZE | SQRT | SOME | SUBSTRING | SUM | TRAILING | TRIM | TRUE | UNKNOWN | UPDATE | UPPER | WHERE | DOT | WS | LEFT_ROUND_BRACKET | RIGHT_ROUND_BRACKET | COMMA | IDENT | HEX_LITERAL | INTEGER_LITERAL | LONG_LITERAL | OCTAL_LITERAL | DOUBLE_LITERAL | FLOAT_LITERAL | EQUALS | GREATER_THAN | GREATER_THAN_EQUAL_TO | LESS_THAN | LESS_THAN_EQUAL_TO | NOT_EQUAL_TO | MULTIPLY | DIVIDE | PLUS | MINUS | POSITIONAL_PARAM | NAMED_PARAM | STRING_LITERAL_DOUBLE_QUOTED | STRING_LITERAL_SINGLE_QUOTED )
        int alt21=88;
        alt21 = dfa21.predict(input);
        switch (alt21) {
            case 1 :
                // JPQL.g3:1:10: ABS
                {
                mABS(); 
                
                }
                break;
            case 2 :
                // JPQL.g3:1:14: ALL
                {
                mALL(); 
                
                }
                break;
            case 3 :
                // JPQL.g3:1:18: AND
                {
                mAND(); 
                
                }
                break;
            case 4 :
                // JPQL.g3:1:22: ANY
                {
                mANY(); 
                
                }
                break;
            case 5 :
                // JPQL.g3:1:26: AS
                {
                mAS(); 
                
                }
                break;
            case 6 :
                // JPQL.g3:1:29: ASC
                {
                mASC(); 
                
                }
                break;
            case 7 :
                // JPQL.g3:1:33: AVG
                {
                mAVG(); 
                
                }
                break;
            case 8 :
                // JPQL.g3:1:37: BETWEEN
                {
                mBETWEEN(); 
                
                }
                break;
            case 9 :
                // JPQL.g3:1:45: BOTH
                {
                mBOTH(); 
                
                }
                break;
            case 10 :
                // JPQL.g3:1:50: BY
                {
                mBY(); 
                
                }
                break;
            case 11 :
                // JPQL.g3:1:53: CONCAT
                {
                mCONCAT(); 
                
                }
                break;
            case 12 :
                // JPQL.g3:1:60: COUNT
                {
                mCOUNT(); 
                
                }
                break;
            case 13 :
                // JPQL.g3:1:66: CURRENT_DATE
                {
                mCURRENT_DATE(); 
                
                }
                break;
            case 14 :
                // JPQL.g3:1:79: CURRENT_TIME
                {
                mCURRENT_TIME(); 
                
                }
                break;
            case 15 :
                // JPQL.g3:1:92: CURRENT_TIMESTAMP
                {
                mCURRENT_TIMESTAMP(); 
                
                }
                break;
            case 16 :
                // JPQL.g3:1:110: DESC
                {
                mDESC(); 
                
                }
                break;
            case 17 :
                // JPQL.g3:1:115: DELETE
                {
                mDELETE(); 
                
                }
                break;
            case 18 :
                // JPQL.g3:1:122: DISTINCT
                {
                mDISTINCT(); 
                
                }
                break;
            case 19 :
                // JPQL.g3:1:131: EMPTY
                {
                mEMPTY(); 
                
                }
                break;
            case 20 :
                // JPQL.g3:1:137: ESCAPE
                {
                mESCAPE(); 
                
                }
                break;
            case 21 :
                // JPQL.g3:1:144: EXISTS
                {
                mEXISTS(); 
                
                }
                break;
            case 22 :
                // JPQL.g3:1:151: FALSE
                {
                mFALSE(); 
                
                }
                break;
            case 23 :
                // JPQL.g3:1:157: FETCH
                {
                mFETCH(); 
                
                }
                break;
            case 24 :
                // JPQL.g3:1:163: FROM
                {
                mFROM(); 
                
                }
                break;
            case 25 :
                // JPQL.g3:1:168: GROUP
                {
                mGROUP(); 
                
                }
                break;
            case 26 :
                // JPQL.g3:1:174: HAVING
                {
                mHAVING(); 
                
                }
                break;
            case 27 :
                // JPQL.g3:1:181: IN
                {
                mIN(); 
                
                }
                break;
            case 28 :
                // JPQL.g3:1:184: INNER
                {
                mINNER(); 
                
                }
                break;
            case 29 :
                // JPQL.g3:1:190: IS
                {
                mIS(); 
                
                }
                break;
            case 30 :
                // JPQL.g3:1:193: JOIN
                {
                mJOIN(); 
                
                }
                break;
            case 31 :
                // JPQL.g3:1:198: LEADING
                {
                mLEADING(); 
                
                }
                break;
            case 32 :
                // JPQL.g3:1:206: LEFT
                {
                mLEFT(); 
                
                }
                break;
            case 33 :
                // JPQL.g3:1:211: LENGTH
                {
                mLENGTH(); 
                
                }
                break;
            case 34 :
                // JPQL.g3:1:218: LIKE
                {
                mLIKE(); 
                
                }
                break;
            case 35 :
                // JPQL.g3:1:223: LOCATE
                {
                mLOCATE(); 
                
                }
                break;
            case 36 :
                // JPQL.g3:1:230: LOWER
                {
                mLOWER(); 
                
                }
                break;
            case 37 :
                // JPQL.g3:1:236: MAX
                {
                mMAX(); 
                
                }
                break;
            case 38 :
                // JPQL.g3:1:240: MEMBER
                {
                mMEMBER(); 
                
                }
                break;
            case 39 :
                // JPQL.g3:1:247: MIN
                {
                mMIN(); 
                
                }
                break;
            case 40 :
                // JPQL.g3:1:251: MOD
                {
                mMOD(); 
                
                }
                break;
            case 41 :
                // JPQL.g3:1:255: NEW
                {
                mNEW(); 
                
                }
                break;
            case 42 :
                // JPQL.g3:1:259: NOT
                {
                mNOT(); 
                
                }
                break;
            case 43 :
                // JPQL.g3:1:263: NULL
                {
                mNULL(); 
                
                }
                break;
            case 44 :
                // JPQL.g3:1:268: OBJECT
                {
                mOBJECT(); 
                
                }
                break;
            case 45 :
                // JPQL.g3:1:275: OF
                {
                mOF(); 
                
                }
                break;
            case 46 :
                // JPQL.g3:1:278: OR
                {
                mOR(); 
                
                }
                break;
            case 47 :
                // JPQL.g3:1:281: ORDER
                {
                mORDER(); 
                
                }
                break;
            case 48 :
                // JPQL.g3:1:287: OUTER
                {
                mOUTER(); 
                
                }
                break;
            case 49 :
                // JPQL.g3:1:293: SELECT
                {
                mSELECT(); 
                
                }
                break;
            case 50 :
                // JPQL.g3:1:300: SET
                {
                mSET(); 
                
                }
                break;
            case 51 :
                // JPQL.g3:1:304: SIZE
                {
                mSIZE(); 
                
                }
                break;
            case 52 :
                // JPQL.g3:1:309: SQRT
                {
                mSQRT(); 
                
                }
                break;
            case 53 :
                // JPQL.g3:1:314: SOME
                {
                mSOME(); 
                
                }
                break;
            case 54 :
                // JPQL.g3:1:319: SUBSTRING
                {
                mSUBSTRING(); 
                
                }
                break;
            case 55 :
                // JPQL.g3:1:329: SUM
                {
                mSUM(); 
                
                }
                break;
            case 56 :
                // JPQL.g3:1:333: TRAILING
                {
                mTRAILING(); 
                
                }
                break;
            case 57 :
                // JPQL.g3:1:342: TRIM
                {
                mTRIM(); 
                
                }
                break;
            case 58 :
                // JPQL.g3:1:347: TRUE
                {
                mTRUE(); 
                
                }
                break;
            case 59 :
                // JPQL.g3:1:352: UNKNOWN
                {
                mUNKNOWN(); 
                
                }
                break;
            case 60 :
                // JPQL.g3:1:360: UPDATE
                {
                mUPDATE(); 
                
                }
                break;
            case 61 :
                // JPQL.g3:1:367: UPPER
                {
                mUPPER(); 
                
                }
                break;
            case 62 :
                // JPQL.g3:1:373: WHERE
                {
                mWHERE(); 
                
                }
                break;
            case 63 :
                // JPQL.g3:1:379: DOT
                {
                mDOT(); 
                
                }
                break;
            case 64 :
                // JPQL.g3:1:383: WS
                {
                mWS(); 
                
                }
                break;
            case 65 :
                // JPQL.g3:1:386: LEFT_ROUND_BRACKET
                {
                mLEFT_ROUND_BRACKET(); 
                
                }
                break;
            case 66 :
                // JPQL.g3:1:405: RIGHT_ROUND_BRACKET
                {
                mRIGHT_ROUND_BRACKET(); 
                
                }
                break;
            case 67 :
                // JPQL.g3:1:425: COMMA
                {
                mCOMMA(); 
                
                }
                break;
            case 68 :
                // JPQL.g3:1:431: IDENT
                {
                mIDENT(); 
                
                }
                break;
            case 69 :
                // JPQL.g3:1:437: HEX_LITERAL
                {
                mHEX_LITERAL(); 
                
                }
                break;
            case 70 :
                // JPQL.g3:1:449: INTEGER_LITERAL
                {
                mINTEGER_LITERAL(); 
                
                }
                break;
            case 71 :
                // JPQL.g3:1:465: LONG_LITERAL
                {
                mLONG_LITERAL(); 
                
                }
                break;
            case 72 :
                // JPQL.g3:1:478: OCTAL_LITERAL
                {
                mOCTAL_LITERAL(); 
                
                }
                break;
            case 73 :
                // JPQL.g3:1:492: DOUBLE_LITERAL
                {
                mDOUBLE_LITERAL(); 
                
                }
                break;
            case 74 :
                // JPQL.g3:1:507: FLOAT_LITERAL
                {
                mFLOAT_LITERAL(); 
                
                }
                break;
            case 75 :
                // JPQL.g3:1:521: EQUALS
                {
                mEQUALS(); 
                
                }
                break;
            case 76 :
                // JPQL.g3:1:528: GREATER_THAN
                {
                mGREATER_THAN(); 
                
                }
                break;
            case 77 :
                // JPQL.g3:1:541: GREATER_THAN_EQUAL_TO
                {
                mGREATER_THAN_EQUAL_TO(); 
                
                }
                break;
            case 78 :
                // JPQL.g3:1:563: LESS_THAN
                {
                mLESS_THAN(); 
                
                }
                break;
            case 79 :
                // JPQL.g3:1:573: LESS_THAN_EQUAL_TO
                {
                mLESS_THAN_EQUAL_TO(); 
                
                }
                break;
            case 80 :
                // JPQL.g3:1:592: NOT_EQUAL_TO
                {
                mNOT_EQUAL_TO(); 
                
                }
                break;
            case 81 :
                // JPQL.g3:1:605: MULTIPLY
                {
                mMULTIPLY(); 
                
                }
                break;
            case 82 :
                // JPQL.g3:1:614: DIVIDE
                {
                mDIVIDE(); 
                
                }
                break;
            case 83 :
                // JPQL.g3:1:621: PLUS
                {
                mPLUS(); 
                
                }
                break;
            case 84 :
                // JPQL.g3:1:626: MINUS
                {
                mMINUS(); 
                
                }
                break;
            case 85 :
                // JPQL.g3:1:632: POSITIONAL_PARAM
                {
                mPOSITIONAL_PARAM(); 
                
                }
                break;
            case 86 :
                // JPQL.g3:1:649: NAMED_PARAM
                {
                mNAMED_PARAM(); 
                
                }
                break;
            case 87 :
                // JPQL.g3:1:661: STRING_LITERAL_DOUBLE_QUOTED
                {
                mSTRING_LITERAL_DOUBLE_QUOTED(); 
                
                }
                break;
            case 88 :
                // JPQL.g3:1:690: STRING_LITERAL_SINGLE_QUOTED
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
        "\1\uffff\1\4\3\uffff";
    static final String DFA12_eofS =
        "\5\uffff";
    static final String DFA12_minS =
        "\2\56\3\uffff";
    static final String DFA12_maxS =
        "\2\71\3\uffff";
    static final String DFA12_acceptS =
        "\2\uffff\1\2\1\1\1\3";
    static final String DFA12_specialS =
        "\5\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\3\1\uffff\12\1",
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
            return "1246:1: fragment NUMERIC_DIGITS : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ | ( '0' .. '9' )+ );";
        }
    }
    static final String DFA15_eotS =
        "\10\uffff";
    static final String DFA15_eofS =
        "\10\uffff";
    static final String DFA15_minS =
        "\2\56\1\60\2\uffff\3\60";
    static final String DFA15_maxS =
        "\1\71\1\146\1\71\2\uffff\3\146";
    static final String DFA15_acceptS =
        "\3\uffff\1\1\1\2\3\uffff";
    static final String DFA15_specialS =
        "\10\uffff}>";
    static final String[] DFA15_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\5\1\uffff\12\1\13\uffff\1\3\37\uffff\1\3\1\4",
            "\12\6",
            "",
            "",
            "\12\7\13\uffff\1\3\37\uffff\1\3\1\4",
            "\12\6\13\uffff\1\3\37\uffff\1\3\1\4",
            "\12\7\13\uffff\1\3\37\uffff\1\3\1\4"
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
            return "1257:1: FLOAT_LITERAL : ( NUMERIC_DIGITS EXPONENT ( FLOAT_SUFFIX )? | NUMERIC_DIGITS FLOAT_SUFFIX );";
        }
    }
    static final String DFA21_eotS =
        "\1\uffff\22\30\1\125\5\uffff\2\131\1\uffff\1\140\1\143\10\uffff"+
        "\1\145\5\30\1\154\15\30\1\174\1\176\13\30\1\u008e\1\30\1\u0090\12"+
        "\30\1\135\2\uffff\1\135\1\u00a1\3\uffff\1\135\1\uffff\1\131\5\uffff"+
        "\1\u00a2\1\uffff\1\u00a3\1\u00a4\1\u00a5\1\u00a6\1\u00a7\1\30\1"+
        "\uffff\17\30\1\uffff\1\30\1\uffff\10\30\1\u00c1\1\u00c2\1\u00c3"+
        "\1\u00c4\1\u00c5\2\30\1\uffff\1\30\1\uffff\4\30\1\u00cd\1\u00ce"+
        "\11\30\1\135\7\uffff\1\30\1\u00d9\4\30\1\u00de\6\30\1\u00e5\3\30"+
        "\1\u00e9\4\30\1\u00ee\1\u00ef\1\30\5\uffff\1\u00f1\3\30\1\u00f5"+
        "\1\u00f6\1\30\2\uffff\1\30\1\u00f9\1\30\1\u00fb\1\u00fc\5\30\1\uffff"+
        "\1\30\1\u0103\2\30\1\uffff\3\30\1\u0109\1\u010a\1\u010b\1\uffff"+
        "\1\u010c\1\30\1\u010e\1\uffff\1\u010f\3\30\2\uffff\1\30\1\uffff"+
        "\1\u0114\1\u0115\1\30\2\uffff\2\30\1\uffff\1\30\2\uffff\2\30\1\u011c"+
        "\1\u011d\2\30\1\uffff\1\u0120\1\u0121\1\30\1\u0123\1\u0124\4\uffff"+
        "\1\u0125\2\uffff\1\u0126\1\u0127\1\30\1\u0129\2\uffff\1\u012a\1"+
        "\u012b\3\30\1\u012f\2\uffff\1\u0130\1\30\2\uffff\1\30\5\uffff\1"+
        "\u0133\3\uffff\2\30\1\u0136\2\uffff\1\30\1\u0139\1\uffff\1\30\1"+
        "\u013b\1\uffff\2\30\1\uffff\1\u013e\1\uffff\2\30\1\uffff\2\30\1"+
        "\u0143\1\u0145\1\uffff\1\30\1\uffff\3\30\1\u014a\1\uffff";
    static final String DFA21_eofS =
        "\u014b\uffff";
    static final String DFA21_minS =
        "\1\11\1\142\1\145\1\157\1\145\1\155\1\141\1\162\1\141\1\156\1\157"+
        "\1\145\1\141\1\145\1\142\1\145\1\162\1\156\1\150\1\60\5\uffff\2"+
        "\56\1\uffff\2\75\10\uffff\1\44\1\144\1\147\1\163\1\154\1\164\1\44"+
        "\1\164\1\162\1\156\1\154\1\163\1\143\1\151\1\160\1\164\1\154\2\157"+
        "\1\166\2\44\1\151\1\143\1\141\1\153\1\155\1\170\1\144\1\156\1\167"+
        "\1\164\1\154\1\44\1\164\1\44\1\152\1\172\1\162\1\154\1\142\1\155"+
        "\1\141\1\153\1\144\1\145\1\60\2\uffff\1\60\1\56\3\uffff\1\56\1\uffff"+
        "\1\56\5\uffff\1\44\1\uffff\5\44\1\167\1\uffff\1\150\1\162\1\156"+
        "\1\143\1\145\1\143\1\164\1\141\1\163\1\164\1\143\1\163\1\155\1\165"+
        "\1\151\1\uffff\1\145\1\uffff\1\156\1\145\1\141\1\147\1\144\1\164"+
        "\1\145\1\142\5\44\1\154\1\145\1\uffff\1\145\1\uffff\2\145\1\164"+
        "\1\145\2\44\1\163\1\145\1\151\1\155\1\145\1\156\1\141\1\145\1\162"+
        "\1\60\7\uffff\1\145\1\44\1\145\1\164\1\141\1\164\1\44\1\151\1\160"+
        "\1\164\1\171\1\150\1\145\1\44\1\160\1\156\1\162\1\44\1\162\2\164"+
        "\1\151\2\44\1\145\5\uffff\1\44\2\162\1\143\2\44\1\143\2\uffff\1"+
        "\164\1\44\1\154\2\44\1\157\1\164\1\162\2\145\1\uffff\1\156\1\44"+
        "\1\164\1\145\1\uffff\1\156\1\145\1\163\3\44\1\uffff\1\44\1\147\1"+
        "\44\1\uffff\1\44\1\145\1\150\1\156\2\uffff\1\162\1\uffff\2\44\1"+
        "\164\2\uffff\1\164\1\162\1\uffff\1\151\2\uffff\1\167\1\145\2\44"+
        "\1\156\1\164\1\uffff\2\44\1\143\2\44\4\uffff\1\44\2\uffff\2\44\1"+
        "\147\1\44\2\uffff\2\44\1\151\2\156\1\44\2\uffff\1\44\1\137\2\uffff"+
        "\1\164\5\uffff\1\44\3\uffff\1\156\1\147\1\44\2\uffff\1\144\1\44"+
        "\1\uffff\1\147\1\44\1\uffff\1\141\1\151\1\uffff\1\44\1\uffff\1\164"+
        "\1\155\1\uffff\2\145\2\44\1\uffff\1\164\1\uffff\1\141\1\155\1\160"+
        "\1\44\1\uffff";
    static final String DFA21_maxS =
        "\1\ufffe\1\166\1\171\1\165\1\151\1\170\2\162\1\141\1\163\3\157\3"+
        "\165\1\162\1\160\1\150\1\71\5\uffff\1\170\1\154\1\uffff\1\75\1\76"+
        "\10\uffff\1\ufffe\1\171\1\147\1\163\1\154\1\164\1\ufffe\1\164\1"+
        "\162\1\165\2\163\1\143\1\151\1\160\1\164\1\154\2\157\1\166\2\ufffe"+
        "\1\151\1\167\1\156\1\153\1\155\1\170\1\144\1\156\1\167\1\164\1\154"+
        "\1\ufffe\1\164\1\ufffe\1\152\1\172\1\162\1\164\2\155\1\165\1\153"+
        "\1\160\1\145\1\146\2\uffff\2\146\3\uffff\1\146\1\uffff\1\154\5\uffff"+
        "\1\ufffe\1\uffff\5\ufffe\1\167\1\uffff\1\150\1\162\1\156\1\143\1"+
        "\145\1\143\1\164\1\141\1\163\1\164\1\143\1\163\1\155\1\165\1\151"+
        "\1\uffff\1\145\1\uffff\1\156\1\145\1\141\1\147\1\144\1\164\1\145"+
        "\1\142\5\ufffe\1\154\1\145\1\uffff\1\145\1\uffff\2\145\1\164\1\145"+
        "\2\ufffe\1\163\1\145\1\151\1\155\1\145\1\156\1\141\1\145\1\162\1"+
        "\146\7\uffff\1\145\1\ufffe\1\145\1\164\1\141\1\164\1\ufffe\1\151"+
        "\1\160\1\164\1\171\1\150\1\145\1\ufffe\1\160\1\156\1\162\1\ufffe"+
        "\1\162\2\164\1\151\2\ufffe\1\145\5\uffff\1\ufffe\2\162\1\143\2\ufffe"+
        "\1\143\2\uffff\1\164\1\ufffe\1\154\2\ufffe\1\157\1\164\1\162\2\145"+
        "\1\uffff\1\156\1\ufffe\1\164\1\145\1\uffff\1\156\1\145\1\163\3\ufffe"+
        "\1\uffff\1\ufffe\1\147\1\ufffe\1\uffff\1\ufffe\1\145\1\150\1\156"+
        "\2\uffff\1\162\1\uffff\2\ufffe\1\164\2\uffff\1\164\1\162\1\uffff"+
        "\1\151\2\uffff\1\167\1\145\2\ufffe\1\156\1\164\1\uffff\2\ufffe\1"+
        "\143\2\ufffe\4\uffff\1\ufffe\2\uffff\2\ufffe\1\147\1\ufffe\2\uffff"+
        "\2\ufffe\1\151\2\156\1\ufffe\2\uffff\1\ufffe\1\137\2\uffff\1\164"+
        "\5\uffff\1\ufffe\3\uffff\1\156\1\147\1\ufffe\2\uffff\1\164\1\ufffe"+
        "\1\uffff\1\147\1\ufffe\1\uffff\1\141\1\151\1\uffff\1\ufffe\1\uffff"+
        "\1\164\1\155\1\uffff\2\145\2\ufffe\1\uffff\1\164\1\uffff\1\141\1"+
        "\155\1\160\1\ufffe\1\uffff";
    static final String DFA21_acceptS =
        "\24\uffff\1\100\1\101\1\102\1\103\1\104\2\uffff\1\113\2\uffff\1"+
        "\121\1\122\1\123\1\124\1\125\1\126\1\127\1\130\57\uffff\1\77\1\105"+
        "\2\uffff\1\106\1\112\1\107\1\uffff\1\111\1\uffff\1\115\1\114\1\120"+
        "\1\117\1\116\1\uffff\1\5\6\uffff\1\12\17\uffff\1\35\1\uffff\1\33"+
        "\17\uffff\1\56\1\uffff\1\55\20\uffff\1\110\1\6\1\3\1\4\1\7\1\1\1"+
        "\2\31\uffff\1\45\1\50\1\47\1\51\1\52\7\uffff\1\62\1\67\12\uffff"+
        "\1\11\4\uffff\1\20\6\uffff\1\30\3\uffff\1\36\4\uffff\1\40\1\42\1"+
        "\uffff\1\53\3\uffff\1\63\1\64\2\uffff\1\65\1\uffff\1\71\1\72\6\uffff"+
        "\1\14\5\uffff\1\23\1\27\1\26\1\31\1\uffff\1\34\1\44\4\uffff\1\57"+
        "\1\60\6\uffff\1\75\1\76\2\uffff\1\13\1\21\1\uffff\1\24\1\25\1\32"+
        "\1\43\1\41\1\uffff\1\46\1\54\1\61\3\uffff\1\74\1\10\2\uffff\1\37"+
        "\2\uffff\1\73\2\uffff\1\22\1\uffff\1\70\2\uffff\1\66\4\uffff\1\15"+
        "\1\uffff\1\16\4\uffff\1\17";
    static final String DFA21_specialS =
        "\u014b\uffff}>";
    static final String[] DFA21_transitionS = {
            "\2\24\2\uffff\1\24\22\uffff\1\24\1\uffff\1\44\1\uffff\1\30\2"+
            "\uffff\1\45\1\25\1\26\1\36\1\40\1\27\1\41\1\23\1\37\1\31\11"+
            "\32\1\43\1\uffff\1\35\1\33\1\34\1\42\1\uffff\32\30\4\uffff\1"+
            "\30\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\30"+
            "\1\13\1\14\1\15\1\16\3\30\1\17\1\20\1\21\1\30\1\22\3\30\5\uffff"+
            "\uff7f\30",
            "\1\51\11\uffff\1\52\1\uffff\1\47\4\uffff\1\46\2\uffff\1\50",
            "\1\53\11\uffff\1\55\11\uffff\1\54",
            "\1\57\5\uffff\1\56",
            "\1\60\3\uffff\1\61",
            "\1\64\5\uffff\1\62\4\uffff\1\63",
            "\1\66\3\uffff\1\65\14\uffff\1\67",
            "\1\70",
            "\1\71",
            "\1\73\4\uffff\1\72",
            "\1\74",
            "\1\76\3\uffff\1\77\5\uffff\1\75",
            "\1\101\3\uffff\1\100\3\uffff\1\103\5\uffff\1\102",
            "\1\104\11\uffff\1\105\5\uffff\1\106",
            "\1\112\3\uffff\1\111\13\uffff\1\107\2\uffff\1\110",
            "\1\115\3\uffff\1\113\5\uffff\1\117\1\uffff\1\114\3\uffff\1\116",
            "\1\120",
            "\1\121\1\uffff\1\122",
            "\1\123",
            "\12\124",
            "",
            "",
            "",
            "",
            "",
            "\1\127\1\uffff\10\130\2\134\13\uffff\1\132\6\uffff\1\133\13"+
            "\uffff\1\126\13\uffff\1\135\2\132\5\uffff\1\133\13\uffff\1\126",
            "\1\127\1\uffff\12\136\13\uffff\1\132\6\uffff\1\133\27\uffff"+
            "\1\135\2\132\5\uffff\1\133",
            "",
            "\1\137",
            "\1\142\1\141",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\2\30\1\144\27\30"+
            "\5\uffff\uff7f\30",
            "\1\146\24\uffff\1\147",
            "\1\150",
            "\1\151",
            "\1\152",
            "\1\153",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\155",
            "\1\156",
            "\1\160\6\uffff\1\157",
            "\1\161\6\uffff\1\162",
            "\1\163",
            "\1\164",
            "\1\165",
            "\1\166",
            "\1\167",
            "\1\170",
            "\1\171",
            "\1\172",
            "\1\173",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\15\30\1\175\14\30"+
            "\5\uffff\uff7f\30",
            "\1\177",
            "\1\u0081\23\uffff\1\u0080",
            "\1\u0083\4\uffff\1\u0084\7\uffff\1\u0082",
            "\1\u0085",
            "\1\u0086",
            "\1\u0087",
            "\1\u0088",
            "\1\u0089",
            "\1\u008a",
            "\1\u008b",
            "\1\u008c",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\3\30\1\u008d\26\30"+
            "\5\uffff\uff7f\30",
            "\1\u008f",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u0091",
            "\1\u0092",
            "\1\u0093",
            "\1\u0094\7\uffff\1\u0095",
            "\1\u0097\12\uffff\1\u0096",
            "\1\u0098",
            "\1\u0099\7\uffff\1\u009a\13\uffff\1\u009b",
            "\1\u009c",
            "\1\u009d\13\uffff\1\u009e",
            "\1\u009f",
            "\12\124\13\uffff\1\132\37\uffff\2\132",
            "",
            "",
            "\12\u00a0\13\uffff\1\132\37\uffff\2\132",
            "\1\127\1\uffff\10\130\2\134\13\uffff\1\132\36\uffff\1\135\2"+
            "\132",
            "",
            "",
            "",
            "\1\127\1\uffff\12\134\13\uffff\1\132\37\uffff\2\132",
            "",
            "\1\127\1\uffff\12\136\13\uffff\1\132\6\uffff\1\133\27\uffff"+
            "\1\135\2\132\5\uffff\1\133",
            "",
            "",
            "",
            "",
            "",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u00a8",
            "",
            "\1\u00a9",
            "\1\u00aa",
            "\1\u00ab",
            "\1\u00ac",
            "\1\u00ad",
            "\1\u00ae",
            "\1\u00af",
            "\1\u00b0",
            "\1\u00b1",
            "\1\u00b2",
            "\1\u00b3",
            "\1\u00b4",
            "\1\u00b5",
            "\1\u00b6",
            "\1\u00b7",
            "",
            "\1\u00b8",
            "",
            "\1\u00b9",
            "\1\u00ba",
            "\1\u00bb",
            "\1\u00bc",
            "\1\u00bd",
            "\1\u00be",
            "\1\u00bf",
            "\1\u00c0",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u00c6",
            "\1\u00c7",
            "",
            "\1\u00c8",
            "",
            "\1\u00c9",
            "\1\u00ca",
            "\1\u00cb",
            "\1\u00cc",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u00cf",
            "\1\u00d0",
            "\1\u00d1",
            "\1\u00d2",
            "\1\u00d3",
            "\1\u00d4",
            "\1\u00d5",
            "\1\u00d6",
            "\1\u00d7",
            "\12\u00a0\13\uffff\1\132\37\uffff\2\132",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u00d8",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u00da",
            "\1\u00db",
            "\1\u00dc",
            "\1\u00dd",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u00df",
            "\1\u00e0",
            "\1\u00e1",
            "\1\u00e2",
            "\1\u00e3",
            "\1\u00e4",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u00e6",
            "\1\u00e7",
            "\1\u00e8",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u00ea",
            "\1\u00eb",
            "\1\u00ec",
            "\1\u00ed",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u00f0",
            "",
            "",
            "",
            "",
            "",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u00f2",
            "\1\u00f3",
            "\1\u00f4",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u00f7",
            "",
            "",
            "\1\u00f8",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u00fa",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u00fd",
            "\1\u00fe",
            "\1\u00ff",
            "\1\u0100",
            "\1\u0101",
            "",
            "\1\u0102",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u0104",
            "\1\u0105",
            "",
            "\1\u0106",
            "\1\u0107",
            "\1\u0108",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u010d",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u0110",
            "\1\u0111",
            "\1\u0112",
            "",
            "",
            "\1\u0113",
            "",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u0116",
            "",
            "",
            "\1\u0117",
            "\1\u0118",
            "",
            "\1\u0119",
            "",
            "",
            "\1\u011a",
            "\1\u011b",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u011e",
            "\1\u011f",
            "",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u0122",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "",
            "",
            "",
            "",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "",
            "",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u0128",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "",
            "",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u012c",
            "\1\u012d",
            "\1\u012e",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "",
            "",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\u0131",
            "",
            "",
            "\1\u0132",
            "",
            "",
            "",
            "",
            "",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "",
            "",
            "",
            "\1\u0134",
            "\1\u0135",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "",
            "",
            "\1\u0137\17\uffff\1\u0138",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "",
            "\1\u013a",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "",
            "\1\u013c",
            "\1\u013d",
            "",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "",
            "\1\u013f",
            "\1\u0140",
            "",
            "\1\u0141",
            "\1\u0142",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\22\30\1\u0144\7\30"+
            "\5\uffff\uff7f\30",
            "",
            "\1\u0146",
            "",
            "\1\u0147",
            "\1\u0148",
            "\1\u0149",
            "\1\30\13\uffff\12\30\45\uffff\1\30\1\uffff\32\30\5\uffff\uff7f"+
            "\30",
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
            return "1:1: Tokens : ( ABS | ALL | AND | ANY | AS | ASC | AVG | BETWEEN | BOTH | BY | CONCAT | COUNT | CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | DESC | DELETE | DISTINCT | EMPTY | ESCAPE | EXISTS | FALSE | FETCH | FROM | GROUP | HAVING | IN | INNER | IS | JOIN | LEADING | LEFT | LENGTH | LIKE | LOCATE | LOWER | MAX | MEMBER | MIN | MOD | NEW | NOT | NULL | OBJECT | OF | OR | ORDER | OUTER | SELECT | SET | SIZE | SQRT | SOME | SUBSTRING | SUM | TRAILING | TRIM | TRUE | UNKNOWN | UPDATE | UPPER | WHERE | DOT | WS | LEFT_ROUND_BRACKET | RIGHT_ROUND_BRACKET | COMMA | IDENT | HEX_LITERAL | INTEGER_LITERAL | LONG_LITERAL | OCTAL_LITERAL | DOUBLE_LITERAL | FLOAT_LITERAL | EQUALS | GREATER_THAN | GREATER_THAN_EQUAL_TO | LESS_THAN | LESS_THAN_EQUAL_TO | NOT_EQUAL_TO | MULTIPLY | DIVIDE | PLUS | MINUS | POSITIONAL_PARAM | NAMED_PARAM | STRING_LITERAL_DOUBLE_QUOTED | STRING_LITERAL_SINGLE_QUOTED );";
        }
    }
 

}