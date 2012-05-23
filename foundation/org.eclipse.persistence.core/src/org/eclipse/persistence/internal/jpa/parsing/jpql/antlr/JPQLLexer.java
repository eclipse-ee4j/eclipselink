// $ANTLR 3.0 JPQL.g 2010-05-03 16:14:54

    package org.eclipse.persistence.internal.jpa.parsing.jpql.antlr;

    import org.eclipse.persistence.internal.jpa.parsing.jpql.InvalidIdentifierStartException;


import org.eclipse.persistence.internal.libraries.antlr.runtime.*;

public class JPQLLexer extends Lexer {
    public static final int EXPONENT=116;
    public static final int FLOAT_SUFFIX=117;
    public static final int DATE_STRING=118;
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
    public static final int LEADING=42;
    public static final int RIGHT_CURLY_BRACKET=108;
    public static final int EMPTY=25;
    public static final int INTEGER_SUFFIX=112;
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
    public static final int SQRT=64;
    public static final int MINUS=92;
    public static final int LONG_LITERAL=96;
    public static final int TRUE=72;
    public static final int JOIN=40;
    public static final int SUBSTRING=66;
    public static final int FLOAT_LITERAL=97;
    public static final int DOUBLE_SUFFIX=115;
    public static final int ANY=7;
    public static final int LOCATE=46;
    public static final int WHEN=78;
    public static final int DESC=21;
    public static final int BETWEEN=11;
    public static final int TREAT=70;

    // delegates
    // delegators

