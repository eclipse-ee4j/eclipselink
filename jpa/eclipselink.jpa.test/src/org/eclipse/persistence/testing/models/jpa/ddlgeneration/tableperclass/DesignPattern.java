/*******************************************************************************
 * Copyright (c) 2010 Frank Schwarz. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/23/2010-2.2 Frank Schwarz 
 *       - 328774: TABLE_PER_CLASS-mapped key of a java.util.Map does not work for querying
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.ddlgeneration.tableperclass;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyJoinColumn;

@Entity
public class DesignPattern {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;
	
	private String name;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Map<ProgrammingLanguage, CodeExample> codeExamples = new HashMap<ProgrammingLanguage, CodeExample>();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Map<ProgrammingLanguage, CodeExample> getCodeExamples() {
		return codeExamples;
	}

	public void setCodeExamples(Map<ProgrammingLanguage, CodeExample> codeExamples) {
		this.codeExamples = codeExamples;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
