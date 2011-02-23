package jpql.query;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Resource entity class used by the {@link QueryBuilderTest}.
 *
 * @author John Bracken
 */
@Entity
public class NoQueryEntity {
	@Id
	private int id;

	public NoQueryEntity() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
