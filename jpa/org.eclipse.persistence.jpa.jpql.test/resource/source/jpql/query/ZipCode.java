package jpql.query;

import javax.persistence.Embeddable;

@Embeddable
@SuppressWarnings("unused")
public class ZipCode {
	private int mainCode;
	private int code;
}