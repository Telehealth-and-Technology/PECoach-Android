package org.t2health.pe;

import java.io.File;

public class ServiceRecording {
	public File file;
	public long startTime;
	public long endTime;

	public long getDuration() {
		return endTime - startTime;
	}
}
