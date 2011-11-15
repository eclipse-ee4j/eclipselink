/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/15/2011-2.3.2 Guy Pelletier
 *       - 363820: Issue with clone method from VPDMultitenantPolicy 
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Don't add an Entity annotation to this class as we don't want this class to 
 * be picked up from other test persistence unit classes that do not exclude 
 * unlisted classes.
 * 
 * @see Related mapping file: 
 * trunk\jpa\eclipselink.jpa.test\resource\eclipselink-annotation-model\multitenant-vpd.xml
 * 
 * @author gpelleti
 */
@DiscriminatorValue("SUBTASK")
public class SubTask extends Task {

}
