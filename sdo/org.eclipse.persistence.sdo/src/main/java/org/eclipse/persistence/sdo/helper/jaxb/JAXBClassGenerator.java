/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     bdoughan - Mar 19/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.sdo.helper.jaxb;

import java.io.FileReader;
import java.io.IOException;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.sdo.helper.DefaultSchemaResolver;
import org.eclipse.persistence.sdo.helper.FileCodeWriter;
import org.eclipse.persistence.sdo.helper.SDOClassGenerator;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;

/**
 * A wrapper for the SDOClassGenerator that understands the JAXB compiler options
 * and translates them to the SDO equivalents.  It will also ignore options that
 * are relevant to JAXB but not SDO.
 */
public class JAXBClassGenerator {

    public static void main(String[] args) {
        if(args.length == 0) {
            return;
        }

        AbstractSessionLog.getLog().setLevel(AbstractSessionLog.FINER);
        SDOClassGenerator generator = new SDOClassGenerator(new SDOHelperContext());
        generator.setImplGenerator(false);

        String sourceDir = null;
        String sourceFile = null;

        int argsLength = args.length;
        for(int i=0; i<argsLength; i++) {
            if("-b".equals(args[i])) {
                argsLength = i;
                break;
            }
        }

        for(int i=0; i<argsLength; i++) {
            if(args[i].equals("-d")) {
                sourceDir = args[++i];
            } else if(i == argsLength - 1) {
                sourceFile = args[i];
            }
        }

        try {
            FileReader reader = new FileReader(sourceFile);
            FileCodeWriter fileCodeWriter = new FileCodeWriter();
            fileCodeWriter.setSourceDir(sourceDir);
            generator.generate(reader, fileCodeWriter, new DefaultSchemaResolver());
        } catch (IOException e) {
            AbstractSessionLog.getLog().log(AbstractSessionLog.SEVERE, "sdo_classgenerator_exception",//
                                            e.getClass().getName(), e.getLocalizedMessage(), generator.getClass());
            AbstractSessionLog.getLog().logThrowable(AbstractSessionLog.SEVERE, e);
        }
    }

}
