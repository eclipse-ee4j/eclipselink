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
import org.eclipse.persistence.internal.libraries.antlr.runtime.tree.*;

class JSONParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ARRAY", "Char", "Digits", "E", "Exp", "FALSE", "Frac", "Hex_Digit", "Int", "NULL", "NUMBER", "Number", "OBJECT", "PAIR", "STRING", "String", "TRUE", "Whitespace", "','", "':'", "'['", "']'", "'false'", "'null'", "'true'", "'{'", "'}'"
    };

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
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public JSONParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public JSONParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return JSONParser.tokenNames; }
    public String getGrammarFileName() { return "<EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g"; }


    public static class message_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "message"
    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:95:1: message : ( object | array );
    public final JSONParser.message_return message() throws RecognitionException {
        JSONParser.message_return retval = new JSONParser.message_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        JSONParser.object_return object1 =null;

        JSONParser.array_return array2 =null;



        try {
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:95:9: ( object | array )
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==29) ) {
                alt1=1;
            }
            else if ( (LA1_0==24) ) {
                alt1=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;

            }
            switch (alt1) {
                case 1 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:95:11: object
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_object_in_message444);
                    object1=object();

                    state._fsp--;

                    adaptor.addChild(root_0, object1.getTree());

                    }
                    break;
                case 2 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:95:20: array
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_array_in_message448);
                    array2=array();

                    state._fsp--;

                    adaptor.addChild(root_0, array2.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "message"


    public static class object_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "object"
    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:97:1: object : '{' ( pair ( ',' pair )* )? '}' -> ^( OBJECT ( pair )* ) ;
    public final JSONParser.object_return object() throws RecognitionException {
        JSONParser.object_return retval = new JSONParser.object_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token char_literal3=null;
        Token char_literal5=null;
        Token char_literal7=null;
        JSONParser.pair_return pair4 =null;

        JSONParser.pair_return pair6 =null;


        Object char_literal3_tree=null;
        Object char_literal5_tree=null;
        Object char_literal7_tree=null;
        RewriteRuleTokenStream stream_30=new RewriteRuleTokenStream(adaptor,"token 30");
        RewriteRuleTokenStream stream_22=new RewriteRuleTokenStream(adaptor,"token 22");
        RewriteRuleTokenStream stream_29=new RewriteRuleTokenStream(adaptor,"token 29");
        RewriteRuleSubtreeStream stream_pair=new RewriteRuleSubtreeStream(adaptor,"rule pair");
        try {
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:97:8: ( '{' ( pair ( ',' pair )* )? '}' -> ^( OBJECT ( pair )* ) )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:97:10: '{' ( pair ( ',' pair )* )? '}'
            {
            char_literal3=(Token)match(input,29,FOLLOW_29_in_object456);  
            stream_29.add(char_literal3);


            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:97:14: ( pair ( ',' pair )* )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==String) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:97:15: pair ( ',' pair )*
                    {
                    pushFollow(FOLLOW_pair_in_object459);
                    pair4=pair();

                    state._fsp--;

                    stream_pair.add(pair4.getTree());

                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:97:20: ( ',' pair )*
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( (LA2_0==22) ) {
                            alt2=1;
                        }


                        switch (alt2) {
                        case 1 :
                            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:97:21: ',' pair
                            {
                            char_literal5=(Token)match(input,22,FOLLOW_22_in_object462);  
                            stream_22.add(char_literal5);


                            pushFollow(FOLLOW_pair_in_object464);
                            pair6=pair();

                            state._fsp--;

                            stream_pair.add(pair6.getTree());

                            }
                            break;

                        default :
                            break loop2;
                        }
                    } while (true);


                    }
                    break;

            }


            char_literal7=(Token)match(input,30,FOLLOW_30_in_object470);  
            stream_30.add(char_literal7);


            // AST REWRITE
            // elements: pair
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 97:38: -> ^( OBJECT ( pair )* )
            {
                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:97:41: ^( OBJECT ( pair )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(OBJECT, "OBJECT")
                , root_1);

                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:97:50: ( pair )*
                while ( stream_pair.hasNext() ) {
                    adaptor.addChild(root_1, stream_pair.nextTree());

                }
                stream_pair.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "object"


    public static class pair_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "pair"
    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:99:1: pair : String ':' value -> ^( PAIR String value ) ;
    public final JSONParser.pair_return pair() throws RecognitionException {
        JSONParser.pair_return retval = new JSONParser.pair_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token String8=null;
        Token char_literal9=null;
        JSONParser.value_return value10 =null;


        Object String8_tree=null;
        Object char_literal9_tree=null;
        RewriteRuleTokenStream stream_String=new RewriteRuleTokenStream(adaptor,"token String");
        RewriteRuleTokenStream stream_23=new RewriteRuleTokenStream(adaptor,"token 23");
        RewriteRuleSubtreeStream stream_value=new RewriteRuleSubtreeStream(adaptor,"rule value");
        try {
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:99:6: ( String ':' value -> ^( PAIR String value ) )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:99:7: String ':' value
            {
            String8=(Token)match(input,String,FOLLOW_String_in_pair486);  
            stream_String.add(String8);


            char_literal9=(Token)match(input,23,FOLLOW_23_in_pair488);  
            stream_23.add(char_literal9);


            pushFollow(FOLLOW_value_in_pair490);
            value10=value();

            state._fsp--;

            stream_value.add(value10.getTree());

            // AST REWRITE
            // elements: value, String
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 99:24: -> ^( PAIR String value )
            {
                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:99:27: ^( PAIR String value )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(PAIR, "PAIR")
                , root_1);

                adaptor.addChild(root_1, 
                stream_String.nextNode()
                );

                adaptor.addChild(root_1, stream_value.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "pair"


    public static class array_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "array"
    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:101:1: array : '[' ( value ( ',' value )* )? ']' -> ^( ARRAY ( value )* ) ;
    public final JSONParser.array_return array() throws RecognitionException {
        JSONParser.array_return retval = new JSONParser.array_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token char_literal11=null;
        Token char_literal13=null;
        Token char_literal15=null;
        JSONParser.value_return value12 =null;

        JSONParser.value_return value14 =null;


        Object char_literal11_tree=null;
        Object char_literal13_tree=null;
        Object char_literal15_tree=null;
        RewriteRuleTokenStream stream_22=new RewriteRuleTokenStream(adaptor,"token 22");
        RewriteRuleTokenStream stream_24=new RewriteRuleTokenStream(adaptor,"token 24");
        RewriteRuleTokenStream stream_25=new RewriteRuleTokenStream(adaptor,"token 25");
        RewriteRuleSubtreeStream stream_value=new RewriteRuleSubtreeStream(adaptor,"rule value");
        try {
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:101:7: ( '[' ( value ( ',' value )* )? ']' -> ^( ARRAY ( value )* ) )
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:101:9: '[' ( value ( ',' value )* )? ']'
            {
            char_literal11=(Token)match(input,24,FOLLOW_24_in_array508);  
            stream_24.add(char_literal11);


            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:101:13: ( value ( ',' value )* )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0==Number||LA5_0==String||LA5_0==24||(LA5_0 >= 26 && LA5_0 <= 29)) ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:101:14: value ( ',' value )*
                    {
                    pushFollow(FOLLOW_value_in_array511);
                    value12=value();

                    state._fsp--;

                    stream_value.add(value12.getTree());

                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:101:20: ( ',' value )*
                    loop4:
                    do {
                        int alt4=2;
                        int LA4_0 = input.LA(1);

                        if ( (LA4_0==22) ) {
                            alt4=1;
                        }


                        switch (alt4) {
                        case 1 :
                            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:101:21: ',' value
                            {
                            char_literal13=(Token)match(input,22,FOLLOW_22_in_array514);  
                            stream_22.add(char_literal13);


                            pushFollow(FOLLOW_value_in_array516);
                            value14=value();

                            state._fsp--;

                            stream_value.add(value14.getTree());

                            }
                            break;

                        default :
                            break loop4;
                        }
                    } while (true);


                    }
                    break;

            }


            char_literal15=(Token)match(input,25,FOLLOW_25_in_array522);  
            stream_25.add(char_literal15);


            // AST REWRITE
            // elements: value
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 101:39: -> ^( ARRAY ( value )* )
            {
                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:101:42: ^( ARRAY ( value )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(ARRAY, "ARRAY")
                , root_1);

                // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:101:50: ( value )*
                while ( stream_value.hasNext() ) {
                    adaptor.addChild(root_1, stream_value.nextTree());

                }
                stream_value.reset();

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "array"


    public static class value_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "value"
    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:103:1: value : ( String -> ^( STRING String ) | Number -> ^( NUMBER Number ) | object | array | 'true' -> TRUE | 'false' -> FALSE | 'null' -> NULL );
    public final JSONParser.value_return value() throws RecognitionException {
        JSONParser.value_return retval = new JSONParser.value_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token String16=null;
        Token Number17=null;
        Token string_literal20=null;
        Token string_literal21=null;
        Token string_literal22=null;
        JSONParser.object_return object18 =null;

        JSONParser.array_return array19 =null;


        Object String16_tree=null;
        Object Number17_tree=null;
        Object string_literal20_tree=null;
        Object string_literal21_tree=null;
        Object string_literal22_tree=null;
        RewriteRuleTokenStream stream_Number=new RewriteRuleTokenStream(adaptor,"token Number");
        RewriteRuleTokenStream stream_String=new RewriteRuleTokenStream(adaptor,"token String");
        RewriteRuleTokenStream stream_26=new RewriteRuleTokenStream(adaptor,"token 26");
        RewriteRuleTokenStream stream_27=new RewriteRuleTokenStream(adaptor,"token 27");
        RewriteRuleTokenStream stream_28=new RewriteRuleTokenStream(adaptor,"token 28");

        try {
            // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:103:7: ( String -> ^( STRING String ) | Number -> ^( NUMBER Number ) | object | array | 'true' -> TRUE | 'false' -> FALSE | 'null' -> NULL )
            int alt6=7;
            switch ( input.LA(1) ) {
            case String:
                {
                alt6=1;
                }
                break;
            case Number:
                {
                alt6=2;
                }
                break;
            case 29:
                {
                alt6=3;
                }
                break;
            case 24:
                {
                alt6=4;
                }
                break;
            case 28:
                {
                alt6=5;
                }
                break;
            case 26:
                {
                alt6=6;
                }
                break;
            case 27:
                {
                alt6=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;

            }

            switch (alt6) {
                case 1 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:103:9: String
                    {
                    String16=(Token)match(input,String,FOLLOW_String_in_value539);  
                    stream_String.add(String16);


                    // AST REWRITE
                    // elements: String
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 103:16: -> ^( STRING String )
                    {
                        // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:103:19: ^( STRING String )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(STRING, "STRING")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_String.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 2 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:104:18: Number
                    {
                    Number17=(Token)match(input,Number,FOLLOW_Number_in_value566);  
                    stream_Number.add(Number17);


                    // AST REWRITE
                    // elements: Number
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 104:25: -> ^( NUMBER Number )
                    {
                        // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:104:28: ^( NUMBER Number )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(NUMBER, "NUMBER")
                        , root_1);

                        adaptor.addChild(root_1, 
                        stream_Number.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 3 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:105:18: object
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_object_in_value593);
                    object18=object();

                    state._fsp--;

                    adaptor.addChild(root_0, object18.getTree());

                    }
                    break;
                case 4 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:106:18: array
                    {
                    root_0 = (Object)adaptor.nil();


                    pushFollow(FOLLOW_array_in_value612);
                    array19=array();

                    state._fsp--;

                    adaptor.addChild(root_0, array19.getTree());

                    }
                    break;
                case 5 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:107:18: 'true'
                    {
                    string_literal20=(Token)match(input,28,FOLLOW_28_in_value631);  
                    stream_28.add(string_literal20);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 107:25: -> TRUE
                    {
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(TRUE, "TRUE")
                        );

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 6 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:108:18: 'false'
                    {
                    string_literal21=(Token)match(input,26,FOLLOW_26_in_value654);  
                    stream_26.add(string_literal21);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 108:26: -> FALSE
                    {
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(FALSE, "FALSE")
                        );

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 7 :
                    // <EclipseLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:109:18: 'null'
                    {
                    string_literal22=(Token)match(input,27,FOLLOW_27_in_value677);  
                    stream_27.add(string_literal22);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 109:25: -> NULL
                    {
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(NULL, "NULL")
                        );

                    }


                    retval.tree = root_0;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
            // do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "value"

    // Delegated rules


 

    public static final BitSet FOLLOW_object_in_message444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_in_message448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_object456 = new BitSet(new long[]{0x0000000040080000L});
    public static final BitSet FOLLOW_pair_in_object459 = new BitSet(new long[]{0x0000000040400000L});
    public static final BitSet FOLLOW_22_in_object462 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_pair_in_object464 = new BitSet(new long[]{0x0000000040400000L});
    public static final BitSet FOLLOW_30_in_object470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_String_in_pair486 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_pair488 = new BitSet(new long[]{0x000000003D088000L});
    public static final BitSet FOLLOW_value_in_pair490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_array508 = new BitSet(new long[]{0x000000003F088000L});
    public static final BitSet FOLLOW_value_in_array511 = new BitSet(new long[]{0x0000000002400000L});
    public static final BitSet FOLLOW_22_in_array514 = new BitSet(new long[]{0x000000003D088000L});
    public static final BitSet FOLLOW_value_in_array516 = new BitSet(new long[]{0x0000000002400000L});
    public static final BitSet FOLLOW_25_in_array522 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_String_in_value539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Number_in_value566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_object_in_value593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_in_value612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_value631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_value654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_value677 = new BitSet(new long[]{0x0000000000000002L});

}