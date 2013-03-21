/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm.record.json;

import org.eclipse.persistence.internal.libraries.antlr.runtime.*;

class JSONLexer extends Lexer {
    public static final int EOF=-1;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__30=30;
    public static final int ARRAY=4;
    public static final int Char=5;
    public static final int Digits=6;
    public static final int E=7;
    public static final int Exp=8;
    public static final int FALSE=9;
    public static final int Frac=10;
    public static final int Hex_Digit=11;
    public static final int Int=12;
    public static final int NULL=13;
    public static final int NUMBER=14;
    public static final int Number=15;
    public static final int OBJECT=16;
    public static final int PAIR=17;
    public static final int STRING=18;
    public static final int String=19;
    public static final int TRUE=20;
    public static final int Whitespace=21;

    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public JSONLexer() {} 
    public JSONLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public JSONLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "<EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g"; }

    // $ANTLR start "T__22"
    public final void mT__22() throws RecognitionException {
        try {
            int _type = T__22;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:18:7: ( ',' )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:18:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__22"

    // $ANTLR start "T__23"
    public final void mT__23() throws RecognitionException {
        try {
            int _type = T__23;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:19:7: ( ':' )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:19:9: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__23"

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:20:7: ( '[' )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:20:9: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__24"

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:21:7: ( ']' )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:21:9: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__25"

    // $ANTLR start "T__26"
    public final void mT__26() throws RecognitionException {
        try {
            int _type = T__26;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:22:7: ( 'false' )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:22:9: 'false'
            {
            match("false"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__26"

    // $ANTLR start "T__27"
    public final void mT__27() throws RecognitionException {
        try {
            int _type = T__27;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:23:7: ( 'null' )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:23:9: 'null'
            {
            match("null"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__27"

    // $ANTLR start "T__28"
    public final void mT__28() throws RecognitionException {
        try {
            int _type = T__28;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:24:7: ( 'true' )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:24:9: 'true'
            {
            match("true"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__28"

    // $ANTLR start "T__29"
    public final void mT__29() throws RecognitionException {
        try {
            int _type = T__29;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:25:7: ( '{' )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:25:9: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__29"

    // $ANTLR start "T__30"
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:26:7: ( '}' )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:26:9: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__30"

    // $ANTLR start "String"
    public final void mString() throws RecognitionException {
        try {
            int _type = String;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:64:9: ( '\"' ( Char )* '\"' )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:64:11: '\"' ( Char )* '\"'
            {
            match('\"'); 

            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:64:15: ( Char )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0 >= '\u0000' && LA1_0 <= '!')||(LA1_0 >= '#' && LA1_0 <= '\uFFFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
                case 1 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:64:15: Char
                    {
                    mChar(); 


                    }
                    break;

                default :
                    break loop1;
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
    // $ANTLR end "String"

    // $ANTLR start "Char"
    public final void mChar() throws RecognitionException {
        try {
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:66:17: (~ ( '\"' | '\\\\' ) | '\\\\\"' | '\\\\\\\\' | '\\\\/' | '\\\\b' | '\\\\f' | '\\\\n' | '\\\\r' | '\\\\t' | '\\\\u' Hex_Digit Hex_Digit Hex_Digit Hex_Digit )
            int alt2=10;
            int LA2_0 = input.LA(1);

            if ( ((LA2_0 >= '\u0000' && LA2_0 <= '!')||(LA2_0 >= '#' && LA2_0 <= '[')||(LA2_0 >= ']' && LA2_0 <= '\uFFFF')) ) {
                alt2=1;
            }
            else if ( (LA2_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                    {
                    alt2=2;
                    }
                    break;
                case '\\':
                    {
                    alt2=3;
                    }
                    break;
                case '/':
                    {
                    alt2=4;
                    }
                    break;
                case 'b':
                    {
                    alt2=5;
                    }
                    break;
                case 'f':
                    {
                    alt2=6;
                    }
                    break;
                case 'n':
                    {
                    alt2=7;
                    }
                    break;
                case 'r':
                    {
                    alt2=8;
                    }
                    break;
                case 't':
                    {
                    alt2=9;
                    }
                    break;
                case 'u':
                    {
                    alt2=10;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 2, 2, input);

                    throw nvae;

                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;

            }
            switch (alt2) {
                case 1 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:66:19: ~ ( '\"' | '\\\\' )
                    {
                    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;
                case 2 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:67:19: '\\\\\"'
                    {
                    match("\\\""); 



                    }
                    break;
                case 3 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:68:19: '\\\\\\\\'
                    {
                    match("\\\\"); 



                    }
                    break;
                case 4 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:69:19: '\\\\/'
                    {
                    match("\\/"); 



                    }
                    break;
                case 5 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:70:19: '\\\\b'
                    {
                    match("\\b"); 



                    }
                    break;
                case 6 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:71:19: '\\\\f'
                    {
                    match("\\f"); 



                    }
                    break;
                case 7 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:72:19: '\\\\n'
                    {
                    match("\\n"); 



                    }
                    break;
                case 8 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:73:19: '\\\\r'
                    {
                    match("\\r"); 



                    }
                    break;
                case 9 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:74:19: '\\\\t'
                    {
                    match("\\t"); 



                    }
                    break;
                case 10 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:75:19: '\\\\u' Hex_Digit Hex_Digit Hex_Digit Hex_Digit
                    {
                    match("\\u"); 



                    mHex_Digit(); 


                    mHex_Digit(); 


                    mHex_Digit(); 


                    mHex_Digit(); 


                    }
                    break;

            }

        }
        finally {
        }
    }
    // $ANTLR end "Char"

    // $ANTLR start "Hex_Digit"
    public final void mHex_Digit() throws RecognitionException {
        try {
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:77:20: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'F')||(input.LA(1) >= 'a' && input.LA(1) <= 'f') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        }
    }
    // $ANTLR end "Hex_Digit"

    // $ANTLR start "Number"
    public final void mNumber() throws RecognitionException {
        try {
            int _type = Number;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:79:9: ( Int ( Frac )? ( Exp )? )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:79:11: Int ( Frac )? ( Exp )?
            {
            mInt(); 


            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:79:15: ( Frac )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='.') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:79:15: Frac
                    {
                    mFrac(); 


                    }
                    break;

            }


            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:79:21: ( Exp )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='E'||LA4_0=='e') ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:79:21: Exp
                    {
                    mExp(); 


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
    // $ANTLR end "Number"

    // $ANTLR start "Int"
    public final void mInt() throws RecognitionException {
        try {
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:81:15: ( ( '-' )? Digits )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:81:17: ( '-' )? Digits
            {
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:81:17: ( '-' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='-') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:81:17: '-'
                    {
                    match('-'); 

                    }
                    break;

            }


            mDigits(); 


            }


        }
        finally {
        }
    }
    // $ANTLR end "Int"

    // $ANTLR start "Frac"
    public final void mFrac() throws RecognitionException {
        try {
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:83:15: ( '.' Digits )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:83:17: '.' Digits
            {
            match('.'); 

            mDigits(); 


            }


        }
        finally {
        }
    }
    // $ANTLR end "Frac"

    // $ANTLR start "Exp"
    public final void mExp() throws RecognitionException {
        try {
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:85:14: ( E Digits )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:85:16: E Digits
            {
            mE(); 


            mDigits(); 


            }


        }
        finally {
        }
    }
    // $ANTLR end "Exp"

    // $ANTLR start "Digits"
    public final void mDigits() throws RecognitionException {
        try {
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:87:17: ( ( '0' .. '9' )+ )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:87:19: ( '0' .. '9' )+
            {
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:87:19: ( '0' .. '9' )+
            int cnt6=0;
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( ((LA6_0 >= '0' && LA6_0 <= '9')) ) {
                    alt6=1;
                }


                switch (alt6) {
                case 1 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:
                    {
                    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

                default :
                    if ( cnt6 >= 1 ) break loop6;
                        EarlyExitException eee =
                            new EarlyExitException(6, input);
                        throw eee;
                }
                cnt6++;
            } while (true);


            }


        }
        finally {
        }
    }
    // $ANTLR end "Digits"

    // $ANTLR start "E"
    public final void mE() throws RecognitionException {
        try {
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:89:12: ( ( 'e' | 'E' ) ( '+' | '-' )? )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:89:13: ( 'e' | 'E' ) ( '+' | '-' )?
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:89:23: ( '+' | '-' )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0=='+'||LA7_0=='-') ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }


            }


        }
        finally {
        }
    }
    // $ANTLR end "E"

    // $ANTLR start "Whitespace"
    public final void mWhitespace() throws RecognitionException {
        try {
            int _type = Whitespace;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:91:12: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )+ )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:91:14: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )+
            {
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:91:14: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( ((LA8_0 >= '\t' && LA8_0 <= '\n')||(LA8_0 >= '\f' && LA8_0 <= '\r')||LA8_0==' ') ) {
                    alt8=1;
                }


                switch (alt8) {
                case 1 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:
                    {
                    if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


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


             _channel = HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "Whitespace"

    public void mTokens() throws RecognitionException {
        // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:1:8: ( T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | String | Number | Whitespace )
        int alt9=12;
        switch ( input.LA(1) ) {
        case ',':
            {
            alt9=1;
            }
            break;
        case ':':
            {
            alt9=2;
            }
            break;
        case '[':
            {
            alt9=3;
            }
            break;
        case ']':
            {
            alt9=4;
            }
            break;
        case 'f':
            {
            alt9=5;
            }
            break;
        case 'n':
            {
            alt9=6;
            }
            break;
        case 't':
            {
            alt9=7;
            }
            break;
        case '{':
            {
            alt9=8;
            }
            break;
        case '}':
            {
            alt9=9;
            }
            break;
        case '\"':
            {
            alt9=10;
            }
            break;
        case '-':
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
            {
            alt9=11;
            }
            break;
        case '\t':
        case '\n':
        case '\f':
        case '\r':
        case ' ':
            {
            alt9=12;
            }
            break;
        default:
            NoViableAltException nvae =
                new NoViableAltException("", 9, 0, input);

            throw nvae;

        }

        switch (alt9) {
            case 1 :
                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:1:10: T__22
                {
                mT__22(); 


                }
                break;
            case 2 :
                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:1:16: T__23
                {
                mT__23(); 


                }
                break;
            case 3 :
                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:1:22: T__24
                {
                mT__24(); 


                }
                break;
            case 4 :
                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:1:28: T__25
                {
                mT__25(); 


                }
                break;
            case 5 :
                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:1:34: T__26
                {
                mT__26(); 


                }
                break;
            case 6 :
                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:1:40: T__27
                {
                mT__27(); 


                }
                break;
            case 7 :
                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:1:46: T__28
                {
                mT__28(); 


                }
                break;
            case 8 :
                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:1:52: T__29
                {
                mT__29(); 


                }
                break;
            case 9 :
                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:1:58: T__30
                {
                mT__30(); 


                }
                break;
            case 10 :
                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:1:64: String
                {
                mString(); 


                }
                break;
            case 11 :
                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:1:71: Number
                {
                mNumber(); 


                }
                break;
            case 12 :
                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:1:78: Whitespace
                {
                mWhitespace(); 


                }
                break;

        }

    }


 

}