package org.eclipse.persistence.jpa.jpql.example;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@SuppressWarnings("unused")
public abstract class AbstractProduct implements Serializable {

	@Basic
	private String partNumber;
}