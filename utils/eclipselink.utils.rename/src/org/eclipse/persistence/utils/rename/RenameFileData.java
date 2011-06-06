package org.eclipse.persistence.utils.rename;

public class RenameFileData {

	private String fileContentsString = null;
	private boolean changed = false;

	public RenameFileData(String fileContentsString, boolean changed) {
		this.fileContentsString = fileContentsString;
		this.changed = changed;
	}

	public String getFileContentsString() {
		return fileContentsString;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public void setFileContentsString(String contents) {
		this.fileContentsString = contents;
	}

}
