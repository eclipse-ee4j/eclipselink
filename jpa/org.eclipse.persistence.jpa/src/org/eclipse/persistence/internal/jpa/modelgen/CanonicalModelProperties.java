/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
    public enum QUALIFIER_POSITION { PRE, POST }
    public static String CANONICAL_MODEL_QUALIFIER = "eclipselink.canonical_model.qualifier";
    public static String CANONICAL_MODEL_QUALIFIER_DEFAULT = "_";
    
    public static String CANONICAL_MODEL_PERSISTENCE_XML_FILE = "eclipselink.canonical_model.persistence_xml";
    public static String CANONICAL_MODEL_PERSISTENCE_XML_FILE_DEFAULT = "META-INF/persistence.xml";
    
    public static String CANONICAL_MODEL_QUALIFIER_POSITION = "eclipselink.canonical_model.qualifier_position";
    public static String CANONICAL_MODEL_QUALIFIER_POSITION_DEFAULT = QUALIFIER_POSITION.POST.name();
    
    public static String CANONICAL_MODEL_PACKAGE_SUFFIX = "eclipselink.canonical_model.package_suffix";
    public static String CANONICAL_MODEL_PACKAGE_SUFFIX_DEFAULT = "";
    
    /**
     * INTERNAL:
     */
    public static String getOption(String option, String optionDefault, Map<String, String> options) {
        String value = options.get(option);
        return (value == null) ? optionDefault : value;
    }
}
