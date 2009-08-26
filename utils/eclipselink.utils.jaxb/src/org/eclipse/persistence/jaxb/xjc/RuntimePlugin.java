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
*     bdoughan - August 12/2009 - 1.2 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.jaxb.xjc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.outline.PackageOutline;

public class RuntimePlugin extends Plugin {

    private static final String OPTION_NAME = "eclipselink";
    private static final String JAXB_PROPERTIES = "/jaxb.properties";
    private static final String RUNTIME_PROPERTY = "javax.xml.bind.context.factory=org.eclipse.persistence.jaxb.JAXBContextFactory";

    @Override
    public void postProcessModel(Model model, ErrorHandler errorHandler) {
        super.postProcessModel(model, errorHandler);
    }

    @Override
    public String getOptionName() {
        return OPTION_NAME;
    }

    @Override
    public String getUsage() {
        return "  -" + getOptionName() + "       :  use the EclipseLink runtime";
    }

    @Override
    public boolean run(Outline outline, Options options, ErrorHandler errorHandler) throws SAXException {
        for(PackageOutline packageOutline :outline.getAllPackageContexts()) {
            String packageName = packageOutline._package().name().replace(".", "/");
            try {
                File jaxbPropertiesFile = new File(options.targetDir.toString() + "/" + packageName + JAXB_PROPERTIES);
                jaxbPropertiesFile.getParentFile().mkdirs();
                jaxbPropertiesFile.createNewFile();
                FileWriter jaxbPropertiesWriter = new FileWriter(jaxbPropertiesFile);
                jaxbPropertiesWriter.write(RUNTIME_PROPERTY);
                jaxbPropertiesWriter.close();
            } catch(IOException e) {
                errorHandler.error(new SAXParseException(null, null, e));
            }
            
        }
        return true;
    }

}