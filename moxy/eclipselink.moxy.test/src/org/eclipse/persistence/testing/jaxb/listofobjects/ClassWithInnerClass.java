/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith  December 15, 2009 
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects;

public class ClassWithInnerClass {
	
	public String name;
		
	public static class MyInner implements Comparable<MyInner> {
		public String innerName;
		
		public boolean equals(Object theObject){
			if(!(theObject instanceof MyInner)){
				return false;
			}
			if(!(innerName.equals(((MyInner)theObject).innerName))){
				return false;
			}
			return true;
		}

		public int compareTo(MyInner o) {
			if(o.innerName == null){
				return 0;	
			}else{
				int thisFirstChar = innerName.charAt(0);
				int firstChar = ((MyInner)o).innerName.charAt(0);
				if(firstChar == thisFirstChar){
					return 0;
				}else if(thisFirstChar < firstChar){
					return -1;
				}else{
					return 1;
				}
			}
				
			
			
		}
	}
	

}
