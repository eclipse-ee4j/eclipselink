// $ANTLR 3.0 JPQL.g3 2007-08-29 14:24:24

    package org.eclipse.persistence.internal.jpa.parsing.jpql.antlr;  


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
    public static final int TEXTCHAR=91;
    public static final int CONCAT=14;
    public static final int SELECT=52;
    public static final int BETWEEN=11;
    public static final int NUM_INT=81;
    public static final int DESC=19;
    public static final int LESS_THAN_EQUAL_TO=76;
    public static final int BOTH=12;
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
    public static final int RIGHT_ROUND_BRACKET=70;
    public static final int UPDATE=63;
    public static final int LOWER=39;
    public static final int POSITIONAL_PARAM=87;
    public static final int ANY=7;
    public static final int FLOAT_SUFFIX=93;
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
    public static final int Tokens=94;
    public static final int SIZE=54;
    public static final int AVG=10;
    public static final int NOT=45;
    public JPQLLexer() {;} 
    public JPQLLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "JPQL.g3"; }

    // $ANTLR start ABS
    public final void mABS() throws RecognitionException {
        try {
            int _type = ABS;
            // JPQL.g3:6:7: ( 'abs' )
            // JPQL.g3:6:7: 'abs'
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
            // JPQL.g3:7:7: ( 'all' )
            // JPQL.g3:7:7: 'all'
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
            // JPQL.g3:8:7: ( 'and' )
            // JPQL.g3:8:7: 'and'
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
            // JPQL.g3:9:7: ( 'any' )
            // JPQL.g3:9:7: 'any'
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
            // JPQL.g3:10:6: ( 'as' )
            // JPQL.g3:10:6: 'as'
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
            // JPQL.g3:11:7: ( 'asc' )
            // JPQL.g3:11:7: 'asc'
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
            // JPQL.g3:12:7: ( 'avg' )
            // JPQL.g3:12:7: 'avg'
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
            // JPQL.g3:13:11: ( 'between' )
            // JPQL.g3:13:11: 'between'
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
            // JPQL.g3:14:8: ( 'both' )
            // JPQL.g3:14:8: 'both'
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
            // JPQL.g3:15:6: ( 'by' )
            // JPQL.g3:15:6: 'by'
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
            // JPQL.g3:16:10: ( 'concat' )
            // JPQL.g3:16:10: 'concat'
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
            // JPQL.g3:17:9: ( 'count' )
            // JPQL.g3:17:9: 'count'
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
            // JPQL.g3:18:16: ( 'current_date' )
            // JPQL.g3:18:16: 'current_date'
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
            // JPQL.g3:19:16: ( 'current_time' )
            // JPQL.g3:19:16: 'current_time'
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
            // JPQL.g3:20:21: ( 'current_timestamp' )
            // JPQL.g3:20:21: 'current_timestamp'
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
            // JPQL.g3:21:8: ( 'desc' )
            // JPQL.g3:21:8: 'desc'
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
            // JPQL.g3:22:10: ( 'delete' )
            // JPQL.g3:22:10: 'delete'
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
            // JPQL.g3:23:12: ( 'distinct' )
            // JPQL.g3:23:12: 'distinct'
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
            // JPQL.g3:24:9: ( 'empty' )
            // JPQL.g3:24:9: 'empty'
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
            // JPQL.g3:25:10: ( 'escape' )
            // JPQL.g3:25:10: 'escape'
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
            // JPQL.g3:26:10: ( 'exists' )
            // JPQL.g3:26:10: 'exists'
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
            // JPQL.g3:27:9: ( 'false' )
            // JPQL.g3:27:9: 'false'
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
            // JPQL.g3:28:9: ( 'fetch' )
            // JPQL.g3:28:9: 'fetch'
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
            // JPQL.g3:29:8: ( 'from' )
            // JPQL.g3:29:8: 'from'
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
            // JPQL.g3:30:9: ( 'group' )
            // JPQL.g3:30:9: 'group'
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
            // JPQL.g3:31:10: ( 'having' )
            // JPQL.g3:31:10: 'having'
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
            // JPQL.g3:32:6: ( 'in' )
            // JPQL.g3:32:6: 'in'
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
            // JPQL.g3:33:9: ( 'inner' )
            // JPQL.g3:33:9: 'inner'
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
            // JPQL.g3:34:6: ( 'is' )
            // JPQL.g3:34:6: 'is'
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
            // JPQL.g3:35:8: ( 'join' )
            // JPQL.g3:35:8: 'join'
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
            // JPQL.g3:36:11: ( 'leading' )
            // JPQL.g3:36:11: 'leading'
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
            // JPQL.g3:37:8: ( 'left' )
            // JPQL.g3:37:8: 'left'
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
            // JPQL.g3:38:10: ( 'length' )
            // JPQL.g3:38:10: 'length'
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
            // JPQL.g3:39:8: ( 'like' )
            // JPQL.g3:39:8: 'like'
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
            // JPQL.g3:40:10: ( 'locate' )
            // JPQL.g3:40:10: 'locate'
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
            // JPQL.g3:41:9: ( 'lower' )
            // JPQL.g3:41:9: 'lower'
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
            // JPQL.g3:42:7: ( 'max' )
            // JPQL.g3:42:7: 'max'
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
            // JPQL.g3:43:10: ( 'member' )
            // JPQL.g3:43:10: 'member'
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
            // JPQL.g3:44:7: ( 'min' )
            // JPQL.g3:44:7: 'min'
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
            // JPQL.g3:45:7: ( 'mod' )
            // JPQL.g3:45:7: 'mod'
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
            // JPQL.g3:46:7: ( 'new' )
            // JPQL.g3:46:7: 'new'
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
            // JPQL.g3:47:7: ( 'not' )
            // JPQL.g3:47:7: 'not'
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
            // JPQL.g3:48:8: ( 'null' )
            // JPQL.g3:48:8: 'null'
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
            // JPQL.g3:49:10: ( 'object' )
            // JPQL.g3:49:10: 'object'
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
            // JPQL.g3:50:6: ( 'of' )
            // JPQL.g3:50:6: 'of'
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
            // JPQL.g3:51:6: ( 'or' )
            // JPQL.g3:51:6: 'or'
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
            // JPQL.g3:52:9: ( 'order' )
            // JPQL.g3:52:9: 'order'
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
            // JPQL.g3:53:9: ( 'outer' )
            // JPQL.g3:53:9: 'outer'
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
            // JPQL.g3:54:10: ( 'select' )
            // JPQL.g3:54:10: 'select'
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
            // JPQL.g3:55:7: ( 'set' )
            // JPQL.g3:55:7: 'set'
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
            // JPQL.g3:56:8: ( 'size' )
            // JPQL.g3:56:8: 'size'
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
            // JPQL.g3:57:8: ( 'sqrt' )
            // JPQL.g3:57:8: 'sqrt'
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
            // JPQL.g3:58:8: ( 'some' )
            // JPQL.g3:58:8: 'some'
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
            // JPQL.g3:59:13: ( 'substring' )
            // JPQL.g3:59:13: 'substring'
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
            // JPQL.g3:60:7: ( 'sum' )
            // JPQL.g3:60:7: 'sum'
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
            // JPQL.g3:61:12: ( 'trailing' )
            // JPQL.g3:61:12: 'trailing'
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
            // JPQL.g3:62:8: ( 'trim' )
            // JPQL.g3:62:8: 'trim'
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
            // JPQL.g3:63:8: ( 'true' )
            // JPQL.g3:63:8: 'true'
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
            // JPQL.g3:64:11: ( 'unknown' )
            // JPQL.g3:64:11: 'unknown'
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
            // JPQL.g3:65:10: ( 'update' )
            // JPQL.g3:65:10: 'update'
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
            // JPQL.g3:66:9: ( 'upper' )
            // JPQL.g3:66:9: 'upper'
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
            // JPQL.g3:67:9: ( 'where' )
            // JPQL.g3:67:9: 'where'
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
            // JPQL.g3:1181:7: ( '.' )
            // JPQL.g3:1181:7: '.'
            {
            match('.'); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DOT

    // $ANTLR start HEX_DIGIT
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // JPQL.g3:1187:9: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // JPQL.g3:1187:9: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
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

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // JPQL.g3:1190:7: ( ( ' ' | '\\t' | '\\n' | '\\r' )+ )
            // JPQL.g3:1190:7: ( ' ' | '\\t' | '\\n' | '\\r' )+
            {
            // JPQL.g3:1190:7: ( ' ' | '\\t' | '\\n' | '\\r' )+
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
            // JPQL.g3:1194:7: ( '(' )
            // JPQL.g3:1194:7: '('
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
            // JPQL.g3:1198:7: ( ')' )
            // JPQL.g3:1198:7: ')'
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
            // JPQL.g3:1202:7: ( ',' )
            // JPQL.g3:1202:7: ','
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
            // JPQL.g3:1206:7: ( TEXTCHAR )
            // JPQL.g3:1206:7: TEXTCHAR
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
    
            // JPQL.g3:1211:7: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | c1= '\\u0080' .. '\\uFFFE' ) ( 'a' .. 'z' | '_' | '$' | '0' .. '9' | c2= '\\u0080' .. '\\uFFFE' )* )
            // JPQL.g3:1211:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | c1= '\\u0080' .. '\\uFFFE' ) ( 'a' .. 'z' | '_' | '$' | '0' .. '9' | c2= '\\u0080' .. '\\uFFFE' )*
            {
            // JPQL.g3:1211:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | c1= '\\u0080' .. '\\uFFFE' )
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
                    new NoViableAltException("1211:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | c1= '\\u0080' .. '\\uFFFE' )", 2, 0, input);
            
                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // JPQL.g3:1211:8: 'a' .. 'z'
                    {
                    matchRange('a','z'); 
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:1211:19: 'A' .. 'Z'
                    {
                    matchRange('A','Z'); 
                    
                    }
                    break;
                case 3 :
                    // JPQL.g3:1211:30: '_'
                    {
                    match('_'); 
                    
                    }
                    break;
                case 4 :
                    // JPQL.g3:1211:36: '$'
                    {
                    match('$'); 
                    
                    }
                    break;
                case 5 :
                    // JPQL.g3:1212:8: c1= '\\u0080' .. '\\uFFFE'
                    {
                    c1 = input.LA(1);
                    matchRange('\u0080','\uFFFE'); 
                    
                               if (!Character.isJavaIdentifierStart(c1)) {
                               throw new RuntimeException("NoViableAltForChar " + c1);
                                  // throw new NoViableAltForCharException(c1, getGrammarFilename(), getLine(), getCharPositionInLine());
                               }
                           
                    
                    }
                    break;
            
            }

            // JPQL.g3:1220:7: ( 'a' .. 'z' | '_' | '$' | '0' .. '9' | c2= '\\u0080' .. '\\uFFFE' )*
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
            	    // JPQL.g3:1220:8: 'a' .. 'z'
            	    {
            	    matchRange('a','z'); 
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g3:1220:19: '_'
            	    {
            	    match('_'); 
            	    
            	    }
            	    break;
            	case 3 :
            	    // JPQL.g3:1220:25: '$'
            	    {
            	    match('$'); 
            	    
            	    }
            	    break;
            	case 4 :
            	    // JPQL.g3:1220:31: '0' .. '9'
            	    {
            	    matchRange('0','9'); 
            	    
            	    }
            	    break;
            	case 5 :
            	    // JPQL.g3:1221:8: c2= '\\u0080' .. '\\uFFFE'
            	    {
            	    c2 = input.LA(1);
            	    matchRange('\u0080','\uFFFE'); 
            	    
            	               if (!Character.isJavaIdentifierPart(c2)) {
            	               throw new RuntimeException("NoViableAltForChar2 " + c2);
            	               //    throw new NoViableAltForCharException(c2, getGrammarFilename(), getLine(), getCharPositionInLine());
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

    // $ANTLR start NUM_INT
    public final void mNUM_INT() throws RecognitionException {
        try {
            int _type = NUM_INT;
             
                boolean isDecimal=false; 
    
            // JPQL.g3:1236:9: ( '.' ( ( '0' .. '9' )+ ( EXPONENT )? ( FLOAT_SUFFIX )? )? | ( '0' ( ( 'x' ) ( HEX_DIGIT )+ | ( '0' .. '7' )+ )? | ( '1' .. '9' ) ( '0' .. '9' )* ) ( ( 'l' ) | {...}? ( '.' ( '0' .. '9' )* ( EXPONENT )? ( FLOAT_SUFFIX )? | EXPONENT ( FLOAT_SUFFIX )? | FLOAT_SUFFIX ) )? )
            int alt19=2;
            int LA19_0 = input.LA(1);
            
            if ( (LA19_0=='.') ) {
                alt19=1;
            }
            else if ( ((LA19_0>='0' && LA19_0<='9')) ) {
                alt19=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1232:1: NUM_INT : ( '.' ( ( '0' .. '9' )+ ( EXPONENT )? ( FLOAT_SUFFIX )? )? | ( '0' ( ( 'x' ) ( HEX_DIGIT )+ | ( '0' .. '7' )+ )? | ( '1' .. '9' ) ( '0' .. '9' )* ) ( ( 'l' ) | {...}? ( '.' ( '0' .. '9' )* ( EXPONENT )? ( FLOAT_SUFFIX )? | EXPONENT ( FLOAT_SUFFIX )? | FLOAT_SUFFIX ) )? );", 19, 0, input);
            
                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // JPQL.g3:1236:9: '.' ( ( '0' .. '9' )+ ( EXPONENT )? ( FLOAT_SUFFIX )? )?
                    {
                    match('.'); 
                    // JPQL.g3:1237:17: ( ( '0' .. '9' )+ ( EXPONENT )? ( FLOAT_SUFFIX )? )?
                    int alt7=2;
                    int LA7_0 = input.LA(1);
                    
                    if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                        alt7=1;
                    }
                    switch (alt7) {
                        case 1 :
                            // JPQL.g3:1237:18: ( '0' .. '9' )+ ( EXPONENT )? ( FLOAT_SUFFIX )?
                            {
                            // JPQL.g3:1237:18: ( '0' .. '9' )+
                            int cnt4=0;
                            loop4:
                            do {
                                int alt4=2;
                                int LA4_0 = input.LA(1);
                                
                                if ( ((LA4_0>='0' && LA4_0<='9')) ) {
                                    alt4=1;
                                }
                                
                            
                                switch (alt4) {
                            	case 1 :
                            	    // JPQL.g3:1237:19: '0' .. '9'
                            	    {
                            	    matchRange('0','9'); 
                            	    
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

                            // JPQL.g3:1237:30: ( EXPONENT )?
                            int alt5=2;
                            int LA5_0 = input.LA(1);
                            
                            if ( (LA5_0=='E'||LA5_0=='e') ) {
                                alt5=1;
                            }
                            switch (alt5) {
                                case 1 :
                                    // JPQL.g3:1237:31: EXPONENT
                                    {
                                    mEXPONENT(); 
                                    
                                    }
                                    break;
                            
                            }

                            // JPQL.g3:1237:42: ( FLOAT_SUFFIX )?
                            int alt6=2;
                            int LA6_0 = input.LA(1);
                            
                            if ( (LA6_0=='d'||LA6_0=='f') ) {
                                alt6=1;
                            }
                            switch (alt6) {
                                case 1 :
                                    // JPQL.g3:1237:43: FLOAT_SUFFIX
                                    {
                                    mFLOAT_SUFFIX(); 
                                    
                                    }
                                    break;
                            
                            }

                            
                            }
                            break;
                    
                    }

                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:1239:9: ( '0' ( ( 'x' ) ( HEX_DIGIT )+ | ( '0' .. '7' )+ )? | ( '1' .. '9' ) ( '0' .. '9' )* ) ( ( 'l' ) | {...}? ( '.' ( '0' .. '9' )* ( EXPONENT )? ( FLOAT_SUFFIX )? | EXPONENT ( FLOAT_SUFFIX )? | FLOAT_SUFFIX ) )?
                    {
                    // JPQL.g3:1239:9: ( '0' ( ( 'x' ) ( HEX_DIGIT )+ | ( '0' .. '7' )+ )? | ( '1' .. '9' ) ( '0' .. '9' )* )
                    int alt12=2;
                    int LA12_0 = input.LA(1);
                    
                    if ( (LA12_0=='0') ) {
                        alt12=1;
                    }
                    else if ( ((LA12_0>='1' && LA12_0<='9')) ) {
                        alt12=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("1239:9: ( '0' ( ( 'x' ) ( HEX_DIGIT )+ | ( '0' .. '7' )+ )? | ( '1' .. '9' ) ( '0' .. '9' )* )", 12, 0, input);
                    
                        throw nvae;
                    }
                    switch (alt12) {
                        case 1 :
                            // JPQL.g3:1239:13: '0' ( ( 'x' ) ( HEX_DIGIT )+ | ( '0' .. '7' )+ )?
                            {
                            match('0'); 
                            isDecimal = true;
                            // JPQL.g3:1240:13: ( ( 'x' ) ( HEX_DIGIT )+ | ( '0' .. '7' )+ )?
                            int alt10=3;
                            int LA10_0 = input.LA(1);
                            
                            if ( (LA10_0=='x') ) {
                                alt10=1;
                            }
                            else if ( ((LA10_0>='0' && LA10_0<='7')) ) {
                                alt10=2;
                            }
                            switch (alt10) {
                                case 1 :
                                    // JPQL.g3:1240:17: ( 'x' ) ( HEX_DIGIT )+
                                    {
                                    // JPQL.g3:1240:17: ( 'x' )
                                    // JPQL.g3:1240:18: 'x'
                                    {
                                    match('x'); 
                                    
                                    }

                                    // JPQL.g3:1241:17: ( HEX_DIGIT )+
                                    int cnt8=0;
                                    loop8:
                                    do {
                                        int alt8=2;
                                        switch ( input.LA(1) ) {
                                        case 'E':
                                        case 'e':
                                            {
                                            int LA8_2 = input.LA(2);
                                            
                                            if ( ((LA8_2>='0' && LA8_2<='9')) ) {
                                                int LA8_6 = input.LA(3);
                                                
                                                if ( (!(isDecimal)) ) {
                                                    alt8=1;
                                                }
                                                
                                            
                                            }
                                            
                                            else {
                                                alt8=1;
                                            }
                                        
                                            }
                                            break;
                                        case 'f':
                                            {
                                            int LA8_3 = input.LA(2);
                                            
                                            if ( (!(isDecimal)) ) {
                                                alt8=1;
                                            }
                                            
                                        
                                            }
                                            break;
                                        case 'd':
                                            {
                                            int LA8_4 = input.LA(2);
                                            
                                            if ( (!(isDecimal)) ) {
                                                alt8=1;
                                            }
                                            
                                        
                                            }
                                            break;
                                        case '0':
                                        case '1':
                                        case '2':
                                        case '3':
                                        case '4':
                                        case '5':
                                        case '6':
                                        case '7':
                                        case '8':
                                        case '9':
                                        case 'A':
                                        case 'B':
                                        case 'C':
                                        case 'D':
                                        case 'F':
                                        case 'a':
                                        case 'b':
                                        case 'c':
                                            {
                                            alt8=1;
                                            }
                                            break;
                                        
                                        }
                                    
                                        switch (alt8) {
                                    	case 1 :
                                    	    // JPQL.g3:1250:21: HEX_DIGIT
                                    	    {
                                    	    mHEX_DIGIT(); 
                                    	    
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

                                    
                                    }
                                    break;
                                case 2 :
                                    // JPQL.g3:1252:17: ( '0' .. '7' )+
                                    {
                                    // JPQL.g3:1252:17: ( '0' .. '7' )+
                                    int cnt9=0;
                                    loop9:
                                    do {
                                        int alt9=2;
                                        int LA9_0 = input.LA(1);
                                        
                                        if ( ((LA9_0>='0' && LA9_0<='7')) ) {
                                            alt9=1;
                                        }
                                        
                                    
                                        switch (alt9) {
                                    	case 1 :
                                    	    // JPQL.g3:1252:18: '0' .. '7'
                                    	    {
                                    	    matchRange('0','7'); 
                                    	    
                                    	    }
                                    	    break;
                                    
                                    	default :
                                    	    if ( cnt9 >= 1 ) break loop9;
                                                EarlyExitException eee =
                                                    new EarlyExitException(9, input);
                                                throw eee;
                                        }
                                        cnt9++;
                                    } while (true);

                                    
                                    }
                                    break;
                            
                            }

                            
                            }
                            break;
                        case 2 :
                            // JPQL.g3:1254:13: ( '1' .. '9' ) ( '0' .. '9' )*
                            {
                            // JPQL.g3:1254:13: ( '1' .. '9' )
                            // JPQL.g3:1254:14: '1' .. '9'
                            {
                            matchRange('1','9'); 
                            
                            }

                            // JPQL.g3:1254:24: ( '0' .. '9' )*
                            loop11:
                            do {
                                int alt11=2;
                                int LA11_0 = input.LA(1);
                                
                                if ( ((LA11_0>='0' && LA11_0<='9')) ) {
                                    alt11=1;
                                }
                                
                            
                                switch (alt11) {
                            	case 1 :
                            	    // JPQL.g3:1254:25: '0' .. '9'
                            	    {
                            	    matchRange('0','9'); 
                            	    
                            	    }
                            	    break;
                            
                            	default :
                            	    break loop11;
                                }
                            } while (true);

                            isDecimal=true;
                            
                            }
                            break;
                    
                    }

                    // JPQL.g3:1256:9: ( ( 'l' ) | {...}? ( '.' ( '0' .. '9' )* ( EXPONENT )? ( FLOAT_SUFFIX )? | EXPONENT ( FLOAT_SUFFIX )? | FLOAT_SUFFIX ) )?
                    int alt18=3;
                    int LA18_0 = input.LA(1);
                    
                    if ( (LA18_0=='l') ) {
                        alt18=1;
                    }
                    else if ( (LA18_0=='.'||LA18_0=='E'||(LA18_0>='d' && LA18_0<='f')) ) {
                        alt18=2;
                    }
                    switch (alt18) {
                        case 1 :
                            // JPQL.g3:1256:13: ( 'l' )
                            {
                            // JPQL.g3:1256:13: ( 'l' )
                            // JPQL.g3:1256:14: 'l'
                            {
                            match('l'); 
                            
                            }

                            
                            }
                            break;
                        case 2 :
                            // JPQL.g3:1259:13: {...}? ( '.' ( '0' .. '9' )* ( EXPONENT )? ( FLOAT_SUFFIX )? | EXPONENT ( FLOAT_SUFFIX )? | FLOAT_SUFFIX )
                            {
                            if ( !(isDecimal) ) {
                                throw new FailedPredicateException(input, "NUM_INT", "isDecimal");
                            }
                            // JPQL.g3:1260:13: ( '.' ( '0' .. '9' )* ( EXPONENT )? ( FLOAT_SUFFIX )? | EXPONENT ( FLOAT_SUFFIX )? | FLOAT_SUFFIX )
                            int alt17=3;
                            switch ( input.LA(1) ) {
                            case '.':
                                {
                                alt17=1;
                                }
                                break;
                            case 'E':
                            case 'e':
                                {
                                alt17=2;
                                }
                                break;
                            case 'd':
                            case 'f':
                                {
                                alt17=3;
                                }
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("1260:13: ( '.' ( '0' .. '9' )* ( EXPONENT )? ( FLOAT_SUFFIX )? | EXPONENT ( FLOAT_SUFFIX )? | FLOAT_SUFFIX )", 17, 0, input);
                            
                                throw nvae;
                            }
                            
                            switch (alt17) {
                                case 1 :
                                    // JPQL.g3:1260:17: '.' ( '0' .. '9' )* ( EXPONENT )? ( FLOAT_SUFFIX )?
                                    {
                                    match('.'); 
                                    // JPQL.g3:1260:21: ( '0' .. '9' )*
                                    loop13:
                                    do {
                                        int alt13=2;
                                        int LA13_0 = input.LA(1);
                                        
                                        if ( ((LA13_0>='0' && LA13_0<='9')) ) {
                                            alt13=1;
                                        }
                                        
                                    
                                        switch (alt13) {
                                    	case 1 :
                                    	    // JPQL.g3:1260:22: '0' .. '9'
                                    	    {
                                    	    matchRange('0','9'); 
                                    	    
                                    	    }
                                    	    break;
                                    
                                    	default :
                                    	    break loop13;
                                        }
                                    } while (true);

                                    // JPQL.g3:1260:33: ( EXPONENT )?
                                    int alt14=2;
                                    int LA14_0 = input.LA(1);
                                    
                                    if ( (LA14_0=='E'||LA14_0=='e') ) {
                                        alt14=1;
                                    }
                                    switch (alt14) {
                                        case 1 :
                                            // JPQL.g3:1260:34: EXPONENT
                                            {
                                            mEXPONENT(); 
                                            
                                            }
                                            break;
                                    
                                    }

                                    // JPQL.g3:1260:45: ( FLOAT_SUFFIX )?
                                    int alt15=2;
                                    int LA15_0 = input.LA(1);
                                    
                                    if ( (LA15_0=='d'||LA15_0=='f') ) {
                                        alt15=1;
                                    }
                                    switch (alt15) {
                                        case 1 :
                                            // JPQL.g3:1260:46: FLOAT_SUFFIX
                                            {
                                            mFLOAT_SUFFIX(); 
                                            
                                            }
                                            break;
                                    
                                    }

                                    
                                    }
                                    break;
                                case 2 :
                                    // JPQL.g3:1261:17: EXPONENT ( FLOAT_SUFFIX )?
                                    {
                                    mEXPONENT(); 
                                    // JPQL.g3:1261:26: ( FLOAT_SUFFIX )?
                                    int alt16=2;
                                    int LA16_0 = input.LA(1);
                                    
                                    if ( (LA16_0=='d'||LA16_0=='f') ) {
                                        alt16=1;
                                    }
                                    switch (alt16) {
                                        case 1 :
                                            // JPQL.g3:1261:27: FLOAT_SUFFIX
                                            {
                                            mFLOAT_SUFFIX(); 
                                            
                                            }
                                            break;
                                    
                                    }

                                    
                                    }
                                    break;
                                case 3 :
                                    // JPQL.g3:1262:17: FLOAT_SUFFIX
                                    {
                                    mFLOAT_SUFFIX(); 
                                    
                                    }
                                    break;
                            
                            }

                            
                            }
                            break;
                    
                    }

                    
                    }
                    break;
            
            }
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NUM_INT

    // $ANTLR start EXPONENT
    public final void mEXPONENT() throws RecognitionException {
        try {
            // JPQL.g3:1271:9: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // JPQL.g3:1271:9: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // JPQL.g3:1271:21: ( '+' | '-' )?
            int alt20=2;
            int LA20_0 = input.LA(1);
            
            if ( (LA20_0=='+'||LA20_0=='-') ) {
                alt20=1;
            }
            switch (alt20) {
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

            // JPQL.g3:1271:32: ( '0' .. '9' )+
            int cnt21=0;
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);
                
                if ( ((LA21_0>='0' && LA21_0<='9')) ) {
                    alt21=1;
                }
                
            
                switch (alt21) {
            	case 1 :
            	    // JPQL.g3:1271:33: '0' .. '9'
            	    {
            	    matchRange('0','9'); 
            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt21 >= 1 ) break loop21;
                        EarlyExitException eee =
                            new EarlyExitException(21, input);
                        throw eee;
                }
                cnt21++;
            } while (true);

            
            }

        }
        finally {
        }
    }
    // $ANTLR end EXPONENT

    // $ANTLR start FLOAT_SUFFIX
    public final void mFLOAT_SUFFIX() throws RecognitionException {

        int tokenType = 0;
    
        try {
             tokenType = NUM_DOUBLE; 
            // JPQL.g3:1278:9: ( 'f' | 'd' )
            int alt22=2;
            int LA22_0 = input.LA(1);
            
            if ( (LA22_0=='f') ) {
                alt22=1;
            }
            else if ( (LA22_0=='d') ) {
                alt22=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1275:1: fragment FLOAT_SUFFIX returns [int tokenType] : ( 'f' | 'd' );", 22, 0, input);
            
                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // JPQL.g3:1278:9: 'f'
                    {
                    match('f'); 
                     tokenType = NUM_FLOAT; 
                    
                    }
                    break;
                case 2 :
                    // JPQL.g3:1279:9: 'd'
                    {
                    match('d'); 
                     tokenType = NUM_DOUBLE; 
                    
                    }
                    break;
            
            }
        }
        finally {
        }
    }
    // $ANTLR end FLOAT_SUFFIX

    // $ANTLR start EQUALS
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            // JPQL.g3:1283:7: ( '=' )
            // JPQL.g3:1283:7: '='
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
            // JPQL.g3:1287:7: ( '>' )
            // JPQL.g3:1287:7: '>'
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
            // JPQL.g3:1291:7: ( '>=' )
            // JPQL.g3:1291:7: '>='
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
            // JPQL.g3:1295:7: ( '<' )
            // JPQL.g3:1295:7: '<'
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
            // JPQL.g3:1299:7: ( '<=' )
            // JPQL.g3:1299:7: '<='
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
            // JPQL.g3:1303:7: ( '<>' )
            // JPQL.g3:1303:7: '<>'
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
            // JPQL.g3:1307:7: ( '*' )
            // JPQL.g3:1307:7: '*'
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
            // JPQL.g3:1311:7: ( '/' )
            // JPQL.g3:1311:7: '/'
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
            // JPQL.g3:1315:7: ( '+' )
            // JPQL.g3:1315:7: '+'
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
            // JPQL.g3:1319:7: ( '-' )
            // JPQL.g3:1319:7: '-'
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
            // JPQL.g3:1324:7: ( '?' ( '1' .. '9' ) ( '0' .. '9' )* )
            // JPQL.g3:1324:7: '?' ( '1' .. '9' ) ( '0' .. '9' )*
            {
            match('?'); 
            // JPQL.g3:1324:11: ( '1' .. '9' )
            // JPQL.g3:1324:12: '1' .. '9'
            {
            matchRange('1','9'); 
            
            }

            // JPQL.g3:1324:22: ( '0' .. '9' )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);
                
                if ( ((LA23_0>='0' && LA23_0<='9')) ) {
                    alt23=1;
                }
                
            
                switch (alt23) {
            	case 1 :
            	    // JPQL.g3:1324:23: '0' .. '9'
            	    {
            	    matchRange('0','9'); 
            	    
            	    }
            	    break;
            
            	default :
            	    break loop23;
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
            // JPQL.g3:1328:7: ( ':' TEXTCHAR )
            // JPQL.g3:1328:7: ':' TEXTCHAR
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
            // JPQL.g3:1334:7: ( '\"' (~ ( '\"' ) )* '\"' )
            // JPQL.g3:1334:7: '\"' (~ ( '\"' ) )* '\"'
            {
            match('\"'); 
            // JPQL.g3:1334:11: (~ ( '\"' ) )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);
                
                if ( ((LA24_0>='\u0000' && LA24_0<='!')||(LA24_0>='#' && LA24_0<='\uFFFE')) ) {
                    alt24=1;
                }
                
            
                switch (alt24) {
            	case 1 :
            	    // JPQL.g3:1334:12: ~ ( '\"' )
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
            	    break loop24;
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
            // JPQL.g3:1338:7: ( '\\'' (~ ( '\\'' ) | ( '\\'\\'' ) )* '\\'' )
            // JPQL.g3:1338:7: '\\'' (~ ( '\\'' ) | ( '\\'\\'' ) )* '\\''
            {
            match('\''); 
            // JPQL.g3:1338:12: (~ ( '\\'' ) | ( '\\'\\'' ) )*
            loop25:
            do {
                int alt25=3;
                int LA25_0 = input.LA(1);
                
                if ( (LA25_0=='\'') ) {
                    int LA25_1 = input.LA(2);
                    
                    if ( (LA25_1=='\'') ) {
                        alt25=2;
                    }
                    
                
                }
                else if ( ((LA25_0>='\u0000' && LA25_0<='&')||(LA25_0>='(' && LA25_0<='\uFFFE')) ) {
                    alt25=1;
                }
                
            
                switch (alt25) {
            	case 1 :
            	    // JPQL.g3:1338:13: ~ ( '\\'' )
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
            	    // JPQL.g3:1338:24: ( '\\'\\'' )
            	    {
            	    // JPQL.g3:1338:24: ( '\\'\\'' )
            	    // JPQL.g3:1338:25: '\\'\\''
            	    {
            	    match("\'\'"); 

            	    
            	    }

            	    
            	    }
            	    break;
            
            	default :
            	    break loop25;
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
        // JPQL.g3:1:10: ( ABS | ALL | AND | ANY | AS | ASC | AVG | BETWEEN | BOTH | BY | CONCAT | COUNT | CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | DESC | DELETE | DISTINCT | EMPTY | ESCAPE | EXISTS | FALSE | FETCH | FROM | GROUP | HAVING | IN | INNER | IS | JOIN | LEADING | LEFT | LENGTH | LIKE | LOCATE | LOWER | MAX | MEMBER | MIN | MOD | NEW | NOT | NULL | OBJECT | OF | OR | ORDER | OUTER | SELECT | SET | SIZE | SQRT | SOME | SUBSTRING | SUM | TRAILING | TRIM | TRUE | UNKNOWN | UPDATE | UPPER | WHERE | DOT | WS | LEFT_ROUND_BRACKET | RIGHT_ROUND_BRACKET | COMMA | IDENT | NUM_INT | EQUALS | GREATER_THAN | GREATER_THAN_EQUAL_TO | LESS_THAN | LESS_THAN_EQUAL_TO | NOT_EQUAL_TO | MULTIPLY | DIVIDE | PLUS | MINUS | POSITIONAL_PARAM | NAMED_PARAM | STRING_LITERAL_DOUBLE_QUOTED | STRING_LITERAL_SINGLE_QUOTED )
        int alt26=83;
        int LA26_0 = input.LA(1);
        
        if ( (LA26_0=='a') ) {
            switch ( input.LA(2) ) {
            case 'l':
                {
                int LA26_37 = input.LA(3);
                
                if ( (LA26_37=='l') ) {
                    int LA26_89 = input.LA(4);
                    
                    if ( (LA26_89=='$'||(LA26_89>='0' && LA26_89<='9')||LA26_89=='_'||(LA26_89>='a' && LA26_89<='z')||(LA26_89>='\u0080' && LA26_89<='\uFFFE')) ) {
                        alt26=68;
                    }
                    else {
                        alt26=2;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 'n':
                {
                switch ( input.LA(3) ) {
                case 'd':
                    {
                    int LA26_90 = input.LA(4);
                    
                    if ( (LA26_90=='$'||(LA26_90>='0' && LA26_90<='9')||LA26_90=='_'||(LA26_90>='a' && LA26_90<='z')||(LA26_90>='\u0080' && LA26_90<='\uFFFE')) ) {
                        alt26=68;
                    }
                    else {
                        alt26=3;}
                    }
                    break;
                case 'y':
                    {
                    int LA26_91 = input.LA(4);
                    
                    if ( (LA26_91=='$'||(LA26_91>='0' && LA26_91<='9')||LA26_91=='_'||(LA26_91>='a' && LA26_91<='z')||(LA26_91>='\u0080' && LA26_91<='\uFFFE')) ) {
                        alt26=68;
                    }
                    else {
                        alt26=4;}
                    }
                    break;
                default:
                    alt26=68;}
            
                }
                break;
            case 'b':
                {
                int LA26_39 = input.LA(3);
                
                if ( (LA26_39=='s') ) {
                    int LA26_92 = input.LA(4);
                    
                    if ( (LA26_92=='$'||(LA26_92>='0' && LA26_92<='9')||LA26_92=='_'||(LA26_92>='a' && LA26_92<='z')||(LA26_92>='\u0080' && LA26_92<='\uFFFE')) ) {
                        alt26=68;
                    }
                    else {
                        alt26=1;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 's':
                {
                int LA26_40 = input.LA(3);
                
                if ( (LA26_40=='c') ) {
                    int LA26_93 = input.LA(4);
                    
                    if ( (LA26_93=='$'||(LA26_93>='0' && LA26_93<='9')||LA26_93=='_'||(LA26_93>='a' && LA26_93<='z')||(LA26_93>='\u0080' && LA26_93<='\uFFFE')) ) {
                        alt26=68;
                    }
                    else {
                        alt26=6;}
                }
                else if ( (LA26_40=='$'||(LA26_40>='0' && LA26_40<='9')||LA26_40=='_'||(LA26_40>='a' && LA26_40<='b')||(LA26_40>='d' && LA26_40<='z')||(LA26_40>='\u0080' && LA26_40<='\uFFFE')) ) {
                    alt26=68;
                }
                else {
                    alt26=5;}
                }
                break;
            case 'v':
                {
                int LA26_41 = input.LA(3);
                
                if ( (LA26_41=='g') ) {
                    int LA26_95 = input.LA(4);
                    
                    if ( (LA26_95=='$'||(LA26_95>='0' && LA26_95<='9')||LA26_95=='_'||(LA26_95>='a' && LA26_95<='z')||(LA26_95>='\u0080' && LA26_95<='\uFFFE')) ) {
                        alt26=68;
                    }
                    else {
                        alt26=7;}
                }
                else {
                    alt26=68;}
                }
                break;
            default:
                alt26=68;}
        
        }
        else if ( (LA26_0=='b') ) {
            switch ( input.LA(2) ) {
            case 'o':
                {
                int LA26_42 = input.LA(3);
                
                if ( (LA26_42=='t') ) {
                    int LA26_96 = input.LA(4);
                    
                    if ( (LA26_96=='h') ) {
                        int LA26_155 = input.LA(5);
                        
                        if ( (LA26_155=='$'||(LA26_155>='0' && LA26_155<='9')||LA26_155=='_'||(LA26_155>='a' && LA26_155<='z')||(LA26_155>='\u0080' && LA26_155<='\uFFFE')) ) {
                            alt26=68;
                        }
                        else {
                            alt26=9;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 'y':
                {
                int LA26_43 = input.LA(3);
                
                if ( (LA26_43=='$'||(LA26_43>='0' && LA26_43<='9')||LA26_43=='_'||(LA26_43>='a' && LA26_43<='z')||(LA26_43>='\u0080' && LA26_43<='\uFFFE')) ) {
                    alt26=68;
                }
                else {
                    alt26=10;}
                }
                break;
            case 'e':
                {
                int LA26_44 = input.LA(3);
                
                if ( (LA26_44=='t') ) {
                    int LA26_98 = input.LA(4);
                    
                    if ( (LA26_98=='w') ) {
                        int LA26_156 = input.LA(5);
                        
                        if ( (LA26_156=='e') ) {
                            int LA26_204 = input.LA(6);
                            
                            if ( (LA26_204=='e') ) {
                                int LA26_244 = input.LA(7);
                                
                                if ( (LA26_244=='n') ) {
                                    int LA26_273 = input.LA(8);
                                    
                                    if ( (LA26_273=='$'||(LA26_273>='0' && LA26_273<='9')||LA26_273=='_'||(LA26_273>='a' && LA26_273<='z')||(LA26_273>='\u0080' && LA26_273<='\uFFFE')) ) {
                                        alt26=68;
                                    }
                                    else {
                                        alt26=8;}
                                }
                                else {
                                    alt26=68;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            default:
                alt26=68;}
        
        }
        else if ( (LA26_0=='c') ) {
            switch ( input.LA(2) ) {
            case 'u':
                {
                int LA26_45 = input.LA(3);
                
                if ( (LA26_45=='r') ) {
                    int LA26_99 = input.LA(4);
                    
                    if ( (LA26_99=='r') ) {
                        int LA26_157 = input.LA(5);
                        
                        if ( (LA26_157=='e') ) {
                            int LA26_205 = input.LA(6);
                            
                            if ( (LA26_205=='n') ) {
                                int LA26_245 = input.LA(7);
                                
                                if ( (LA26_245=='t') ) {
                                    int LA26_274 = input.LA(8);
                                    
                                    if ( (LA26_274=='_') ) {
                                        switch ( input.LA(9) ) {
                                        case 't':
                                            {
                                            int LA26_298 = input.LA(10);
                                            
                                            if ( (LA26_298=='i') ) {
                                                int LA26_303 = input.LA(11);
                                                
                                                if ( (LA26_303=='m') ) {
                                                    int LA26_306 = input.LA(12);
                                                    
                                                    if ( (LA26_306=='e') ) {
                                                        int LA26_308 = input.LA(13);
                                                        
                                                        if ( (LA26_308=='s') ) {
                                                            int LA26_310 = input.LA(14);
                                                            
                                                            if ( (LA26_310=='t') ) {
                                                                int LA26_313 = input.LA(15);
                                                                
                                                                if ( (LA26_313=='a') ) {
                                                                    int LA26_314 = input.LA(16);
                                                                    
                                                                    if ( (LA26_314=='m') ) {
                                                                        int LA26_315 = input.LA(17);
                                                                        
                                                                        if ( (LA26_315=='p') ) {
                                                                            int LA26_316 = input.LA(18);
                                                                            
                                                                            if ( (LA26_316=='$'||(LA26_316>='0' && LA26_316<='9')||LA26_316=='_'||(LA26_316>='a' && LA26_316<='z')||(LA26_316>='\u0080' && LA26_316<='\uFFFE')) ) {
                                                                                alt26=68;
                                                                            }
                                                                            else {
                                                                                alt26=15;}
                                                                        }
                                                                        else {
                                                                            alt26=68;}
                                                                    }
                                                                    else {
                                                                        alt26=68;}
                                                                }
                                                                else {
                                                                    alt26=68;}
                                                            }
                                                            else {
                                                                alt26=68;}
                                                        }
                                                        else if ( (LA26_308=='$'||(LA26_308>='0' && LA26_308<='9')||LA26_308=='_'||(LA26_308>='a' && LA26_308<='r')||(LA26_308>='t' && LA26_308<='z')||(LA26_308>='\u0080' && LA26_308<='\uFFFE')) ) {
                                                            alt26=68;
                                                        }
                                                        else {
                                                            alt26=14;}
                                                    }
                                                    else {
                                                        alt26=68;}
                                                }
                                                else {
                                                    alt26=68;}
                                            }
                                            else {
                                                alt26=68;}
                                            }
                                            break;
                                        case 'd':
                                            {
                                            int LA26_299 = input.LA(10);
                                            
                                            if ( (LA26_299=='a') ) {
                                                int LA26_304 = input.LA(11);
                                                
                                                if ( (LA26_304=='t') ) {
                                                    int LA26_307 = input.LA(12);
                                                    
                                                    if ( (LA26_307=='e') ) {
                                                        int LA26_309 = input.LA(13);
                                                        
                                                        if ( (LA26_309=='$'||(LA26_309>='0' && LA26_309<='9')||LA26_309=='_'||(LA26_309>='a' && LA26_309<='z')||(LA26_309>='\u0080' && LA26_309<='\uFFFE')) ) {
                                                            alt26=68;
                                                        }
                                                        else {
                                                            alt26=13;}
                                                    }
                                                    else {
                                                        alt26=68;}
                                                }
                                                else {
                                                    alt26=68;}
                                            }
                                            else {
                                                alt26=68;}
                                            }
                                            break;
                                        default:
                                            alt26=68;}
                                    
                                    }
                                    else {
                                        alt26=68;}
                                }
                                else {
                                    alt26=68;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 'o':
                {
                switch ( input.LA(3) ) {
                case 'n':
                    {
                    int LA26_100 = input.LA(4);
                    
                    if ( (LA26_100=='c') ) {
                        int LA26_158 = input.LA(5);
                        
                        if ( (LA26_158=='a') ) {
                            int LA26_206 = input.LA(6);
                            
                            if ( (LA26_206=='t') ) {
                                int LA26_246 = input.LA(7);
                                
                                if ( (LA26_246=='$'||(LA26_246>='0' && LA26_246<='9')||LA26_246=='_'||(LA26_246>='a' && LA26_246<='z')||(LA26_246>='\u0080' && LA26_246<='\uFFFE')) ) {
                                    alt26=68;
                                }
                                else {
                                    alt26=11;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                    }
                    break;
                case 'u':
                    {
                    int LA26_101 = input.LA(4);
                    
                    if ( (LA26_101=='n') ) {
                        int LA26_159 = input.LA(5);
                        
                        if ( (LA26_159=='t') ) {
                            int LA26_207 = input.LA(6);
                            
                            if ( (LA26_207=='$'||(LA26_207>='0' && LA26_207<='9')||LA26_207=='_'||(LA26_207>='a' && LA26_207<='z')||(LA26_207>='\u0080' && LA26_207<='\uFFFE')) ) {
                                alt26=68;
                            }
                            else {
                                alt26=12;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                    }
                    break;
                default:
                    alt26=68;}
            
                }
                break;
            default:
                alt26=68;}
        
        }
        else if ( (LA26_0=='d') ) {
            switch ( input.LA(2) ) {
            case 'i':
                {
                int LA26_47 = input.LA(3);
                
                if ( (LA26_47=='s') ) {
                    int LA26_102 = input.LA(4);
                    
                    if ( (LA26_102=='t') ) {
                        int LA26_160 = input.LA(5);
                        
                        if ( (LA26_160=='i') ) {
                            int LA26_208 = input.LA(6);
                            
                            if ( (LA26_208=='n') ) {
                                int LA26_248 = input.LA(7);
                                
                                if ( (LA26_248=='c') ) {
                                    int LA26_276 = input.LA(8);
                                    
                                    if ( (LA26_276=='t') ) {
                                        int LA26_293 = input.LA(9);
                                        
                                        if ( (LA26_293=='$'||(LA26_293>='0' && LA26_293<='9')||LA26_293=='_'||(LA26_293>='a' && LA26_293<='z')||(LA26_293>='\u0080' && LA26_293<='\uFFFE')) ) {
                                            alt26=68;
                                        }
                                        else {
                                            alt26=18;}
                                    }
                                    else {
                                        alt26=68;}
                                }
                                else {
                                    alt26=68;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 'e':
                {
                switch ( input.LA(3) ) {
                case 'l':
                    {
                    int LA26_103 = input.LA(4);
                    
                    if ( (LA26_103=='e') ) {
                        int LA26_161 = input.LA(5);
                        
                        if ( (LA26_161=='t') ) {
                            int LA26_209 = input.LA(6);
                            
                            if ( (LA26_209=='e') ) {
                                int LA26_249 = input.LA(7);
                                
                                if ( (LA26_249=='$'||(LA26_249>='0' && LA26_249<='9')||LA26_249=='_'||(LA26_249>='a' && LA26_249<='z')||(LA26_249>='\u0080' && LA26_249<='\uFFFE')) ) {
                                    alt26=68;
                                }
                                else {
                                    alt26=17;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                    }
                    break;
                case 's':
                    {
                    int LA26_104 = input.LA(4);
                    
                    if ( (LA26_104=='c') ) {
                        int LA26_162 = input.LA(5);
                        
                        if ( (LA26_162=='$'||(LA26_162>='0' && LA26_162<='9')||LA26_162=='_'||(LA26_162>='a' && LA26_162<='z')||(LA26_162>='\u0080' && LA26_162<='\uFFFE')) ) {
                            alt26=68;
                        }
                        else {
                            alt26=16;}
                    }
                    else {
                        alt26=68;}
                    }
                    break;
                default:
                    alt26=68;}
            
                }
                break;
            default:
                alt26=68;}
        
        }
        else if ( (LA26_0=='e') ) {
            switch ( input.LA(2) ) {
            case 'x':
                {
                int LA26_49 = input.LA(3);
                
                if ( (LA26_49=='i') ) {
                    int LA26_105 = input.LA(4);
                    
                    if ( (LA26_105=='s') ) {
                        int LA26_163 = input.LA(5);
                        
                        if ( (LA26_163=='t') ) {
                            int LA26_211 = input.LA(6);
                            
                            if ( (LA26_211=='s') ) {
                                int LA26_250 = input.LA(7);
                                
                                if ( (LA26_250=='$'||(LA26_250>='0' && LA26_250<='9')||LA26_250=='_'||(LA26_250>='a' && LA26_250<='z')||(LA26_250>='\u0080' && LA26_250<='\uFFFE')) ) {
                                    alt26=68;
                                }
                                else {
                                    alt26=21;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 's':
                {
                int LA26_50 = input.LA(3);
                
                if ( (LA26_50=='c') ) {
                    int LA26_106 = input.LA(4);
                    
                    if ( (LA26_106=='a') ) {
                        int LA26_164 = input.LA(5);
                        
                        if ( (LA26_164=='p') ) {
                            int LA26_212 = input.LA(6);
                            
                            if ( (LA26_212=='e') ) {
                                int LA26_251 = input.LA(7);
                                
                                if ( (LA26_251=='$'||(LA26_251>='0' && LA26_251<='9')||LA26_251=='_'||(LA26_251>='a' && LA26_251<='z')||(LA26_251>='\u0080' && LA26_251<='\uFFFE')) ) {
                                    alt26=68;
                                }
                                else {
                                    alt26=20;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 'm':
                {
                int LA26_51 = input.LA(3);
                
                if ( (LA26_51=='p') ) {
                    int LA26_107 = input.LA(4);
                    
                    if ( (LA26_107=='t') ) {
                        int LA26_165 = input.LA(5);
                        
                        if ( (LA26_165=='y') ) {
                            int LA26_213 = input.LA(6);
                            
                            if ( (LA26_213=='$'||(LA26_213>='0' && LA26_213<='9')||LA26_213=='_'||(LA26_213>='a' && LA26_213<='z')||(LA26_213>='\u0080' && LA26_213<='\uFFFE')) ) {
                                alt26=68;
                            }
                            else {
                                alt26=19;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            default:
                alt26=68;}
        
        }
        else if ( (LA26_0=='f') ) {
            switch ( input.LA(2) ) {
            case 'r':
                {
                int LA26_52 = input.LA(3);
                
                if ( (LA26_52=='o') ) {
                    int LA26_108 = input.LA(4);
                    
                    if ( (LA26_108=='m') ) {
                        int LA26_166 = input.LA(5);
                        
                        if ( (LA26_166=='$'||(LA26_166>='0' && LA26_166<='9')||LA26_166=='_'||(LA26_166>='a' && LA26_166<='z')||(LA26_166>='\u0080' && LA26_166<='\uFFFE')) ) {
                            alt26=68;
                        }
                        else {
                            alt26=24;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 'a':
                {
                int LA26_53 = input.LA(3);
                
                if ( (LA26_53=='l') ) {
                    int LA26_109 = input.LA(4);
                    
                    if ( (LA26_109=='s') ) {
                        int LA26_167 = input.LA(5);
                        
                        if ( (LA26_167=='e') ) {
                            int LA26_215 = input.LA(6);
                            
                            if ( (LA26_215=='$'||(LA26_215>='0' && LA26_215<='9')||LA26_215=='_'||(LA26_215>='a' && LA26_215<='z')||(LA26_215>='\u0080' && LA26_215<='\uFFFE')) ) {
                                alt26=68;
                            }
                            else {
                                alt26=22;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 'e':
                {
                int LA26_54 = input.LA(3);
                
                if ( (LA26_54=='t') ) {
                    int LA26_110 = input.LA(4);
                    
                    if ( (LA26_110=='c') ) {
                        int LA26_168 = input.LA(5);
                        
                        if ( (LA26_168=='h') ) {
                            int LA26_216 = input.LA(6);
                            
                            if ( (LA26_216=='$'||(LA26_216>='0' && LA26_216<='9')||LA26_216=='_'||(LA26_216>='a' && LA26_216<='z')||(LA26_216>='\u0080' && LA26_216<='\uFFFE')) ) {
                                alt26=68;
                            }
                            else {
                                alt26=23;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            default:
                alt26=68;}
        
        }
        else if ( (LA26_0=='g') ) {
            int LA26_7 = input.LA(2);
            
            if ( (LA26_7=='r') ) {
                int LA26_55 = input.LA(3);
                
                if ( (LA26_55=='o') ) {
                    int LA26_111 = input.LA(4);
                    
                    if ( (LA26_111=='u') ) {
                        int LA26_169 = input.LA(5);
                        
                        if ( (LA26_169=='p') ) {
                            int LA26_217 = input.LA(6);
                            
                            if ( (LA26_217=='$'||(LA26_217>='0' && LA26_217<='9')||LA26_217=='_'||(LA26_217>='a' && LA26_217<='z')||(LA26_217>='\u0080' && LA26_217<='\uFFFE')) ) {
                                alt26=68;
                            }
                            else {
                                alt26=25;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
            }
            else {
                alt26=68;}
        }
        else if ( (LA26_0=='h') ) {
            int LA26_8 = input.LA(2);
            
            if ( (LA26_8=='a') ) {
                int LA26_56 = input.LA(3);
                
                if ( (LA26_56=='v') ) {
                    int LA26_112 = input.LA(4);
                    
                    if ( (LA26_112=='i') ) {
                        int LA26_170 = input.LA(5);
                        
                        if ( (LA26_170=='n') ) {
                            int LA26_218 = input.LA(6);
                            
                            if ( (LA26_218=='g') ) {
                                int LA26_256 = input.LA(7);
                                
                                if ( (LA26_256=='$'||(LA26_256>='0' && LA26_256<='9')||LA26_256=='_'||(LA26_256>='a' && LA26_256<='z')||(LA26_256>='\u0080' && LA26_256<='\uFFFE')) ) {
                                    alt26=68;
                                }
                                else {
                                    alt26=26;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
            }
            else {
                alt26=68;}
        }
        else if ( (LA26_0=='i') ) {
            switch ( input.LA(2) ) {
            case 'n':
                {
                int LA26_57 = input.LA(3);
                
                if ( (LA26_57=='n') ) {
                    int LA26_113 = input.LA(4);
                    
                    if ( (LA26_113=='e') ) {
                        int LA26_171 = input.LA(5);
                        
                        if ( (LA26_171=='r') ) {
                            int LA26_219 = input.LA(6);
                            
                            if ( (LA26_219=='$'||(LA26_219>='0' && LA26_219<='9')||LA26_219=='_'||(LA26_219>='a' && LA26_219<='z')||(LA26_219>='\u0080' && LA26_219<='\uFFFE')) ) {
                                alt26=68;
                            }
                            else {
                                alt26=28;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else if ( (LA26_57=='$'||(LA26_57>='0' && LA26_57<='9')||LA26_57=='_'||(LA26_57>='a' && LA26_57<='m')||(LA26_57>='o' && LA26_57<='z')||(LA26_57>='\u0080' && LA26_57<='\uFFFE')) ) {
                    alt26=68;
                }
                else {
                    alt26=27;}
                }
                break;
            case 's':
                {
                int LA26_58 = input.LA(3);
                
                if ( (LA26_58=='$'||(LA26_58>='0' && LA26_58<='9')||LA26_58=='_'||(LA26_58>='a' && LA26_58<='z')||(LA26_58>='\u0080' && LA26_58<='\uFFFE')) ) {
                    alt26=68;
                }
                else {
                    alt26=29;}
                }
                break;
            default:
                alt26=68;}
        
        }
        else if ( (LA26_0=='j') ) {
            int LA26_10 = input.LA(2);
            
            if ( (LA26_10=='o') ) {
                int LA26_59 = input.LA(3);
                
                if ( (LA26_59=='i') ) {
                    int LA26_116 = input.LA(4);
                    
                    if ( (LA26_116=='n') ) {
                        int LA26_172 = input.LA(5);
                        
                        if ( (LA26_172=='$'||(LA26_172>='0' && LA26_172<='9')||LA26_172=='_'||(LA26_172>='a' && LA26_172<='z')||(LA26_172>='\u0080' && LA26_172<='\uFFFE')) ) {
                            alt26=68;
                        }
                        else {
                            alt26=30;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
            }
            else {
                alt26=68;}
        }
        else if ( (LA26_0=='l') ) {
            switch ( input.LA(2) ) {
            case 'o':
                {
                switch ( input.LA(3) ) {
                case 'c':
                    {
                    int LA26_117 = input.LA(4);
                    
                    if ( (LA26_117=='a') ) {
                        int LA26_173 = input.LA(5);
                        
                        if ( (LA26_173=='t') ) {
                            int LA26_221 = input.LA(6);
                            
                            if ( (LA26_221=='e') ) {
                                int LA26_258 = input.LA(7);
                                
                                if ( (LA26_258=='$'||(LA26_258>='0' && LA26_258<='9')||LA26_258=='_'||(LA26_258>='a' && LA26_258<='z')||(LA26_258>='\u0080' && LA26_258<='\uFFFE')) ) {
                                    alt26=68;
                                }
                                else {
                                    alt26=35;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                    }
                    break;
                case 'w':
                    {
                    int LA26_118 = input.LA(4);
                    
                    if ( (LA26_118=='e') ) {
                        int LA26_174 = input.LA(5);
                        
                        if ( (LA26_174=='r') ) {
                            int LA26_222 = input.LA(6);
                            
                            if ( (LA26_222=='$'||(LA26_222>='0' && LA26_222<='9')||LA26_222=='_'||(LA26_222>='a' && LA26_222<='z')||(LA26_222>='\u0080' && LA26_222<='\uFFFE')) ) {
                                alt26=68;
                            }
                            else {
                                alt26=36;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                    }
                    break;
                default:
                    alt26=68;}
            
                }
                break;
            case 'e':
                {
                switch ( input.LA(3) ) {
                case 'n':
                    {
                    int LA26_119 = input.LA(4);
                    
                    if ( (LA26_119=='g') ) {
                        int LA26_175 = input.LA(5);
                        
                        if ( (LA26_175=='t') ) {
                            int LA26_223 = input.LA(6);
                            
                            if ( (LA26_223=='h') ) {
                                int LA26_260 = input.LA(7);
                                
                                if ( (LA26_260=='$'||(LA26_260>='0' && LA26_260<='9')||LA26_260=='_'||(LA26_260>='a' && LA26_260<='z')||(LA26_260>='\u0080' && LA26_260<='\uFFFE')) ) {
                                    alt26=68;
                                }
                                else {
                                    alt26=33;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                    }
                    break;
                case 'a':
                    {
                    int LA26_120 = input.LA(4);
                    
                    if ( (LA26_120=='d') ) {
                        int LA26_176 = input.LA(5);
                        
                        if ( (LA26_176=='i') ) {
                            int LA26_224 = input.LA(6);
                            
                            if ( (LA26_224=='n') ) {
                                int LA26_261 = input.LA(7);
                                
                                if ( (LA26_261=='g') ) {
                                    int LA26_283 = input.LA(8);
                                    
                                    if ( (LA26_283=='$'||(LA26_283>='0' && LA26_283<='9')||LA26_283=='_'||(LA26_283>='a' && LA26_283<='z')||(LA26_283>='\u0080' && LA26_283<='\uFFFE')) ) {
                                        alt26=68;
                                    }
                                    else {
                                        alt26=31;}
                                }
                                else {
                                    alt26=68;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                    }
                    break;
                case 'f':
                    {
                    int LA26_121 = input.LA(4);
                    
                    if ( (LA26_121=='t') ) {
                        int LA26_177 = input.LA(5);
                        
                        if ( (LA26_177=='$'||(LA26_177>='0' && LA26_177<='9')||LA26_177=='_'||(LA26_177>='a' && LA26_177<='z')||(LA26_177>='\u0080' && LA26_177<='\uFFFE')) ) {
                            alt26=68;
                        }
                        else {
                            alt26=32;}
                    }
                    else {
                        alt26=68;}
                    }
                    break;
                default:
                    alt26=68;}
            
                }
                break;
            case 'i':
                {
                int LA26_62 = input.LA(3);
                
                if ( (LA26_62=='k') ) {
                    int LA26_122 = input.LA(4);
                    
                    if ( (LA26_122=='e') ) {
                        int LA26_178 = input.LA(5);
                        
                        if ( (LA26_178=='$'||(LA26_178>='0' && LA26_178<='9')||LA26_178=='_'||(LA26_178>='a' && LA26_178<='z')||(LA26_178>='\u0080' && LA26_178<='\uFFFE')) ) {
                            alt26=68;
                        }
                        else {
                            alt26=34;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            default:
                alt26=68;}
        
        }
        else if ( (LA26_0=='m') ) {
            switch ( input.LA(2) ) {
            case 'o':
                {
                int LA26_63 = input.LA(3);
                
                if ( (LA26_63=='d') ) {
                    int LA26_123 = input.LA(4);
                    
                    if ( (LA26_123=='$'||(LA26_123>='0' && LA26_123<='9')||LA26_123=='_'||(LA26_123>='a' && LA26_123<='z')||(LA26_123>='\u0080' && LA26_123<='\uFFFE')) ) {
                        alt26=68;
                    }
                    else {
                        alt26=40;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 'i':
                {
                int LA26_64 = input.LA(3);
                
                if ( (LA26_64=='n') ) {
                    int LA26_124 = input.LA(4);
                    
                    if ( (LA26_124=='$'||(LA26_124>='0' && LA26_124<='9')||LA26_124=='_'||(LA26_124>='a' && LA26_124<='z')||(LA26_124>='\u0080' && LA26_124<='\uFFFE')) ) {
                        alt26=68;
                    }
                    else {
                        alt26=39;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 'a':
                {
                int LA26_65 = input.LA(3);
                
                if ( (LA26_65=='x') ) {
                    int LA26_125 = input.LA(4);
                    
                    if ( (LA26_125=='$'||(LA26_125>='0' && LA26_125<='9')||LA26_125=='_'||(LA26_125>='a' && LA26_125<='z')||(LA26_125>='\u0080' && LA26_125<='\uFFFE')) ) {
                        alt26=68;
                    }
                    else {
                        alt26=37;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 'e':
                {
                int LA26_66 = input.LA(3);
                
                if ( (LA26_66=='m') ) {
                    int LA26_126 = input.LA(4);
                    
                    if ( (LA26_126=='b') ) {
                        int LA26_182 = input.LA(5);
                        
                        if ( (LA26_182=='e') ) {
                            int LA26_227 = input.LA(6);
                            
                            if ( (LA26_227=='r') ) {
                                int LA26_262 = input.LA(7);
                                
                                if ( (LA26_262=='$'||(LA26_262>='0' && LA26_262<='9')||LA26_262=='_'||(LA26_262>='a' && LA26_262<='z')||(LA26_262>='\u0080' && LA26_262<='\uFFFE')) ) {
                                    alt26=68;
                                }
                                else {
                                    alt26=38;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            default:
                alt26=68;}
        
        }
        else if ( (LA26_0=='n') ) {
            switch ( input.LA(2) ) {
            case 'u':
                {
                int LA26_67 = input.LA(3);
                
                if ( (LA26_67=='l') ) {
                    int LA26_127 = input.LA(4);
                    
                    if ( (LA26_127=='l') ) {
                        int LA26_183 = input.LA(5);
                        
                        if ( (LA26_183=='$'||(LA26_183>='0' && LA26_183<='9')||LA26_183=='_'||(LA26_183>='a' && LA26_183<='z')||(LA26_183>='\u0080' && LA26_183<='\uFFFE')) ) {
                            alt26=68;
                        }
                        else {
                            alt26=43;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 'o':
                {
                int LA26_68 = input.LA(3);
                
                if ( (LA26_68=='t') ) {
                    int LA26_128 = input.LA(4);
                    
                    if ( (LA26_128=='$'||(LA26_128>='0' && LA26_128<='9')||LA26_128=='_'||(LA26_128>='a' && LA26_128<='z')||(LA26_128>='\u0080' && LA26_128<='\uFFFE')) ) {
                        alt26=68;
                    }
                    else {
                        alt26=42;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 'e':
                {
                int LA26_69 = input.LA(3);
                
                if ( (LA26_69=='w') ) {
                    int LA26_129 = input.LA(4);
                    
                    if ( (LA26_129=='$'||(LA26_129>='0' && LA26_129<='9')||LA26_129=='_'||(LA26_129>='a' && LA26_129<='z')||(LA26_129>='\u0080' && LA26_129<='\uFFFE')) ) {
                        alt26=68;
                    }
                    else {
                        alt26=41;}
                }
                else {
                    alt26=68;}
                }
                break;
            default:
                alt26=68;}
        
        }
        else if ( (LA26_0=='o') ) {
            switch ( input.LA(2) ) {
            case 'f':
                {
                int LA26_70 = input.LA(3);
                
                if ( (LA26_70=='$'||(LA26_70>='0' && LA26_70<='9')||LA26_70=='_'||(LA26_70>='a' && LA26_70<='z')||(LA26_70>='\u0080' && LA26_70<='\uFFFE')) ) {
                    alt26=68;
                }
                else {
                    alt26=45;}
                }
                break;
            case 'r':
                {
                int LA26_71 = input.LA(3);
                
                if ( (LA26_71=='d') ) {
                    int LA26_131 = input.LA(4);
                    
                    if ( (LA26_131=='e') ) {
                        int LA26_186 = input.LA(5);
                        
                        if ( (LA26_186=='r') ) {
                            int LA26_229 = input.LA(6);
                            
                            if ( (LA26_229=='$'||(LA26_229>='0' && LA26_229<='9')||LA26_229=='_'||(LA26_229>='a' && LA26_229<='z')||(LA26_229>='\u0080' && LA26_229<='\uFFFE')) ) {
                                alt26=68;
                            }
                            else {
                                alt26=47;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else if ( (LA26_71=='$'||(LA26_71>='0' && LA26_71<='9')||LA26_71=='_'||(LA26_71>='a' && LA26_71<='c')||(LA26_71>='e' && LA26_71<='z')||(LA26_71>='\u0080' && LA26_71<='\uFFFE')) ) {
                    alt26=68;
                }
                else {
                    alt26=46;}
                }
                break;
            case 'u':
                {
                int LA26_72 = input.LA(3);
                
                if ( (LA26_72=='t') ) {
                    int LA26_133 = input.LA(4);
                    
                    if ( (LA26_133=='e') ) {
                        int LA26_187 = input.LA(5);
                        
                        if ( (LA26_187=='r') ) {
                            int LA26_230 = input.LA(6);
                            
                            if ( (LA26_230=='$'||(LA26_230>='0' && LA26_230<='9')||LA26_230=='_'||(LA26_230>='a' && LA26_230<='z')||(LA26_230>='\u0080' && LA26_230<='\uFFFE')) ) {
                                alt26=68;
                            }
                            else {
                                alt26=48;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 'b':
                {
                int LA26_73 = input.LA(3);
                
                if ( (LA26_73=='j') ) {
                    int LA26_134 = input.LA(4);
                    
                    if ( (LA26_134=='e') ) {
                        int LA26_188 = input.LA(5);
                        
                        if ( (LA26_188=='c') ) {
                            int LA26_231 = input.LA(6);
                            
                            if ( (LA26_231=='t') ) {
                                int LA26_265 = input.LA(7);
                                
                                if ( (LA26_265=='$'||(LA26_265>='0' && LA26_265<='9')||LA26_265=='_'||(LA26_265>='a' && LA26_265<='z')||(LA26_265>='\u0080' && LA26_265<='\uFFFE')) ) {
                                    alt26=68;
                                }
                                else {
                                    alt26=44;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            default:
                alt26=68;}
        
        }
        else if ( (LA26_0=='s') ) {
            switch ( input.LA(2) ) {
            case 'u':
                {
                switch ( input.LA(3) ) {
                case 'b':
                    {
                    int LA26_135 = input.LA(4);
                    
                    if ( (LA26_135=='s') ) {
                        int LA26_189 = input.LA(5);
                        
                        if ( (LA26_189=='t') ) {
                            int LA26_232 = input.LA(6);
                            
                            if ( (LA26_232=='r') ) {
                                int LA26_266 = input.LA(7);
                                
                                if ( (LA26_266=='i') ) {
                                    int LA26_286 = input.LA(8);
                                    
                                    if ( (LA26_286=='n') ) {
                                        int LA26_295 = input.LA(9);
                                        
                                        if ( (LA26_295=='g') ) {
                                            int LA26_301 = input.LA(10);
                                            
                                            if ( (LA26_301=='$'||(LA26_301>='0' && LA26_301<='9')||LA26_301=='_'||(LA26_301>='a' && LA26_301<='z')||(LA26_301>='\u0080' && LA26_301<='\uFFFE')) ) {
                                                alt26=68;
                                            }
                                            else {
                                                alt26=54;}
                                        }
                                        else {
                                            alt26=68;}
                                    }
                                    else {
                                        alt26=68;}
                                }
                                else {
                                    alt26=68;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                    }
                    break;
                case 'm':
                    {
                    int LA26_136 = input.LA(4);
                    
                    if ( (LA26_136=='$'||(LA26_136>='0' && LA26_136<='9')||LA26_136=='_'||(LA26_136>='a' && LA26_136<='z')||(LA26_136>='\u0080' && LA26_136<='\uFFFE')) ) {
                        alt26=68;
                    }
                    else {
                        alt26=55;}
                    }
                    break;
                default:
                    alt26=68;}
            
                }
                break;
            case 'e':
                {
                switch ( input.LA(3) ) {
                case 't':
                    {
                    int LA26_137 = input.LA(4);
                    
                    if ( (LA26_137=='$'||(LA26_137>='0' && LA26_137<='9')||LA26_137=='_'||(LA26_137>='a' && LA26_137<='z')||(LA26_137>='\u0080' && LA26_137<='\uFFFE')) ) {
                        alt26=68;
                    }
                    else {
                        alt26=50;}
                    }
                    break;
                case 'l':
                    {
                    int LA26_138 = input.LA(4);
                    
                    if ( (LA26_138=='e') ) {
                        int LA26_192 = input.LA(5);
                        
                        if ( (LA26_192=='c') ) {
                            int LA26_233 = input.LA(6);
                            
                            if ( (LA26_233=='t') ) {
                                int LA26_267 = input.LA(7);
                                
                                if ( (LA26_267=='$'||(LA26_267>='0' && LA26_267<='9')||LA26_267=='_'||(LA26_267>='a' && LA26_267<='z')||(LA26_267>='\u0080' && LA26_267<='\uFFFE')) ) {
                                    alt26=68;
                                }
                                else {
                                    alt26=49;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                    }
                    break;
                default:
                    alt26=68;}
            
                }
                break;
            case 'o':
                {
                int LA26_76 = input.LA(3);
                
                if ( (LA26_76=='m') ) {
                    int LA26_139 = input.LA(4);
                    
                    if ( (LA26_139=='e') ) {
                        int LA26_193 = input.LA(5);
                        
                        if ( (LA26_193=='$'||(LA26_193>='0' && LA26_193<='9')||LA26_193=='_'||(LA26_193>='a' && LA26_193<='z')||(LA26_193>='\u0080' && LA26_193<='\uFFFE')) ) {
                            alt26=68;
                        }
                        else {
                            alt26=53;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 'i':
                {
                int LA26_77 = input.LA(3);
                
                if ( (LA26_77=='z') ) {
                    int LA26_140 = input.LA(4);
                    
                    if ( (LA26_140=='e') ) {
                        int LA26_194 = input.LA(5);
                        
                        if ( (LA26_194=='$'||(LA26_194>='0' && LA26_194<='9')||LA26_194=='_'||(LA26_194>='a' && LA26_194<='z')||(LA26_194>='\u0080' && LA26_194<='\uFFFE')) ) {
                            alt26=68;
                        }
                        else {
                            alt26=51;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 'q':
                {
                int LA26_78 = input.LA(3);
                
                if ( (LA26_78=='r') ) {
                    int LA26_141 = input.LA(4);
                    
                    if ( (LA26_141=='t') ) {
                        int LA26_195 = input.LA(5);
                        
                        if ( (LA26_195=='$'||(LA26_195>='0' && LA26_195<='9')||LA26_195=='_'||(LA26_195>='a' && LA26_195<='z')||(LA26_195>='\u0080' && LA26_195<='\uFFFE')) ) {
                            alt26=68;
                        }
                        else {
                            alt26=52;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            default:
                alt26=68;}
        
        }
        else if ( (LA26_0=='t') ) {
            int LA26_16 = input.LA(2);
            
            if ( (LA26_16=='r') ) {
                switch ( input.LA(3) ) {
                case 'u':
                    {
                    int LA26_142 = input.LA(4);
                    
                    if ( (LA26_142=='e') ) {
                        int LA26_196 = input.LA(5);
                        
                        if ( (LA26_196=='$'||(LA26_196>='0' && LA26_196<='9')||LA26_196=='_'||(LA26_196>='a' && LA26_196<='z')||(LA26_196>='\u0080' && LA26_196<='\uFFFE')) ) {
                            alt26=68;
                        }
                        else {
                            alt26=58;}
                    }
                    else {
                        alt26=68;}
                    }
                    break;
                case 'i':
                    {
                    int LA26_143 = input.LA(4);
                    
                    if ( (LA26_143=='m') ) {
                        int LA26_197 = input.LA(5);
                        
                        if ( (LA26_197=='$'||(LA26_197>='0' && LA26_197<='9')||LA26_197=='_'||(LA26_197>='a' && LA26_197<='z')||(LA26_197>='\u0080' && LA26_197<='\uFFFE')) ) {
                            alt26=68;
                        }
                        else {
                            alt26=57;}
                    }
                    else {
                        alt26=68;}
                    }
                    break;
                case 'a':
                    {
                    int LA26_144 = input.LA(4);
                    
                    if ( (LA26_144=='i') ) {
                        int LA26_198 = input.LA(5);
                        
                        if ( (LA26_198=='l') ) {
                            int LA26_239 = input.LA(6);
                            
                            if ( (LA26_239=='i') ) {
                                int LA26_268 = input.LA(7);
                                
                                if ( (LA26_268=='n') ) {
                                    int LA26_288 = input.LA(8);
                                    
                                    if ( (LA26_288=='g') ) {
                                        int LA26_296 = input.LA(9);
                                        
                                        if ( (LA26_296=='$'||(LA26_296>='0' && LA26_296<='9')||LA26_296=='_'||(LA26_296>='a' && LA26_296<='z')||(LA26_296>='\u0080' && LA26_296<='\uFFFE')) ) {
                                            alt26=68;
                                        }
                                        else {
                                            alt26=56;}
                                    }
                                    else {
                                        alt26=68;}
                                }
                                else {
                                    alt26=68;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                    }
                    break;
                default:
                    alt26=68;}
            
            }
            else {
                alt26=68;}
        }
        else if ( (LA26_0=='u') ) {
            switch ( input.LA(2) ) {
            case 'n':
                {
                int LA26_80 = input.LA(3);
                
                if ( (LA26_80=='k') ) {
                    int LA26_145 = input.LA(4);
                    
                    if ( (LA26_145=='n') ) {
                        int LA26_199 = input.LA(5);
                        
                        if ( (LA26_199=='o') ) {
                            int LA26_240 = input.LA(6);
                            
                            if ( (LA26_240=='w') ) {
                                int LA26_269 = input.LA(7);
                                
                                if ( (LA26_269=='n') ) {
                                    int LA26_289 = input.LA(8);
                                    
                                    if ( (LA26_289=='$'||(LA26_289>='0' && LA26_289<='9')||LA26_289=='_'||(LA26_289>='a' && LA26_289<='z')||(LA26_289>='\u0080' && LA26_289<='\uFFFE')) ) {
                                        alt26=68;
                                    }
                                    else {
                                        alt26=59;}
                                }
                                else {
                                    alt26=68;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
                }
                break;
            case 'p':
                {
                switch ( input.LA(3) ) {
                case 'p':
                    {
                    int LA26_146 = input.LA(4);
                    
                    if ( (LA26_146=='e') ) {
                        int LA26_200 = input.LA(5);
                        
                        if ( (LA26_200=='r') ) {
                            int LA26_241 = input.LA(6);
                            
                            if ( (LA26_241=='$'||(LA26_241>='0' && LA26_241<='9')||LA26_241=='_'||(LA26_241>='a' && LA26_241<='z')||(LA26_241>='\u0080' && LA26_241<='\uFFFE')) ) {
                                alt26=68;
                            }
                            else {
                                alt26=61;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                    }
                    break;
                case 'd':
                    {
                    int LA26_147 = input.LA(4);
                    
                    if ( (LA26_147=='a') ) {
                        int LA26_201 = input.LA(5);
                        
                        if ( (LA26_201=='t') ) {
                            int LA26_242 = input.LA(6);
                            
                            if ( (LA26_242=='e') ) {
                                int LA26_271 = input.LA(7);
                                
                                if ( (LA26_271=='$'||(LA26_271>='0' && LA26_271<='9')||LA26_271=='_'||(LA26_271>='a' && LA26_271<='z')||(LA26_271>='\u0080' && LA26_271<='\uFFFE')) ) {
                                    alt26=68;
                                }
                                else {
                                    alt26=60;}
                            }
                            else {
                                alt26=68;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                    }
                    break;
                default:
                    alt26=68;}
            
                }
                break;
            default:
                alt26=68;}
        
        }
        else if ( (LA26_0=='w') ) {
            int LA26_18 = input.LA(2);
            
            if ( (LA26_18=='h') ) {
                int LA26_82 = input.LA(3);
                
                if ( (LA26_82=='e') ) {
                    int LA26_148 = input.LA(4);
                    
                    if ( (LA26_148=='r') ) {
                        int LA26_202 = input.LA(5);
                        
                        if ( (LA26_202=='e') ) {
                            int LA26_243 = input.LA(6);
                            
                            if ( (LA26_243=='$'||(LA26_243>='0' && LA26_243<='9')||LA26_243=='_'||(LA26_243>='a' && LA26_243<='z')||(LA26_243>='\u0080' && LA26_243<='\uFFFE')) ) {
                                alt26=68;
                            }
                            else {
                                alt26=62;}
                        }
                        else {
                            alt26=68;}
                    }
                    else {
                        alt26=68;}
                }
                else {
                    alt26=68;}
            }
            else {
                alt26=68;}
        }
        else if ( (LA26_0=='.') ) {
            int LA26_19 = input.LA(2);
            
            if ( ((LA26_19>='0' && LA26_19<='9')) ) {
                alt26=69;
            }
            else {
                alt26=63;}
        }
        else if ( ((LA26_0>='\t' && LA26_0<='\n')||LA26_0=='\r'||LA26_0==' ') ) {
            alt26=64;
        }
        else if ( (LA26_0=='(') ) {
            alt26=65;
        }
        else if ( (LA26_0==')') ) {
            alt26=66;
        }
        else if ( (LA26_0==',') ) {
            alt26=67;
        }
        else if ( (LA26_0=='$'||(LA26_0>='A' && LA26_0<='Z')||LA26_0=='_'||LA26_0=='k'||(LA26_0>='p' && LA26_0<='r')||LA26_0=='v'||(LA26_0>='x' && LA26_0<='z')||(LA26_0>='\u0080' && LA26_0<='\uFFFE')) ) {
            alt26=68;
        }
        else if ( ((LA26_0>='0' && LA26_0<='9')) ) {
            alt26=69;
        }
        else if ( (LA26_0=='=') ) {
            alt26=70;
        }
        else if ( (LA26_0=='>') ) {
            int LA26_27 = input.LA(2);
            
            if ( (LA26_27=='=') ) {
                alt26=72;
            }
            else {
                alt26=71;}
        }
        else if ( (LA26_0=='<') ) {
            switch ( input.LA(2) ) {
            case '>':
                {
                alt26=75;
                }
                break;
            case '=':
                {
                alt26=74;
                }
                break;
            default:
                alt26=73;}
        
        }
        else if ( (LA26_0=='*') ) {
            alt26=76;
        }
        else if ( (LA26_0=='/') ) {
            alt26=77;
        }
        else if ( (LA26_0=='+') ) {
            alt26=78;
        }
        else if ( (LA26_0=='-') ) {
            alt26=79;
        }
        else if ( (LA26_0=='?') ) {
            alt26=80;
        }
        else if ( (LA26_0==':') ) {
            alt26=81;
        }
        else if ( (LA26_0=='\"') ) {
            alt26=82;
        }
        else if ( (LA26_0=='\'') ) {
            alt26=83;
        }
        else {
            NoViableAltException nvae =
                new NoViableAltException("1:1: Tokens : ( ABS | ALL | AND | ANY | AS | ASC | AVG | BETWEEN | BOTH | BY | CONCAT | COUNT | CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | DESC | DELETE | DISTINCT | EMPTY | ESCAPE | EXISTS | FALSE | FETCH | FROM | GROUP | HAVING | IN | INNER | IS | JOIN | LEADING | LEFT | LENGTH | LIKE | LOCATE | LOWER | MAX | MEMBER | MIN | MOD | NEW | NOT | NULL | OBJECT | OF | OR | ORDER | OUTER | SELECT | SET | SIZE | SQRT | SOME | SUBSTRING | SUM | TRAILING | TRIM | TRUE | UNKNOWN | UPDATE | UPPER | WHERE | DOT | WS | LEFT_ROUND_BRACKET | RIGHT_ROUND_BRACKET | COMMA | IDENT | NUM_INT | EQUALS | GREATER_THAN | GREATER_THAN_EQUAL_TO | LESS_THAN | LESS_THAN_EQUAL_TO | NOT_EQUAL_TO | MULTIPLY | DIVIDE | PLUS | MINUS | POSITIONAL_PARAM | NAMED_PARAM | STRING_LITERAL_DOUBLE_QUOTED | STRING_LITERAL_SINGLE_QUOTED );", 26, 0, input);
        
            throw nvae;
        }
        switch (alt26) {
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
                // JPQL.g3:1:437: NUM_INT
                {
                mNUM_INT(); 
                
                }
                break;
            case 70 :
                // JPQL.g3:1:445: EQUALS
                {
                mEQUALS(); 
                
                }
                break;
            case 71 :
                // JPQL.g3:1:452: GREATER_THAN
                {
                mGREATER_THAN(); 
                
                }
                break;
            case 72 :
                // JPQL.g3:1:465: GREATER_THAN_EQUAL_TO
                {
                mGREATER_THAN_EQUAL_TO(); 
                
                }
                break;
            case 73 :
                // JPQL.g3:1:487: LESS_THAN
                {
                mLESS_THAN(); 
                
                }
                break;
            case 74 :
                // JPQL.g3:1:497: LESS_THAN_EQUAL_TO
                {
                mLESS_THAN_EQUAL_TO(); 
                
                }
                break;
            case 75 :
                // JPQL.g3:1:516: NOT_EQUAL_TO
                {
                mNOT_EQUAL_TO(); 
                
                }
                break;
            case 76 :
                // JPQL.g3:1:529: MULTIPLY
                {
                mMULTIPLY(); 
                
                }
                break;
            case 77 :
                // JPQL.g3:1:538: DIVIDE
                {
                mDIVIDE(); 
                
                }
                break;
            case 78 :
                // JPQL.g3:1:545: PLUS
                {
                mPLUS(); 
                
                }
                break;
            case 79 :
                // JPQL.g3:1:550: MINUS
                {
                mMINUS(); 
                
                }
                break;
            case 80 :
                // JPQL.g3:1:556: POSITIONAL_PARAM
                {
                mPOSITIONAL_PARAM(); 
                
                }
                break;
            case 81 :
                // JPQL.g3:1:573: NAMED_PARAM
                {
                mNAMED_PARAM(); 
                
                }
                break;
            case 82 :
                // JPQL.g3:1:585: STRING_LITERAL_DOUBLE_QUOTED
                {
                mSTRING_LITERAL_DOUBLE_QUOTED(); 
                
                }
                break;
            case 83 :
                // JPQL.g3:1:614: STRING_LITERAL_SINGLE_QUOTED
                {
                mSTRING_LITERAL_SINGLE_QUOTED(); 
                
                }
                break;
        
        }
    
    }


 

}