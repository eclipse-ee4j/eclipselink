/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     08/10/2009-2.0 Guy Pelletier 
 *       - 267391: JPA 2.0 implement/extend/use an APT tooling library for MetaModel API canonical classes
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.modelgen;

import java.util.Map;

/**
 * The main APT processor to generate the JPA 2.0 Canonical model. 
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.2
 */
public abstract class CanonicalModelProperties {
    /**
     * This optional property specifies the prefix that will be added to the 
     * start of the class name of any canonical model class generated. 
     * By default the prefix is not used.
     */    
    public static final String CANONICAL_MODEL_PREFIX = "eclipselink.canonicalmodel.prefix";
    public static String CANONICAL_MODEL_PREFIX_DEFAULT = "";
    
    /**
     * This optional property specifies the suffix that will be added to the 
     * end of the class name of any canonical model class generated. By default 
     * the suffix value is "_". If this property is specified the value must be 
     * a non-empty string that contains valid characters for use in a Java class 
     * name.
     */
    public static final String CANONICAL_MODEL_SUFFIX = "eclipselink.canonicalmodel.suffix";
    public static String CANONICAL_MODEL_SUFFIX_DEFAULT = "_";
    
    /**
     * This optional property specifies a sub-package name that can be used to 
     * have the canonical model generator generate its classes in a sub-package 
     * of the package where the corresponding entity class is located. By 
     * default the canonical model classes are generated into the same package 
     * as the entity classes. 
     */
    public static final String CANONICAL_MODEL_SUB_PACKAGE = "eclipselink.canonicalmodel.subpackage";
    public static String CANONICAL_MODEL_SUB_PACKAGE_DEFAULT = "";
    
    /**
     * INTERNAL:
     */
    public static String getOption(String option, String optionDefault, Map<String, String> options) {
        String value = options.get(option);
        return (value == null) ? optionDefault : value;
    }
}
