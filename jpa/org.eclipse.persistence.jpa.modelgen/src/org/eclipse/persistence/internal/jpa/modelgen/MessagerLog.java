/*
 * Copyright (c) 2019, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.jpa.modelgen;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.localization.LoggingLocalization;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

/**
 * This is a wrapper class for javax.annotation.processing.Messager.
 * It is used when messages need to be logged through annotation processor.
 */
final class MessagerLog extends AbstractSessionLog {

    /**
     * Represents the Map that stores log levels per the name space strings. The
     * keys are category names. The values are log levels.
     */
    private final Map<String, Integer> categoryLogLevelMap;
    private final Messager out;
    private static final Writer NULL_WRITER = new Writer() {
            //no-op
            @Override public void write(char[] cbuf, int off, int len) throws IOException {}
            @Override public void flush() throws IOException {}
            @Override public void close() throws IOException {}
        };


    public MessagerLog(Messager out, Map<String, String> options) {
        super();
        this.out = out;
        writer = NULL_WRITER;
        categoryLogLevelMap = new HashMap<>(loggerCatagories.length);
        for (String loggerCategory : loggerCatagories) {
            categoryLogLevelMap.put(loggerCategory, null);
        }
        initOrUpdateLevels(options);
    }

    @Override
    public void setLevel(int level, String category) {
        if (category == null) {
            this.level = level;
        } else if (categoryLogLevelMap.containsKey(category)) {
            categoryLogLevelMap.put(category, level);
        } else {
            log(SessionLog.WARNING, SessionLog.PROCESSOR,
                    "Unrecognized logger category: {0}",
                    category, false);
        }
    }

    @Override
    public int getLevel(String category) {
        if (category != null) {
            Integer lvl = categoryLogLevelMap.get(category);
            if (lvl != null) {
                return lvl;
            }
        }
        return super.getLevel(category);
    }

    @Override
    public boolean shouldPrintThread() {
        if (shouldPrintThread == null) {
            return getLevel() < FINER;
        }
        return shouldPrintThread;
    }

    @Override
    public boolean shouldPrintDate() {
        if (shouldPrintDate == null) {
            return getLevel() < FINER;
        }
        return shouldPrintDate;
    }

    @Override
    public boolean shouldPrintConnection() {
        if (shouldPrintConnection == null) {
            return getLevel() < FINER;
        }
        return shouldPrintConnection;
    }

    @Override
    public boolean shouldLog(int level, String category) {
        return (getLevel(category) <= level);
    }

    @Override
    public void log(SessionLogEntry entry) {
        if (!shouldLog(entry.getLevel(), entry.getNameSpace())) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(getPrefixString(entry.getLevel(), entry.getNameSpace()));
        sb.append(getSupplementDetailString(entry));

        if (entry.hasMessage()) {
            sb.append(formatMessage(entry));
            sb.append(Helper.cr());
        }

        if (entry.hasException()) {
            if (shouldLogExceptionStackTrace()) {
                for (StackTraceElement stackTrace : entry.getException().getStackTrace()) {
                    sb.append(stackTrace);
                    sb.append(Helper.cr());
                }
            } else {
                sb.append(entry.getException().toString());
            }
            sb.append(Helper.cr());
        }

        if (getWriter() == NULL_WRITER) {
            out.printMessage(translateLevelToKind(entry.getLevel()), sb);
        } else {
            try {
                getWriter().write(sb.toString());
                getWriter().flush();
            } catch (IOException ioe) {
                throw ValidationException.logIOError(ioe);
            }
        }
    }

