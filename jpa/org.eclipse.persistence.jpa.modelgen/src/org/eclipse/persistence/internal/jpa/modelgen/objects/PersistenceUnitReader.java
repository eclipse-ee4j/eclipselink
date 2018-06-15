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
//     11/20/2009-2.0 Guy Pelletier/Mitesh Meswani
//       - 295376: Improve usability of MetaModel generator
//     04/29/2010-2.0.3 Guy Pelletier
//       - 311020: Canonical model generator should not throw an exception when no persistence.xml is found
//     06/01/2010-2.1 Guy Pelletier
//       - 315195: Add new property to avoid reading XML during the canonical model generation
//     03/06/2013-2.5 Guy Pelletier
//       - 267391: JPA 2.1 Functionality for Java EE 7 (JSR-338)
package org.eclipse.persistence.internal.jpa.modelgen.objects;

import static org.eclipse.persistence.config.PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML;
import static org.eclipse.persistence.config.PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML_DEFAULT;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_LOAD_XML;
import static org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties.CANONICAL_MODEL_LOAD_XML_DEFAULT;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.StringTokenizer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProperties;
import org.eclipse.persistence.internal.jpa.modelgen.MetadataMirrorFactory;
import org.eclipse.persistence.logging.LogCategory;
import org.eclipse.persistence.logging.LogLevel;

/**
 * Used to read persistence units through the java annotation processing API.
 *
 * @author Guy Pelletier, Peter Krogh
 * @since EclipseLink 1.2
 */
public class PersistenceUnitReader {

    /** Current logger. */
    protected final MetadataLogger logger;
    /** Annotation model processing environment. */
    protected final ProcessingEnvironment processingEnv;

    /**
     * INTERNAL:
     */
    public PersistenceUnitReader(final MetadataLogger logger, final ProcessingEnvironment processingEnv) throws IOException {
        this.logger = logger;
        this.processingEnv = processingEnv;
    }

    /**
     * INTERAL:
     * Close the given input stream.
     */
    protected void closeInputStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException exception) {
                throw ValidationException.fileError(exception);
            }
        }
    }

    /**
     * INTERNAL:
     */
    protected FileObject getFileObject(String filename, ProcessingEnvironment processingEnv) throws IOException {
        return processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", filename);
    }

    /**
     * INTERNAL:
     * Return an input stream for the given filename.
     */
    protected InputStream getInputStream(String filename, boolean loadingPersistenceXML) {
        InputStream inputStream = null;

        try {
            FileObject fileObject = getFileObject(filename, processingEnv);
            inputStream = fileObject.openInputStream();
        } catch (Exception ioe) {
            // If we can't find the persistence.xml from the class output
            // we'll try from the current directory using regular IO.
            try {
                inputStream = new FileInputStream(filename);
            } catch (IOException e) {
                if (loadingPersistenceXML) {
                    // If loading the persistence.xml, log a BIG warning message.
                    if (logger.shouldLog(LogLevel.WARNING, LogCategory.PROCESSOR)) {
                        processingEnv.getMessager().printMessage(Kind.NOTE, "The persistence xml file [" + filename
                                + "] was not found. NO GENERATION will occur!! Please ensure a persistence xml file is available either from the CLASS_OUTPUT directory [META-INF/persistence.xml] or using the eclipselink.persistencexml property to specify its location.");
                    }
                } else {
                    // For any other mapping file log a message.
                    if (logger.shouldLog(LogLevel.INFO, LogCategory.PROCESSOR)) {
                        processingEnv.getMessager().printMessage(Kind.NOTE, "Optional file was not found: " + filename + " continuing with generation.");
                    }
                }
            }
        }

        return inputStream;
    }

    /**
     * INTERNAL:
     * This method will look for an process the -A eclipselink.persistenceunits
     * option. This list is treated as an include/filter list and if it is not
     * specified all persistence units are processed.
     */
    protected HashSet<String> getPersistenceUnitList(ProcessingEnvironment processingEnv ) {
        String persistenceUnits = processingEnv.getOptions().get(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_UNITS);
        HashSet<String> persistenceUnitList = null;

        if (persistenceUnits != null) {
            persistenceUnitList = new HashSet<String>();
            StringTokenizer st = new StringTokenizer(persistenceUnits, ",");

            while (st.hasMoreTokens()) {
                persistenceUnitList.add(st.nextToken().trim());
            }
        }

        return persistenceUnitList;
    }

    /**
     * INTERNAL:
     */
    public void initPersistenceUnits(final MetadataMirrorFactory factory) {
        // As a performance enhancement to avoid reloading and merging XML metadata for every compile round,
        // the user may choose to turn off the XML loading by setting the load XML flag to false.
        if (Boolean.valueOf(CanonicalModelProperties.getOption(CANONICAL_MODEL_LOAD_XML, CANONICAL_MODEL_LOAD_XML_DEFAULT, processingEnv.getOptions()))) {
            final String filename = CanonicalModelProperties.getOption(ECLIPSELINK_PERSISTENCE_XML, ECLIPSELINK_PERSISTENCE_XML_DEFAULT, processingEnv.getOptions());
            HashSet<String> persistenceUnitList = getPersistenceUnitList(processingEnv);

            InputStream inStream1 = null;
            InputStream inStream2 = null;

            try {
                inStream1 = getInputStream(filename, true);

                // If the persistence.xml was not found, then there is nothing
                // to do.
                if (inStream1 != null) {
                    PersistenceXML persistenceXML;

                    try {
                        // Try a 2.1 context first.
                        persistenceXML = (PersistenceXML) PersistenceXMLMappings.createXML2_1Context().createUnmarshaller().unmarshal(inStream1);
                    } catch (Exception e) {
                        // Catch all exceptions and try a 2.0 context second
                        // with a new input stream.
                        inStream2 = getInputStream(filename, true);
                        persistenceXML = (PersistenceXML) PersistenceXMLMappings.createXML2_0Context().createUnmarshaller().unmarshal(inStream2);
                    }

                    for (SEPersistenceUnitInfo puInfo : persistenceXML.getPersistenceUnitInfos()) {
                        // If no persistence unit list has been specified or one
                        // has been specified and this persistence unit info's
                        // name
                        // appears in that list then add it.
                        if (persistenceUnitList == null || persistenceUnitList.contains(puInfo.getPersistenceUnitName())) {
                            factory.addPersistenceUnit(puInfo, new PersistenceUnit(puInfo, factory, this));
                        }
                    }
                }
            } finally {
                closeInputStream(inStream1);
                closeInputStream(inStream2);
            }
        }
    }

}

