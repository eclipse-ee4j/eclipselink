package jpql.query;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class LargeProject extends Project {

	@Basic
	private String category;

	@Basic
	@Temporal(TemporalType.DATE)
	private Date endDate;

	@OneToOne
	private Project parent;

	@Basic
	@Temporal(TemporalType.DATE)
	private Date startDate;

	public LargeProject() {
		super();
	}
}