    private void initOrUpdateLevels(Map<String, String> settings) {
        String logLevelString = settings.get(PersistenceUnitProperties.LOGGING_LEVEL);
        if (logLevelString != null) {
            setLevel(translateStringToLoggingLevel(logLevelString));
        }
        // category-specific logging level
        for (Map.Entry<String, String> entry: settings.entrySet()) {
            if (entry.getKey().startsWith(PersistenceUnitProperties.CATEGORY_LOGGING_LEVEL_)) {
                String ctg = entry.getKey().substring(PersistenceUnitProperties.CATEGORY_LOGGING_LEVEL_.length());
                setLevel(translateStringToLoggingLevel(entry.getValue()), ctg);
            }
        }

        String tsString = settings.get(PersistenceUnitProperties.LOGGING_TIMESTAMP);
        if (tsString != null) {
            setShouldPrintDate(Boolean.parseBoolean(tsString));
        }
        String threadString = settings.get(PersistenceUnitProperties.LOGGING_THREAD);
        if (threadString != null) {
            setShouldPrintThread(Boolean.parseBoolean(threadString));
        }
        String sessionString = settings.get(PersistenceUnitProperties.LOGGING_SESSION);
        if (sessionString != null) {
            setShouldPrintSession(Boolean.parseBoolean(sessionString));
        }
        String connectionString = settings.get(PersistenceUnitProperties.LOGGING_CONNECTION);
        if (connectionString != null) {
            setShouldPrintConnection(Boolean.parseBoolean(connectionString));
        }
        String exString = settings.get(PersistenceUnitProperties.LOGGING_EXCEPTIONS);
        if (exString != null) {
            setShouldLogExceptionStackTrace(Boolean.parseBoolean(exString));
        }
        String displayData = settings.get(PersistenceUnitProperties.LOGGING_PARAMETERS);
        if (displayData != null) {
            setShouldDisplayData(Boolean.parseBoolean(displayData));
        }
        // Set logging file.
        String loggingFileString = settings.get(PersistenceUnitProperties.LOGGING_FILE);
        if (loggingFileString != null) {
            if (!loggingFileString.trim().equals("")) {
                try {
                    FileOutputStream fos = new FileOutputStream(loggingFileString);
                    setWriter(fos);
                } catch (IOException e) {
                    throw ValidationException.invalidLoggingFile(loggingFileString, e);
                }
            } else {
                throw ValidationException.invalidLoggingFile();
            }
        }
    }

    private CharSequence getPrefixString(int level, String category) {
        StringBuilder sb = new StringBuilder();
        switch (level) {
            case SEVERE:
                if (SEVERE_PREFIX == null) {
                    SEVERE_PREFIX = LoggingLocalization.buildMessage("toplink_severe");
                }
                sb.append(SEVERE_PREFIX);
                break;
            case WARNING:
                if (WARNING_PREFIX == null) {
                    WARNING_PREFIX = LoggingLocalization.buildMessage("toplink_warning");
                }
                sb.append(WARNING_PREFIX);
                break;
            case INFO:
                if (INFO_PREFIX == null) {
                    INFO_PREFIX = LoggingLocalization.buildMessage("toplink_info");
                }
                sb.append(INFO_PREFIX);
                break;
            case CONFIG:
                if (CONFIG_PREFIX == null) {
                    CONFIG_PREFIX = LoggingLocalization.buildMessage("toplink_config");
                }
                sb.append(CONFIG_PREFIX);
                break;
            case FINE:
                if (FINE_PREFIX == null) {
                    FINE_PREFIX = LoggingLocalization.buildMessage("toplink_fine");
                }
                sb.append(FINE_PREFIX);
                break;
            case FINER:
                if (FINER_PREFIX == null) {
                    FINER_PREFIX = LoggingLocalization.buildMessage("toplink_finer");
                }
                sb.append(FINER_PREFIX);
                break;
            case FINEST:
                if (FINEST_PREFIX == null) {
                    FINEST_PREFIX = LoggingLocalization.buildMessage("toplink_finest");
                }
                sb.append(FINEST_PREFIX);
                break;
            default:
                if (TOPLINK_PREFIX == null) {
                    TOPLINK_PREFIX = LoggingLocalization.buildMessage("toplink");
                }
                sb.append(TOPLINK_PREFIX);
        }
        if (category != null) {
            sb.append(category);
            sb.append(": ");
        }
        return sb;
    }

    private Diagnostic.Kind translateLevelToKind(int level) {
        switch (level) {
            case SEVERE:
            case WARNING:
                return Diagnostic.Kind.WARNING;
            case INFO:
                return Diagnostic.Kind.NOTE;
            default:
                return Diagnostic.Kind.OTHER;
        }
    }
}
