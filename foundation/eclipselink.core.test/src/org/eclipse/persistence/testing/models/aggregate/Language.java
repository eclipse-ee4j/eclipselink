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
package org.eclipse.persistence.testing.models.aggregate;

import java.io.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class Language implements Serializable {
    public Number id;
    public String language;

    public static Language example1() {
        Language example = new Language();

        example.setLanguage("C");
        return example;
    }

    public static Language example2() {
        Language example = new Language();

        example.setLanguage("C++");
        return example;
    }

    public static Language example3() {
        Language example = new Language();

        example.setLanguage("Smalltalk");
        return example;
    }

    public static Language example4() {
        Language example = new Language();

        example.setLanguage("Java");
        return example;
    }

    public static Language example5() {
        Language example = new Language();

        example.setLanguage("LISP");
        return example;
    }

    public static Language example6() {
        Language example = new Language();

        example.setLanguage("FORTRAN");
        return example;
    }

    public static Language example7() {
        Language example = new Language();

        example.setLanguage("BASIC");
        return example;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String aString) {
        language = aString;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("AGG_LAN");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("LANG", String.class, 30);

        return definition;
    }

    public String toString() {
        return String.valueOf(getLanguage());
    }
}
