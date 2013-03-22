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
package org.eclipse.persistence.internal.codegen;


/**
 * INTERNAL:
 * <p><b>Purpose</b>: Model an access level, i.e. private/protected/final/static/etc.
 *
 * @since TopLink 3.0
 * @author James Sutherland
 */
public class AccessLevel {
    protected int level;
    public static int PUBLIC = 1;
    public static int PROTECTED = 2;
    public static int PACKAGE = 3;
    public static int PRIVATE = 4;
    protected boolean isAbstract;
    protected boolean isFinal;
    protected boolean isNative;
    protected boolean isStatic;
    protected boolean isSynchronized;
    protected boolean isTransient;
    protected boolean isVolatile;

    public AccessLevel() {
        this.level = PUBLIC;
        this.isStatic = false;
        this.isFinal = false;
        this.isTransient = false;
    }

    public AccessLevel(int level) {
        this.level = level;
        this.isStatic = false;
        this.isFinal = false;
        this.isTransient = false;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof AccessLevel)) {
            return false;
        }

        AccessLevel accessLevel = (AccessLevel)object;
        return ((this.level == accessLevel.level) && (this.isStatic == accessLevel.isStatic) && (this.isFinal == accessLevel.isFinal) && (this.isTransient == accessLevel.isTransient));
    }

    public int getLevel() {
        return level;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public boolean isNative() {
        return isNative;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isSynchronized() {
        return isSynchronized;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public boolean isVolatile() {
        return isVolatile;
    }

    public void setIsAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public void setIsFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public void setIsNative(boolean isNative) {
        this.isNative = isNative;
    }

    public void setIsStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public void setIsSynchronized(boolean isSynchronized) {
        this.isSynchronized = isSynchronized;
    }

    public void setIsTransient(boolean isTransient) {
        this.isTransient = isTransient;
    }

    public void setIsVolatile(boolean isVolatile) {
        this.isVolatile = isVolatile;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void write(CodeGenerator generator) {
        boolean needsSpace = true;
        if (getLevel() == PUBLIC) {
            generator.write("public");
        } else if (getLevel() == PROTECTED) {
            generator.write("protected");
        } else if (getLevel() == PACKAGE) {
            // Nothing required/default.
            needsSpace = false;
        } else if (getLevel() == PRIVATE) {
            generator.write("private");
        }

        if (isAbstract()) {
            if (needsSpace) {
                generator.write(" ");
            }
            generator.write("abstract");
            needsSpace = true;
        }
        if (isStatic()) {
            if (needsSpace) {
                generator.write(" ");
            }
            generator.write("static");
            needsSpace = true;
        }
        if (isFinal()) {
            if (needsSpace) {
                generator.write(" ");
            }
            generator.write("final");
            needsSpace = true;
        }
        if (isTransient()) {
            if (needsSpace) {
                generator.write(" ");
            }
            generator.write("transient");
            needsSpace = true;
        }
        if (isVolatile()) {
            if (needsSpace) {
                generator.write(" ");
            }
            generator.write("volatile");
            needsSpace = true;
        }
        if (isNative()) {
            if (needsSpace) {
                generator.write(" ");
            }
            generator.write("native");
            needsSpace = true;
        }
        if (isSynchronized()) {
            if (needsSpace) {
                generator.write(" ");
            }
            generator.write("synchronized");
            needsSpace = true;
        }
    }
}
