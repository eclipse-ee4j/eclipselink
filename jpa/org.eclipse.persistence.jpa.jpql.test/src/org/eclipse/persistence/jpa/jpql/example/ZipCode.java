package org.eclipse.persistence.jpa.jpql.example;

import javax.persistence.Embeddable;

@Embeddable
@SuppressWarnings("unused")
public class ZipCode {
	private int mainCode;
	private int code;
}