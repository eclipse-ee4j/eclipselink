/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     08/10/2009-2.0 Guy Pelletier
//       - 267391: JPA 2.0 implement/extend/use an APT tooling library for MetaModel API canonical classes
//     06/01/2010-2.1 Guy Pelletier
//       - 315195: Add new property to avoid reading XML during the canonical model generation
//     10/18/2010-2.2 Guy Pelletier
//       - 322921: OutOfMemory in annotation processor
package org.eclipse.persistence.internal.jpa.modelgen;

import java.util.Map;

import org.eclipse.persistence.config.PersistenceUnitProperties;

/**
 * Properties supported by the APT processor to generate the JPA 2.0 Canonical model.
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
    public static final String CANONICAL_MODEL_PREFIX = PersistenceUnitProperties.CANONICAL_MODEL_PREFIX;
    public static String CANONICAL_MODEL_PREFIX_DEFAULT = PersistenceUnitProperties.CANONICAL_MODEL_PREFIX_DEFAULT;

    /**
     * This optional property specifies the suffix that will be added to the
     * end of the class name of any canonical model class generated. The suffix
     * defaults to "_" unless a prefix is specified. If this property is
     * specified, the value must be a non-empty string that contains valid
     * characters for use in a Java class name.
     */
    public static final String CANONICAL_MODEL_SUFFIX = PersistenceUnitProperties.CANONICAL_MODEL_SUFFIX;
    public static String CANONICAL_MODEL_SUFFIX_DEFAULT = PersistenceUnitProperties.CANONICAL_MODEL_SUFFIX_DEFAULT;

    /**
     * This optional property specifies a sub-package name that can be used to
     * have the canonical model generator generate its classes in a sub-package
     * of the package where the corresponding entity class is located. By
     * default the canonical model classes are generated into the same package
     * as the entity classes.
     */
    public static final String CANONICAL_MODEL_SUB_PACKAGE = PersistenceUnitProperties.CANONICAL_MODEL_SUB_PACKAGE;
    public static String CANONICAL_MODEL_SUB_PACKAGE_DEFAULT = PersistenceUnitProperties.CANONICAL_MODEL_SUB_PACKAGE_DEFAULT;

    /**
     * This optional property can be used a performance enhancement between
     * compile rounds. It is used to avoid reloading XML metadata on each
     * compile which may only contain a single class etc. The default value
     * is true and should be left as such for the initial generation to capture
     * the XML metadata. Afterwards users may choose to set this flag if no
     * changes to XML are expected thereafter.
     */
    public static final String CANONICAL_MODEL_LOAD_XML = PersistenceUnitProperties.CANONICAL_MODEL_LOAD_XML;
    public static final String CANONICAL_MODEL_LOAD_XML_DEFAULT = PersistenceUnitProperties.CANONICAL_MODEL_LOAD_XML_DEFAULT;

    /**
     * This optional property can be used a performance enhancement between
     * compile rounds within an IDE. It is used to avoid using a static metadata
     * factory between 'cache' metadata from incremental builds. Turning this
     * off in some use cases (IDE) could result in a loss of functionality.
     * The default value is true and should be left as such for full feature
     * support.
     */
    public static final String CANONICAL_MODEL_USE_STATIC_FACTORY = PersistenceUnitProperties.CANONICAL_MODEL_USE_STATIC_FACTORY;
    public static final String CANONICAL_MODEL_USE_STATIC_FACTORY_DEFAULT = PersistenceUnitProperties.CANONICAL_MODEL_USE_STATIC_FACTORY_DEFAULT;

    /**
     * This optional property can be used to avoid using of date in
     * {@link javax.annotation.Generated} annotation
     * The default value is true and should be left as such for full feature
     * support.
     */
    public static final String CANONICAL_MODEL_GENERATE_TIMESTAMP = PersistenceUnitProperties.CANONICAL_MODEL_GENERATE_TIMESTAMP;
    public static final String CANONICAL_MODEL_GENERATE_TIMESTAMP_DEFAULT = PersistenceUnitProperties.CANONICAL_MODEL_GENERATE_TIMESTAMP_DEFAULT;

    // This value must match LogCategory.PROCESSOR.getLogLevelProperty()
    /**
     * This optional property can be used to set processor logging level of Canonical model generator.
     */
    public static final String CANONICAL_MODEL_PROCESSOR_LOG_LEVEL = PersistenceUnitProperties.CATEGORY_LOGGING_LEVEL_ + "processor";

    /**
     * This optional property can be used to set global logging level of Canonical model generator.
     */
    public static final String CANONICAL_MODEL_GLOBAL_LOG_LEVEL = PersistenceUnitProperties.LOGGING_LEVEL;

    /**
     * INTERNAL:
     */
    public static String getOption(String option, String optionDefault, Map<String, String> options) {
        String value = options.get(option);
        return (value == null) ? optionDefault : value;
    }
}
