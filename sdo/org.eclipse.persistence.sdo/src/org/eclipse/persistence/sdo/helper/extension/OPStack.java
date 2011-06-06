/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.sdo.helper.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Utility class used for processing an XPath Expression into postfix
 * notation.  For example, consider the following expression:
 *
 *     quantity=5 and (specialOrder='false' or productName='Lawnmower')
 *
 * The operations performed to create a postfix expression are:
 *     1) add "quantity" to the list
 *     2) push "=" onto the stack
 *     3) add "5" to the list
 *     4) pop "=" off the stack, and add to the list
 *     5) push "and" onto the stack
 *     6) push "(" onto the stack
 *     7) add "specialOrder" to the list
 *     8) push "=" onto the stack
 *     9) add "'false'" to the list
 *    10) pop "=" off the stack, and add to the list
 *    11) push "or" onto the stack
 *    12) add "productName" to the list
 *    13) push "=" onto the stack
 *    14) add "'Lawnmower'" to the list
 *    15) pop "=" off the stack, and add to the list
 *    16) pop "or" off the stack, and add to the list
 *    17) push ")" onto the stack
 *    18) pop "(" ")" pair off the stack
 *    19) pop "and" off the stack, and add to the list
 *
 * The resulting postfix expression is:
 *
 *     quantity 5 = specialOrder 'false' = productName 'Lawnmower' = or and
 */
public class OPStack {

    /** Visibility reduced from [public] in 2.1.0. May 15 2007 */
    private int state;

    /** Visibility reduced from [public] in 2.1.0. May 15 2007 */
    private Stack stack;

    /** Visibility reduced from [public] in 2.1.0. May 15 2007 */

    //private String expression;

    /** Visibility reduced from [public] in 2.1.0. May 15 2007 */
    private ArrayList tokens;

    /** Visibility reduced from [public] in 2.1.0. May 15 2007 */
    private List out;

    /**
     * The default constructor initializes the stack and
     * Token list.
     */
    public OPStack() {
        stack = new Stack();
        tokens = new ArrayList();
    }

    /**
     * Removes a '(' ')' pair from the stack.
     */
    private void removeOCpair() {
        stack.pop();
        stack.pop();
    }

    /**
     * Removes lower priority tokens from the stack (adding them to the
     * List that will be returned), then push the new token onto the
     * stack.
     *
     * @param out
     * @param t
     */
    private void pushSpew(List outList, Token t) {
        while (!stack.empty() && (((Token)stack.peek()).getPriority() >= t.getPriority())) {
            outList.add(stack.pop());
        }
        stack.push(t);
    }

    /**
     * Returns the next Token in the list.
     *
     * @return
     */
    private Token getNextToken() {
        if (tokens.size() > 0) {
            Token tok = (Token)tokens.get(0);
            tokens.remove(0);
            return tok;
        }
        return null;
    }

