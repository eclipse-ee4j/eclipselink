/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "OBJECT", "PAIR", "STRING", "NUMBER", "ARRAY", "TRUE", "FALSE", "NULL", "Char", "String", "Hex_Digit", "Int", "Frac", "Exp", "Number", "Digits", "E", "Whitespace", "'{'", "','", "'}'", "':'", "'['", "']'", "'true'", "'false'", "'null'"
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
    public static final int OBJECT=4;
    public static final int PAIR=5;
    public static final int STRING=6;
    public static final int NUMBER=7;
    public static final int ARRAY=8;
    public static final int TRUE=9;
    public static final int FALSE=10;
    public static final int NULL=11;
    public static final int Char=12;
    public static final int String=13;
    public static final int Hex_Digit=14;
    public static final int Int=15;
    public static final int Frac=16;
    public static final int Exp=17;
    public static final int Number=18;
    public static final int Digits=19;
    public static final int E=20;
    public static final int Whitespace=21;

    // delegates
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
    public String getGrammarFileName() { return "<EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g"; }


    public static class object_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "object"
    // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:95:1: object : '{' ( pair ( ',' pair )* )? '}' -> ^( OBJECT ( pair )* ) ;
    public final JSONParser.object_return object() throws RecognitionException {
        JSONParser.object_return retval = new JSONParser.object_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal1=null;
        Token char_literal3=null;
        Token char_literal5=null;
        JSONParser.pair_return pair2 = null;

        JSONParser.pair_return pair4 = null;


        Object char_literal1_tree=null;
        Object char_literal3_tree=null;
        Object char_literal5_tree=null;
        RewriteRuleTokenStream stream_22=new RewriteRuleTokenStream(adaptor,"token 22");
        RewriteRuleTokenStream stream_23=new RewriteRuleTokenStream(adaptor,"token 23");
        RewriteRuleTokenStream stream_24=new RewriteRuleTokenStream(adaptor,"token 24");
        RewriteRuleSubtreeStream stream_pair=new RewriteRuleSubtreeStream(adaptor,"rule pair");
        try {
            // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:95:8: ( '{' ( pair ( ',' pair )* )? '}' -> ^( OBJECT ( pair )* ) )
            // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:95:10: '{' ( pair ( ',' pair )* )? '}'
            {
            char_literal1=(Token)match(input,22,FOLLOW_22_in_object444);
            stream_22.add(char_literal1);

            // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:95:14: ( pair ( ',' pair )* )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==String) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:95:15: pair ( ',' pair )*
                    {
                    pushFollow(FOLLOW_pair_in_object447);
                    pair2=pair();

                    state._fsp--;

                    stream_pair.add(pair2.getTree());
                    // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:95:20: ( ',' pair )*
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( (LA1_0==23) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                        case 1 :
                            // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:95:21: ',' pair
                            {
                            char_literal3=(Token)match(input,23,FOLLOW_23_in_object450);
                            stream_23.add(char_literal3);

                            pushFollow(FOLLOW_pair_in_object452);
                            pair4=pair();

                            state._fsp--;

                            stream_pair.add(pair4.getTree());

                            }
                            break;

                        default :
                            break loop1;
                        }
                    } while (true);


                    }
                    break;

            }

            char_literal5=(Token)match(input,24,FOLLOW_24_in_object458);
            stream_24.add(char_literal5);



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
            // 95:38: -> ^( OBJECT ( pair )* )
            {
                // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:95:41: ^( OBJECT ( pair )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(OBJECT, "OBJECT"), root_1);

                // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:95:50: ( pair )*
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
        }
        return retval;
    }
    // $ANTLR end "object"

    public static class pair_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "pair"
    // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:97:1: pair : String ':' value -> ^( PAIR String value ) ;
    public final JSONParser.pair_return pair() throws RecognitionException {
        JSONParser.pair_return retval = new JSONParser.pair_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token String6=null;
        Token char_literal7=null;
        JSONParser.value_return value8 = null;


        Object String6_tree=null;
        Object char_literal7_tree=null;
        RewriteRuleTokenStream stream_String=new RewriteRuleTokenStream(adaptor,"token String");
        RewriteRuleTokenStream stream_25=new RewriteRuleTokenStream(adaptor,"token 25");
        RewriteRuleSubtreeStream stream_value=new RewriteRuleSubtreeStream(adaptor,"rule value");
        try {
            // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:97:6: ( String ':' value -> ^( PAIR String value ) )
            // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:97:7: String ':' value
            {
            String6=(Token)match(input,String,FOLLOW_String_in_pair474);
            stream_String.add(String6);

            char_literal7=(Token)match(input,25,FOLLOW_25_in_pair476);
            stream_25.add(char_literal7);

            pushFollow(FOLLOW_value_in_pair478);
            value8=value();

            state._fsp--;

            stream_value.add(value8.getTree());


            // AST REWRITE
            // elements: String, value
            // token labels:
            // rule labels: retval
            // token list labels:
            // rule list labels:
            // wildcard labels:
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 97:24: -> ^( PAIR String value )
            {
                // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:97:27: ^( PAIR String value )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(PAIR, "PAIR"), root_1);

                adaptor.addChild(root_1, stream_String.nextNode());
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
        }
        return retval;
    }
    // $ANTLR end "pair"

    public static class array_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "array"
    // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:99:1: array : '[' ( value ( ',' value )* )? ']' -> ^( ARRAY ( value )* ) ;
    public final JSONParser.array_return array() throws RecognitionException {
        JSONParser.array_return retval = new JSONParser.array_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token char_literal9=null;
        Token char_literal11=null;
        Token char_literal13=null;
        JSONParser.value_return value10 = null;

        JSONParser.value_return value12 = null;


        Object char_literal9_tree=null;
        Object char_literal11_tree=null;
        Object char_literal13_tree=null;
        RewriteRuleTokenStream stream_23=new RewriteRuleTokenStream(adaptor,"token 23");
        RewriteRuleTokenStream stream_26=new RewriteRuleTokenStream(adaptor,"token 26");
        RewriteRuleTokenStream stream_27=new RewriteRuleTokenStream(adaptor,"token 27");
        RewriteRuleSubtreeStream stream_value=new RewriteRuleSubtreeStream(adaptor,"rule value");
        try {
            // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:99:7: ( '[' ( value ( ',' value )* )? ']' -> ^( ARRAY ( value )* ) )
            // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:99:9: '[' ( value ( ',' value )* )? ']'
            {
            char_literal9=(Token)match(input,26,FOLLOW_26_in_array496);
            stream_26.add(char_literal9);

            // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:99:13: ( value ( ',' value )* )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==String||LA4_0==Number||LA4_0==22||LA4_0==26||(LA4_0>=28 && LA4_0<=30)) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:99:14: value ( ',' value )*
                    {
                    pushFollow(FOLLOW_value_in_array499);
                    value10=value();

                    state._fsp--;

                    stream_value.add(value10.getTree());
                    // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:99:20: ( ',' value )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==23) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                        case 1 :
                            // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:99:21: ',' value
                            {
                            char_literal11=(Token)match(input,23,FOLLOW_23_in_array502);
                            stream_23.add(char_literal11);

                            pushFollow(FOLLOW_value_in_array504);
                            value12=value();

                            state._fsp--;

                            stream_value.add(value12.getTree());

                            }
                            break;

                        default :
                            break loop3;
                        }
                    } while (true);


                    }
                    break;

            }

            char_literal13=(Token)match(input,27,FOLLOW_27_in_array510);
            stream_27.add(char_literal13);



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
            // 99:39: -> ^( ARRAY ( value )* )
            {
                // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:99:42: ^( ARRAY ( value )* )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ARRAY, "ARRAY"), root_1);

                // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:99:50: ( value )*
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
        }
        return retval;
    }
    // $ANTLR end "array"

    public static class value_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "value"
    // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:101:1: value : ( String -> ^( STRING String ) | Number -> ^( NUMBER Number ) | object | array | 'true' -> TRUE | 'false' -> FALSE | 'null' -> NULL );
    public final JSONParser.value_return value() throws RecognitionException {
        JSONParser.value_return retval = new JSONParser.value_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token String14=null;
        Token Number15=null;
        Token string_literal18=null;
        Token string_literal19=null;
        Token string_literal20=null;
        JSONParser.object_return object16 = null;

        JSONParser.array_return array17 = null;


        Object String14_tree=null;
        Object Number15_tree=null;
        Object string_literal18_tree=null;
        Object string_literal19_tree=null;
        Object string_literal20_tree=null;
        RewriteRuleTokenStream stream_30=new RewriteRuleTokenStream(adaptor,"token 30");
        RewriteRuleTokenStream stream_Number=new RewriteRuleTokenStream(adaptor,"token Number");
        RewriteRuleTokenStream stream_String=new RewriteRuleTokenStream(adaptor,"token String");
        RewriteRuleTokenStream stream_28=new RewriteRuleTokenStream(adaptor,"token 28");
        RewriteRuleTokenStream stream_29=new RewriteRuleTokenStream(adaptor,"token 29");

        try {
            // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:101:7: ( String -> ^( STRING String ) | Number -> ^( NUMBER Number ) | object | array | 'true' -> TRUE | 'false' -> FALSE | 'null' -> NULL )
            int alt5=7;
            switch ( input.LA(1) ) {
            case String:
                {
                alt5=1;
                }
                break;
            case Number:
                {
                alt5=2;
                }
                break;
            case 22:
                {
                alt5=3;
                }
                break;
            case 26:
                {
                alt5=4;
                }
                break;
            case 28:
                {
                alt5=5;
                }
                break;
            case 29:
                {
                alt5=6;
                }
                break;
            case 30:
                {
                alt5=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:101:9: String
                    {
                    String14=(Token)match(input,String,FOLLOW_String_in_value527);
                    stream_String.add(String14);



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
                    // 101:16: -> ^( STRING String )
                    {
                        // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:101:19: ^( STRING String )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(STRING, "STRING"), root_1);

                        adaptor.addChild(root_1, stream_String.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:102:18: Number
                    {
                    Number15=(Token)match(input,Number,FOLLOW_Number_in_value554);
                    stream_Number.add(Number15);



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
                    // 102:25: -> ^( NUMBER Number )
                    {
                        // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:102:28: ^( NUMBER Number )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(NUMBER, "NUMBER"), root_1);

                        adaptor.addChild(root_1, stream_Number.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:103:18: object
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_object_in_value581);
                    object16=object();

                    state._fsp--;

                    adaptor.addChild(root_0, object16.getTree());

                    }
                    break;
                case 4 :
                    // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:104:18: array
                    {
                    root_0 = (Object)adaptor.nil();

                    pushFollow(FOLLOW_array_in_value600);
                    array17=array();

                    state._fsp--;

                    adaptor.addChild(root_0, array17.getTree());

                    }
                    break;
                case 5 :
                    // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:105:18: 'true'
                    {
                    string_literal18=(Token)match(input,28,FOLLOW_28_in_value619);
                    stream_28.add(string_literal18);



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
                    // 105:25: -> TRUE
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(TRUE, "TRUE"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 6 :
                    // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:106:18: 'false'
                    {
                    string_literal19=(Token)match(input,29,FOLLOW_29_in_value642);
                    stream_29.add(string_literal19);



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
                    // 106:26: -> FALSE
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(FALSE, "FALSE"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 7 :
                    // <EclipeLink_Trunk>\\foundation\\org.eclipse.persistence.core\\resource\\org\\eclipse\\persistence\\internal\\oxm\\record\\json\\JSON.g:107:18: 'null'
                    {
                    string_literal20=(Token)match(input,30,FOLLOW_30_in_value665);
                    stream_30.add(string_literal20);



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
                    // 107:25: -> NULL
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(NULL, "NULL"));

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
        }
        return retval;
    }
    // $ANTLR end "value"

    // Delegated rules




    public static final BitSet FOLLOW_22_in_object444 = new BitSet(new long[]{0x0000000001002000L});
    public static final BitSet FOLLOW_pair_in_object447 = new BitSet(new long[]{0x0000000001800000L});
    public static final BitSet FOLLOW_23_in_object450 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_pair_in_object452 = new BitSet(new long[]{0x0000000001800000L});
    public static final BitSet FOLLOW_24_in_object458 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_String_in_pair474 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_pair476 = new BitSet(new long[]{0x0000000074442000L});
    public static final BitSet FOLLOW_value_in_pair478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_array496 = new BitSet(new long[]{0x000000007C442000L});
    public static final BitSet FOLLOW_value_in_array499 = new BitSet(new long[]{0x0000000008800000L});
    public static final BitSet FOLLOW_23_in_array502 = new BitSet(new long[]{0x0000000074442000L});
    public static final BitSet FOLLOW_value_in_array504 = new BitSet(new long[]{0x0000000008800000L});
    public static final BitSet FOLLOW_27_in_array510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_String_in_value527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_Number_in_value554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_object_in_value581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_array_in_value600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_value619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_value642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_value665 = new BitSet(new long[]{0x0000000000000002L});

}