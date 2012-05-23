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
 *     Praba Vijayaratnam - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.javadoc.xmlrootelement;

import javax.xml.bind.annotation.*;

@XmlRootElement
public class Point2D {
	public int x;
	public int y;

	public Point2D() {
		;
	}

	public Point2D(int _x, int _y) {
		x = _x;
		y = _y;
	}

	public boolean equals(Object object) {
		Point2D point = ((Point2D) object);
		return (point.x == this.x) && (point.y == this.y);
	}
}