    /**
     * Parse an XPath Expression into Token objects.
     *
     * @param exp
     */
    private void parseExpression(String exp) {
        StringBuffer sbuf = new StringBuffer();
        char ch;
        Token tok;
        for (int i = 0; i < exp.length(); i++) {
            ch = exp.charAt(i);
            // 'and' and 'or' should be preceded by whitespace
            if (ch == ' ') {
                if (((i + 2) < exp.length()) && ((exp.charAt(i + 1) == 'o') && (exp.charAt(i + 2) == 'r'))) {
                    tok = new Token(sbuf.toString(), Token.ARG);
                    tokens.add(tok);
                    sbuf = new StringBuffer();
                    sbuf.append(exp.charAt(++i));
                    sbuf.append(exp.charAt(++i));
                    tok = new Token(sbuf.toString(), Token.DI, Token.OR_PR);
                    tokens.add(tok);
                    sbuf = new StringBuffer();
                } else if (((i + 3) < exp.length()) && ((exp.charAt(i + 1) == 'a') && (exp.charAt(i + 2) == 'n') && (exp.charAt(i + 3) == 'd'))) {
                    tok = new Token(sbuf.toString(), Token.ARG);
                    tokens.add(tok);
                    sbuf = new StringBuffer();
                    sbuf.append(exp.charAt(++i));
                    sbuf.append(exp.charAt(++i));
                    sbuf.append(exp.charAt(++i));
                    tok = new Token(sbuf.toString(), Token.DI, Token.AND_PR);
                    tokens.add(tok);
                    sbuf = new StringBuffer();
                }
            } else if (ch == '=') {
                tok = new Token(sbuf.toString(), Token.ARG);
                tokens.add(tok);
                tok = new Token(Character.toString(ch), Token.DI, Token.EQ_PR);
                tokens.add(tok);
                sbuf = new StringBuffer();
            } else if ((ch == '!') && (((i + 1) < exp.length()) && (exp.charAt(i + 1) == '='))) {
                tok = new Token(sbuf.toString(), Token.ARG);
                tokens.add(tok);
                sbuf = new StringBuffer();
                sbuf.append(Character.toString(ch));
                sbuf.append(exp.charAt(++i));
                tok = new Token(sbuf.toString().trim(), Token.DI, Token.NEQ_PR);
                tokens.add(tok);
                sbuf = new StringBuffer();
            } else if (ch == '<') {
                tok = new Token(sbuf.toString(), Token.ARG);
                tokens.add(tok);
                sbuf = new StringBuffer();
                sbuf.append(Character.toString(ch));
                if (((i + 1) < exp.length()) && (exp.charAt(i + 1) == '=')) {
                    sbuf.append(exp.charAt(++i));
                    tok = new Token(sbuf.toString().trim(), Token.DI, Token.LEQ_PR);
                } else {
                    tok = new Token(sbuf.toString().trim(), Token.DI, Token.LT_PR);
                }
                tokens.add(tok);
                sbuf = new StringBuffer();
            } else if (ch == '>') {
                tok = new Token(sbuf.toString(), Token.ARG);
                tokens.add(tok);
                sbuf = new StringBuffer();
                sbuf.append(Character.toString(ch));
                if (((i + 1) < exp.length()) && (exp.charAt(i + 1) == '=')) {
                    sbuf.append(exp.charAt(++i));
                    tok = new Token(sbuf.toString().trim(), Token.DI, Token.GEQ_PR);
                } else {
                    tok = new Token(sbuf.toString().trim(), Token.DI, Token.GT_PR);
                }
                tokens.add(tok);
                sbuf = new StringBuffer();
            } else if (ch == '(') {
                if (sbuf.length() > 0) {
                    tok = new Token(sbuf.toString(), Token.ARG);
                    tokens.add(tok);
                    sbuf = new StringBuffer();
                }
                tok = new Token(Character.toString(ch), Token.OPEN);
                tokens.add(tok);
            } else if (ch == ')') {
                if (sbuf.length() > 0) {
                    tok = new Token(sbuf.toString(), Token.ARG);
                    tokens.add(tok);
                    sbuf = new StringBuffer();
                }
                tok = new Token(Character.toString(ch), Token.CLOSE);
                tokens.add(tok);
            } else {
                if (ch != ' ') {
                    sbuf.append(ch);
                }
            }
        }
        if (sbuf.length() > 0) {
            tok = new Token(sbuf.toString(), Token.ARG);
            tokens.add(tok);
            sbuf = new StringBuffer();
        }
    }

    /**
     * Process an XPath Expression into postfix notation.
     *
     * @param exp
     * @return
     */
    public List processExpression(String exp) {
        parseExpression(exp);
        out = new ArrayList();
        state = 0;
        stack.push(Token.OpenExp);// start off with an open of the expression
        Token t;
        while ((t = getNextToken()) != null) {
            switch (t.getType()) {
            case Token.ARG:
                if (state != 0) {
                    out.clear();
                    return out;
                }
                out.add(t);
                state = 1;
                break;
            case Token.PRE:
                if (state != 0) {
                    out.clear();
                    return out;
                }
                stack.push(t);
                break;
            case Token.OPEN:
                stack.push(t);
                break;
            case Token.DI:
                if (state != 1) {
                    out.clear();
                    return out;
                }
                pushSpew(out, t);
                state = 0;
                break;
            case Token.POST:
                if (state != 1) {
                    out.clear();
                    return out;
                }
                pushSpew(out, t);
                out.add(stack.pop());
                break;
            case Token.CLOSE:
                pushSpew(out, t);
                removeOCpair();
            }
        }
        pushSpew(out, Token.CloseExp);
        removeOCpair();
        if (state != 1) {
            out.clear();
        }
        return out;
    }
}
