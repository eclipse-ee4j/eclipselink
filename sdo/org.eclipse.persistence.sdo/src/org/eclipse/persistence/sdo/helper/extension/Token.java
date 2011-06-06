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

/**
 * A token is used for processing an XPath Expression 
 * into postfix notation via OPStack.  Each token 
 * knows its type (1-6) as well as its priority.
 */
public class Token {
    public static final int ARG = 1;
    public static final int PRE = 2;
    public static final int OPEN = 3;
    public static final int DI = 4;
    public static final int POST = 5;
    public static final int CLOSE = 6;
    public static final int OR_PR = 10;
    public static final int AND_PR = 11;
    public static final int EQ_PR = 12;
    public static final int NEQ_PR = 13;
    public static final int LEQ_PR = 14;
    public static final int LT_PR = 15;
    public static final int GEQ_PR = 16;
    public static final int GT_PR = 17;
    
    private String name;
    private int type;
    private int priority;
    public static Token OpenExp = new Token("OpenExp", Token.OPEN);
    public static Token CloseExp = new Token("CloseExp", Token.CLOSE);
    
    /**
     * This constructor sets the name and type to the input
     * values, and sets the priority to the input type.
     * 
     * @param name
     * @param type
     */
    public Token (String name, int type) {
        this.name = name;
        this.type = type;
        this.priority = type;
    }

    /**
     * This constructor sets the name, type and priority
     * to the input values.
     * 
     * @param name
     * @param type
     * @param priority
     */
    public Token (String name, int type, int priority) {
        this.name = name;
        this.type = type;
        this.priority = priority;
    }
    
    public String getName() {
        return name;
    }
    
    public int getType() {
        return type;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public String toString() {
        return name;
    }
}
