/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.parsing.jpql;

import java.util.List;
import java.util.ArrayList;

import org.eclipse.persistence.internal.libraries.antlr.runtime.*;
// Third party (ANLTR) stuff
/*import org.eclipse.persistence.internal.libraries.antlr.ANTLRException;
import org.eclipse.persistence.internal.libraries.antlr.LLkParser;
import org.eclipse.persistence.internal.libraries.antlr.MismatchedCharException;
import org.eclipse.persistence.internal.libraries.antlr.MismatchedTokenException;
import org.eclipse.persistence.internal.libraries.antlr.NoViableAltException;
import org.eclipse.persistence.internal.libraries.antlr.NoViableAltForCharException;
import org.eclipse.persistence.internal.libraries.antlr.ParserSharedInputState;
import org.eclipse.persistence.internal.libraries.antlr.RecognitionException;
import org.eclipse.persistence.internal.libraries.antlr.Token;
import org.eclipse.persistence.internal.libraries.antlr.TokenBuffer;
import org.eclipse.persistence.internal.libraries.antlr.TokenStream;
import org.eclipse.persistence.internal.libraries.antlr.TokenStreamException;
import org.eclipse.persistence.internal.libraries.antlr.TokenStreamRecognitionException;*/

//toplink imports
import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.internal.jpa.parsing.JPQLParseTree;
import org.eclipse.persistence.internal.jpa.parsing.NodeFactory;
import org.eclipse.persistence.internal.jpa.parsing.NodeFactoryImpl;
import org.eclipse.persistence.internal.jpa.parsing.jpql.antlr.JPQLParserBuilder;

/**
 * EJBQLParser is the superclass of the ANTLR generated parser.
 */
public abstract class JPQLParser extends org.eclipse.persistence.internal.libraries.antlr.runtime.Parser {

    /** List of errors. */
    private List errors = new ArrayList();

    /** The name of the query being compiled. 
     *  The variable is null for dynamic queries. 
     */
    private String queryName = null;
    
    /** The text of the query being compiled. */
    private String queryText = null;

    /** The factory to create parse tree nodes. */
    protected NodeFactory factory;
    
    protected JPQLParser(TokenStream stream) {
        super(stream);
    }

