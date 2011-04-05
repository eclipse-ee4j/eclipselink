package jpql.query;

import java.util.Date;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
@SuppressWarnings("unused")
public class ShelfLife {

	@Temporal(TemporalType.DATE)
	private Date soldDate;
}