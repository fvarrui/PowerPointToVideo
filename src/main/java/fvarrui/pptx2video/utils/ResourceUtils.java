package fvarrui.pptx2video.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ResourceUtils {

	public static void copyStreamToFile(InputStream input, File output) {
		try {
			Files.copy(input, output.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyResourceToFile(String resource, File output) {
		copyStreamToFile(ResourceUtils.class.getResourceAsStream(resource), output);
	}

}
