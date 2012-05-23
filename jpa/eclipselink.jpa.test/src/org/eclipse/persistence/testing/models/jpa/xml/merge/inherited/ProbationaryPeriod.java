/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     07/06/2011-2.3.1 Guy Pelletier 
 *       - 349906: NPE while using eclipselink in the application
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.xml.merge.inherited;

import javax.persistence.Embeddable;

@Embeddable
public class ProbationaryPeriod extends WorkPeriod {

}
