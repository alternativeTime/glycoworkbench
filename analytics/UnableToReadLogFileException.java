package org.eurocarbdb.application.glycoworkbench.analytics;

public class UnableToReadLogFileException extends Exception {

	public UnableToReadLogFileException() {
	}

	public UnableToReadLogFileException(String message) {
		super(message);

	}

	public UnableToReadLogFileException(Throwable cause) {
		super(cause);

	}

	public UnableToReadLogFileException(String message, Throwable cause) {
		super(message, cause);

	}

	public UnableToReadLogFileException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

}
