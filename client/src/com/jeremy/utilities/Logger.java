package com.jeremy.utilities;

import static com.jeremy.utilities.Utilities.*;

public class Logger {

	private static final String INFO = "INFO  > ";
	private static final String DEBUG = "DEBUG > ";
	private static final String WARN = "WARN  > ";
	private static final String ERROR = "ERROR > ";

	public synchronized static void write(Object... message) {
		System.out.print(join(" ", message));
	}

	public synchronized static void writeln(Object... message) {
		write(message);
		write("\n");
	}

	public synchronized static void info(Object... message) {
		write(INFO);
		writeln(message);
	}

	public synchronized static void debug(Object... message) {
		write(DEBUG);
		writeln(message);
	}

	public synchronized static void warn(Object... message) {
		write(WARN);
		writeln(message);
	}

	public synchronized static void error(Object... message) {
		write(ERROR);
		writeln(message);
	}

	public synchronized static void error(Exception e) {
		write(ERROR);
		write(e.getClass().getSimpleName());
		writeln(e.getMessage() != null ? ": " + e.getMessage() : ": ");
		for (StackTraceElement element : e.getStackTrace()) {
			write(ERROR);
			write("\t " + element.getClassName() + "." + element.getMethodName());
			writeln("(" + element.getFileName() + ":" + element.getLineNumber() + ")");
		}
	}

}