    public JPQLLexer() {;} 
    public JPQLLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public JPQLLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "JPQL.g"; }

    // $ANTLR start "ABS"
    public final void mABS() throws RecognitionException {
        try {
            int _type = ABS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:9:5: ( 'abs' )
            // JPQL.g:9:7: 'abs'
            {
            match("abs"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ABS"

    // $ANTLR start "ALL"
    public final void mALL() throws RecognitionException {
        try {
            int _type = ALL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:10:5: ( 'all' )
            // JPQL.g:10:7: 'all'
            {
            match("all"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ALL"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:11:5: ( 'and' )
            // JPQL.g:11:7: 'and'
            {
            match("and"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "ANY"
    public final void mANY() throws RecognitionException {
        try {
            int _type = ANY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:12:5: ( 'any' )
            // JPQL.g:12:7: 'any'
            {
            match("any"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ANY"

    // $ANTLR start "AS"
    public final void mAS() throws RecognitionException {
        try {
            int _type = AS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:13:4: ( 'as' )
            // JPQL.g:13:6: 'as'
            {
            match("as"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AS"

    // $ANTLR start "ASC"
    public final void mASC() throws RecognitionException {
        try {
            int _type = ASC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:14:5: ( 'asc' )
            // JPQL.g:14:7: 'asc'
            {
            match("asc"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASC"

    // $ANTLR start "AVG"
    public final void mAVG() throws RecognitionException {
        try {
            int _type = AVG;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:15:5: ( 'avg' )
            // JPQL.g:15:7: 'avg'
            {
            match("avg"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AVG"

    // $ANTLR start "BETWEEN"
    public final void mBETWEEN() throws RecognitionException {
        try {
            int _type = BETWEEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:16:9: ( 'between' )
            // JPQL.g:16:11: 'between'
            {
            match("between"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BETWEEN"

    // $ANTLR start "BOTH"
    public final void mBOTH() throws RecognitionException {
        try {
            int _type = BOTH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:17:6: ( 'both' )
            // JPQL.g:17:8: 'both'
            {
            match("both"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BOTH"

    // $ANTLR start "BY"
    public final void mBY() throws RecognitionException {
        try {
            int _type = BY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:18:4: ( 'by' )
            // JPQL.g:18:6: 'by'
            {
            match("by"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BY"

    // $ANTLR start "CASE"
    public final void mCASE() throws RecognitionException {
        try {
            int _type = CASE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:19:6: ( 'case' )
            // JPQL.g:19:8: 'case'
            {
            match("case"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CASE"

    // $ANTLR start "COALESCE"
    public final void mCOALESCE() throws RecognitionException {
        try {
            int _type = COALESCE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:20:10: ( 'coalesce' )
            // JPQL.g:20:12: 'coalesce'
            {
            match("coalesce"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COALESCE"

    // $ANTLR start "CONCAT"
    public final void mCONCAT() throws RecognitionException {
        try {
            int _type = CONCAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:21:8: ( 'concat' )
            // JPQL.g:21:10: 'concat'
            {
            match("concat"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CONCAT"

    // $ANTLR start "COUNT"
    public final void mCOUNT() throws RecognitionException {
        try {
            int _type = COUNT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:22:7: ( 'count' )
            // JPQL.g:22:9: 'count'
            {
            match("count"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COUNT"

    // $ANTLR start "CURRENT_DATE"
    public final void mCURRENT_DATE() throws RecognitionException {
        try {
            int _type = CURRENT_DATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:23:14: ( 'current_date' )
            // JPQL.g:23:16: 'current_date'
            {
            match("current_date"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CURRENT_DATE"

    // $ANTLR start "CURRENT_TIME"
    public final void mCURRENT_TIME() throws RecognitionException {
        try {
            int _type = CURRENT_TIME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:24:14: ( 'current_time' )
            // JPQL.g:24:16: 'current_time'
            {
            match("current_time"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CURRENT_TIME"

    // $ANTLR start "CURRENT_TIMESTAMP"
    public final void mCURRENT_TIMESTAMP() throws RecognitionException {
        try {
            int _type = CURRENT_TIMESTAMP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:25:19: ( 'current_timestamp' )
            // JPQL.g:25:21: 'current_timestamp'
            {
            match("current_timestamp"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CURRENT_TIMESTAMP"

    // $ANTLR start "DESC"
    public final void mDESC() throws RecognitionException {
        try {
            int _type = DESC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:26:6: ( 'desc' )
            // JPQL.g:26:8: 'desc'
            {
            match("desc"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DESC"

    // $ANTLR start "DELETE"
    public final void mDELETE() throws RecognitionException {
        try {
            int _type = DELETE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:27:8: ( 'delete' )
            // JPQL.g:27:10: 'delete'
            {
            match("delete"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DELETE"

    // $ANTLR start "DISTINCT"
    public final void mDISTINCT() throws RecognitionException {
        try {
            int _type = DISTINCT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:28:10: ( 'distinct' )
            // JPQL.g:28:12: 'distinct'
            {
            match("distinct"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DISTINCT"

    // $ANTLR start "ELSE"
    public final void mELSE() throws RecognitionException {
        try {
            int _type = ELSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:29:6: ( 'else' )
            // JPQL.g:29:8: 'else'
            {
            match("else"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ELSE"

    // $ANTLR start "EMPTY"
    public final void mEMPTY() throws RecognitionException {
        try {
            int _type = EMPTY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:30:7: ( 'empty' )
            // JPQL.g:30:9: 'empty'
            {
            match("empty"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EMPTY"

    // $ANTLR start "END"
    public final void mEND() throws RecognitionException {
        try {
            int _type = END;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:31:5: ( 'end' )
            // JPQL.g:31:7: 'end'
            {
            match("end"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "END"

    // $ANTLR start "ENTRY"
    public final void mENTRY() throws RecognitionException {
        try {
            int _type = ENTRY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:32:7: ( 'entry' )
            // JPQL.g:32:9: 'entry'
            {
            match("entry"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ENTRY"

    // $ANTLR start "ESCAPE"
    public final void mESCAPE() throws RecognitionException {
        try {
            int _type = ESCAPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:33:8: ( 'escape' )
            // JPQL.g:33:10: 'escape'
            {
            match("escape"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ESCAPE"

    // $ANTLR start "EXISTS"
    public final void mEXISTS() throws RecognitionException {
        try {
            int _type = EXISTS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:34:8: ( 'exists' )
            // JPQL.g:34:10: 'exists'
            {
            match("exists"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EXISTS"

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:35:7: ( 'false' )
            // JPQL.g:35:9: 'false'
            {
            match("false"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FALSE"

    // $ANTLR start "FETCH"
    public final void mFETCH() throws RecognitionException {
        try {
            int _type = FETCH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:36:7: ( 'fetch' )
            // JPQL.g:36:9: 'fetch'
            {
            match("fetch"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FETCH"

    // $ANTLR start "FUNC"
    public final void mFUNC() throws RecognitionException {
        try {
            int _type = FUNC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:37:6: ( 'func' )
            // JPQL.g:37:8: 'func'
            {
            match("func"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FUNC"

    // $ANTLR start "FROM"
    public final void mFROM() throws RecognitionException {
        try {
            int _type = FROM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:38:6: ( 'from' )
            // JPQL.g:38:8: 'from'
            {
            match("from"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FROM"

    // $ANTLR start "GROUP"
    public final void mGROUP() throws RecognitionException {
        try {
            int _type = GROUP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:39:7: ( 'group' )
            // JPQL.g:39:9: 'group'
            {
            match("group"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GROUP"

    // $ANTLR start "HAVING"
    public final void mHAVING() throws RecognitionException {
        try {
            int _type = HAVING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:40:8: ( 'having' )
            // JPQL.g:40:10: 'having'
            {
            match("having"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "HAVING"

    // $ANTLR start "IN"
    public final void mIN() throws RecognitionException {
        try {
            int _type = IN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:41:4: ( 'in' )
            // JPQL.g:41:6: 'in'
            {
            match("in"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IN"

    // $ANTLR start "INDEX"
    public final void mINDEX() throws RecognitionException {
        try {
            int _type = INDEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:42:7: ( 'index' )
            // JPQL.g:42:9: 'index'
            {
            match("index"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INDEX"

    // $ANTLR start "INNER"
    public final void mINNER() throws RecognitionException {
        try {
            int _type = INNER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:43:7: ( 'inner' )
            // JPQL.g:43:9: 'inner'
            {
            match("inner"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INNER"

    // $ANTLR start "IS"
    public final void mIS() throws RecognitionException {
        try {
            int _type = IS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:44:4: ( 'is' )
            // JPQL.g:44:6: 'is'
            {
            match("is"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IS"

    // $ANTLR start "JOIN"
    public final void mJOIN() throws RecognitionException {
        try {
            int _type = JOIN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:45:6: ( 'join' )
            // JPQL.g:45:8: 'join'
            {
            match("join"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "JOIN"

    // $ANTLR start "KEY"
    public final void mKEY() throws RecognitionException {
        try {
            int _type = KEY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:46:5: ( 'key' )
            // JPQL.g:46:7: 'key'
            {
            match("key"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "KEY"

    // $ANTLR start "LEADING"
    public final void mLEADING() throws RecognitionException {
        try {
            int _type = LEADING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:47:9: ( 'leading' )
            // JPQL.g:47:11: 'leading'
            {
            match("leading"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEADING"

    // $ANTLR start "LEFT"
    public final void mLEFT() throws RecognitionException {
        try {
            int _type = LEFT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:48:6: ( 'left' )
            // JPQL.g:48:8: 'left'
            {
            match("left"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEFT"

    // $ANTLR start "LENGTH"
    public final void mLENGTH() throws RecognitionException {
        try {
            int _type = LENGTH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:49:8: ( 'length' )
            // JPQL.g:49:10: 'length'
            {
            match("length"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LENGTH"

    // $ANTLR start "LIKE"
    public final void mLIKE() throws RecognitionException {
        try {
            int _type = LIKE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:50:6: ( 'like' )
            // JPQL.g:50:8: 'like'
            {
            match("like"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LIKE"

    // $ANTLR start "LOCATE"
    public final void mLOCATE() throws RecognitionException {
        try {
            int _type = LOCATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:51:8: ( 'locate' )
            // JPQL.g:51:10: 'locate'
            {
            match("locate"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LOCATE"

    // $ANTLR start "LOWER"
    public final void mLOWER() throws RecognitionException {
        try {
            int _type = LOWER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:52:7: ( 'lower' )
            // JPQL.g:52:9: 'lower'
            {
            match("lower"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LOWER"

    // $ANTLR start "MAX"
    public final void mMAX() throws RecognitionException {
        try {
            int _type = MAX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:53:5: ( 'max' )
            // JPQL.g:53:7: 'max'
            {
            match("max"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MAX"

    // $ANTLR start "MEMBER"
    public final void mMEMBER() throws RecognitionException {
        try {
            int _type = MEMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:54:8: ( 'member' )
            // JPQL.g:54:10: 'member'
            {
            match("member"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MEMBER"

    // $ANTLR start "MIN"
    public final void mMIN() throws RecognitionException {
        try {
            int _type = MIN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:55:5: ( 'min' )
            // JPQL.g:55:7: 'min'
            {
            match("min"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MIN"

    // $ANTLR start "MOD"
    public final void mMOD() throws RecognitionException {
        try {
            int _type = MOD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:56:5: ( 'mod' )
            // JPQL.g:56:7: 'mod'
            {
            match("mod"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MOD"

    // $ANTLR start "NEW"
    public final void mNEW() throws RecognitionException {
        try {
            int _type = NEW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:57:5: ( 'new' )
            // JPQL.g:57:7: 'new'
            {
            match("new"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NEW"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:58:5: ( 'not' )
            // JPQL.g:58:7: 'not'
            {
            match("not"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "NULL"
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:59:6: ( 'null' )
            // JPQL.g:59:8: 'null'
            {
            match("null"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NULL"

    // $ANTLR start "NULLIF"
    public final void mNULLIF() throws RecognitionException {
        try {
            int _type = NULLIF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:60:8: ( 'nullif' )
            // JPQL.g:60:10: 'nullif'
            {
            match("nullif"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NULLIF"

    // $ANTLR start "OBJECT"
    public final void mOBJECT() throws RecognitionException {
        try {
            int _type = OBJECT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:61:8: ( 'object' )
            // JPQL.g:61:10: 'object'
            {
            match("object"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OBJECT"

    // $ANTLR start "OF"
    public final void mOF() throws RecognitionException {
        try {
            int _type = OF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:62:4: ( 'of' )
            // JPQL.g:62:6: 'of'
            {
            match("of"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OF"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:63:4: ( 'or' )
            // JPQL.g:63:6: 'or'
            {
            match("or"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "ORDER"
    public final void mORDER() throws RecognitionException {
        try {
            int _type = ORDER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:64:7: ( 'order' )
            // JPQL.g:64:9: 'order'
            {
            match("order"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ORDER"

    // $ANTLR start "OUTER"
    public final void mOUTER() throws RecognitionException {
        try {
            int _type = OUTER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:65:7: ( 'outer' )
            // JPQL.g:65:9: 'outer'
            {
            match("outer"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OUTER"

    // $ANTLR start "SELECT"
    public final void mSELECT() throws RecognitionException {
        try {
            int _type = SELECT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:66:8: ( 'select' )
            // JPQL.g:66:10: 'select'
            {
            match("select"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SELECT"

    // $ANTLR start "SET"
    public final void mSET() throws RecognitionException {
        try {
            int _type = SET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:67:5: ( 'set' )
            // JPQL.g:67:7: 'set'
            {
            match("set"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SET"

    // $ANTLR start "SIZE"
    public final void mSIZE() throws RecognitionException {
        try {
            int _type = SIZE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:68:6: ( 'size' )
            // JPQL.g:68:8: 'size'
            {
            match("size"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SIZE"

    // $ANTLR start "SQRT"
    public final void mSQRT() throws RecognitionException {
        try {
            int _type = SQRT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:69:6: ( 'sqrt' )
            // JPQL.g:69:8: 'sqrt'
            {
            match("sqrt"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SQRT"

    // $ANTLR start "SOME"
    public final void mSOME() throws RecognitionException {
        try {
            int _type = SOME;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:70:6: ( 'some' )
            // JPQL.g:70:8: 'some'
            {
            match("some"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SOME"

    // $ANTLR start "SUBSTRING"
    public final void mSUBSTRING() throws RecognitionException {
        try {
            int _type = SUBSTRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:71:11: ( 'substring' )
            // JPQL.g:71:13: 'substring'
            {
            match("substring"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SUBSTRING"

    // $ANTLR start "SUM"
    public final void mSUM() throws RecognitionException {
        try {
            int _type = SUM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:72:5: ( 'sum' )
            // JPQL.g:72:7: 'sum'
            {
            match("sum"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SUM"

    // $ANTLR start "THEN"
    public final void mTHEN() throws RecognitionException {
        try {
            int _type = THEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:73:6: ( 'then' )
            // JPQL.g:73:8: 'then'
            {
            match("then"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "THEN"

    // $ANTLR start "TRAILING"
    public final void mTRAILING() throws RecognitionException {
        try {
            int _type = TRAILING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:74:10: ( 'trailing' )
            // JPQL.g:74:12: 'trailing'
            {
            match("trailing"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TRAILING"

    // $ANTLR start "TREAT"
    public final void mTREAT() throws RecognitionException {
        try {
            int _type = TREAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:75:7: ( 'treat' )
            // JPQL.g:75:9: 'treat'
            {
            match("treat"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TREAT"

    // $ANTLR start "TRIM"
    public final void mTRIM() throws RecognitionException {
        try {
            int _type = TRIM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:76:6: ( 'trim' )
            // JPQL.g:76:8: 'trim'
            {
            match("trim"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TRIM"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:77:6: ( 'true' )
            // JPQL.g:77:8: 'true'
            {
            match("true"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TRUE"

    // $ANTLR start "TYPE"
    public final void mTYPE() throws RecognitionException {
        try {
            int _type = TYPE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:78:6: ( 'type' )
            // JPQL.g:78:8: 'type'
            {
            match("type"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TYPE"

    // $ANTLR start "UNKNOWN"
    public final void mUNKNOWN() throws RecognitionException {
        try {
            int _type = UNKNOWN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:79:9: ( 'unknown' )
            // JPQL.g:79:11: 'unknown'
            {
            match("unknown"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "UNKNOWN"

    // $ANTLR start "UPDATE"
    public final void mUPDATE() throws RecognitionException {
        try {
            int _type = UPDATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:80:8: ( 'update' )
            // JPQL.g:80:10: 'update'
            {
            match("update"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "UPDATE"

    // $ANTLR start "UPPER"
    public final void mUPPER() throws RecognitionException {
        try {
            int _type = UPPER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:81:7: ( 'upper' )
            // JPQL.g:81:9: 'upper'
            {
            match("upper"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "UPPER"

    // $ANTLR start "VALUE"
    public final void mVALUE() throws RecognitionException {
        try {
            int _type = VALUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:82:7: ( 'value' )
            // JPQL.g:82:9: 'value'
            {
            match("value"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "VALUE"

    // $ANTLR start "WHEN"
    public final void mWHEN() throws RecognitionException {
        try {
            int _type = WHEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:83:6: ( 'when' )
            // JPQL.g:83:8: 'when'
            {
            match("when"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHEN"

    // $ANTLR start "WHERE"
    public final void mWHERE() throws RecognitionException {
        try {
            int _type = WHERE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:84:7: ( 'where' )
            // JPQL.g:84:9: 'where'
            {
            match("where"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHERE"

    // $ANTLR start "DOT"
    public final void mDOT() throws RecognitionException {
        try {
            int _type = DOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1394:5: ( '.' )
            // JPQL.g:1394:7: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1397:5: ( ( ' ' | '\\t' | '\\n' | '\\r' )+ )
            // JPQL.g:1397:7: ( ' ' | '\\t' | '\\n' | '\\r' )+
            {
            // JPQL.g:1397:7: ( ' ' | '\\t' | '\\n' | '\\r' )+
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
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


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

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "LEFT_ROUND_BRACKET"
    public final void mLEFT_ROUND_BRACKET() throws RecognitionException {
        try {
            int _type = LEFT_ROUND_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1401:5: ( '(' )
            // JPQL.g:1401:7: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEFT_ROUND_BRACKET"

    // $ANTLR start "LEFT_CURLY_BRACKET"
    public final void mLEFT_CURLY_BRACKET() throws RecognitionException {
        try {
            int _type = LEFT_CURLY_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1405:5: ( '{' )
            // JPQL.g:1405:7: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LEFT_CURLY_BRACKET"

    // $ANTLR start "RIGHT_ROUND_BRACKET"
    public final void mRIGHT_ROUND_BRACKET() throws RecognitionException {
        try {
            int _type = RIGHT_ROUND_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1409:5: ( ')' )
            // JPQL.g:1409:7: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RIGHT_ROUND_BRACKET"

    // $ANTLR start "RIGHT_CURLY_BRACKET"
    public final void mRIGHT_CURLY_BRACKET() throws RecognitionException {
        try {
            int _type = RIGHT_CURLY_BRACKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1413:5: ( '}' )
            // JPQL.g:1413:7: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RIGHT_CURLY_BRACKET"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1417:5: ( ',' )
            // JPQL.g:1417:7: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "IDENT"
    public final void mIDENT() throws RecognitionException {
        try {
            int _type = IDENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1421:5: ( TEXTCHAR )
            // JPQL.g:1421:7: TEXTCHAR
            {
            mTEXTCHAR(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IDENT"

    // $ANTLR start "TEXTCHAR"
    public final void mTEXTCHAR() throws RecognitionException {
        try {
            int c1;
            int c2;

            // JPQL.g:1426:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '`' | '~' | '@' | '#' | '%' | '^' | '&' | '|' | '[' | ']' | ';' c1= '\\u0080' .. '\\uFFFE' ) ( 'a' .. 'z' | '_' | '$' | '0' .. '9' | c2= '\\u0080' .. '\\uFFFE' )* )
            // JPQL.g:1426:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '`' | '~' | '@' | '#' | '%' | '^' | '&' | '|' | '[' | ']' | ';' c1= '\\u0080' .. '\\uFFFE' ) ( 'a' .. 'z' | '_' | '$' | '0' .. '9' | c2= '\\u0080' .. '\\uFFFE' )*
            {
            // JPQL.g:1426:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '$' | '`' | '~' | '@' | '#' | '%' | '^' | '&' | '|' | '[' | ']' | ';' c1= '\\u0080' .. '\\uFFFE' )
            int alt2=15;
            switch ( input.LA(1) ) {
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt2=1;
                }
                break;
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
                {
                alt2=2;
                }
                break;
            case '_':
                {
                alt2=3;
                }
                break;
            case '$':
                {
                alt2=4;
                }
                break;
            case '`':
                {
                alt2=5;
                }
                break;
            case '~':
                {
                alt2=6;
                }
                break;
            case '@':
                {
                alt2=7;
                }
                break;
            case '#':
                {
                alt2=8;
                }
                break;
            case '%':
                {
                alt2=9;
                }
                break;
            case '^':
                {
                alt2=10;
                }
                break;
            case '&':
                {
                alt2=11;
                }
                break;
            case '|':
                {
                alt2=12;
                }
                break;
            case '[':
                {
                alt2=13;
                }
                break;
            case ']':
                {
                alt2=14;
                }
                break;
            case ';':
                {
                alt2=15;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // JPQL.g:1426:8: 'a' .. 'z'
                    {
                    matchRange('a','z'); 

                    }
                    break;
                case 2 :
                    // JPQL.g:1426:19: 'A' .. 'Z'
                    {
                    matchRange('A','Z'); 

                    }
                    break;
                case 3 :
                    // JPQL.g:1426:30: '_'
                    {
                    match('_'); 

                    }
                    break;
                case 4 :
                    // JPQL.g:1426:36: '$'
                    {
                    match('$'); 

                    }
                    break;
                case 5 :
                    // JPQL.g:1426:42: '`'
                    {
                    match('`'); 

                    }
                    break;
                case 6 :
                    // JPQL.g:1426:48: '~'
                    {
                    match('~'); 

                    }
                    break;
                case 7 :
                    // JPQL.g:1426:54: '@'
                    {
                    match('@'); 

                    }
                    break;
                case 8 :
                    // JPQL.g:1426:60: '#'
                    {
                    match('#'); 

                    }
                    break;
                case 9 :
                    // JPQL.g:1426:66: '%'
                    {
                    match('%'); 

                    }
                    break;
                case 10 :
                    // JPQL.g:1426:72: '^'
                    {
                    match('^'); 

                    }
                    break;
                case 11 :
                    // JPQL.g:1426:78: '&'
                    {
                    match('&'); 

                    }
                    break;
                case 12 :
                    // JPQL.g:1426:84: '|'
                    {
                    match('|'); 

                    }
                    break;
                case 13 :
                    // JPQL.g:1426:90: '['
                    {
                    match('['); 

                    }
                    break;
                case 14 :
                    // JPQL.g:1426:96: ']'
                    {
                    match(']'); 

                    }
                    break;
                case 15 :
                    // JPQL.g:1426:102: ';' c1= '\\u0080' .. '\\uFFFE'
                    {
                    match(';'); 
                    c1 = input.LA(1);
                    matchRange('\u0080','\uFFFE'); 

                               if (!Character.isJavaIdentifierStart(c1)) {
                                    throw new InvalidIdentifierStartException(c1, getLine(), getCharPositionInLine());
                               }
                           

                    }
                    break;

            }

            // JPQL.g:1434:7: ( 'a' .. 'z' | '_' | '$' | '0' .. '9' | c2= '\\u0080' .. '\\uFFFE' )*
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
            	    // JPQL.g:1434:8: 'a' .. 'z'
            	    {
            	    matchRange('a','z'); 

            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:1434:19: '_'
            	    {
            	    match('_'); 

            	    }
            	    break;
            	case 3 :
            	    // JPQL.g:1434:25: '$'
            	    {
            	    match('$'); 

            	    }
            	    break;
            	case 4 :
            	    // JPQL.g:1434:31: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;
            	case 5 :
            	    // JPQL.g:1435:8: c2= '\\u0080' .. '\\uFFFE'
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
    // $ANTLR end "TEXTCHAR"

    // $ANTLR start "HEX_LITERAL"
    public final void mHEX_LITERAL() throws RecognitionException {
        try {
            int _type = HEX_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1445:13: ( '0' ( 'x' | 'X' ) ( HEX_DIGIT )+ )
            // JPQL.g:1445:15: '0' ( 'x' | 'X' ) ( HEX_DIGIT )+
            {
            match('0'); 
            if ( input.LA(1)=='X'||input.LA(1)=='x' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // JPQL.g:1445:29: ( HEX_DIGIT )+
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
            	    // JPQL.g:1445:29: HEX_DIGIT
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

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "HEX_LITERAL"

    // $ANTLR start "INTEGER_LITERAL"
    public final void mINTEGER_LITERAL() throws RecognitionException {
        try {
            int _type = INTEGER_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1447:17: ( ( MINUS )? ( '0' | '1' .. '9' ( '0' .. '9' )* ) )
            // JPQL.g:1447:19: ( MINUS )? ( '0' | '1' .. '9' ( '0' .. '9' )* )
            {
            // JPQL.g:1447:19: ( MINUS )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // JPQL.g:1447:19: MINUS
                    {
                    mMINUS(); 

                    }
                    break;

            }

            // JPQL.g:1447:26: ( '0' | '1' .. '9' ( '0' .. '9' )* )
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
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // JPQL.g:1447:27: '0'
                    {
                    match('0'); 

                    }
                    break;
                case 2 :
                    // JPQL.g:1447:33: '1' .. '9' ( '0' .. '9' )*
                    {
                    matchRange('1','9'); 
                    // JPQL.g:1447:42: ( '0' .. '9' )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>='0' && LA6_0<='9')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // JPQL.g:1447:42: '0' .. '9'
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

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INTEGER_LITERAL"

    // $ANTLR start "LONG_LITERAL"
    public final void mLONG_LITERAL() throws RecognitionException {
        try {
            int _type = LONG_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1449:14: ( INTEGER_LITERAL INTEGER_SUFFIX )
            // JPQL.g:1449:16: INTEGER_LITERAL INTEGER_SUFFIX
            {
            mINTEGER_LITERAL(); 
            mINTEGER_SUFFIX(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LONG_LITERAL"

    // $ANTLR start "OCTAL_LITERAL"
    public final void mOCTAL_LITERAL() throws RecognitionException {
        try {
            int _type = OCTAL_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1451:15: ( ( MINUS )? '0' ( '0' .. '7' )+ )
            // JPQL.g:1451:17: ( MINUS )? '0' ( '0' .. '7' )+
            {
            // JPQL.g:1451:17: ( MINUS )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0=='-') ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // JPQL.g:1451:17: MINUS
                    {
                    mMINUS(); 

                    }
                    break;

            }

            match('0'); 
            // JPQL.g:1451:28: ( '0' .. '7' )+
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
            	    // JPQL.g:1451:29: '0' .. '7'
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

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OCTAL_LITERAL"

    // $ANTLR start "HEX_DIGIT"
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // JPQL.g:1456:5: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // JPQL.g:1456:9: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "HEX_DIGIT"

    // $ANTLR start "INTEGER_SUFFIX"
    public final void mINTEGER_SUFFIX() throws RecognitionException {
        try {
            // JPQL.g:1460:16: ( ( 'l' | 'L' ) )
            // JPQL.g:1460:18: ( 'l' | 'L' )
            {
            if ( input.LA(1)=='L'||input.LA(1)=='l' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "INTEGER_SUFFIX"

    // $ANTLR start "NUMERIC_DIGITS"
    public final void mNUMERIC_DIGITS() throws RecognitionException {
        try {
            // JPQL.g:1464:5: ( ( MINUS )? ( '0' .. '9' )+ '.' ( '0' .. '9' )* | ( MINUS )? '.' ( '0' .. '9' )+ | ( MINUS )? ( '0' .. '9' )+ )
            int alt17=3;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // JPQL.g:1464:9: ( MINUS )? ( '0' .. '9' )+ '.' ( '0' .. '9' )*
                    {
                    // JPQL.g:1464:9: ( MINUS )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0=='-') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // JPQL.g:1464:9: MINUS
                            {
                            mMINUS(); 

                            }
                            break;

                    }

                    // JPQL.g:1464:16: ( '0' .. '9' )+
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
                    	    // JPQL.g:1464:17: '0' .. '9'
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
                    // JPQL.g:1464:32: ( '0' .. '9' )*
                    loop12:
                    do {
                        int alt12=2;
                        int LA12_0 = input.LA(1);

                        if ( ((LA12_0>='0' && LA12_0<='9')) ) {
                            alt12=1;
                        }


                        switch (alt12) {
                    	case 1 :
                    	    // JPQL.g:1464:33: '0' .. '9'
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
                    // JPQL.g:1465:9: ( MINUS )? '.' ( '0' .. '9' )+
                    {
                    // JPQL.g:1465:9: ( MINUS )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0=='-') ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // JPQL.g:1465:9: MINUS
                            {
                            mMINUS(); 

                            }
                            break;

                    }

                    match('.'); 
                    // JPQL.g:1465:20: ( '0' .. '9' )+
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
                    	    // JPQL.g:1465:21: '0' .. '9'
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
                    // JPQL.g:1466:9: ( MINUS )? ( '0' .. '9' )+
                    {
                    // JPQL.g:1466:9: ( MINUS )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0=='-') ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // JPQL.g:1466:9: MINUS
                            {
                            mMINUS(); 

                            }
                            break;

                    }

                    // JPQL.g:1466:16: ( '0' .. '9' )+
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
                    	    // JPQL.g:1466:17: '0' .. '9'
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
    // $ANTLR end "NUMERIC_DIGITS"

    // $ANTLR start "DOUBLE_LITERAL"
    public final void mDOUBLE_LITERAL() throws RecognitionException {
        try {
            int _type = DOUBLE_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1470:5: ( NUMERIC_DIGITS ( DOUBLE_SUFFIX )? )
            // JPQL.g:1470:9: NUMERIC_DIGITS ( DOUBLE_SUFFIX )?
            {
            mNUMERIC_DIGITS(); 
            // JPQL.g:1470:24: ( DOUBLE_SUFFIX )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0=='d') ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // JPQL.g:1470:24: DOUBLE_SUFFIX
                    {
                    mDOUBLE_SUFFIX(); 

                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE_LITERAL"

    // $ANTLR start "FLOAT_LITERAL"
    public final void mFLOAT_LITERAL() throws RecognitionException {
        try {
            int _type = FLOAT_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1474:5: ( NUMERIC_DIGITS EXPONENT ( FLOAT_SUFFIX )? | NUMERIC_DIGITS FLOAT_SUFFIX )
            int alt20=2;
            alt20 = dfa20.predict(input);
            switch (alt20) {
                case 1 :
                    // JPQL.g:1474:9: NUMERIC_DIGITS EXPONENT ( FLOAT_SUFFIX )?
                    {
                    mNUMERIC_DIGITS(); 
                    mEXPONENT(); 
                    // JPQL.g:1474:33: ( FLOAT_SUFFIX )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0=='f') ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // JPQL.g:1474:33: FLOAT_SUFFIX
                            {
                            mFLOAT_SUFFIX(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // JPQL.g:1475:9: NUMERIC_DIGITS FLOAT_SUFFIX
                    {
                    mNUMERIC_DIGITS(); 
                    mFLOAT_SUFFIX(); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FLOAT_LITERAL"

    // $ANTLR start "EXPONENT"
    public final void mEXPONENT() throws RecognitionException {
        try {
            // JPQL.g:1481:5: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // JPQL.g:1481:9: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // JPQL.g:1481:21: ( '+' | '-' )?
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
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // JPQL.g:1481:32: ( '0' .. '9' )+
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
            	    // JPQL.g:1481:33: '0' .. '9'
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
    // $ANTLR end "EXPONENT"

    // $ANTLR start "FLOAT_SUFFIX"
    public final void mFLOAT_SUFFIX() throws RecognitionException {
        try {
            // JPQL.g:1487:5: ( 'f' )
            // JPQL.g:1487:9: 'f'
            {
            match('f'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "FLOAT_SUFFIX"

    // $ANTLR start "DATE_LITERAL"
    public final void mDATE_LITERAL() throws RecognitionException {
        try {
            int _type = DATE_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1491:5: ( LEFT_CURLY_BRACKET ( 'd' ) ( ' ' | '\\t' )+ '\\'' DATE_STRING '\\'' ( ' ' | '\\t' )* RIGHT_CURLY_BRACKET )
            // JPQL.g:1491:7: LEFT_CURLY_BRACKET ( 'd' ) ( ' ' | '\\t' )+ '\\'' DATE_STRING '\\'' ( ' ' | '\\t' )* RIGHT_CURLY_BRACKET
            {
            mLEFT_CURLY_BRACKET(); 
            // JPQL.g:1491:26: ( 'd' )
            // JPQL.g:1491:27: 'd'
            {
            match('d'); 

            }

            // JPQL.g:1491:32: ( ' ' | '\\t' )+
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
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


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
            // JPQL.g:1491:68: ( ' ' | '\\t' )*
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
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);

            mRIGHT_CURLY_BRACKET(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DATE_LITERAL"

    // $ANTLR start "TIME_LITERAL"
    public final void mTIME_LITERAL() throws RecognitionException {
        try {
            int _type = TIME_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1495:5: ( LEFT_CURLY_BRACKET ( 't' ) ( ' ' | '\\t' )+ '\\'' TIME_STRING '\\'' ( ' ' | '\\t' )* RIGHT_CURLY_BRACKET )
            // JPQL.g:1495:7: LEFT_CURLY_BRACKET ( 't' ) ( ' ' | '\\t' )+ '\\'' TIME_STRING '\\'' ( ' ' | '\\t' )* RIGHT_CURLY_BRACKET
            {
            mLEFT_CURLY_BRACKET(); 
            // JPQL.g:1495:26: ( 't' )
            // JPQL.g:1495:27: 't'
            {
            match('t'); 

            }

            // JPQL.g:1495:32: ( ' ' | '\\t' )+
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
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


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
            // JPQL.g:1495:68: ( ' ' | '\\t' )*
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
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);

            mRIGHT_CURLY_BRACKET(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TIME_LITERAL"

    // $ANTLR start "TIMESTAMP_LITERAL"
    public final void mTIMESTAMP_LITERAL() throws RecognitionException {
        try {
            int _type = TIMESTAMP_LITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1499:5: ( LEFT_CURLY_BRACKET ( 'ts' ) ( ' ' | '\\t' )+ '\\'' DATE_STRING ' ' TIME_STRING '\\'' ( ' ' | '\\t' )* RIGHT_CURLY_BRACKET )
            // JPQL.g:1499:7: LEFT_CURLY_BRACKET ( 'ts' ) ( ' ' | '\\t' )+ '\\'' DATE_STRING ' ' TIME_STRING '\\'' ( ' ' | '\\t' )* RIGHT_CURLY_BRACKET
            {
            mLEFT_CURLY_BRACKET(); 
            // JPQL.g:1499:26: ( 'ts' )
            // JPQL.g:1499:27: 'ts'
            {
            match("ts"); 


            }

            // JPQL.g:1499:33: ( ' ' | '\\t' )+
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
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


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
            // JPQL.g:1499:85: ( ' ' | '\\t' )*
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
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);

            mRIGHT_CURLY_BRACKET(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TIMESTAMP_LITERAL"

    // $ANTLR start "DATE_STRING"
    public final void mDATE_STRING() throws RecognitionException {
        try {
            int _type = DATE_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1503:5: ( '0' .. '9' '0' .. '9' '0' .. '9' '0' .. '9' '-' '0' .. '9' '0' .. '9' '-' '0' .. '9' '0' .. '9' )
            // JPQL.g:1503:7: '0' .. '9' '0' .. '9' '0' .. '9' '0' .. '9' '-' '0' .. '9' '0' .. '9' '-' '0' .. '9' '0' .. '9'
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

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DATE_STRING"

    // $ANTLR start "TIME_STRING"
    public final void mTIME_STRING() throws RecognitionException {
        try {
            int _type = TIME_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1507:5: ( '0' .. '9' ( '0' .. '9' )? ':' '0' .. '9' '0' .. '9' ':' '0' .. '9' '0' .. '9' '.' ( '0' .. '9' )* )
            // JPQL.g:1507:7: '0' .. '9' ( '0' .. '9' )? ':' '0' .. '9' '0' .. '9' ':' '0' .. '9' '0' .. '9' '.' ( '0' .. '9' )*
            {
            matchRange('0','9'); 
            // JPQL.g:1507:16: ( '0' .. '9' )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( ((LA29_0>='0' && LA29_0<='9')) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // JPQL.g:1507:17: '0' .. '9'
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
            // JPQL.g:1507:76: ( '0' .. '9' )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);

                if ( ((LA30_0>='0' && LA30_0<='9')) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // JPQL.g:1507:76: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TIME_STRING"

    // $ANTLR start "DOUBLE_SUFFIX"
    public final void mDOUBLE_SUFFIX() throws RecognitionException {
        try {
            // JPQL.g:1512:5: ( 'd' )
            // JPQL.g:1512:7: 'd'
            {
            match('d'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE_SUFFIX"

    // $ANTLR start "EQUALS"
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1516:5: ( '=' )
            // JPQL.g:1516:7: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQUALS"

    // $ANTLR start "GREATER_THAN"
    public final void mGREATER_THAN() throws RecognitionException {
        try {
            int _type = GREATER_THAN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1520:5: ( '>' )
            // JPQL.g:1520:7: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GREATER_THAN"

    // $ANTLR start "GREATER_THAN_EQUAL_TO"
    public final void mGREATER_THAN_EQUAL_TO() throws RecognitionException {
        try {
            int _type = GREATER_THAN_EQUAL_TO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1524:5: ( '>=' )
            // JPQL.g:1524:7: '>='
            {
            match(">="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GREATER_THAN_EQUAL_TO"

    // $ANTLR start "LESS_THAN"
    public final void mLESS_THAN() throws RecognitionException {
        try {
            int _type = LESS_THAN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1528:5: ( '<' )
            // JPQL.g:1528:7: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LESS_THAN"

    // $ANTLR start "LESS_THAN_EQUAL_TO"
    public final void mLESS_THAN_EQUAL_TO() throws RecognitionException {
        try {
            int _type = LESS_THAN_EQUAL_TO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1532:5: ( '<=' )
            // JPQL.g:1532:7: '<='
            {
            match("<="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LESS_THAN_EQUAL_TO"

    // $ANTLR start "NOT_EQUAL_TO"
    public final void mNOT_EQUAL_TO() throws RecognitionException {
        try {
            int _type = NOT_EQUAL_TO;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1536:5: ( '<>' | '!=' )
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0=='<') ) {
                alt31=1;
            }
            else if ( (LA31_0=='!') ) {
                alt31=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // JPQL.g:1536:7: '<>'
                    {
                    match("<>"); 


                    }
                    break;
                case 2 :
                    // JPQL.g:1537:7: '!='
                    {
                    match("!="); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NOT_EQUAL_TO"

    // $ANTLR start "MULTIPLY"
    public final void mMULTIPLY() throws RecognitionException {
        try {
            int _type = MULTIPLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1541:5: ( '*' )
            // JPQL.g:1541:7: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MULTIPLY"

    // $ANTLR start "DIVIDE"
    public final void mDIVIDE() throws RecognitionException {
        try {
            int _type = DIVIDE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1545:5: ( '/' )
            // JPQL.g:1545:7: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIVIDE"

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1549:5: ( '+' )
            // JPQL.g:1549:7: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1553:5: ( '-' )
            // JPQL.g:1553:7: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINUS"

    // $ANTLR start "POSITIONAL_PARAM"
    public final void mPOSITIONAL_PARAM() throws RecognitionException {
        try {
            int _type = POSITIONAL_PARAM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1558:5: ( '?' ( '1' .. '9' ) ( '0' .. '9' )* )
            // JPQL.g:1558:7: '?' ( '1' .. '9' ) ( '0' .. '9' )*
            {
            match('?'); 
            // JPQL.g:1558:11: ( '1' .. '9' )
            // JPQL.g:1558:12: '1' .. '9'
            {
            matchRange('1','9'); 

            }

            // JPQL.g:1558:22: ( '0' .. '9' )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( ((LA32_0>='0' && LA32_0<='9')) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // JPQL.g:1558:23: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    break loop32;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "POSITIONAL_PARAM"

    // $ANTLR start "NAMED_PARAM"
    public final void mNAMED_PARAM() throws RecognitionException {
        try {
            int _type = NAMED_PARAM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1562:5: ( ':' TEXTCHAR )
            // JPQL.g:1562:7: ':' TEXTCHAR
            {
            match(':'); 
            mTEXTCHAR(); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NAMED_PARAM"

    // $ANTLR start "STRING_LITERAL_DOUBLE_QUOTED"
    public final void mSTRING_LITERAL_DOUBLE_QUOTED() throws RecognitionException {
        try {
            int _type = STRING_LITERAL_DOUBLE_QUOTED;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1568:5: ( '\"' (~ ( '\"' ) )* '\"' )
            // JPQL.g:1568:7: '\"' (~ ( '\"' ) )* '\"'
            {
            match('\"'); 
            // JPQL.g:1568:11: (~ ( '\"' ) )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( ((LA33_0>='\u0000' && LA33_0<='!')||(LA33_0>='#' && LA33_0<='\uFFFF')) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // JPQL.g:1568:12: ~ ( '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING_LITERAL_DOUBLE_QUOTED"

    // $ANTLR start "STRING_LITERAL_SINGLE_QUOTED"
    public final void mSTRING_LITERAL_SINGLE_QUOTED() throws RecognitionException {
        try {
            int _type = STRING_LITERAL_SINGLE_QUOTED;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // JPQL.g:1572:5: ( '\\'' (~ ( '\\'' ) | ( '\\'\\'' ) )* '\\'' )
            // JPQL.g:1572:7: '\\'' (~ ( '\\'' ) | ( '\\'\\'' ) )* '\\''
            {
            match('\''); 
            // JPQL.g:1572:12: (~ ( '\\'' ) | ( '\\'\\'' ) )*
            loop34:
            do {
                int alt34=3;
                int LA34_0 = input.LA(1);

                if ( (LA34_0=='\'') ) {
                    int LA34_1 = input.LA(2);

                    if ( (LA34_1=='\'') ) {
                        alt34=2;
                    }


                }
                else if ( ((LA34_0>='\u0000' && LA34_0<='&')||(LA34_0>='(' && LA34_0<='\uFFFF')) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // JPQL.g:1572:13: ~ ( '\\'' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;
            	case 2 :
            	    // JPQL.g:1572:24: ( '\\'\\'' )
            	    {
            	    // JPQL.g:1572:24: ( '\\'\\'' )
            	    // JPQL.g:1572:25: '\\'\\''
            	    {
            	    match("''"); 


            	    }


            	    }
            	    break;

            	default :
            	    break loop34;
                }
            } while (true);

            match('\''); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING_LITERAL_SINGLE_QUOTED"

    public void mTokens() throws RecognitionException {
        // JPQL.g:1:8: ( ABS | ALL | AND | ANY | AS | ASC | AVG | BETWEEN | BOTH | BY | CASE | COALESCE | CONCAT | COUNT | CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | DESC | DELETE | DISTINCT | ELSE | EMPTY | END | ENTRY | ESCAPE | EXISTS | FALSE | FETCH | FUNC | FROM | GROUP | HAVING | IN | INDEX | INNER | IS | JOIN | KEY | LEADING | LEFT | LENGTH | LIKE | LOCATE | LOWER | MAX | MEMBER | MIN | MOD | NEW | NOT | NULL | NULLIF | OBJECT | OF | OR | ORDER | OUTER | SELECT | SET | SIZE | SQRT | SOME | SUBSTRING | SUM | THEN | TRAILING | TREAT | TRIM | TRUE | TYPE | UNKNOWN | UPDATE | UPPER | VALUE | WHEN | WHERE | DOT | WS | LEFT_ROUND_BRACKET | LEFT_CURLY_BRACKET | RIGHT_ROUND_BRACKET | RIGHT_CURLY_BRACKET | COMMA | IDENT | HEX_LITERAL | INTEGER_LITERAL | LONG_LITERAL | OCTAL_LITERAL | DOUBLE_LITERAL | FLOAT_LITERAL | DATE_LITERAL | TIME_LITERAL | TIMESTAMP_LITERAL | DATE_STRING | TIME_STRING | EQUALS | GREATER_THAN | GREATER_THAN_EQUAL_TO | LESS_THAN | LESS_THAN_EQUAL_TO | NOT_EQUAL_TO | MULTIPLY | DIVIDE | PLUS | MINUS | POSITIONAL_PARAM | NAMED_PARAM | STRING_LITERAL_DOUBLE_QUOTED | STRING_LITERAL_SINGLE_QUOTED )
        int alt35=109;
        alt35 = dfa35.predict(input);
        switch (alt35) {
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
                // JPQL.g:1:398: TREAT
                {
                mTREAT(); 

                }
                break;
            case 68 :
                // JPQL.g:1:404: TRIM
                {
                mTRIM(); 

                }
                break;
            case 69 :
                // JPQL.g:1:409: TRUE
                {
                mTRUE(); 

                }
                break;
            case 70 :
                // JPQL.g:1:414: TYPE
                {
                mTYPE(); 

                }
                break;
            case 71 :
                // JPQL.g:1:419: UNKNOWN
                {
                mUNKNOWN(); 

                }
                break;
            case 72 :
                // JPQL.g:1:427: UPDATE
                {
                mUPDATE(); 

                }
                break;
            case 73 :
                // JPQL.g:1:434: UPPER
                {
                mUPPER(); 

                }
                break;
            case 74 :
                // JPQL.g:1:440: VALUE
                {
                mVALUE(); 

                }
                break;
            case 75 :
                // JPQL.g:1:446: WHEN
                {
                mWHEN(); 

                }
                break;
            case 76 :
                // JPQL.g:1:451: WHERE
                {
                mWHERE(); 

                }
                break;
            case 77 :
                // JPQL.g:1:457: DOT
                {
                mDOT(); 

                }
                break;
            case 78 :
                // JPQL.g:1:461: WS
                {
                mWS(); 

                }
                break;
            case 79 :
                // JPQL.g:1:464: LEFT_ROUND_BRACKET
                {
                mLEFT_ROUND_BRACKET(); 

                }
                break;
            case 80 :
                // JPQL.g:1:483: LEFT_CURLY_BRACKET
                {
                mLEFT_CURLY_BRACKET(); 

                }
                break;
            case 81 :
                // JPQL.g:1:502: RIGHT_ROUND_BRACKET
                {
                mRIGHT_ROUND_BRACKET(); 

                }
                break;
            case 82 :
                // JPQL.g:1:522: RIGHT_CURLY_BRACKET
                {
                mRIGHT_CURLY_BRACKET(); 

                }
                break;
            case 83 :
                // JPQL.g:1:542: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 84 :
                // JPQL.g:1:548: IDENT
                {
                mIDENT(); 

                }
                break;
            case 85 :
                // JPQL.g:1:554: HEX_LITERAL
                {
                mHEX_LITERAL(); 

                }
                break;
            case 86 :
                // JPQL.g:1:566: INTEGER_LITERAL
                {
                mINTEGER_LITERAL(); 

                }
                break;
            case 87 :
                // JPQL.g:1:582: LONG_LITERAL
                {
                mLONG_LITERAL(); 

                }
                break;
            case 88 :
                // JPQL.g:1:595: OCTAL_LITERAL
                {
                mOCTAL_LITERAL(); 

                }
                break;
            case 89 :
                // JPQL.g:1:609: DOUBLE_LITERAL
                {
                mDOUBLE_LITERAL(); 

                }
                break;
            case 90 :
                // JPQL.g:1:624: FLOAT_LITERAL
                {
                mFLOAT_LITERAL(); 

                }
                break;
            case 91 :
                // JPQL.g:1:638: DATE_LITERAL
                {
                mDATE_LITERAL(); 

                }
                break;
            case 92 :
                // JPQL.g:1:651: TIME_LITERAL
                {
                mTIME_LITERAL(); 

                }
                break;
            case 93 :
                // JPQL.g:1:664: TIMESTAMP_LITERAL
                {
                mTIMESTAMP_LITERAL(); 

                }
                break;
            case 94 :
                // JPQL.g:1:682: DATE_STRING
                {
                mDATE_STRING(); 

                }
                break;
            case 95 :
                // JPQL.g:1:694: TIME_STRING
                {
                mTIME_STRING(); 

                }
                break;
            case 96 :
                // JPQL.g:1:706: EQUALS
                {
                mEQUALS(); 

                }
                break;
            case 97 :
                // JPQL.g:1:713: GREATER_THAN
                {
                mGREATER_THAN(); 

                }
                break;
            case 98 :
                // JPQL.g:1:726: GREATER_THAN_EQUAL_TO
                {
                mGREATER_THAN_EQUAL_TO(); 

                }
                break;
            case 99 :
                // JPQL.g:1:748: LESS_THAN
                {
                mLESS_THAN(); 

                }
                break;
            case 100 :
                // JPQL.g:1:758: LESS_THAN_EQUAL_TO
                {
                mLESS_THAN_EQUAL_TO(); 

                }
                break;
            case 101 :
                // JPQL.g:1:777: NOT_EQUAL_TO
                {
                mNOT_EQUAL_TO(); 

                }
                break;
            case 102 :
                // JPQL.g:1:790: MULTIPLY
                {
                mMULTIPLY(); 

                }
                break;
            case 103 :
                // JPQL.g:1:799: DIVIDE
                {
                mDIVIDE(); 

                }
                break;
            case 104 :
                // JPQL.g:1:806: PLUS
                {
                mPLUS(); 

                }
                break;
            case 105 :
                // JPQL.g:1:811: MINUS
                {
                mMINUS(); 

                }
                break;
            case 106 :
                // JPQL.g:1:817: POSITIONAL_PARAM
                {
                mPOSITIONAL_PARAM(); 

                }
                break;
            case 107 :
                // JPQL.g:1:834: NAMED_PARAM
                {
                mNAMED_PARAM(); 

                }
                break;
            case 108 :
                // JPQL.g:1:846: STRING_LITERAL_DOUBLE_QUOTED
                {
                mSTRING_LITERAL_DOUBLE_QUOTED(); 

                }
                break;
            case 109 :
                // JPQL.g:1:875: STRING_LITERAL_SINGLE_QUOTED
                {
                mSTRING_LITERAL_SINGLE_QUOTED(); 

                }
                break;

        }

    }


    protected DFA17 dfa17 = new DFA17(this);
    protected DFA20 dfa20 = new DFA20(this);
    protected DFA35 dfa35 = new DFA35(this);
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
            return "1462:1: fragment NUMERIC_DIGITS : ( ( MINUS )? ( '0' .. '9' )+ '.' ( '0' .. '9' )* | ( MINUS )? '.' ( '0' .. '9' )+ | ( MINUS )? ( '0' .. '9' )+ );";
        }
    }
    static final String DFA20_eotS =
        "\11\uffff";
    static final String DFA20_eofS =
        "\11\uffff";
    static final String DFA20_minS =
        "\1\55\2\56\2\60\2\uffff\2\60";
    static final String DFA20_maxS =
        "\2\71\1\146\1\71\1\146\2\uffff\2\146";
    static final String DFA20_acceptS =
        "\5\uffff\1\2\1\1\2\uffff";
    static final String DFA20_specialS =
        "\11\uffff}>";
    static final String[] DFA20_transitionS = {
            "\1\1\1\3\1\uffff\12\2",
            "\1\3\1\uffff\12\2",
            "\1\4\1\uffff\12\2\13\uffff\1\6\37\uffff\1\6\1\5",
            "\12\7",
            "\12\10\13\uffff\1\6\37\uffff\1\6\1\5",
            "",
            "",
            "\12\7\13\uffff\1\6\37\uffff\1\6\1\5",
            "\12\10\13\uffff\1\6\37\uffff\1\6\1\5"
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
            return "1473:1: FLOAT_LITERAL : ( NUMERIC_DIGITS EXPONENT ( FLOAT_SUFFIX )? | NUMERIC_DIGITS FLOAT_SUFFIX );";
        }
    }
    static final String DFA35_eotS =
        "\1\uffff\24\34\1\142\2\uffff\1\143\4\uffff\1\151\1\157\1\151\1"+
        "\uffff\1\165\1\167\10\uffff\3\34\1\175\3\34\1\u0081\20\34\1\u0098"+
        "\1\u0099\15\34\1\u00aa\1\u00ac\15\34\1\156\5\uffff\1\u00c3\1\156"+
        "\2\uffff\1\156\5\uffff\3\151\4\uffff\1\u00ca\1\u00cb\1\u00cc\1\u00cd"+
        "\1\u00ce\1\uffff\1\u00cf\2\34\1\uffff\12\34\1\u00dc\13\34\2\uffff"+
        "\1\34\1\u00e9\6\34\1\u00f0\1\34\1\u00f2\1\u00f3\1\u00f4\1\u00f5"+
        "\2\34\1\uffff\1\34\1\uffff\2\34\1\u00fb\4\34\1\u0100\13\34\2\uffff"+
        "\1\u00c3\1\uffff\2\156\1\u00c3\1\156\2\151\6\uffff\1\34\1\u0111"+
        "\1\u0112\4\34\1\u0117\2\34\1\u011a\1\34\1\uffff\5\34\1\u0121\1\u0122"+
        "\4\34\1\u0127\1\uffff\1\34\1\u0129\1\34\1\u012b\2\34\1\uffff\1\34"+
        "\4\uffff\1\u0130\4\34\1\uffff\1\u0135\1\u0136\1\u0137\1\34\1\uffff"+
        "\1\u0139\2\34\1\u013c\1\u013d\1\u013e\4\34\1\u0143\1\34\1\u00c3"+
        "\1\156\1\151\1\34\2\uffff\2\34\1\u0149\1\34\1\uffff\2\34\1\uffff"+
        "\1\u014d\1\u014e\2\34\1\u0151\1\u0152\2\uffff\1\u0153\1\34\1\u0155"+
        "\1\u0156\1\uffff\1\34\1\uffff\1\34\1\uffff\1\34\1\u015a\2\34\1\uffff"+
        "\1\34\1\u015e\1\u015f\1\34\3\uffff\1\34\1\uffff\1\34\1\u0163\3\uffff"+
        "\2\34\1\u0166\1\u0167\1\uffff\1\u0168\1\uffff\2\34\1\u016b\1\uffff"+
        "\1\34\1\u016d\1\34\2\uffff\1\u016f\1\u0170\3\uffff\1\u0171\2\uffff"+
        "\1\34\1\u0173\1\u0174\1\uffff\1\u0175\1\u0176\1\u0177\2\uffff\1"+
        "\u0178\2\34\1\uffff\1\34\1\u017c\3\uffff\1\u017d\1\34\1\uffff\1"+
        "\34\1\uffff\1\34\3\uffff\1\u0181\6\uffff\2\34\1\u0184\2\uffff\1"+
        "\u0185\1\34\1\u0188\1\uffff\1\34\1\u018a\2\uffff\2\34\1\uffff\1"+
        "\u018d\1\uffff\2\34\1\uffff\2\34\1\u0192\1\u0194\1\uffff\1\34\1"+
        "\uffff\3\34\1\u0199\1\uffff";
    static final String DFA35_eofS =
        "\u019a\uffff";
    static final String DFA35_minS =
        "\1\11\1\142\1\145\1\141\1\145\1\154\1\141\1\162\1\141\1\156\1\157"+
        "\2\145\1\141\1\145\1\142\1\145\1\150\1\156\1\141\1\150\1\60\2\uffff"+
        "\1\144\4\uffff\3\56\1\uffff\2\75\10\uffff\1\163\1\154\1\144\1\44"+
        "\1\147\2\164\1\44\1\163\1\141\1\162\1\154\2\163\1\160\1\144\1\143"+
        "\1\151\1\154\1\164\1\156\2\157\1\166\2\44\1\151\1\171\1\141\1\153"+
        "\1\143\1\170\1\155\1\156\1\144\1\167\1\164\1\154\1\152\2\44\1\164"+
        "\1\154\1\172\1\162\1\155\1\142\1\145\1\141\1\160\1\153\1\144\1\154"+
        "\1\145\1\60\2\uffff\1\11\2\uffff\2\56\2\uffff\1\60\4\uffff\1\60"+
        "\3\56\4\uffff\5\44\1\uffff\1\44\1\167\1\150\1\uffff\1\145\1\154"+
        "\1\143\1\156\1\162\1\143\1\145\1\164\1\145\1\164\1\44\1\162\1\141"+
        "\2\163\2\143\1\155\1\165\1\151\2\145\2\uffff\1\156\1\44\1\144\1"+
        "\164\1\147\1\145\1\141\1\145\1\44\1\142\4\44\1\154\1\145\1\uffff"+
        "\1\145\1\uffff\2\145\1\44\1\145\1\164\1\145\1\163\1\44\1\156\1\151"+
        "\1\141\1\155\2\145\1\156\1\141\1\145\1\165\1\156\2\uffff\1\56\1"+
        "\uffff\1\56\1\60\4\56\6\uffff\1\145\2\44\1\145\1\141\1\164\1\145"+
        "\1\44\1\164\1\151\1\44\1\171\1\uffff\1\171\1\160\1\164\1\145\1\150"+
        "\2\44\1\160\1\156\1\170\1\162\1\44\1\uffff\1\151\1\44\1\164\1\44"+
        "\1\164\1\162\1\uffff\1\145\4\uffff\1\44\1\143\2\162\1\143\1\uffff"+
        "\3\44\1\164\1\uffff\1\44\1\154\1\164\3\44\1\157\1\164\1\162\1\145"+
        "\1\44\1\145\3\55\1\145\2\uffff\1\163\1\164\1\44\1\156\1\uffff\1"+
        "\145\1\156\1\uffff\2\44\1\145\1\163\2\44\2\uffff\1\44\1\147\2\44"+
        "\1\uffff\1\156\1\uffff\1\150\1\uffff\1\145\1\44\1\162\1\146\1\uffff"+
        "\1\164\2\44\1\164\3\uffff\1\162\1\uffff\1\151\1\44\3\uffff\1\167"+
        "\1\145\2\44\1\uffff\1\44\1\uffff\1\156\1\143\1\44\1\uffff\1\164"+
        "\1\44\1\143\2\uffff\2\44\3\uffff\1\44\2\uffff\1\147\2\44\1\uffff"+
        "\3\44\2\uffff\1\44\1\151\1\156\1\uffff\1\156\1\44\3\uffff\1\44\1"+
        "\145\1\uffff\1\137\1\uffff\1\164\3\uffff\1\44\6\uffff\1\156\1\147"+
        "\1\44\2\uffff\1\44\1\144\1\44\1\uffff\1\147\1\44\2\uffff\1\141\1"+
        "\151\1\uffff\1\44\1\uffff\1\164\1\155\1\uffff\2\145\2\44\1\uffff"+
        "\1\164\1\uffff\1\141\1\155\1\160\1\44\1\uffff";
    static final String DFA35_maxS =
        "\1\176\1\166\1\171\1\165\1\151\1\170\1\165\1\162\1\141\1\163\1"+
        "\157\1\145\2\157\3\165\1\171\1\160\1\141\1\150\1\71\2\uffff\1\164"+
        "\4\uffff\1\170\1\71\1\154\1\uffff\1\75\1\76\10\uffff\1\163\1\154"+
        "\1\171\1\ufffe\1\147\2\164\1\ufffe\1\163\1\165\1\162\3\163\1\160"+
        "\1\164\1\143\1\151\1\154\1\164\1\156\2\157\1\166\2\ufffe\1\151\1"+
        "\171\1\156\1\153\1\167\1\170\1\155\1\156\1\144\1\167\1\164\1\154"+
        "\1\152\2\ufffe\2\164\1\172\1\162\2\155\1\145\1\165\1\160\1\153\1"+
        "\160\1\154\1\145\1\146\2\uffff\1\163\2\uffff\2\146\2\uffff\1\146"+
        "\4\uffff\1\71\3\154\4\uffff\5\ufffe\1\uffff\1\ufffe\1\167\1\150"+
        "\1\uffff\1\145\1\154\1\143\1\156\1\162\1\143\1\145\1\164\1\145\1"+
        "\164\1\ufffe\1\162\1\141\2\163\2\143\1\155\1\165\1\151\2\145\2\uffff"+
        "\1\156\1\ufffe\1\144\1\164\1\147\1\145\1\141\1\145\1\ufffe\1\142"+
        "\4\ufffe\1\154\1\145\1\uffff\1\145\1\uffff\2\145\1\ufffe\1\145\1"+
        "\164\1\145\1\163\1\ufffe\1\156\1\151\1\141\1\155\2\145\1\156\1\141"+
        "\1\145\1\165\1\162\2\uffff\1\146\1\uffff\4\146\2\154\6\uffff\1\145"+
        "\2\ufffe\1\145\1\141\1\164\1\145\1\ufffe\1\164\1\151\1\ufffe\1\171"+
        "\1\uffff\1\171\1\160\1\164\1\145\1\150\2\ufffe\1\160\1\156\1\170"+
        "\1\162\1\ufffe\1\uffff\1\151\1\ufffe\1\164\1\ufffe\1\164\1\162\1"+
        "\uffff\1\145\4\uffff\1\ufffe\1\143\2\162\1\143\1\uffff\3\ufffe\1"+
        "\164\1\uffff\1\ufffe\1\154\1\164\3\ufffe\1\157\1\164\1\162\1\145"+
        "\1\ufffe\1\145\2\146\1\154\1\145\2\uffff\1\163\1\164\1\ufffe\1\156"+
        "\1\uffff\1\145\1\156\1\uffff\2\ufffe\1\145\1\163\2\ufffe\2\uffff"+
        "\1\ufffe\1\147\2\ufffe\1\uffff\1\156\1\uffff\1\150\1\uffff\1\145"+
        "\1\ufffe\1\162\1\146\1\uffff\1\164\2\ufffe\1\164\3\uffff\1\162\1"+
        "\uffff\1\151\1\ufffe\3\uffff\1\167\1\145\2\ufffe\1\uffff\1\ufffe"+
        "\1\uffff\1\156\1\143\1\ufffe\1\uffff\1\164\1\ufffe\1\143\2\uffff"+
        "\2\ufffe\3\uffff\1\ufffe\2\uffff\1\147\2\ufffe\1\uffff\3\ufffe\2"+
        "\uffff\1\ufffe\1\151\1\156\1\uffff\1\156\1\ufffe\3\uffff\1\ufffe"+
        "\1\145\1\uffff\1\137\1\uffff\1\164\3\uffff\1\ufffe\6\uffff\1\156"+
        "\1\147\1\ufffe\2\uffff\1\ufffe\1\164\1\ufffe\1\uffff\1\147\1\ufffe"+
        "\2\uffff\1\141\1\151\1\uffff\1\ufffe\1\uffff\1\164\1\155\1\uffff"+
        "\2\145\2\ufffe\1\uffff\1\164\1\uffff\1\141\1\155\1\160\1\ufffe\1"+
        "\uffff";
    static final String DFA35_acceptS =
        "\26\uffff\1\116\1\117\1\uffff\1\121\1\122\1\123\1\124\3\uffff\1"+
        "\140\2\uffff\1\145\1\146\1\147\1\150\1\152\1\153\1\154\1\155\67"+
        "\uffff\1\115\1\120\1\uffff\1\133\1\125\2\uffff\1\126\1\127\1\uffff"+
        "\1\137\1\132\1\131\1\151\4\uffff\1\142\1\141\1\144\1\143\5\uffff"+
        "\1\5\3\uffff\1\12\26\uffff\1\41\1\44\20\uffff\1\66\1\uffff\1\67"+
        "\23\uffff\1\135\1\134\1\uffff\1\130\6\uffff\1\1\1\2\1\3\1\4\1\6"+
        "\1\7\14\uffff\1\27\14\uffff\1\46\6\uffff\1\55\1\uffff\1\57\1\60"+
        "\1\61\1\62\5\uffff\1\73\4\uffff\1\100\20\uffff\1\11\1\13\4\uffff"+
        "\1\22\2\uffff\1\25\6\uffff\1\35\1\36\4\uffff\1\45\1\uffff\1\50\1"+
        "\uffff\1\52\4\uffff\1\63\4\uffff\1\74\1\75\1\76\1\uffff\1\101\2"+
        "\uffff\1\104\1\105\1\106\4\uffff\1\113\1\uffff\1\136\3\uffff\1\16"+
        "\3\uffff\1\26\1\30\2\uffff\1\33\1\34\1\37\1\uffff\1\42\1\43\3\uffff"+
        "\1\54\3\uffff\1\70\1\71\3\uffff\1\103\2\uffff\1\111\1\112\1\114"+
        "\2\uffff\1\15\1\uffff\1\23\1\uffff\1\31\1\32\1\40\1\uffff\1\51\1"+
        "\53\1\56\1\64\1\65\1\72\3\uffff\1\110\1\10\3\uffff\1\47\2\uffff"+
        "\1\107\1\14\2\uffff\1\24\1\uffff\1\102\2\uffff\1\77\4\uffff\1\17"+
        "\1\uffff\1\20\4\uffff\1\21";
    static final String DFA35_specialS =
        "\u019a\uffff}>";
    static final String[] DFA35_transitionS = {
            "\2\26\2\uffff\1\26\22\uffff\1\26\1\43\1\51\4\34\1\52\1\27\1"+
            "\31\1\44\1\46\1\33\1\36\1\25\1\45\1\35\11\37\1\50\1\34\1\42"+
            "\1\40\1\41\1\47\34\34\1\uffff\4\34\1\1\1\2\1\3\1\4\1\5\1\6\1"+
            "\7\1\10\1\11\1\12\1\13\1\14\1\15\1\16\1\17\3\34\1\20\1\21\1"+
            "\22\1\23\1\24\3\34\1\30\1\34\1\32\1\34",
            "\1\53\11\uffff\1\54\1\uffff\1\55\4\uffff\1\56\2\uffff\1\57",
            "\1\60\11\uffff\1\61\11\uffff\1\62",
            "\1\63\15\uffff\1\64\5\uffff\1\65",
            "\1\66\3\uffff\1\67",
            "\1\70\1\71\1\72\4\uffff\1\73\4\uffff\1\74",
            "\1\75\3\uffff\1\76\14\uffff\1\100\2\uffff\1\77",
            "\1\101",
            "\1\102",
            "\1\103\4\uffff\1\104",
            "\1\105",
            "\1\106",
            "\1\107\3\uffff\1\110\5\uffff\1\111",
            "\1\112\3\uffff\1\113\3\uffff\1\114\5\uffff\1\115",
            "\1\116\11\uffff\1\117\5\uffff\1\120",
            "\1\121\3\uffff\1\122\13\uffff\1\123\2\uffff\1\124",
            "\1\125\3\uffff\1\126\5\uffff\1\130\1\uffff\1\127\3\uffff\1"+
            "\131",
            "\1\132\11\uffff\1\133\6\uffff\1\134",
            "\1\135\1\uffff\1\136",
            "\1\137",
            "\1\140",
            "\12\141",
            "",
            "",
            "\1\145\17\uffff\1\144",
            "",
            "",
            "",
            "",
            "\1\153\1\uffff\10\147\2\150\1\154\12\uffff\1\155\6\uffff\1"+
            "\152\13\uffff\1\146\13\uffff\1\156\2\155\5\uffff\1\152\13\uffff"+
            "\1\146",
            "\1\160\1\uffff\1\161\11\162",
            "\1\153\1\uffff\12\163\1\154\12\uffff\1\155\6\uffff\1\152\27"+
            "\uffff\1\156\2\155\5\uffff\1\152",
            "",
            "\1\164",
            "\1\166\1\43",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\170",
            "\1\171",
            "\1\172\24\uffff\1\173",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\2\34\1\174\27\34"+
            "\5\uffff\uff7f\34",
            "\1\176",
            "\1\177",
            "\1\u0080",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0082",
            "\1\u0083\14\uffff\1\u0084\6\uffff\1\u0085",
            "\1\u0086",
            "\1\u0088\6\uffff\1\u0087",
            "\1\u0089",
            "\1\u008a",
            "\1\u008b",
            "\1\u008c\17\uffff\1\u008d",
            "\1\u008e",
            "\1\u008f",
            "\1\u0090",
            "\1\u0091",
            "\1\u0092",
            "\1\u0093",
            "\1\u0094",
            "\1\u0095",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\3\34\1\u0096\11"+
            "\34\1\u0097\14\34\5\uffff\uff7f\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u009a",
            "\1\u009b",
            "\1\u009c\4\uffff\1\u009d\7\uffff\1\u009e",
            "\1\u009f",
            "\1\u00a0\23\uffff\1\u00a1",
            "\1\u00a2",
            "\1\u00a3",
            "\1\u00a4",
            "\1\u00a5",
            "\1\u00a6",
            "\1\u00a7",
            "\1\u00a8",
            "\1\u00a9",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\3\34\1\u00ab\26"+
            "\34\5\uffff\uff7f\34",
            "\1\u00ad",
            "\1\u00ae\7\uffff\1\u00af",
            "\1\u00b0",
            "\1\u00b1",
            "\1\u00b2",
            "\1\u00b3\12\uffff\1\u00b4",
            "\1\u00b5",
            "\1\u00b6\3\uffff\1\u00b7\3\uffff\1\u00b8\13\uffff\1\u00b9",
            "\1\u00ba",
            "\1\u00bb",
            "\1\u00bc\13\uffff\1\u00bd",
            "\1\u00be",
            "\1\u00bf",
            "\12\141\13\uffff\1\155\37\uffff\2\155",
            "",
            "",
            "\1\u00c1\26\uffff\1\u00c1\122\uffff\1\u00c0",
            "",
            "",
            "\1\153\1\uffff\10\u00c2\2\u00c4\1\154\12\uffff\1\155\36\uffff"+
            "\1\156\2\155",
            "\1\153\1\uffff\12\u00c4\1\154\12\uffff\1\155\37\uffff\2\155",
            "",
            "",
            "\12\u00c5\13\uffff\1\155\37\uffff\2\155",
            "",
            "",
            "",
            "",
            "\12\141",
            "\1\153\1\uffff\10\u00c6\2\u00c7\13\uffff\1\155\6\uffff\1\152"+
            "\27\uffff\1\156\2\155\5\uffff\1\152",
            "\1\153\1\uffff\12\u00c8\13\uffff\1\155\6\uffff\1\152\27\uffff"+
            "\1\156\2\155\5\uffff\1\152",
            "\1\153\1\uffff\12\u00c9\1\154\12\uffff\1\155\6\uffff\1\152"+
            "\27\uffff\1\156\2\155\5\uffff\1\152",
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
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u00d0",
            "\1\u00d1",
            "",
            "\1\u00d2",
            "\1\u00d3",
            "\1\u00d4",
            "\1\u00d5",
            "\1\u00d6",
            "\1\u00d7",
            "\1\u00d8",
            "\1\u00d9",
            "\1\u00da",
            "\1\u00db",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
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
            "\1\u00e7",
            "",
            "",
            "\1\u00e8",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u00ea",
            "\1\u00eb",
            "\1\u00ec",
            "\1\u00ed",
            "\1\u00ee",
            "\1\u00ef",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u00f1",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u00f6",
            "\1\u00f7",
            "",
            "\1\u00f8",
            "",
            "\1\u00f9",
            "\1\u00fa",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u00fc",
            "\1\u00fd",
            "\1\u00fe",
            "\1\u00ff",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0101",
            "\1\u0102",
            "\1\u0103",
            "\1\u0104",
            "\1\u0105",
            "\1\u0106",
            "\1\u0107",
            "\1\u0108",
            "\1\u0109",
            "\1\u010a",
            "\1\u010b\3\uffff\1\u010c",
            "",
            "",
            "\1\153\1\uffff\10\u010d\2\u010e\13\uffff\1\155\36\uffff\1"+
            "\156\2\155",
            "",
            "\1\153\1\uffff\12\u010e\13\uffff\1\155\37\uffff\2\155",
            "\12\u00c5\13\uffff\1\155\37\uffff\2\155",
            "\1\153\1\uffff\10\u00c6\2\u00c7\13\uffff\1\155\36\uffff\1"+
            "\156\2\155",
            "\1\153\1\uffff\12\u00c7\13\uffff\1\155\37\uffff\2\155",
            "\1\153\1\uffff\12\u00c8\13\uffff\1\155\6\uffff\1\152\27\uffff"+
            "\1\156\2\155\5\uffff\1\152",
            "\1\153\1\uffff\12\u010f\13\uffff\1\155\6\uffff\1\152\27\uffff"+
            "\1\156\2\155\5\uffff\1\152",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\u0110",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0113",
            "\1\u0114",
            "\1\u0115",
            "\1\u0116",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0118",
            "\1\u0119",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u011b",
            "",
            "\1\u011c",
            "\1\u011d",
            "\1\u011e",
            "\1\u011f",
            "\1\u0120",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0123",
            "\1\u0124",
            "\1\u0125",
            "\1\u0126",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\u0128",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u012a",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u012c",
            "\1\u012d",
            "",
            "\1\u012e",
            "",
            "",
            "",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\10\34\1\u012f\21"+
            "\34\5\uffff\uff7f\34",
            "\1\u0131",
            "\1\u0132",
            "\1\u0133",
            "\1\u0134",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0138",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u013a",
            "\1\u013b",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u013f",
            "\1\u0140",
            "\1\u0141",
            "\1\u0142",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0144",
            "\1\u0145\1\153\1\uffff\10\u00c6\2\u00c7\13\uffff\1\155\36"+
            "\uffff\1\156\2\155",
            "\1\u0145\1\153\1\uffff\12\u00c7\13\uffff\1\155\37\uffff\2"+
            "\155",
            "\1\u0145\1\153\1\uffff\12\u00c8\13\uffff\1\155\6\uffff\1\152"+
            "\27\uffff\1\156\2\155\5\uffff\1\152",
            "\1\u0146",
            "",
            "",
            "\1\u0147",
            "\1\u0148",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u014a",
            "",
            "\1\u014b",
            "\1\u014c",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u014f",
            "\1\u0150",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0154",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\u0157",
            "",
            "\1\u0158",
            "",
            "\1\u0159",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u015b",
            "\1\u015c",
            "",
            "\1\u015d",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0160",
            "",
            "",
            "",
            "\1\u0161",
            "",
            "\1\u0162",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "",
            "",
            "\1\u0164",
            "\1\u0165",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\u0169",
            "\1\u016a",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\u016c",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u016e",
            "",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "",
            "\1\u0172",
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
            "",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0179",
            "\1\u017a",
            "",
            "\1\u017b",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u017e",
            "",
            "\1\u017f",
            "",
            "\1\u0180",
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
            "",
            "\1\u0182",
            "\1\u0183",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\u0186\17\uffff\1\u0187",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\u0189",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "",
            "\1\u018b",
            "\1\u018c",
            "",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "",
            "\1\u018e",
            "\1\u018f",
            "",
            "\1\u0190",
            "\1\u0191",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\22\34\1\u0193\7"+
            "\34\5\uffff\uff7f\34",
            "",
            "\1\u0195",
            "",
            "\1\u0196",
            "\1\u0197",
            "\1\u0198",
            "\1\34\13\uffff\12\34\45\uffff\1\34\1\uffff\32\34\5\uffff\uff7f"+
            "\34",
            ""
    };

    static final short[] DFA35_eot = DFA.unpackEncodedString(DFA35_eotS);
    static final short[] DFA35_eof = DFA.unpackEncodedString(DFA35_eofS);
    static final char[] DFA35_min = DFA.unpackEncodedStringToUnsignedChars(DFA35_minS);
    static final char[] DFA35_max = DFA.unpackEncodedStringToUnsignedChars(DFA35_maxS);
    static final short[] DFA35_accept = DFA.unpackEncodedString(DFA35_acceptS);
    static final short[] DFA35_special = DFA.unpackEncodedString(DFA35_specialS);
    static final short[][] DFA35_transition;

    static {
        int numStates = DFA35_transitionS.length;
        DFA35_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA35_transition[i] = DFA.unpackEncodedString(DFA35_transitionS[i]);
        }
    }

    class DFA35 extends DFA {

        public DFA35(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 35;
            this.eot = DFA35_eot;
            this.eof = DFA35_eof;
            this.min = DFA35_min;
            this.max = DFA35_max;
            this.accept = DFA35_accept;
            this.special = DFA35_special;
            this.transition = DFA35_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( ABS | ALL | AND | ANY | AS | ASC | AVG | BETWEEN | BOTH | BY | CASE | COALESCE | CONCAT | COUNT | CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | DESC | DELETE | DISTINCT | ELSE | EMPTY | END | ENTRY | ESCAPE | EXISTS | FALSE | FETCH | FUNC | FROM | GROUP | HAVING | IN | INDEX | INNER | IS | JOIN | KEY | LEADING | LEFT | LENGTH | LIKE | LOCATE | LOWER | MAX | MEMBER | MIN | MOD | NEW | NOT | NULL | NULLIF | OBJECT | OF | OR | ORDER | OUTER | SELECT | SET | SIZE | SQRT | SOME | SUBSTRING | SUM | THEN | TRAILING | TREAT | TRIM | TRUE | TYPE | UNKNOWN | UPDATE | UPPER | VALUE | WHEN | WHERE | DOT | WS | LEFT_ROUND_BRACKET | LEFT_CURLY_BRACKET | RIGHT_ROUND_BRACKET | RIGHT_CURLY_BRACKET | COMMA | IDENT | HEX_LITERAL | INTEGER_LITERAL | LONG_LITERAL | OCTAL_LITERAL | DOUBLE_LITERAL | FLOAT_LITERAL | DATE_LITERAL | TIME_LITERAL | TIMESTAMP_LITERAL | DATE_STRING | TIME_STRING | EQUALS | GREATER_THAN | GREATER_THAN_EQUAL_TO | LESS_THAN | LESS_THAN_EQUAL_TO | NOT_EQUAL_TO | MULTIPLY | DIVIDE | PLUS | MINUS | POSITIONAL_PARAM | NAMED_PARAM | STRING_LITERAL_DOUBLE_QUOTED | STRING_LITERAL_SINGLE_QUOTED );";
        }
    }
 

}