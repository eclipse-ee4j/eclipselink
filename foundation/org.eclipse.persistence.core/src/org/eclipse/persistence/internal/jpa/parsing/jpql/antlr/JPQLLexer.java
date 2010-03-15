// $ANTLR 3.0 JPQL.g 2010-03-15 11:19:21

    package org.eclipse.persistence.internal.jpa.parsing.jpql.antlr;

    import org.eclipse.persistence.internal.jpa.parsing.jpql.InvalidIdentifierStartException;


import org.eclipse.persistence.internal.libraries.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class JPQLLexer extends Lexer {
    public static final int EXPONENT=115;
    public static final int FLOAT_SUFFIX=116;
    public static final int DATE_STRING=117;
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
    public static final int LEADING=42;
    public static final int RIGHT_CURLY_BRACKET=107;
    public static final int EMPTY=25;
    public static final int INTEGER_SUFFIX=111;
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
    public static final int MINUS=91;
    public static final int SQRT=64;
    public static final int Tokens=119;
    public static final int LONG_LITERAL=95;
    public static final int TRUE=71;
    public static final int JOIN=40;
    public static final int SUBSTRING=66;
    public static final int FLOAT_LITERAL=96;
    public static final int DOUBLE_SUFFIX=114;
    public static final int ANY=7;
    public static final int LOCATE=46;
    public static final int WHEN=77;
    public static final int DESC=21;
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

    // $ANTLR start CASE
    public final void mCASE() throws RecognitionException {
        try {
            int _type = CASE;
            // JPQL.g:18:8: ( 'case' )
            // JPQL.g:18:8: 'case'
            {
            match("case"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end CASE

    // $ANTLR start COALESCE
    public final void mCOALESCE() throws RecognitionException {
        try {
            int _type = COALESCE;
            // JPQL.g:19:12: ( 'coalesce' )
            // JPQL.g:19:12: 'coalesce'
            {
            match("coalesce"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end COALESCE

    // $ANTLR start CONCAT
    public final void mCONCAT() throws RecognitionException {
        try {
            int _type = CONCAT;
            // JPQL.g:20:10: ( 'concat' )
            // JPQL.g:20:10: 'concat'
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
            // JPQL.g:21:9: ( 'count' )
            // JPQL.g:21:9: 'count'
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
            // JPQL.g:22:16: ( 'current_date' )
            // JPQL.g:22:16: 'current_date'
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
            // JPQL.g:23:16: ( 'current_time' )
            // JPQL.g:23:16: 'current_time'
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
            // JPQL.g:24:21: ( 'current_timestamp' )
            // JPQL.g:24:21: 'current_timestamp'
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
            // JPQL.g:25:8: ( 'desc' )
            // JPQL.g:25:8: 'desc'
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
            // JPQL.g:26:10: ( 'delete' )
            // JPQL.g:26:10: 'delete'
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
            // JPQL.g:27:12: ( 'distinct' )
            // JPQL.g:27:12: 'distinct'
            {
            match("distinct"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DISTINCT

    // $ANTLR start ELSE
    public final void mELSE() throws RecognitionException {
        try {
            int _type = ELSE;
            // JPQL.g:28:8: ( 'else' )
            // JPQL.g:28:8: 'else'
            {
            match("else"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ELSE

    // $ANTLR start EMPTY
    public final void mEMPTY() throws RecognitionException {
        try {
            int _type = EMPTY;
            // JPQL.g:29:9: ( 'empty' )
            // JPQL.g:29:9: 'empty'
            {
            match("empty"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end EMPTY

    // $ANTLR start END
    public final void mEND() throws RecognitionException {
        try {
            int _type = END;
            // JPQL.g:30:7: ( 'end' )
            // JPQL.g:30:7: 'end'
            {
            match("end"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end END

    // $ANTLR start ENTRY
    public final void mENTRY() throws RecognitionException {
        try {
            int _type = ENTRY;
            // JPQL.g:31:9: ( 'entry' )
            // JPQL.g:31:9: 'entry'
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
            // JPQL.g:32:10: ( 'escape' )
            // JPQL.g:32:10: 'escape'
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
            // JPQL.g:33:10: ( 'exists' )
            // JPQL.g:33:10: 'exists'
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
            // JPQL.g:34:9: ( 'false' )
            // JPQL.g:34:9: 'false'
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
            // JPQL.g:35:9: ( 'fetch' )
            // JPQL.g:35:9: 'fetch'
            {
            match("fetch"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FETCH

    // $ANTLR start FUNC
    public final void mFUNC() throws RecognitionException {
        try {
            int _type = FUNC;
            // JPQL.g:36:8: ( 'func' )
            // JPQL.g:36:8: 'func'
            {
            match("func"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end FUNC

    // $ANTLR start FROM
    public final void mFROM() throws RecognitionException {
        try {
            int _type = FROM;
            // JPQL.g:37:8: ( 'from' )
            // JPQL.g:37:8: 'from'
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
            // JPQL.g:38:9: ( 'group' )
            // JPQL.g:38:9: 'group'
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
            // JPQL.g:39:10: ( 'having' )
            // JPQL.g:39:10: 'having'
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
            // JPQL.g:40:6: ( 'in' )
            // JPQL.g:40:6: 'in'
            {
            match("in"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end IN

    // $ANTLR start INDEX
    public final void mINDEX() throws RecognitionException {
        try {
            int _type = INDEX;
            // JPQL.g:41:9: ( 'index' )
            // JPQL.g:41:9: 'index'
            {
            match("index"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INDEX

    // $ANTLR start INNER
    public final void mINNER() throws RecognitionException {
        try {
            int _type = INNER;
            // JPQL.g:42:9: ( 'inner' )
            // JPQL.g:42:9: 'inner'
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
            // JPQL.g:43:6: ( 'is' )
            // JPQL.g:43:6: 'is'
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
            // JPQL.g:44:8: ( 'join' )
            // JPQL.g:44:8: 'join'
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
            // JPQL.g:45:7: ( 'key' )
            // JPQL.g:45:7: 'key'
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
            // JPQL.g:46:11: ( 'leading' )
            // JPQL.g:46:11: 'leading'
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
            // JPQL.g:47:8: ( 'left' )
            // JPQL.g:47:8: 'left'
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
            // JPQL.g:48:10: ( 'length' )
            // JPQL.g:48:10: 'length'
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
            // JPQL.g:49:8: ( 'like' )
            // JPQL.g:49:8: 'like'
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
            // JPQL.g:50:10: ( 'locate' )
            // JPQL.g:50:10: 'locate'
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
            // JPQL.g:51:9: ( 'lower' )
            // JPQL.g:51:9: 'lower'
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
            // JPQL.g:52:7: ( 'max' )
            // JPQL.g:52:7: 'max'
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
            // JPQL.g:53:10: ( 'member' )
            // JPQL.g:53:10: 'member'
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
            // JPQL.g:54:7: ( 'min' )
            // JPQL.g:54:7: 'min'
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
            // JPQL.g:55:7: ( 'mod' )
            // JPQL.g:55:7: 'mod'
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
            // JPQL.g:56:7: ( 'new' )
            // JPQL.g:56:7: 'new'
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
            // JPQL.g:57:7: ( 'not' )
            // JPQL.g:57:7: 'not'
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
            // JPQL.g:58:8: ( 'null' )
            // JPQL.g:58:8: 'null'
            {
            match("null"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NULL

    // $ANTLR start NULLIF
    public final void mNULLIF() throws RecognitionException {
        try {
            int _type = NULLIF;
            // JPQL.g:59:10: ( 'nullif' )
            // JPQL.g:59:10: 'nullif'
            {
            match("nullif"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NULLIF

    // $ANTLR start OBJECT
    public final void mOBJECT() throws RecognitionException {
        try {
            int _type = OBJECT;
            // JPQL.g:60:10: ( 'object' )
            // JPQL.g:60:10: 'object'
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
            // JPQL.g:61:6: ( 'of' )
            // JPQL.g:61:6: 'of'
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
            // JPQL.g:62:6: ( 'or' )
            // JPQL.g:62:6: 'or'
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
            // JPQL.g:63:9: ( 'order' )
            // JPQL.g:63:9: 'order'
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
            // JPQL.g:64:9: ( 'outer' )
            // JPQL.g:64:9: 'outer'
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
            // JPQL.g:65:10: ( 'select' )
            // JPQL.g:65:10: 'select'
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
            // JPQL.g:66:7: ( 'set' )
            // JPQL.g:66:7: 'set'
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
            // JPQL.g:67:8: ( 'size' )
            // JPQL.g:67:8: 'size'
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
            // JPQL.g:68:8: ( 'sqrt' )
            // JPQL.g:68:8: 'sqrt'
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
            // JPQL.g:69:8: ( 'some' )
            // JPQL.g:69:8: 'some'
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
            // JPQL.g:70:13: ( 'substring' )
            // JPQL.g:70:13: 'substring'
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
            // JPQL.g:71:7: ( 'sum' )
            // JPQL.g:71:7: 'sum'
            {
            match("sum"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end SUM

    // $ANTLR start THEN
    public final void mTHEN() throws RecognitionException {
        try {
            int _type = THEN;
            // JPQL.g:72:8: ( 'then' )
            // JPQL.g:72:8: 'then'
            {
            match("then"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end THEN

    // $ANTLR start TRAILING
    public final void mTRAILING() throws RecognitionException {
        try {
            int _type = TRAILING;
            // JPQL.g:73:12: ( 'trailing' )
            // JPQL.g:73:12: 'trailing'
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
            // JPQL.g:74:8: ( 'trim' )
            // JPQL.g:74:8: 'trim'
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
            // JPQL.g:75:8: ( 'true' )
            // JPQL.g:75:8: 'true'
            {
            match("true"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TRUE

    // $ANTLR start TYPE
    public final void mTYPE() throws RecognitionException {
        try {
            int _type = TYPE;
            // JPQL.g:76:8: ( 'type' )
            // JPQL.g:76:8: 'type'
            {
            match("type"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TYPE

    // $ANTLR start UNKNOWN
    public final void mUNKNOWN() throws RecognitionException {
        try {
            int _type = UNKNOWN;
            // JPQL.g:77:11: ( 'unknown' )
            // JPQL.g:77:11: 'unknown'
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
            // JPQL.g:78:10: ( 'update' )
            // JPQL.g:78:10: 'update'
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
            // JPQL.g:79:9: ( 'upper' )
            // JPQL.g:79:9: 'upper'
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
            // JPQL.g:80:9: ( 'value' )
            // JPQL.g:80:9: 'value'
            {
            match("value"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end VALUE

    // $ANTLR start WHEN
    public final void mWHEN() throws RecognitionException {
        try {
            int _type = WHEN;
            // JPQL.g:81:8: ( 'when' )
            // JPQL.g:81:8: 'when'
            {
            match("when"); 

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WHEN

    // $ANTLR start WHERE
    public final void mWHERE() throws RecognitionException {
        try {
            int _type = WHERE;
            // JPQL.g:82:9: ( 'where' )
            // JPQL.g:82:9: 'where'
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
            // JPQL.g:1403:7: ( '.' )
            // JPQL.g:1403:7: '.'
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
            // JPQL.g:1406:7: ( ( ' ' | '\\t' | '\\n' | '\\r' )+ )
            // JPQL.g:1406:7: ( ' ' | '\\t' | '\\n' | '\\r' )+
            {
            // JPQL.g:1406:7: ( ' ' | '\\t' | '\\n' | '\\r' )+
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
            // JPQL.g:1410:7: ( '(' )
            // JPQL.g:1410:7: '('
            {
            match('('); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LEFT_ROUND_BRACKET

    // $ANTLR start LEFT_CURLY_BRACKET
    public final void mLEFT_CURLY_BRACKET() throws RecognitionException {
        try {
            int _type = LEFT_CURLY_BRACKET;
            // JPQL.g:1414:7: ( '{' )
            // JPQL.g:1414:7: '{'
            {
            match('{'); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end LEFT_CURLY_BRACKET

    // $ANTLR start RIGHT_ROUND_BRACKET
    public final void mRIGHT_ROUND_BRACKET() throws RecognitionException {
        try {
            int _type = RIGHT_ROUND_BRACKET;
            // JPQL.g:1418:7: ( ')' )
            // JPQL.g:1418:7: ')'
            {
            match(')'); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RIGHT_ROUND_BRACKET

    // $ANTLR start RIGHT_CURLY_BRACKET
    public final void mRIGHT_CURLY_BRACKET() throws RecognitionException {
        try {
            int _type = RIGHT_CURLY_BRACKET;
            // JPQL.g:1422:7: ( '}' )
            // JPQL.g:1422:7: '}'
            {
            match('}'); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end RIGHT_CURLY_BRACKET

    // $ANTLR start COMMA
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            // JPQL.g:1426:7: ( ',' )
            // JPQL.g:1426:7: ','
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
            // JPQL.g:1430:7: ( TEXTCHAR )
            // JPQL.g:1430:7: TEXTCHAR
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
    
            // JPQL.g:1435:7: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | c1= '\\u0080' .. '\\uFFFE' ) ( 'a' .. 'z' | '_' | '$' | '0' .. '9' | c2= '\\u0080' .. '\\uFFFE' )* )
            // JPQL.g:1435:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | c1= '\\u0080' .. '\\uFFFE' ) ( 'a' .. 'z' | '_' | '$' | '0' .. '9' | c2= '\\u0080' .. '\\uFFFE' )*
            {
            // JPQL.g:1435:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | c1= '\\u0080' .. '\\uFFFE' )
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
                    new NoViableAltException("1435:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | c1= '\\u0080' .. '\\uFFFE' )", 2, 0, input);
            
                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // JPQL.g:1435:8: 'a' .. 'z'
                    {
                    matchRange('a','z'); 
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1435:19: 'A' .. 'Z'
                    {
                    matchRange('A','Z'); 
                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1435:30: '_'
                    {
                    match('_'); 
                    
                    }
                    break;
                case 4 :
                    // JPQL.g:1435:36: '$'
                    {
                    match('$'); 
                    
                    }
                    break;
                case 5 :
                    // JPQL.g:1436:8: c1= '\\u0080' .. '\\uFFFE'
                    {
                    c1 = input.LA(1);
                    matchRange('\u0080','\uFFFE'); 
                    
                               if (!Character.isJavaIdentifierStart(c1)) {
                                    throw new InvalidIdentifierStartException(c1, getLine(), getCharPositionInLine());
                               }
                           
                    
                    }
                    break;
            
            }

            // JPQL.g:1443:7: ( 'a' .. 'z' | '_' | '$' | '0' .. '9' | c2= '\\u0080' .. '\\uFFFE' )*
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
            	    // JPQL.g:1443:8: 'a' .. 'z'
            	    {
            	    matchRange('a','z'); 
            	    
            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:1443:19: '_'
            	    {
            	    match('_'); 
            	    
            	    }
            	    break;
            	case 3 :
            	    // JPQL.g:1443:25: '$'
            	    {
            	    match('$'); 
            	    
            	    }
            	    break;
            	case 4 :
            	    // JPQL.g:1443:31: '0' .. '9'
            	    {
            	    matchRange('0','9'); 
            	    
            	    }
            	    break;
            	case 5 :
            	    // JPQL.g:1444:8: c2= '\\u0080' .. '\\uFFFE'
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
            // JPQL.g:1454:15: ( '0' ( 'x' | 'X' ) ( HEX_DIGIT )+ )
            // JPQL.g:1454:15: '0' ( 'x' | 'X' ) ( HEX_DIGIT )+
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

            // JPQL.g:1454:29: ( HEX_DIGIT )+
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
            	    // JPQL.g:1454:29: HEX_DIGIT
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
            // JPQL.g:1456:19: ( ( MINUS )? ( '0' | '1' .. '9' ( '0' .. '9' )* ) )
            // JPQL.g:1456:19: ( MINUS )? ( '0' | '1' .. '9' ( '0' .. '9' )* )
            {
            // JPQL.g:1456:19: ( MINUS )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            
            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // JPQL.g:1456:19: MINUS
                    {
                    mMINUS(); 
                    
                    }
                    break;
            
            }

            // JPQL.g:1456:26: ( '0' | '1' .. '9' ( '0' .. '9' )* )
            int alt7=2;
            int LA7_0 = input.LA(1);
            
            if ( (LA7_0=='0') ) {
                alt7=1;
            }
            else if ( ((LA7_0>='1' && LA7_0<='9')) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1456:26: ( '0' | '1' .. '9' ( '0' .. '9' )* )", 7, 0, input);
            
                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // JPQL.g:1456:27: '0'
                    {
                    match('0'); 
                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1456:33: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 
                    // JPQL.g:1456:42: ( '0' .. '9' )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);
                        
                        if ( ((LA6_0>='0' && LA6_0<='9')) ) {
                            alt6=1;
                        }
                        
                    
                        switch (alt6) {
                    	case 1 :
                    	    // JPQL.g:1456:42: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 
                    	    
                    	    }
                    	    break;
                    
                    	default :
                    	    break loop6;
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
            // JPQL.g:1458:16: ( INTEGER_LITERAL INTEGER_SUFFIX )
            // JPQL.g:1458:16: INTEGER_LITERAL INTEGER_SUFFIX
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
            // JPQL.g:1460:17: ( ( MINUS )? '0' ( '0' .. '7' )+ )
            // JPQL.g:1460:17: ( MINUS )? '0' ( '0' .. '7' )+
            {
            // JPQL.g:1460:17: ( MINUS )?
            int alt8=2;
            int LA8_0 = input.LA(1);
            
            if ( (LA8_0=='-') ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // JPQL.g:1460:17: MINUS
                    {
                    mMINUS(); 
                    
                    }
                    break;
            
            }

            match('0'); 
            // JPQL.g:1460:28: ( '0' .. '7' )+
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
            	    // JPQL.g:1460:29: '0' .. '7'
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
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end OCTAL_LITERAL

    // $ANTLR start HEX_DIGIT
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // JPQL.g:1465:9: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // JPQL.g:1465:9: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
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
            // JPQL.g:1469:18: ( ( 'l' | 'L' ) )
            // JPQL.g:1469:18: ( 'l' | 'L' )
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
            // JPQL.g:1473:9: ( ( MINUS )? ( '0' .. '9' )+ '.' ( '0' .. '9' )* | ( MINUS )? '.' ( '0' .. '9' )+ | ( MINUS )? ( '0' .. '9' )+ )
            int alt17=3;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // JPQL.g:1473:9: ( MINUS )? ( '0' .. '9' )+ '.' ( '0' .. '9' )*
                    {
                    // JPQL.g:1473:9: ( MINUS )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);
                    
                    if ( (LA10_0=='-') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // JPQL.g:1473:9: MINUS
                            {
                            mMINUS(); 
                            
                            }
                            break;
                    
                    }

                    // JPQL.g:1473:16: ( '0' .. '9' )+
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
                    	    // JPQL.g:1473:17: '0' .. '9'
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

                    match('.'); 
                    // JPQL.g:1473:32: ( '0' .. '9' )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);
                        
                        if ( ((LA12_0>='0' && LA12_0<='9')) ) {
                            alt12=1;
                        }
                        
                    
                        switch (alt12) {
                    	case 1 :
                    	    // JPQL.g:1473:33: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 
                    	    
                    	    }
                    	    break;
                    
                    	default :
                    	    break loop12;
                        }
                    } while (true);

                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1474:9: ( MINUS )? '.' ( '0' .. '9' )+
                    {
                    // JPQL.g:1474:9: ( MINUS )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);
                    
                    if ( (LA13_0=='-') ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // JPQL.g:1474:9: MINUS
                            {
                            mMINUS(); 
                            
                            }
                            break;
                    
                    }

                    match('.'); 
                    // JPQL.g:1474:20: ( '0' .. '9' )+
                    int cnt14=0;
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);
                        
                        if ( ((LA14_0>='0' && LA14_0<='9')) ) {
                            alt14=1;
                        }
                        
                    
                        switch (alt14) {
                    	case 1 :
                    	    // JPQL.g:1474:21: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 
                    	    
                    	    }
                    	    break;
                    
                    	default :
                    	    if ( cnt14 >= 1 ) break loop14;
                                EarlyExitException eee =
                                    new EarlyExitException(14, input);
                                throw eee;
                        }
                        cnt14++;
                    } while (true);

                    
                    }
                    break;
                case 3 :
                    // JPQL.g:1475:9: ( MINUS )? ( '0' .. '9' )+
                    {
                    // JPQL.g:1475:9: ( MINUS )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);
                    
                    if ( (LA15_0=='-') ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // JPQL.g:1475:9: MINUS
                            {
                            mMINUS(); 
                            
                            }
                            break;
                    
                    }

                    // JPQL.g:1475:16: ( '0' .. '9' )+
                    int cnt16=0;
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);
                        
                        if ( ((LA16_0>='0' && LA16_0<='9')) ) {
                            alt16=1;
                        }
                        
                    
                        switch (alt16) {
                    	case 1 :
                    	    // JPQL.g:1475:17: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 
                    	    
                    	    }
                    	    break;
                    
                    	default :
                    	    if ( cnt16 >= 1 ) break loop16;
                                EarlyExitException eee =
                                    new EarlyExitException(16, input);
                                throw eee;
                        }
                        cnt16++;
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
            // JPQL.g:1479:9: ( NUMERIC_DIGITS ( DOUBLE_SUFFIX )? )
            // JPQL.g:1479:9: NUMERIC_DIGITS ( DOUBLE_SUFFIX )?
            {
            mNUMERIC_DIGITS(); 
            // JPQL.g:1479:24: ( DOUBLE_SUFFIX )?
            int alt18=2;
            int LA18_0 = input.LA(1);
            
            if ( (LA18_0=='d') ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // JPQL.g:1479:24: DOUBLE_SUFFIX
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
            // JPQL.g:1483:9: ( NUMERIC_DIGITS EXPONENT ( FLOAT_SUFFIX )? | NUMERIC_DIGITS FLOAT_SUFFIX )
            int alt20=2;
            alt20 = dfa20.predict(input);
            switch (alt20) {
                case 1 :
                    // JPQL.g:1483:9: NUMERIC_DIGITS EXPONENT ( FLOAT_SUFFIX )?
                    {
                    mNUMERIC_DIGITS(); 
                    mEXPONENT(); 
                    // JPQL.g:1483:33: ( FLOAT_SUFFIX )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);
                    
                    if ( (LA19_0=='f') ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // JPQL.g:1483:33: FLOAT_SUFFIX
                            {
                            mFLOAT_SUFFIX(); 
                            
                            }
                            break;
                    
                    }

                    
                    }
                    break;
                case 2 :
                    // JPQL.g:1484:9: NUMERIC_DIGITS FLOAT_SUFFIX
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
            // JPQL.g:1490:9: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // JPQL.g:1490:9: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recover(mse);    throw mse;
            }

            // JPQL.g:1490:21: ( '+' | '-' )?
            int alt21=2;
            int LA21_0 = input.LA(1);
            
            if ( (LA21_0=='+'||LA21_0=='-') ) {
                alt21=1;
            }
            switch (alt21) {
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

            // JPQL.g:1490:32: ( '0' .. '9' )+
            int cnt22=0;
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);
                
                if ( ((LA22_0>='0' && LA22_0<='9')) ) {
                    alt22=1;
                }
                
            
                switch (alt22) {
            	case 1 :
            	    // JPQL.g:1490:33: '0' .. '9'
            	    {
            	    matchRange('0','9'); 
            	    
            	    }
            	    break;
            
            	default :
            	    if ( cnt22 >= 1 ) break loop22;
                        EarlyExitException eee =
                            new EarlyExitException(22, input);
                        throw eee;
                }
                cnt22++;
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
            // JPQL.g:1496:9: ( 'f' )
            // JPQL.g:1496:9: 'f'
            {
            match('f'); 
            
            }

        }
        finally {
        }
    }
    // $ANTLR end FLOAT_SUFFIX

    // $ANTLR start DATE_LITERAL
    public final void mDATE_LITERAL() throws RecognitionException {
        try {
            int _type = DATE_LITERAL;
            // JPQL.g:1500:7: ( LEFT_CURLY_BRACKET ( 'd' ) ( ' ' | '\\t' )+ '\\'' DATE_STRING '\\'' ( ' ' | '\\t' )* RIGHT_CURLY_BRACKET )
            // JPQL.g:1500:7: LEFT_CURLY_BRACKET ( 'd' ) ( ' ' | '\\t' )+ '\\'' DATE_STRING '\\'' ( ' ' | '\\t' )* RIGHT_CURLY_BRACKET
            {
            mLEFT_CURLY_BRACKET(); 
            // JPQL.g:1500:26: ( 'd' )
            // JPQL.g:1500:27: 'd'
            {
            match('d'); 
            
            }

            // JPQL.g:1500:32: ( ' ' | '\\t' )+
            int cnt23=0;
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);
                
                if ( (LA23_0=='\t'||LA23_0==' ') ) {
                    alt23=1;
                }
                
            
                switch (alt23) {
            	case 1 :
            	    // JPQL.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
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
            	    if ( cnt23 >= 1 ) break loop23;
                        EarlyExitException eee =
                            new EarlyExitException(23, input);
                        throw eee;
                }
                cnt23++;
            } while (true);

            match('\''); 
            mDATE_STRING(); 
            match('\''); 
            // JPQL.g:1500:68: ( ' ' | '\\t' )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);
                
                if ( (LA24_0=='\t'||LA24_0==' ') ) {
                    alt24=1;
                }
                
            
                switch (alt24) {
            	case 1 :
            	    // JPQL.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
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

            mRIGHT_CURLY_BRACKET(); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DATE_LITERAL

    // $ANTLR start TIME_LITERAL
    public final void mTIME_LITERAL() throws RecognitionException {
        try {
            int _type = TIME_LITERAL;
            // JPQL.g:1504:7: ( LEFT_CURLY_BRACKET ( 't' ) ( ' ' | '\\t' )+ '\\'' TIME_STRING '\\'' ( ' ' | '\\t' )* RIGHT_CURLY_BRACKET )
            // JPQL.g:1504:7: LEFT_CURLY_BRACKET ( 't' ) ( ' ' | '\\t' )+ '\\'' TIME_STRING '\\'' ( ' ' | '\\t' )* RIGHT_CURLY_BRACKET
            {
            mLEFT_CURLY_BRACKET(); 
            // JPQL.g:1504:26: ( 't' )
            // JPQL.g:1504:27: 't'
            {
            match('t'); 
            
            }

            // JPQL.g:1504:32: ( ' ' | '\\t' )+
            int cnt25=0;
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);
                
                if ( (LA25_0=='\t'||LA25_0==' ') ) {
                    alt25=1;
                }
                
            
                switch (alt25) {
            	case 1 :
            	    // JPQL.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
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
            	    if ( cnt25 >= 1 ) break loop25;
                        EarlyExitException eee =
                            new EarlyExitException(25, input);
                        throw eee;
                }
                cnt25++;
            } while (true);

            match('\''); 
            mTIME_STRING(); 
            match('\''); 
            // JPQL.g:1504:68: ( ' ' | '\\t' )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);
                
                if ( (LA26_0=='\t'||LA26_0==' ') ) {
                    alt26=1;
                }
                
            
                switch (alt26) {
            	case 1 :
            	    // JPQL.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
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
            	    break loop26;
                }
            } while (true);

            mRIGHT_CURLY_BRACKET(); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TIME_LITERAL

    // $ANTLR start TIMESTAMP_LITERAL
    public final void mTIMESTAMP_LITERAL() throws RecognitionException {
        try {
            int _type = TIMESTAMP_LITERAL;
            // JPQL.g:1508:7: ( LEFT_CURLY_BRACKET ( 'ts' ) ( ' ' | '\\t' )+ '\\'' DATE_STRING ' ' TIME_STRING '\\'' ( ' ' | '\\t' )* RIGHT_CURLY_BRACKET )
            // JPQL.g:1508:7: LEFT_CURLY_BRACKET ( 'ts' ) ( ' ' | '\\t' )+ '\\'' DATE_STRING ' ' TIME_STRING '\\'' ( ' ' | '\\t' )* RIGHT_CURLY_BRACKET
            {
            mLEFT_CURLY_BRACKET(); 
            // JPQL.g:1508:26: ( 'ts' )
            // JPQL.g:1508:27: 'ts'
            {
            match("ts"); 

            
            }

            // JPQL.g:1508:33: ( ' ' | '\\t' )+
            int cnt27=0;
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);
                
                if ( (LA27_0=='\t'||LA27_0==' ') ) {
                    alt27=1;
                }
                
            
                switch (alt27) {
            	case 1 :
            	    // JPQL.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
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
            	    if ( cnt27 >= 1 ) break loop27;
                        EarlyExitException eee =
                            new EarlyExitException(27, input);
                        throw eee;
                }
                cnt27++;
            } while (true);

            match('\''); 
            mDATE_STRING(); 
            match(' '); 
            mTIME_STRING(); 
            match('\''); 
            // JPQL.g:1508:85: ( ' ' | '\\t' )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);
                
                if ( (LA28_0=='\t'||LA28_0==' ') ) {
                    alt28=1;
                }
                
            
                switch (alt28) {
            	case 1 :
            	    // JPQL.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
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
            	    break loop28;
                }
            } while (true);

            mRIGHT_CURLY_BRACKET(); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TIMESTAMP_LITERAL

    // $ANTLR start DATE_STRING
    public final void mDATE_STRING() throws RecognitionException {
        try {
            int _type = DATE_STRING;
            // JPQL.g:1512:7: ( '0' .. '9' '0' .. '9' '0' .. '9' '0' .. '9' '-' '0' .. '9' '0' .. '9' '-' '0' .. '9' '0' .. '9' )
            // JPQL.g:1512:7: '0' .. '9' '0' .. '9' '0' .. '9' '0' .. '9' '-' '0' .. '9' '0' .. '9' '-' '0' .. '9' '0' .. '9'
            {
            matchRange('0','9'); 
            matchRange('0','9'); 
            matchRange('0','9'); 
            matchRange('0','9'); 
            match('-'); 
            matchRange('0','9'); 
            matchRange('0','9'); 
            match('-'); 
            matchRange('0','9'); 
            matchRange('0','9'); 
            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end DATE_STRING

    // $ANTLR start TIME_STRING
    public final void mTIME_STRING() throws RecognitionException {
        try {
            int _type = TIME_STRING;
            // JPQL.g:1516:7: ( '0' .. '9' ( '0' .. '9' )? ':' '0' .. '9' '0' .. '9' ':' '0' .. '9' '0' .. '9' '.' ( '0' .. '9' )* )
            // JPQL.g:1516:7: '0' .. '9' ( '0' .. '9' )? ':' '0' .. '9' '0' .. '9' ':' '0' .. '9' '0' .. '9' '.' ( '0' .. '9' )*
            {
            matchRange('0','9'); 
            // JPQL.g:1516:16: ( '0' .. '9' )?
            int alt29=2;
            int LA29_0 = input.LA(1);
            
            if ( ((LA29_0>='0' && LA29_0<='9')) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // JPQL.g:1516:17: '0' .. '9'
                    {
                    matchRange('0','9'); 
                    
                    }
                    break;
            
            }

            match(':'); 
            matchRange('0','9'); 
            matchRange('0','9'); 
            match(':'); 
            matchRange('0','9'); 
            matchRange('0','9'); 
            match('.'); 
            // JPQL.g:1516:76: ( '0' .. '9' )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);
                
                if ( ((LA30_0>='0' && LA30_0<='9')) ) {
                    alt30=1;
                }
                
            
                switch (alt30) {
            	case 1 :
            	    // JPQL.g:1516:76: '0' .. '9'
            	    {
            	    matchRange('0','9'); 
            	    
            	    }
            	    break;
            
            	default :
            	    break loop30;
                }
            } while (true);

            
            }
    
            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end TIME_STRING

    // $ANTLR start DOUBLE_SUFFIX
    public final void mDOUBLE_SUFFIX() throws RecognitionException {
        try {
            // JPQL.g:1521:7: ( 'd' )
            // JPQL.g:1521:7: 'd'
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
            // JPQL.g:1525:7: ( '=' )
            // JPQL.g:1525:7: '='
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
            // JPQL.g:1529:7: ( '>' )
            // JPQL.g:1529:7: '>'
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
            // JPQL.g:1533:7: ( '>=' )
            // JPQL.g:1533:7: '>='
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
            // JPQL.g:1537:7: ( '<' )
            // JPQL.g:1537:7: '<'
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
            // JPQL.g:1541:7: ( '<=' )
            // JPQL.g:1541:7: '<='
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
            // JPQL.g:1545:7: ( '<>' )
            // JPQL.g:1545:7: '<>'
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
            // JPQL.g:1549:7: ( '*' )
            // JPQL.g:1549:7: '*'
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
            // JPQL.g:1553:7: ( '/' )
            // JPQL.g:1553:7: '/'
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
            // JPQL.g:1557:7: ( '+' )
            // JPQL.g:1557:7: '+'
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
            // JPQL.g:1561:7: ( '-' )
            // JPQL.g:1561:7: '-'
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
            // JPQL.g:1566:7: ( '?' ( '1' .. '9' ) ( '0' .. '9' )* )
            // JPQL.g:1566:7: '?' ( '1' .. '9' ) ( '0' .. '9' )*
            {
            match('?'); 
            // JPQL.g:1566:11: ( '1' .. '9' )
            // JPQL.g:1566:12: '1' .. '9'
            {
            matchRange('1','9'); 
            
            }

            // JPQL.g:1566:22: ( '0' .. '9' )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);
                
                if ( ((LA31_0>='0' && LA31_0<='9')) ) {
                    alt31=1;
                }
                
            
                switch (alt31) {
            	case 1 :
            	    // JPQL.g:1566:23: '0' .. '9'
            	    {
            	    matchRange('0','9'); 
            	    
            	    }
            	    break;
            
            	default :
            	    break loop31;
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
            // JPQL.g:1570:7: ( ':' TEXTCHAR )
            // JPQL.g:1570:7: ':' TEXTCHAR
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
            // JPQL.g:1576:7: ( '\"' (~ ( '\"' ) )* '\"' )
            // JPQL.g:1576:7: '\"' (~ ( '\"' ) )* '\"'
            {
            match('\"'); 
            // JPQL.g:1576:11: (~ ( '\"' ) )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);
                
                if ( ((LA32_0>='\u0000' && LA32_0<='!')||(LA32_0>='#' && LA32_0<='\uFFFE')) ) {
                    alt32=1;
                }
                
            
                switch (alt32) {
            	case 1 :
            	    // JPQL.g:1576:12: ~ ( '\"' )
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
            	    break loop32;
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
            // JPQL.g:1580:7: ( '\\'' (~ ( '\\'' ) | ( '\\'\\'' ) )* '\\'' )
            // JPQL.g:1580:7: '\\'' (~ ( '\\'' ) | ( '\\'\\'' ) )* '\\''
            {
            match('\''); 
            // JPQL.g:1580:12: (~ ( '\\'' ) | ( '\\'\\'' ) )*
            loop33:
            do {
                int alt33=3;
                int LA33_0 = input.LA(1);
                
                if ( (LA33_0=='\'') ) {
                    int LA33_1 = input.LA(2);
                    
                    if ( (LA33_1=='\'') ) {
                        alt33=2;
                    }
                    
                
                }
                else if ( ((LA33_0>='\u0000' && LA33_0<='&')||(LA33_0>='(' && LA33_0<='\uFFFE')) ) {
                    alt33=1;
                }
                
            
                switch (alt33) {
            	case 1 :
            	    // JPQL.g:1580:13: ~ ( '\\'' )
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
            	    // JPQL.g:1580:24: ( '\\'\\'' )
            	    {
            	    // JPQL.g:1580:24: ( '\\'\\'' )
            	    // JPQL.g:1580:25: '\\'\\''
            	    {
            	    match("\'\'"); 

            	    
            	    }

            	    
            	    }
            	    break;
            
            	default :
            	    break loop33;
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
        // JPQL.g:1:10: ( ABS | ALL | AND | ANY | AS | ASC | AVG | BETWEEN | BOTH | BY | CASE | COALESCE | CONCAT | COUNT | CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | DESC | DELETE | DISTINCT | ELSE | EMPTY | END | ENTRY | ESCAPE | EXISTS | FALSE | FETCH | FUNC | FROM | GROUP | HAVING | IN | INDEX | INNER | IS | JOIN | KEY | LEADING | LEFT | LENGTH | LIKE | LOCATE | LOWER | MAX | MEMBER | MIN | MOD | NEW | NOT | NULL | NULLIF | OBJECT | OF | OR | ORDER | OUTER | SELECT | SET | SIZE | SQRT | SOME | SUBSTRING | SUM | THEN | TRAILING | TRIM | TRUE | TYPE | UNKNOWN | UPDATE | UPPER | VALUE | WHEN | WHERE | DOT | WS | LEFT_ROUND_BRACKET | LEFT_CURLY_BRACKET | RIGHT_ROUND_BRACKET | RIGHT_CURLY_BRACKET | COMMA | IDENT | HEX_LITERAL | INTEGER_LITERAL | LONG_LITERAL | OCTAL_LITERAL | DOUBLE_LITERAL | FLOAT_LITERAL | DATE_LITERAL | TIME_LITERAL | TIMESTAMP_LITERAL | DATE_STRING | TIME_STRING | EQUALS | GREATER_THAN | GREATER_THAN_EQUAL_TO | LESS_THAN | LESS_THAN_EQUAL_TO | NOT_EQUAL_TO | MULTIPLY | DIVIDE | PLUS | MINUS | POSITIONAL_PARAM | NAMED_PARAM | STRING_LITERAL_DOUBLE_QUOTED | STRING_LITERAL_SINGLE_QUOTED )
        int alt34=108;
        alt34 = dfa34.predict(input);
        switch (alt34) {
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
                // JPQL.g:1:53: CASE
                {
                mCASE(); 
                
                }
                break;
            case 12 :
                // JPQL.g:1:58: COALESCE
                {
                mCOALESCE(); 
                
                }
                break;
            case 13 :
                // JPQL.g:1:67: CONCAT
                {
                mCONCAT(); 
                
                }
                break;
            case 14 :
                // JPQL.g:1:74: COUNT
                {
                mCOUNT(); 
                
                }
                break;
            case 15 :
                // JPQL.g:1:80: CURRENT_DATE
                {
                mCURRENT_DATE(); 
                
                }
                break;
            case 16 :
                // JPQL.g:1:93: CURRENT_TIME
                {
                mCURRENT_TIME(); 
                
                }
                break;
            case 17 :
                // JPQL.g:1:106: CURRENT_TIMESTAMP
                {
                mCURRENT_TIMESTAMP(); 
                
                }
                break;
            case 18 :
                // JPQL.g:1:124: DESC
                {
                mDESC(); 
                
                }
                break;
            case 19 :
                // JPQL.g:1:129: DELETE
                {
                mDELETE(); 
                
                }
                break;
            case 20 :
                // JPQL.g:1:136: DISTINCT
                {
                mDISTINCT(); 
                
                }
                break;
            case 21 :
                // JPQL.g:1:145: ELSE
                {
                mELSE(); 
                
                }
                break;
            case 22 :
                // JPQL.g:1:150: EMPTY
                {
                mEMPTY(); 
                
                }
                break;
            case 23 :
                // JPQL.g:1:156: END
                {
                mEND(); 
                
                }
                break;
            case 24 :
                // JPQL.g:1:160: ENTRY
                {
                mENTRY(); 
                
                }
                break;
            case 25 :
                // JPQL.g:1:166: ESCAPE
                {
                mESCAPE(); 
                
                }
                break;
            case 26 :
                // JPQL.g:1:173: EXISTS
                {
                mEXISTS(); 
                
                }
                break;
            case 27 :
                // JPQL.g:1:180: FALSE
                {
                mFALSE(); 
                
                }
                break;
            case 28 :
                // JPQL.g:1:186: FETCH
                {
                mFETCH(); 
                
                }
                break;
            case 29 :
                // JPQL.g:1:192: FUNC
                {
                mFUNC(); 
                
                }
                break;
            case 30 :
                // JPQL.g:1:197: FROM
                {
                mFROM(); 
                
                }
                break;
            case 31 :
                // JPQL.g:1:202: GROUP
                {
                mGROUP(); 
                
                }
                break;
            case 32 :
                // JPQL.g:1:208: HAVING
                {
                mHAVING(); 
                
                }
                break;
            case 33 :
                // JPQL.g:1:215: IN
                {
                mIN(); 
                
                }
                break;
            case 34 :
                // JPQL.g:1:218: INDEX
                {
                mINDEX(); 
                
                }
                break;
            case 35 :
                // JPQL.g:1:224: INNER
                {
                mINNER(); 
                
                }
                break;
            case 36 :
                // JPQL.g:1:230: IS
                {
                mIS(); 
                
                }
                break;
            case 37 :
                // JPQL.g:1:233: JOIN
                {
                mJOIN(); 
                
                }
                break;
            case 38 :
                // JPQL.g:1:238: KEY
                {
                mKEY(); 
                
                }
                break;
            case 39 :
                // JPQL.g:1:242: LEADING
                {
                mLEADING(); 
                
                }
                break;
            case 40 :
                // JPQL.g:1:250: LEFT
                {
                mLEFT(); 
                
                }
                break;
            case 41 :
                // JPQL.g:1:255: LENGTH
                {
                mLENGTH(); 
                
                }
                break;
            case 42 :
                // JPQL.g:1:262: LIKE
                {
                mLIKE(); 
                
                }
                break;
            case 43 :
                // JPQL.g:1:267: LOCATE
                {
                mLOCATE(); 
                
                }
                break;
            case 44 :
                // JPQL.g:1:274: LOWER
                {
                mLOWER(); 
                
                }
                break;
            case 45 :
                // JPQL.g:1:280: MAX
                {
                mMAX(); 
                
                }
                break;
            case 46 :
                // JPQL.g:1:284: MEMBER
                {
                mMEMBER(); 
                
                }
                break;
            case 47 :
                // JPQL.g:1:291: MIN
                {
                mMIN(); 
                
                }
                break;
            case 48 :
                // JPQL.g:1:295: MOD
                {
                mMOD(); 
                
                }
                break;
            case 49 :
                // JPQL.g:1:299: NEW
                {
                mNEW(); 
                
                }
                break;
            case 50 :
                // JPQL.g:1:303: NOT
                {
                mNOT(); 
                
                }
                break;
            case 51 :
                // JPQL.g:1:307: NULL
                {
                mNULL(); 
                
                }
                break;
            case 52 :
                // JPQL.g:1:312: NULLIF
                {
                mNULLIF(); 
                
                }
                break;
            case 53 :
                // JPQL.g:1:319: OBJECT
                {
                mOBJECT(); 
                
                }
                break;
            case 54 :
                // JPQL.g:1:326: OF
                {
                mOF(); 
                
                }
                break;
            case 55 :
                // JPQL.g:1:329: OR
                {
                mOR(); 
                
                }
                break;
            case 56 :
                // JPQL.g:1:332: ORDER
                {
                mORDER(); 
                
                }
                break;
            case 57 :
                // JPQL.g:1:338: OUTER
                {
                mOUTER(); 
                
                }
                break;
            case 58 :
                // JPQL.g:1:344: SELECT
                {
                mSELECT(); 
                
                }
                break;
            case 59 :
                // JPQL.g:1:351: SET
                {
                mSET(); 
                
                }
                break;
            case 60 :
                // JPQL.g:1:355: SIZE
                {
                mSIZE(); 
                
                }
                break;
            case 61 :
                // JPQL.g:1:360: SQRT
                {
                mSQRT(); 
                
                }
                break;
            case 62 :
                // JPQL.g:1:365: SOME
                {
                mSOME(); 
                
                }
                break;
            case 63 :
                // JPQL.g:1:370: SUBSTRING
                {
                mSUBSTRING(); 
                
                }
                break;
            case 64 :
                // JPQL.g:1:380: SUM
                {
                mSUM(); 
                
                }
                break;
            case 65 :
                // JPQL.g:1:384: THEN
                {
                mTHEN(); 
                
                }
                break;
            case 66 :
                // JPQL.g:1:389: TRAILING
                {
                mTRAILING(); 
                
                }
                break;
            case 67 :
                // JPQL.g:1:398: TRIM
                {
                mTRIM(); 
                
                }
                break;
            case 68 :
                // JPQL.g:1:403: TRUE
                {
                mTRUE(); 
                
                }
                break;
            case 69 :
                // JPQL.g:1:408: TYPE
                {
                mTYPE(); 
                
                }
                break;
            case 70 :
                // JPQL.g:1:413: UNKNOWN
                {
                mUNKNOWN(); 
                
                }
                break;
            case 71 :
                // JPQL.g:1:421: UPDATE
                {
                mUPDATE(); 
                
                }
                break;
            case 72 :
                // JPQL.g:1:428: UPPER
                {
                mUPPER(); 
                
                }
                break;
            case 73 :
                // JPQL.g:1:434: VALUE
                {
                mVALUE(); 
                
                }
                break;
            case 74 :
                // JPQL.g:1:440: WHEN
                {
                mWHEN(); 
                
                }
                break;
            case 75 :
                // JPQL.g:1:445: WHERE
                {
                mWHERE(); 
                
                }
                break;
            case 76 :
                // JPQL.g:1:451: DOT
                {
                mDOT(); 
                
                }
                break;
            case 77 :
                // JPQL.g:1:455: WS
                {
                mWS(); 
                
                }
                break;
            case 78 :
                // JPQL.g:1:458: LEFT_ROUND_BRACKET
                {
                mLEFT_ROUND_BRACKET(); 
                
                }
                break;
            case 79 :
                // JPQL.g:1:477: LEFT_CURLY_BRACKET
                {
                mLEFT_CURLY_BRACKET(); 
                
                }
                break;
            case 80 :
                // JPQL.g:1:496: RIGHT_ROUND_BRACKET
                {
                mRIGHT_ROUND_BRACKET(); 
                
                }
                break;
            case 81 :
                // JPQL.g:1:516: RIGHT_CURLY_BRACKET
                {
                mRIGHT_CURLY_BRACKET(); 
                
                }
                break;
            case 82 :
                // JPQL.g:1:536: COMMA
                {
                mCOMMA(); 
                
                }
                break;
            case 83 :
                // JPQL.g:1:542: IDENT
                {
                mIDENT(); 
                
                }
                break;
            case 84 :
                // JPQL.g:1:548: HEX_LITERAL
                {
                mHEX_LITERAL(); 
                
                }
                break;
            case 85 :
                // JPQL.g:1:560: INTEGER_LITERAL
                {
                mINTEGER_LITERAL(); 
                
                }
                break;
            case 86 :
                // JPQL.g:1:576: LONG_LITERAL
                {
                mLONG_LITERAL(); 
                
                }
                break;
            case 87 :
                // JPQL.g:1:589: OCTAL_LITERAL
                {
                mOCTAL_LITERAL(); 
                
                }
                break;
            case 88 :
                // JPQL.g:1:603: DOUBLE_LITERAL
                {
                mDOUBLE_LITERAL(); 
                
                }
                break;
            case 89 :
                // JPQL.g:1:618: FLOAT_LITERAL
                {
                mFLOAT_LITERAL(); 
                
                }
                break;
            case 90 :
                // JPQL.g:1:632: DATE_LITERAL
                {
                mDATE_LITERAL(); 
                
                }
                break;
            case 91 :
                // JPQL.g:1:645: TIME_LITERAL
                {
                mTIME_LITERAL(); 
                
                }
                break;
            case 92 :
                // JPQL.g:1:658: TIMESTAMP_LITERAL
                {
                mTIMESTAMP_LITERAL(); 
                
                }
                break;
            case 93 :
                // JPQL.g:1:676: DATE_STRING
                {
                mDATE_STRING(); 
                
                }
                break;
            case 94 :
                // JPQL.g:1:688: TIME_STRING
                {
                mTIME_STRING(); 
                
                }
                break;
            case 95 :
                // JPQL.g:1:700: EQUALS
                {
                mEQUALS(); 
                
                }
                break;
            case 96 :
                // JPQL.g:1:707: GREATER_THAN
                {
                mGREATER_THAN(); 
                
                }
                break;
            case 97 :
                // JPQL.g:1:720: GREATER_THAN_EQUAL_TO
                {
                mGREATER_THAN_EQUAL_TO(); 
                
                }
                break;
            case 98 :
                // JPQL.g:1:742: LESS_THAN
                {
                mLESS_THAN(); 
                
                }
                break;
            case 99 :
                // JPQL.g:1:752: LESS_THAN_EQUAL_TO
                {
                mLESS_THAN_EQUAL_TO(); 
                
                }
                break;
            case 100 :
                // JPQL.g:1:771: NOT_EQUAL_TO
                {
                mNOT_EQUAL_TO(); 
                
                }
                break;
            case 101 :
                // JPQL.g:1:784: MULTIPLY
                {
                mMULTIPLY(); 
                
                }
                break;
            case 102 :
                // JPQL.g:1:793: DIVIDE
                {
                mDIVIDE(); 
                
                }
                break;
            case 103 :
                // JPQL.g:1:800: PLUS
                {
                mPLUS(); 
                
                }
                break;
            case 104 :
                // JPQL.g:1:805: MINUS
                {
                mMINUS(); 
                
                }
                break;
            case 105 :
                // JPQL.g:1:811: POSITIONAL_PARAM
                {
                mPOSITIONAL_PARAM(); 
                
                }
                break;
            case 106 :
                // JPQL.g:1:828: NAMED_PARAM
                {
                mNAMED_PARAM(); 
                
                }
                break;
            case 107 :
                // JPQL.g:1:840: STRING_LITERAL_DOUBLE_QUOTED
                {
                mSTRING_LITERAL_DOUBLE_QUOTED(); 
                
                }
                break;
            case 108 :
                // JPQL.g:1:869: STRING_LITERAL_SINGLE_QUOTED
                {
                mSTRING_LITERAL_SINGLE_QUOTED(); 
                
                }
                break;
        
        }
    
    }


    protected DFA17 dfa17 = new DFA17(this);
    protected DFA20 dfa20 = new DFA20(this);
    protected DFA34 dfa34 = new DFA34(this);
    static final String DFA17_eotS =
        "\2\uffff\1\5\3\uffff";
    static final String DFA17_eofS =
        "\6\uffff";
    static final String DFA17_minS =
        "\1\55\2\56\3\uffff";
    static final String DFA17_maxS =
        "\3\71\3\uffff";
    static final String DFA17_acceptS =
        "\3\uffff\1\2\1\1\1\3";
    static final String DFA17_specialS =
        "\6\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\1\1\3\1\uffff\12\2",
            "\1\3\1\uffff\12\2",
            "\1\4\1\uffff\12\2",
            "",
            "",
            ""
    };
    
    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;
    
    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }
    
    class DFA17 extends DFA {
    
        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "1471:1: fragment NUMERIC_DIGITS : ( ( MINUS )? ( '0' .. '9' )+ '.' ( '0' .. '9' )* | ( MINUS )? '.' ( '0' .. '9' )+ | ( MINUS )? ( '0' .. '9' )+ );";
        }
    }
    static final String DFA20_eotS =
        "\11\uffff";
    static final String DFA20_eofS =
        "\11\uffff";
    static final String DFA20_minS =
        "\1\55\2\56\1\60\1\uffff\1\60\1\uffff\2\60";
    static final String DFA20_maxS =
        "\2\71\1\146\1\71\1\uffff\1\146\1\uffff\2\146";
    static final String DFA20_acceptS =
        "\4\uffff\1\1\1\uffff\1\2\2\uffff";
    static final String DFA20_specialS =
        "\11\uffff}>";
    static final String[] DFA20_transitionS = {
            "\1\1\1\3\1\uffff\12\2",
            "\1\3\1\uffff\12\2",
            "\1\5\1\uffff\12\2\13\uffff\1\4\37\uffff\1\4\1\6",
            "\12\7",
            "",
            "\12\10\13\uffff\1\4\37\uffff\1\4\1\6",
            "",
            "\12\7\13\uffff\1\4\37\uffff\1\4\1\6",
            "\12\10\13\uffff\1\4\37\uffff\1\4\1\6"
    };
    
    static final short[] DFA20_eot = DFA.unpackEncodedString(DFA20_eotS);
    static final short[] DFA20_eof = DFA.unpackEncodedString(DFA20_eofS);
    static final char[] DFA20_min = DFA.unpackEncodedStringToUnsignedChars(DFA20_minS);
    static final char[] DFA20_max = DFA.unpackEncodedStringToUnsignedChars(DFA20_maxS);
    static final short[] DFA20_accept = DFA.unpackEncodedString(DFA20_acceptS);
    static final short[] DFA20_special = DFA.unpackEncodedString(DFA20_specialS);
    static final short[][] DFA20_transition;
    
    static {
        int numStates = DFA20_transitionS.length;
        DFA20_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA20_transition[i] = DFA.unpackEncodedString(DFA20_transitionS[i]);
        }
    }
    
    class DFA20 extends DFA {
    
        public DFA20(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 20;
            this.eot = DFA20_eot;
            this.eof = DFA20_eof;
            this.min = DFA20_min;
            this.max = DFA20_max;
            this.accept = DFA20_accept;
            this.special = DFA20_special;
            this.transition = DFA20_transition;
        }
        public String getDescription() {
            return "1482:1: FLOAT_LITERAL : ( NUMERIC_DIGITS EXPONENT ( FLOAT_SUFFIX )? | NUMERIC_DIGITS FLOAT_SUFFIX );";
        }
    }
    static final String DFA34_eotS =
        "\1\uffff\24\34\1\141\2\uffff\1\142\4\uffff\1\147\1\156\1\147\1\uffff"+
        "\1\164\1\167\7\uffff\2\34\1\173\3\34\1\u0080\21\34\1\u0098\1\u0099"+
        "\15\34\1\u00ab\1\34\1\u00ad\14\34\1\150\5\uffff\1\u00c2\2\uffff"+
        "\1\150\3\uffff\1\150\1\uffff\1\147\1\uffff\2\147\5\uffff\1\u00c9"+
        "\1\u00ca\1\u00cb\1\uffff\1\u00cc\1\u00cd\1\u00ce\1\34\1\uffff\13"+
        "\34\1\u00db\13\34\2\uffff\1\34\1\u00e8\6\34\1\u00ef\1\u00f0\1\34"+
        "\1\u00f2\1\34\1\u00f4\1\u00f5\2\34\1\uffff\1\34\1\uffff\1\u00f9"+
        "\3\34\1\u00fd\14\34\2\uffff\1\u00c2\1\uffff\2\150\1\u00c2\1\150"+
        "\2\147\6\uffff\1\u010e\4\34\1\u0113\1\34\1\u0115\4\34\1\uffff\1"+
        "\34\1\u011b\1\34\1\u011d\2\34\1\u0120\4\34\1\u0125\1\uffff\2\34"+
        "\1\u0128\2\34\1\u012b\2\uffff\1\34\1\uffff\1\u012e\2\uffff\3\34"+
        "\1\uffff\1\34\1\u0133\1\u0134\1\uffff\1\34\1\u0136\1\u0137\1\34"+
        "\1\u0139\1\u013a\1\u013b\5\34\1\u0141\1\u00c2\1\150\1\147\1\uffff"+
        "\3\34\1\u0146\1\uffff\1\34\1\uffff\3\34\1\u014b\1\34\1\uffff\1\u014d"+
        "\1\uffff\1\u014e\1\u014f\1\uffff\1\u0150\1\34\1\u0152\1\u0153\1"+
        "\uffff\2\34\1\uffff\1\u0156\1\34\1\uffff\2\34\1\uffff\1\34\1\u015b"+
        "\1\u015c\1\34\2\uffff\1\34\2\uffff\1\34\3\uffff\1\34\1\u0161\1\34"+
        "\1\u0163\1\u0164\2\uffff\2\34\1\u0167\1\uffff\1\34\1\u0169\1\34"+
        "\1\u016b\1\uffff\1\u016c\4\uffff\1\u016d\2\uffff\1\34\1\u016f\1"+
        "\uffff\1\u0170\1\u0171\1\u0172\1\u0173\2\uffff\1\34\1\u0175\2\34"+
        "\1\uffff\1\u0178\2\uffff\1\u0179\1\34\1\uffff\1\34\1\uffff\1\34"+
        "\3\uffff\1\u017d\5\uffff\1\34\1\uffff\1\34\1\u0180\2\uffff\1\u0181"+
        "\1\34\1\u0184\1\uffff\1\34\1\u0186\2\uffff\2\34\1\uffff\1\u0189"+
        "\1\uffff\2\34\1\uffff\2\34\1\u018f\1\u0190\1\34\2\uffff\3\34\1\u0195"+
        "\1\uffff";
    static final String DFA34_eofS =
        "\u0196\uffff";
    static final String DFA34_minS =
        "\1\11\1\142\1\145\1\141\1\145\1\154\1\141\1\162\1\141\1\156\1\157"+
        "\2\145\1\141\1\145\1\142\1\145\1\150\1\156\1\141\1\150\1\60\2\uffff"+
        "\1\144\4\uffff\3\56\1\uffff\2\75\7\uffff\1\154\1\163\1\44\1\144"+
        "\1\147\1\164\1\44\1\164\1\141\1\163\1\162\1\154\1\163\1\143\1\144"+
        "\1\151\1\163\1\160\1\157\1\154\1\164\1\156\1\157\1\166\2\44\1\151"+
        "\1\171\1\141\1\143\1\153\1\144\1\156\1\155\1\170\1\154\1\164\1\167"+
        "\1\152\1\44\1\164\1\44\1\142\1\162\1\155\1\154\1\172\1\160\1\141"+
        "\1\145\1\153\1\144\1\154\1\145\1\60\2\uffff\1\11\2\uffff\1\56\2"+
        "\uffff\1\60\3\uffff\1\56\1\uffff\1\56\1\60\2\56\5\uffff\3\44\1\uffff"+
        "\3\44\1\150\1\uffff\1\167\1\154\1\143\1\156\1\145\1\162\1\143\1"+
        "\145\1\164\1\141\1\162\1\44\1\163\1\145\1\164\1\155\1\163\2\143"+
        "\1\165\1\151\2\145\2\uffff\1\156\1\44\1\144\1\147\1\164\1\145\1"+
        "\141\1\145\2\44\1\142\1\44\1\154\2\44\2\145\1\uffff\1\145\1\uffff"+
        "\1\44\1\163\1\164\1\145\1\44\3\145\1\151\1\155\1\145\2\156\1\145"+
        "\1\141\1\165\1\156\2\uffff\1\56\1\uffff\1\56\1\60\4\56\6\uffff\1"+
        "\44\2\145\1\141\1\164\1\44\1\145\1\44\1\164\1\151\1\160\1\171\1"+
        "\uffff\1\164\1\44\1\171\1\44\1\145\1\150\1\44\1\160\1\156\1\170"+
        "\1\162\1\44\1\uffff\1\151\1\164\1\44\1\162\1\164\1\44\2\uffff\1"+
        "\145\1\uffff\1\44\2\uffff\1\143\2\162\1\uffff\1\164\2\44\1\uffff"+
        "\1\143\2\44\1\154\3\44\1\157\1\162\1\164\2\145\1\44\3\55\1\uffff"+
        "\1\145\1\163\1\164\1\44\1\uffff\1\156\1\uffff\1\145\1\156\1\145"+
        "\1\44\1\163\1\uffff\1\44\1\uffff\2\44\1\uffff\1\44\1\147\2\44\1"+
        "\uffff\1\156\1\150\1\uffff\1\44\1\145\1\uffff\1\162\1\146\1\uffff"+
        "\1\164\2\44\1\162\2\uffff\1\164\2\uffff\1\151\3\uffff\1\167\1\44"+
        "\1\145\2\44\2\uffff\1\156\1\143\1\44\1\uffff\1\164\1\44\1\143\1"+
        "\44\1\uffff\1\44\4\uffff\1\44\2\uffff\1\147\1\44\1\uffff\4\44\2"+
        "\uffff\1\151\1\44\2\156\1\uffff\1\44\2\uffff\1\44\1\145\1\uffff"+
        "\1\137\1\uffff\1\164\3\uffff\1\44\5\uffff\1\156\1\uffff\1\147\1"+
        "\44\2\uffff\1\44\1\144\1\44\1\uffff\1\147\1\44\2\uffff\1\151\1\141"+
        "\1\uffff\1\44\1\uffff\1\155\1\164\1\uffff\2\145\2\44\1\164\2\uffff"+
        "\1\141\1\155\1\160\1\44\1\uffff";
    static final String DFA34_maxS =
        "\1\ufffe\1\166\1\171\1\165\1\151\1\170\1\165\1\162\1\141\1\163\1"+
        "\157\1\145\2\157\3\165\1\171\1\160\1\141\1\150\1\71\2\uffff\1\164"+
        "\4\uffff\1\170\1\71\1\154\1\uffff\1\75\1\76\7\uffff\1\154\1\163"+
        "\1\ufffe\1\171\1\147\1\164\1\ufffe\1\164\1\165\1\163\1\162\2\163"+
        "\1\143\1\164\1\151\1\163\1\160\1\157\1\154\1\164\1\156\1\157\1\166"+
        "\2\ufffe\1\151\1\171\1\156\1\167\1\153\1\144\1\156\1\155\1\170\1"+
        "\154\1\164\1\167\1\152\1\ufffe\1\164\1\ufffe\1\155\1\162\1\155\1"+
        "\164\1\172\1\160\1\165\1\145\1\153\1\160\1\154\1\145\1\146\2\uffff"+
        "\1\163\2\uffff\1\146\2\uffff\1\146\3\uffff\1\146\1\uffff\1\154\1"+
        "\71\2\154\5\uffff\3\ufffe\1\uffff\3\ufffe\1\150\1\uffff\1\167\1"+
        "\154\1\143\1\156\1\145\1\162\1\143\1\145\1\164\1\141\1\162\1\ufffe"+
        "\1\163\1\145\1\164\1\155\1\163\2\143\1\165\1\151\2\145\2\uffff\1"+
        "\156\1\ufffe\1\144\1\147\1\164\1\145\1\141\1\145\2\ufffe\1\142\1"+
        "\ufffe\1\154\2\ufffe\2\145\1\uffff\1\145\1\uffff\1\ufffe\1\163\1"+
        "\164\1\145\1\ufffe\3\145\1\151\1\155\1\145\2\156\1\145\1\141\1\165"+
        "\1\162\2\uffff\1\146\1\uffff\4\146\2\154\6\uffff\1\ufffe\2\145\1"+
        "\141\1\164\1\ufffe\1\145\1\ufffe\1\164\1\151\1\160\1\171\1\uffff"+
        "\1\164\1\ufffe\1\171\1\ufffe\1\145\1\150\1\ufffe\1\160\1\156\1\170"+
        "\1\162\1\ufffe\1\uffff\1\151\1\164\1\ufffe\1\162\1\164\1\ufffe\2"+
        "\uffff\1\145\1\uffff\1\ufffe\2\uffff\1\143\2\162\1\uffff\1\164\2"+
        "\ufffe\1\uffff\1\143\2\ufffe\1\154\3\ufffe\1\157\1\162\1\164\2\145"+
        "\1\ufffe\2\146\1\154\1\uffff\1\145\1\163\1\164\1\ufffe\1\uffff\1"+
        "\156\1\uffff\1\145\1\156\1\145\1\ufffe\1\163\1\uffff\1\ufffe\1\uffff"+
        "\2\ufffe\1\uffff\1\ufffe\1\147\2\ufffe\1\uffff\1\156\1\150\1\uffff"+
        "\1\ufffe\1\145\1\uffff\1\162\1\146\1\uffff\1\164\2\ufffe\1\162\2"+
        "\uffff\1\164\2\uffff\1\151\3\uffff\1\167\1\ufffe\1\145\2\ufffe\2"+
        "\uffff\1\156\1\143\1\ufffe\1\uffff\1\164\1\ufffe\1\143\1\ufffe\1"+
        "\uffff\1\ufffe\4\uffff\1\ufffe\2\uffff\1\147\1\ufffe\1\uffff\4\ufffe"+
        "\2\uffff\1\151\1\ufffe\2\156\1\uffff\1\ufffe\2\uffff\1\ufffe\1\145"+
        "\1\uffff\1\137\1\uffff\1\164\3\uffff\1\ufffe\5\uffff\1\156\1\uffff"+
        "\1\147\1\ufffe\2\uffff\1\ufffe\1\164\1\ufffe\1\uffff\1\147\1\ufffe"+
        "\2\uffff\1\151\1\141\1\uffff\1\ufffe\1\uffff\1\155\1\164\1\uffff"+
        "\2\145\2\ufffe\1\164\2\uffff\1\141\1\155\1\160\1\ufffe\1\uffff";
    static final String DFA34_acceptS =
        "\26\uffff\1\115\1\116\1\uffff\1\120\1\121\1\122\1\123\3\uffff\1"+
        "\137\2\uffff\1\145\1\146\1\147\1\151\1\152\1\153\1\154\67\uffff"+
        "\1\114\1\117\1\uffff\1\132\1\124\1\uffff\1\125\1\130\1\uffff\1\136"+
        "\1\126\1\131\1\uffff\1\150\4\uffff\1\141\1\140\1\144\1\143\1\142"+
        "\3\uffff\1\5\4\uffff\1\12\27\uffff\1\41\1\44\21\uffff\1\67\1\uffff"+
        "\1\66\21\uffff\1\134\1\133\1\uffff\1\127\6\uffff\1\2\1\1\1\6\1\3"+
        "\1\4\1\7\14\uffff\1\27\14\uffff\1\46\6\uffff\1\60\1\57\1\uffff\1"+
        "\55\1\uffff\1\62\1\61\3\uffff\1\100\3\uffff\1\73\20\uffff\1\11\4"+
        "\uffff\1\13\1\uffff\1\22\5\uffff\1\25\1\uffff\1\36\2\uffff\1\35"+
        "\4\uffff\1\45\2\uffff\1\50\2\uffff\1\52\2\uffff\1\63\4\uffff\1\75"+
        "\1\76\1\uffff\1\74\1\105\1\uffff\1\103\1\104\1\101\5\uffff\1\112"+
        "\1\135\3\uffff\1\16\4\uffff\1\30\1\uffff\1\26\1\33\1\34\1\37\1\uffff"+
        "\1\42\1\43\2\uffff\1\54\4\uffff\1\70\1\71\4\uffff\1\110\1\uffff"+
        "\1\111\1\113\2\uffff\1\15\1\uffff\1\23\1\uffff\1\31\1\32\1\40\1"+
        "\uffff\1\51\1\53\1\56\1\64\1\65\1\uffff\1\72\2\uffff\1\107\1\10"+
        "\3\uffff\1\47\2\uffff\1\106\1\14\2\uffff\1\24\1\uffff\1\102\2\uffff"+
        "\1\77\5\uffff\1\20\1\17\4\uffff\1\21";
    static final String DFA34_specialS =
        "\u0196\uffff}>";
    static final String[] DFA34_transitionS = {
            "\2\26\2\uffff\1\26\22\uffff\1\26\1\uffff\1\50\1\uffff\1\34\2"+
            "\uffff\1\51\1\27\1\31\1\43\1\45\1\33\1\36\1\25\1\44\1\35\11"+
            "\37\1\47\1\uffff\1\42\1\40\1\41\1\46\1\uffff\32\34\4\uffff\1"+
            "\34\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13"+
            "\1\14\1\15\1\16\1\17\3\34\1\20\1\21\1\22\1\23\1\24\3\34\1\30"+
            "\1\uffff\1\32\2\uffff\uff7f\34",
            "\1\53\11\uffff\1\52\1\uffff\1\55\4\uffff\1\54\2\uffff\1\56",
            "\1\61\11\uffff\1\57\11\uffff\1\60",
            "\1\63\15\uffff\1\62\5\uffff\1\64",
            "\1\65\3\uffff\1\66",
            "\1\72\1\73\1\70\4\uffff\1\67\4\uffff\1\71",
            "\1\75\3\uffff\1\76\14\uffff\1\74\2\uffff\1\77",
            "\1\100",
            "\1\101",
            "\1\102\4\uffff\1\103",
            "\1\104",
            "\1\105",
            "\1\106\3\uffff\1\110\5\uffff\1\107",
            "\1\114\3\uffff\1\113\3\uffff\1\112\5\uffff\1\111",
            "\1\117\11\uffff\1\116\5\uffff\1\115",
            "\1\120\3\uffff\1\123\13\uffff\1\121\2\uffff\1\122",
            "\1\127\3\uffff\1\130\5\uffff\1\126\1\uffff\1\125\3\uffff\1\124",
            "\1\133\11\uffff\1\132\6\uffff\1\131",
            "\1\134\1\uffff\1\135",
            "\1\136",
            "\1\137",
            "\12\140",
            "",
            "",
            "\1\144\17\uffff\1\143",
            "",
            "",
            "",
            "",
            "\1\151\1\uffff\10\146\2\155\1\152\12\uffff\1\154\6\uffff\1\153"+
            "\13\uffff\1\145\13\uffff\1\150\2\154\5\uffff\1\153\13\uffff"+
            "\1\145",
            "\1\160\1\uffff\1\157\11\161",
            "\1\151\1\uffff\12\162\1\152\12\uffff\1\154\6\uffff\1\153\27"+
            "\uffff\1\150\2\154\5\uffff\1\153",
            "",
            "\1\163",
            "\1\166\1\165",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\170",
            "\1\171",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\2\34\1\172\27\34"+
            "\5\uffff\uff7f\34",
            "\1\174\24\uffff\1\175",
            "\1\176",
            "\1\177",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0081",
            "\1\u0082\14\uffff\1\u0083\6\uffff\1\u0084",
            "\1\u0085",
            "\1\u0086",
            "\1\u0088\6\uffff\1\u0087",
            "\1\u0089",
            "\1\u008a",
            "\1\u008c\17\uffff\1\u008b",
            "\1\u008d",
            "\1\u008e",
            "\1\u008f",
            "\1\u0090",
            "\1\u0091",
            "\1\u0092",
            "\1\u0093",
            "\1\u0094",
            "\1\u0095",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\3\34\1\u0096\11\34"+
            "\1\u0097\14\34\5\uffff\uff7f\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u009a",
            "\1\u009b",
            "\1\u009c\4\uffff\1\u009e\7\uffff\1\u009d",
            "\1\u00a0\23\uffff\1\u009f",
            "\1\u00a1",
            "\1\u00a2",
            "\1\u00a3",
            "\1\u00a4",
            "\1\u00a5",
            "\1\u00a6",
            "\1\u00a7",
            "\1\u00a8",
            "\1\u00a9",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\3\34\1\u00aa\26\34"+
            "\5\uffff\uff7f\34",
            "\1\u00ac",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u00af\12\uffff\1\u00ae",
            "\1\u00b0",
            "\1\u00b1",
            "\1\u00b3\7\uffff\1\u00b2",
            "\1\u00b4",
            "\1\u00b5",
            "\1\u00b6\7\uffff\1\u00b7\13\uffff\1\u00b8",
            "\1\u00b9",
            "\1\u00ba",
            "\1\u00bc\13\uffff\1\u00bb",
            "\1\u00bd",
            "\1\u00be",
            "\12\140\13\uffff\1\154\37\uffff\2\154",
            "",
            "",
            "\1\u00c0\26\uffff\1\u00c0\122\uffff\1\u00bf",
            "",
            "",
            "\1\151\1\uffff\10\u00c1\2\u00c3\1\152\12\uffff\1\154\36\uffff"+
            "\1\150\2\154",
            "",
            "",
            "\12\u00c4\13\uffff\1\154\37\uffff\2\154",
            "",
            "",
            "",
            "\1\151\1\uffff\12\u00c3\1\152\12\uffff\1\154\37\uffff\2\154",
            "",
            "\1\151\1\uffff\10\u00c5\2\u00c6\13\uffff\1\154\6\uffff\1\153"+
            "\27\uffff\1\150\2\154\5\uffff\1\153",
            "\12\140",
            "\1\151\1\uffff\12\u00c7\13\uffff\1\154\6\uffff\1\153\27\uffff"+
            "\1\150\2\154\5\uffff\1\153",
            "\1\151\1\uffff\12\u00c8\1\152\12\uffff\1\154\6\uffff\1\153\27"+
            "\uffff\1\150\2\154\5\uffff\1\153",
            "",
            "",
            "",
            "",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u00cf",
            "",
            "\1\u00d0",
            "\1\u00d1",
            "\1\u00d2",
            "\1\u00d3",
            "\1\u00d4",
            "\1\u00d5",
            "\1\u00d6",
            "\1\u00d7",
            "\1\u00d8",
            "\1\u00d9",
            "\1\u00da",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u00dc",
            "\1\u00dd",
            "\1\u00de",
            "\1\u00df",
            "\1\u00e0",
            "\1\u00e1",
            "\1\u00e2",
            "\1\u00e3",
            "\1\u00e4",
            "\1\u00e5",
            "\1\u00e6",
            "",
            "",
            "\1\u00e7",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u00e9",
            "\1\u00ea",
            "\1\u00eb",
            "\1\u00ec",
            "\1\u00ed",
            "\1\u00ee",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u00f1",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u00f3",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u00f6",
            "\1\u00f7",
            "",
            "\1\u00f8",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u00fa",
            "\1\u00fb",
            "\1\u00fc",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u00fe",
            "\1\u00ff",
            "\1\u0100",
            "\1\u0101",
            "\1\u0102",
            "\1\u0103",
            "\1\u0104",
            "\1\u0105",
            "\1\u0106",
            "\1\u0107",
            "\1\u0108",
            "\1\u010a\3\uffff\1\u0109",
            "",
            "",
            "\1\151\1\uffff\10\u010b\2\u010c\13\uffff\1\154\36\uffff\1\150"+
            "\2\154",
            "",
            "\1\151\1\uffff\12\u010c\13\uffff\1\154\37\uffff\2\154",
            "\12\u00c4\13\uffff\1\154\37\uffff\2\154",
            "\1\151\1\uffff\10\u00c5\2\u00c6\13\uffff\1\154\36\uffff\1\150"+
            "\2\154",
            "\1\151\1\uffff\12\u00c6\13\uffff\1\154\37\uffff\2\154",
            "\1\151\1\uffff\12\u00c7\13\uffff\1\154\6\uffff\1\153\27\uffff"+
            "\1\150\2\154\5\uffff\1\153",
            "\1\151\1\uffff\12\u010d\13\uffff\1\154\6\uffff\1\153\27\uffff"+
            "\1\150\2\154\5\uffff\1\153",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u010f",
            "\1\u0110",
            "\1\u0111",
            "\1\u0112",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0114",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0116",
            "\1\u0117",
            "\1\u0118",
            "\1\u0119",
            "",
            "\1\u011a",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u011c",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u011e",
            "\1\u011f",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0121",
            "\1\u0122",
            "\1\u0123",
            "\1\u0124",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\u0126",
            "\1\u0127",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0129",
            "\1\u012a",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "",
            "\1\u012c",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\10\34\1\u012d\21"+
            "\34\5\uffff\uff7f\34",
            "",
            "",
            "\1\u012f",
            "\1\u0130",
            "\1\u0131",
            "",
            "\1\u0132",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\u0135",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0138",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u013c",
            "\1\u013d",
            "\1\u013e",
            "\1\u013f",
            "\1\u0140",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0142\1\151\1\uffff\10\u00c5\2\u00c6\13\uffff\1\154\36\uffff"+
            "\1\150\2\154",
            "\1\u0142\1\151\1\uffff\12\u00c6\13\uffff\1\154\37\uffff\2\154",
            "\1\u0142\1\151\1\uffff\12\u00c7\13\uffff\1\154\6\uffff\1\153"+
            "\27\uffff\1\150\2\154\5\uffff\1\153",
            "",
            "\1\u0143",
            "\1\u0144",
            "\1\u0145",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\u0147",
            "",
            "\1\u0148",
            "\1\u0149",
            "\1\u014a",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u014c",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0151",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\u0154",
            "\1\u0155",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0157",
            "",
            "\1\u0158",
            "\1\u0159",
            "",
            "\1\u015a",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u015d",
            "",
            "",
            "\1\u015e",
            "",
            "",
            "\1\u015f",
            "",
            "",
            "",
            "\1\u0160",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0162",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "",
            "\1\u0165",
            "\1\u0166",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\u0168",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u016a",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "",
            "",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "",
            "\1\u016e",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "",
            "\1\u0174",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0176",
            "\1\u0177",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u017a",
            "",
            "\1\u017b",
            "",
            "\1\u017c",
            "",
            "",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "",
            "",
            "",
            "",
            "\1\u017e",
            "",
            "\1\u017f",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0183\17\uffff\1\u0182",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\u0185",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "",
            "\1\u0187",
            "\1\u0188",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\u018a",
            "\1\u018b",
            "",
            "\1\u018c",
            "\1\u018d",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\22\34\1\u018e\7\34"+
            "\5\uffff\uff7f\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0191",
            "",
            "",
            "\1\u0192",
            "\1\u0193",
            "\1\u0194",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            ""
    };
    
    static final short[] DFA34_eot = DFA.unpackEncodedString(DFA34_eotS);
    static final short[] DFA34_eof = DFA.unpackEncodedString(DFA34_eofS);
    static final char[] DFA34_min = DFA.unpackEncodedStringToUnsignedChars(DFA34_minS);
    static final char[] DFA34_max = DFA.unpackEncodedStringToUnsignedChars(DFA34_maxS);
    static final short[] DFA34_accept = DFA.unpackEncodedString(DFA34_acceptS);
    static final short[] DFA34_special = DFA.unpackEncodedString(DFA34_specialS);
    static final short[][] DFA34_transition;
    
    static {
        int numStates = DFA34_transitionS.length;
        DFA34_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA34_transition[i] = DFA.unpackEncodedString(DFA34_transitionS[i]);
        }
    }
    
    class DFA34 extends DFA {
    
        public DFA34(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 34;
            this.eot = DFA34_eot;
            this.eof = DFA34_eof;
            this.min = DFA34_min;
            this.max = DFA34_max;
            this.accept = DFA34_accept;
            this.special = DFA34_special;
            this.transition = DFA34_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( ABS | ALL | AND | ANY | AS | ASC | AVG | BETWEEN | BOTH | BY | CASE | COALESCE | CONCAT | COUNT | CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | DESC | DELETE | DISTINCT | ELSE | EMPTY | END | ENTRY | ESCAPE | EXISTS | FALSE | FETCH | FUNC | FROM | GROUP | HAVING | IN | INDEX | INNER | IS | JOIN | KEY | LEADING | LEFT | LENGTH | LIKE | LOCATE | LOWER | MAX | MEMBER | MIN | MOD | NEW | NOT | NULL | NULLIF | OBJECT | OF | OR | ORDER | OUTER | SELECT | SET | SIZE | SQRT | SOME | SUBSTRING | SUM | THEN | TRAILING | TRIM | TRUE | TYPE | UNKNOWN | UPDATE | UPPER | VALUE | WHEN | WHERE | DOT | WS | LEFT_ROUND_BRACKET | LEFT_CURLY_BRACKET | RIGHT_ROUND_BRACKET | RIGHT_CURLY_BRACKET | COMMA | IDENT | HEX_LITERAL | INTEGER_LITERAL | LONG_LITERAL | OCTAL_LITERAL | DOUBLE_LITERAL | FLOAT_LITERAL | DATE_LITERAL | TIME_LITERAL | TIMESTAMP_LITERAL | DATE_STRING | TIME_STRING | EQUALS | GREATER_THAN | GREATER_THAN_EQUAL_TO | LESS_THAN | LESS_THAN_EQUAL_TO | NOT_EQUAL_TO | MULTIPLY | DIVIDE | PLUS | MINUS | POSITIONAL_PARAM | NAMED_PARAM | STRING_LITERAL_DOUBLE_QUOTED | STRING_LITERAL_SINGLE_QUOTED );";
        }
    }
 

}