    public JPQLParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }
    
    /**
     * INTERNAL
     * Returns the ANTLR version currently used.
     */
    public static String ANTLRVersion() throws Exception {
        return "2.7.3";
    }

    /**
     * INTERNAL
     * Builds a parser, parses the specified query string and returns the
     * parse tree. Any error in the query text results in an JPQLException.
     * This method is used for dynamic queries.
     */
    public static JPQLParseTree buildParseTree(String queryText) 
        throws JPQLException {
        return buildParseTree(null, queryText);
    }
    
    /**
     * INTERNAL
     * Builds a parser, parses the specified query string and returns the
     * parse tree. Any error in the query text results in an JPQLException.
     */
    public static JPQLParseTree buildParseTree(String queryName, String queryText) 
        throws JPQLException {
        JPQLParser parser = buildParserFor(queryName, queryText);
        return parser.parse();
    }

    /**
     * INTERNAL
     * Creates a parser for the specified query string. The query string is
     * not parsed (see method parse).      
     * This method is used for dynamic queries.
     */
    public static JPQLParser buildParserFor(String queryText) 
        throws JPQLException {
        return buildParserFor(null, queryText);
    }
        
    /**
     * INTERNAL
     * Creates a parser for the specified query string. The query string is
     * not parsed (see method parse).
     */
    public static JPQLParser buildParserFor(String queryName, String queryText) 
        throws JPQLException {
        try {
            JPQLParser parser = JPQLParserBuilder.buildParser(queryText);
            parser.setQueryName(queryName);
            parser.setQueryText(queryText);
            parser.setNodeFactory(new NodeFactoryImpl(parser.getQueryInfo()));
            return parser;
        } catch (Exception ex) {
            throw JPQLException.generalParsingException(queryText, ex);
        }
    }

    /**
     * INTERNAL
     * Parse the query string that was specified on parser creation.
     */
    public JPQLParseTree parse() 
        throws JPQLException {
        try {
            document();
        } catch (Exception e) {
            addError(e);
        }
        
        // Handle any errors generated by the Parser
        if (hasErrors()) {
            throw generateException();
        }

        // return the parser tree
        return getParseTree();
    }

    /**
     * INTERNAL
     * Returns the parse tree created by a successful run of the parse
     * method. 
     */
    public JPQLParseTree getParseTree() {
        return (JPQLParseTree)getRootNode();
    }
    
    /**
     * INTERNAL
     * Return the text of the current query being compiled.
     */
    public String getQueryText() {
        return queryText;
    }

    /**
     * INTERNAL
     * Set the text of the current query being compiled. 
     * Please note, setting the query text using this method is for error 
     * handling and debugging purposes.
     */
    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }
    
    /**
     * INTERNAL
     * Return the name of the current query being compiled. This method returns 
     * <code>null</code> if the current query is a dynamic query and not a named
     * query.
     */
    public String getQueryName() {
        return queryText;
    }

    /**
     * INTERNAL
     * Set the name of the current query being compiled. 
     * Please note, setting the query name using this method is for error 
     * handling and debugging purposes.
     */
    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    /**
     * INTERNAL
     * Return the the query text prefixed by the query name in case of a 
     * named query. The method returns just the query text in case of a dynamic
     * query.
     */
    public String getQueryInfo() {
        return (queryName == null) ? queryText :
            queryName + ": " + queryText;
    }
    
    /** 
     * INTERNAL
     * Set the factory used by the parser to create a parse tree and parse
     * tree nodes.
     */
    public void setNodeFactory(NodeFactory factory) {
        this.factory = factory;
    }

    /** 
     * INTERNAL
     * Returns the factory used by the parser to create a parse tree and parse
     * tree nodes. 
     */
    public NodeFactory getNodeFactory() {
        return factory;
    }

    /**
     * INTERNAL
     * Returns the list of errors found during the parsing process.
     */
    public List getErrors() {
        return errors;
    }

    /**
     * INTERNAL
     * Returns true if there were errors during the parsing process.
     */
    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    /**
     * INTERNAL
     * Add the exception to the list of errors.
     */
    public void addError(Exception e) {
        if (e instanceof RecognitionException) {
            e = handleRecognitionException((RecognitionException)e);
        } else if (!(e instanceof JPQLException)) {
            e = JPQLException.generalParsingException(getQueryInfo(), e);
        }
        errors.add(e);
    }

    /**
     * INTERNAL
     * Generate an exception which encapsulates all the exceptions generated
     * by this parser. Special case where the first exception is an
     * JPQLException. 
     */
    protected JPQLException generateException() {
        //Handle exceptions we expect (such as expressionSotSupported)
        Exception firstException = (Exception)getErrors().get(0);
        if (firstException instanceof JPQLException) {
            return (JPQLException)firstException;
        }

        //Handle general exceptions, such as NPE
        JPQLException exception = 
            JPQLException.generalParsingException(getQueryInfo());
        exception.setInternalExceptions(getErrors());
        return exception;
    }

    /**
     * INTERNAL
     * Map an exception thrown by the ANTLR generated code to an
     * JPQLException. 
     */
    //gf1166 Wrap ANTLRException inside JPQLException
    protected JPQLException handleRecognitionException(RecognitionException ex) {
        JPQLException result = null;
        // TODO: figure out the equivalent
       /* if (ex instanceof MismatchedCharException) {
        	MismatchedCharException mismatched = (MismatchedCharException)ex;
            if (mismatched.foundChar == EOF_CHAR) {
                result = JPQLException.unexpectedEOF(getQueryInfo(), 
                    mismatched.getLine(), mismatched.getColumn(), ex);
            } else if (mismatched.mismatchType == MismatchedCharException.CHAR) {
                result = JPQLException.expectedCharFound(getQueryInfo(), 
                    mismatched.getLine(), mismatched.getColumn(), 
                    String.valueOf((char)mismatched.expecting), 
                    String.valueOf((char)mismatched.foundChar), 
                    ex);
            }
        }
        else*/ 
        if (ex instanceof MismatchedTokenException) {
            MismatchedTokenException mismatched = (MismatchedTokenException)ex;
            Token token = mismatched.token;
            if (token != null) {
                if (token.equals(Token.EOF_TOKEN)) {
                    result = JPQLException.unexpectedEOF(getQueryInfo(), 
                        mismatched.line, mismatched.charPositionInLine, ex);
                }
                else {
                    result = JPQLException.syntaxErrorAt(getQueryInfo(),
                        mismatched.line, mismatched.charPositionInLine,
                        token.getText(), ex);
                }
            }
        }
        else if (ex instanceof NoViableAltException) {
            NoViableAltException noviable = (NoViableAltException)ex;
            Token token = noviable.token;
            if (token != null) {
                if (token.equals(Token.EOF_TOKEN)) {
                    result = JPQLException.unexpectedEOF(getQueryInfo(),
                        noviable.line, noviable.charPositionInLine, ex);
                }
                else {
                    result = JPQLException.unexpectedToken(getQueryInfo(), 
                        noviable.line, noviable.charPositionInLine, 
                        token.getText(), ex);
                }
            }
        } else if (ex instanceof InvalidIdentifierException){
        	InvalidIdentifierException invalid = (InvalidIdentifierException)ex;
            Token token = invalid.getToken();
            if (token != null) {
                if (token.equals(Token.EOF_TOKEN)) {
                    result = JPQLException.unexpectedEOF(getQueryInfo(),
                    		token.getLine(), token.getCharPositionInLine(), ex);
                }
                else {
                    result = JPQLException.unexpectedToken(getQueryInfo(), 
                    		token.getLine(), token.getCharPositionInLine(), 
                        token.getText(), ex);
                }
            }
        } else if (ex instanceof InvalidIdentifierStartException) {
        	InvalidIdentifierStartException invalid = (InvalidIdentifierStartException)ex;
            result = JPQLException.unexpectedChar(getQueryInfo(),
            		invalid.line, invalid.charPositionInLine,
                String.valueOf((char)invalid.c), ex);
        }
        /*
        else if (ex instanceof TokenStreamRecognitionException) {
            result = handleANTLRException(((TokenStreamRecognitionException)ex).recog);
        }*/
        
        
        if (result == null) {
            // no special handling from aboves matches the exception if this
            // line is reached => make it a syntax error
            result = JPQLException.syntaxError(getQueryInfo(), ex);
        }
        return result;
    }

    /**
     * Method called by the ANTLR generated code in case of an error.
     */
    public void reportError(RecognitionException ex) {
        addError(ex);
    }

    /** 
     * This is the parser start method. It will be implemented by the ANTLR
     * generated subclass.
     */
    public abstract void document() throws RecognitionException;

    /**
     * Returns the root node after representing the parse tree for the current
     * query string. It will be implemented by the ANTLR generated subclass.
     */
    public abstract Object getRootNode();

}
