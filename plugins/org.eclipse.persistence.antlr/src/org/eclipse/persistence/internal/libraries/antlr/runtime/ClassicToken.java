package org.eclipse.persistence.internal.libraries.antlr.runtime;

/** A Token object like we'd use in ANTLR 2.x; has an actual string created
 *  and associated with this object.  These objects are needed for imaginary
 *  tree nodes that have payload objects.  We need to create a Token object
 *  that has a string; the tree node will point at this token.  CommonToken
 *  has indexes into a char stream and hence cannot be used to introduce
 *  new strings.
 */
public class ClassicToken implements Token {
	protected String text;
	protected int type;
	protected int line;
	protected int charPositionInLine;
	protected int channel=DEFAULT_CHANNEL;

	/** What token number is this from 0..n-1 tokens */
	protected int index;

	public ClassicToken(int type) {
		this.type = type;
	}

	public ClassicToken(Token oldToken) {
		text = oldToken.getText();
		type = oldToken.getType();
		line = oldToken.getLine();
		charPositionInLine = oldToken.getCharPositionInLine();
		channel = oldToken.getChannel();
	}

	public ClassicToken(int type, String text) {
		this.type = type;
		this.text = text;
	}

	public ClassicToken(int type, String text, int channel) {
		this.type = type;
		this.text = text;
		this.channel = channel;
	}

	public int getType() {
		return type;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getLine() {
		return line;
	}

	public int getCharPositionInLine() {
		return charPositionInLine;
	}

	public void setCharPositionInLine(int charPositionInLine) {
		this.charPositionInLine = charPositionInLine;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getTokenIndex() {
		return index;
	}

	public void setTokenIndex(int index) {
		this.index = index;
	}

	public String toString() {
		String channelStr = "";
		if ( channel>0 ) {
			channelStr=",channel="+channel;
		}
		String txt = getText();
		if ( txt!=null ) {
			txt = txt.replaceAll("\n","\\\\n");
			txt = txt.replaceAll("\r","\\\\r");
			txt = txt.replaceAll("\t","\\\\t");
		}
		else {
			txt = "<no text>";
		}
		return "[@"+getTokenIndex()+",'"+txt+"',<"+type+">"+channelStr+","+line+":"+getCharPositionInLine()+"]";
	}
}
