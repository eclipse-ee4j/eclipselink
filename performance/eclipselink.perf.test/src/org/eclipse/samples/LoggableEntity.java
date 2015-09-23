package org.eclipse.samples;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;

import org.eclipse.persistence.testing.perf.jpa.model.metaannotations.EListener;

@Entity
@EntityListeners(EListener.class)
public @interface LoggableEntity { }
