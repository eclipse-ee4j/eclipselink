/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.oxm.jaxb.compiler;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;

import org.eclipse.persistence.internal.localization.JAXBLocalization;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLUnmarshaller;

/**
 * This class generates Java implementation classes, a Mapping Workbench project
 * and a TopLink Deployment XML from information gleaned from <CODE>orajaxb</CODE>.
 *
 * @author    Rick Barkhouse <rick.barkhouse@oracle.com>
 * @since    04/14/2004 12:03:14
 */
public class TopLinkJAXBGenerator {
    private static final String fsep = System.getProperty("file.separator");
    private boolean debug = false;

    // ===========================================================================
    public void generate(MWOXProject mwProject, MWXmlSchema mwSchema, String sourceDir, String workbenchDir, String implClassPkg, String projectName, String bindingFileName, boolean debug, boolean useDomPlatform, boolean generateWorkbenchProject) throws TopLinkJAXBGenerationException {
        this.debug = debug;

        try {
            Hashtable newBindingSchemas = getNewBindingSchemas(sourceDir, bindingFileName, implClassPkg);
            new TopLinkJAXBMappingsGenerator().generate(newBindingSchemas, mwProject, mwSchema, projectName, workbenchDir, sourceDir, implClassPkg, useDomPlatform, generateWorkbenchProject);
            TopLinkJAXBGeneratorLog.log(JAXBLocalization.buildMessage("generate_source"));
            new TopLinkJAXBSourceGenerator().generate(newBindingSchemas, mwSchema, sourceDir, sourceDir, implClassPkg);
        } catch (Exception e) {
            throw new TopLinkJAXBGenerationException(e);
        }
    }

    private Hashtable getNewBindingSchemas(String outputDir, String bindingFileName, String implClassPackage) {
        TopLinkJAXBGeneratorLog.log(JAXBLocalization.buildMessage("read_customization"));

        Hashtable table = new Hashtable();
        TopLinkJAXBBindingSchemaProject proj = new TopLinkJAXBBindingSchemaProject();
        XMLContext context = new XMLContext(proj);
        XMLUnmarshaller unmarshaller = context.createUnmarshaller();

        File bindingFile = new File(outputDir, bindingFileName);
        TopLinkJAXBBindingSchemaCollection obj = (TopLinkJAXBBindingSchemaCollection)unmarshaller.unmarshal(bindingFile);

        // unless we're in debug mode, attempt to remove the customization file from the filesystem
        if (!this.debug) {
            try {
                bindingFile.delete();
            } catch (SecurityException sex) {
                // delete request denied - ignore
            }
        }

        // add any typesafe enumeration info to the binding schemas
        obj.handleTypesafeEnumerations();

        for (int i = 0; i < obj.getBindings().size(); i++) {
            TopLinkJAXBBindingSchema schema = (TopLinkJAXBBindingSchema)obj.getBindings().elementAt(i);
            if (schema.getClassName() != null) {
                schema.setImplClassPackage(implClassPackage);
                String fullClassName = schema.getFullClassName();
                table.put(fullClassName, schema);
            }
        }

        calculateInnerClasses(table);
        return table;
    }

    /**
    * Enumerate the hashtable of BindingSchemas, and for any inner classes:
    *    - Find its enclosing class' BindingSchema
    *    - Add the inner class to the enclosing class' list of inner classes
    *    - Remove the inner class from the hashtable
    */
    private void calculateInnerClasses(Hashtable table) {
        Enumeration schemasEnum = table.elements();
        Vector schemasToRemove = new Vector();

        while (schemasEnum.hasMoreElements()) {
            TopLinkJAXBBindingSchema schema = (TopLinkJAXBBindingSchema)schemasEnum.nextElement();
            if (schema.getIsInnerInterface()) {
                TopLinkJAXBBindingSchema enclosingSchema = (TopLinkJAXBBindingSchema)table.get(schema.getEnclosingClassName());
                schema.setEnclosingSchema(enclosingSchema);
                enclosingSchema.getInnerInterfaces().add(schema);
                schemasToRemove.add(schema.getFullClassName());
            }
        }

        for (int i = 0; i < schemasToRemove.size(); i++) {
            table.remove(schemasToRemove.elementAt(i));
        }
    }
}