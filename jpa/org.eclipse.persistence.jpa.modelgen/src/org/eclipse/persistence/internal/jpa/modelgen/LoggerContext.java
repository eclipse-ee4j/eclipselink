/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/22/2018-3.0 Tomas Kraus
//       - 433205: Canonical Model Generator is too verbose, no way to disable output
package org.eclipse.persistence.internal.jpa.modelgen;

import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_GLOBAL_LOG_LEVEL;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_PROCESSOR_LOG_LEVEL;

import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.modelgen.objects.PersistenceUnit;
import org.eclipse.persistence.logging.LogCategory;
import org.eclipse.persistence.logging.LogLevel;

/**
 * Logger options context.
 */
class LoggerContext {

    /**
     * Builds logger options context from options {@code Map}.
     *
     * @param options options {@code Map} with logging level options
     * @return logger options context with values based on options {@code Map}
     */
    static LoggerContext buildContext(final Map<String,String> options) {
        final String glob = options.get(CANONICAL_MODEL_GLOBAL_LOG_LEVEL);
        final String proc = options.get(CANONICAL_MODEL_PROCESSOR_LOG_LEVEL);
        return new LoggerContext(
                glob != null, LogLevel.toValue(glob, LogLevel.INFO),
                proc != null, LogLevel.toValue(proc, LogLevel.INFO));
    }

    /**
     * Update provided {@link MetadataLogger} log level settings from PU property.
     * Logger is updated only when {@code PROCESSOR} logging category level
     * is set properly in corresponding PU property.
     *
     * @param logger logger to be updated
     * @param persistenceUnit persistence unit with properties
     */
    static void updateMetadataLogger(final MetadataLogger logger, final PersistenceUnit persistenceUnit) {
        String levelStr = persistenceUnit.getPersistenceUnitProperty(LogCategory.PROCESSOR.getLogLevelProperty());
        if (levelStr == null) {
            levelStr = persistenceUnit.getPersistenceUnitProperty(CANONICAL_MODEL_GLOBAL_LOG_LEVEL);
        }
        if (levelStr != null) {
            final LogLevel level = LogLevel.toValue(levelStr);
            if (level != null) {
                logger.getSession().getSessionLog().setLevel(level.getId(), LogCategory.PROCESSOR.getName());
            }
        }
    }

    /** Holds an information whether global logging level option is set. */
    private final boolean isGlobal;

    /** Global logging level determined from options. Value from option or default value. */
    private final LogLevel global;

    /** Holds an information whether processor logging level option is set. */
    private final boolean isProcessor;

    /** Processor logging level determined from options. Value from option or default value. */
    private final LogLevel processor;

    /**
     * Creates an instance of logger options context.
     *
     * @param isGlobal whether global logging level option is set
     * @param global logging level determined from options
     * @param isProcessor whether processor logging level option is set
     * @param processor logging level determined from options
     */
    private LoggerContext(final boolean isGlobal, final LogLevel global, final boolean isProcessor, final LogLevel processor) {
        this.isGlobal = isGlobal;
        this.global = global;
        this.isProcessor = isProcessor;
        this.processor = processor;
    }

    /**
     * Get an information whether global logging level option is set.
     *
     * @return value of {@code true} when global logging level option is set or {@code true} otherwise.
     */
    boolean isGlobal() {
        return isGlobal;
    }

    /**
     * Get global logging level.
     *
     * @return global logging level determined from options
     */
    LogLevel getGlobal() {
        return global;
    }

    /**
     * Get an information whether processor logging level option is set.
     *
     * @return value of {@code true} when processor logging level option is set or {@code true} otherwise.
     */
    boolean isProcessor() {
        return isProcessor;
    }

    /**
     * Get processor logging level.
     *
     * @return processor logging level determined from options
     */
    LogLevel getProcessor() {
        return processor;
    }

    /**
     * Get an information whether any logging level option is set (at least one from global and processor).
     *
     * @return value of {@code true} when at least one logging level option is set or {@code true} otherwise.
     */
    boolean isAny() {
        return isGlobal || isProcessor;
    }

    /**
     * Update {@link MetadataLogger} instance using values from this logger options context.
     *
     * @param logger {@link MetadataLogger} instance to update
     */
    void updateMetadataLogger(final MetadataLogger logger) {
        if (isProcessor || !isGlobal) {
            logger.getSession().getSessionLog().setLevel(processor.getId(), LogCategory.PROCESSOR.getName());
        } else {
            logger.getSession().getSessionLog().setLevel(global.getId(), LogCategory.PROCESSOR.getName());
        }
        logger.getSession().getSessionLog().setLevel(global.getId());
    }

}
