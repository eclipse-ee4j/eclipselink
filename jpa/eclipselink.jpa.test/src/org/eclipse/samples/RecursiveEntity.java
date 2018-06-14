package org.eclipse.samples;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@RecursiveAnnotation
public class RecursiveEntity {
	@Id
	private Long id = 0L;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
