package jpql.query;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractProduct implements Serializable {

	@Basic
	private String partNumber;
}