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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.eis.xmlfile;

import java.util.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.eis.interactions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.sessions.factories.*;
import org.eclipse.persistence.internal.eis.adapters.xmlfile.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * <b>Purpose</b>: To define system behavior.
 * 
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li>
 * Login and return an initialize database session.
 * </li>
 * <li>
 * Create and populate the database.
 * </li>
 * </ul>
 * </p>
 */
public class EmployeeSystem extends TestSystem {

    /**
     * Use the default EmployeeProject.
     */
    public EmployeeSystem() {
        project = XMLProjectReader.read("org/eclipse/persistence/testing/models/employee/eis/xmlfile/employee-project.xml", getClass().getClassLoader());
    }

    public void addDescriptors(DatabaseSession session) {
        if (project == null) {
            project = XMLProjectReader.read("org/eclipse/persistence/testing/models/employee/eis/xmlfile/employee-project.xml", getClass().getClassLoader());
        }

        session.addDescriptors(project);
    }

    public void createTables(DatabaseSession session) {
        // Drop tables
        XQueryInteraction interaction = new XQueryInteraction();
        interaction.setFunctionName("drop-sequence");
        XMLFileInteractionSpec spec = new XMLFileInteractionSpec();
        spec.setFileName("sequence.xml");
        spec.setInteractionType(XMLFileInteractionSpec.DELETE);
        interaction.setInteractionSpec(spec);
        session.executeNonSelectingCall(interaction);

        interaction = new XQueryInteraction();
        interaction.setFunctionName("drop-EMPLOYEE");
        spec = new XMLFileInteractionSpec();
        spec.setFileName("EMPLOYEE.xml");
        spec.setInteractionType(XMLFileInteractionSpec.DELETE);
        interaction.setInteractionSpec(spec);
        session.executeNonSelectingCall(interaction);

        interaction = new XQueryInteraction();
        interaction.setFunctionName("drop-PROJECT");
        spec = new XMLFileInteractionSpec();
        spec.setFileName("PROJECT.xml");
        spec.setInteractionType(XMLFileInteractionSpec.DELETE);
        interaction.setInteractionSpec(spec);
        session.executeNonSelectingCall(interaction);

        // Create sequences
        DataModifyQuery query = new DataModifyQuery();
        query.addArgument("sequence-name");
        query.addArgument("sequence-count");
        interaction = new XQueryInteraction();
        interaction.setFunctionName("create-sequence");
        spec = new XMLFileInteractionSpec();
        spec.setFileName("sequence.xml");
        spec.setXPath("sequence");
        spec.setInteractionType(XMLFileInteractionSpec.INSERT);
        interaction.setInteractionSpec(spec);
        interaction.setInputRootElementName("sequence");
        interaction.addArgument("sequence-name");
        interaction.addArgument("sequence-count");
        query.setCall(interaction);

        Vector arguments = new Vector(2);
        arguments.add("EMP_SEQ");
        arguments.add(new Integer(0));
        session.executeQuery(query, arguments);

        arguments = new Vector(2);
        arguments.add("PROJ_SEQ");
        arguments.add(new Integer(0));
        session.executeQuery(query, arguments);
    }

    /**
     * This method will instantiate all of the example instances and insert them into the database
     * using the given session.
     */
    public void populate(DatabaseSession session) {
        EmployeePopulator system = new EmployeePopulator();
        UnitOfWork unitOfWork = session.acquireUnitOfWork();

        system.buildExamples();
        Vector allObjects = new Vector();
        PopulationManager.getDefaultManager().addAllObjectsForClass(Employee.class, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForAbstractClass(LargeProject.class, session, allObjects);
        PopulationManager.getDefaultManager().addAllObjectsForAbstractClass(SmallProject.class, session, allObjects);
        unitOfWork.registerAllObjects(allObjects);
        unitOfWork.commit();
    }
}
