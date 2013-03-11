package jpql.query;

import javax.persistence.Embeddable;

@Embeddable
public class ZipCode {

	private int mainCode;
	private int code;

	public int getCode() {
		return code;
	}

	public int getMainCode() {
		return mainCode;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setMainCode(int mainCode) {
		this.mainCode = mainCode;
	}
}