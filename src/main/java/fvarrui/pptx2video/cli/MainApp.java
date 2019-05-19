package fvarrui.pptx2video.cli;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;

import fvarrui.pptx2video.pptx.PowerPointUtils;
import fvarrui.pptx2video.tts.TextToSpeech;
import fvarrui.pptx2video.video.VideoUtils;

public class MainApp {
	
	public static void convert() throws IOException, InterruptedException, CommandLineException {

		String pptx = "samples/deportes.pptx";
		
		File tempDir = new File("temp"); // Files.createTempDirectory("pptx2video_").toFile();
		if (tempDir.exists()) FileUtils.deleteDirectory(tempDir);
		tempDir.mkdir();
		
		System.out.println("Temp directory: " + tempDir);
		
		List<File> slides = PowerPointUtils.extractImages(pptx, tempDir);

		System.out.println("Extracted slides: " + slides.size());
		
		List<String> notes = PowerPointUtils.extractNotes(pptx);
		
		List<File> speeches = TextToSpeech.textsToWav(notes, tempDir);
		
		System.out.println(slides);
		System.out.println(speeches);
		
		System.out.println("Recording videos:");
		List<File> videos = VideoUtils.createVideos(slides, speeches, tempDir);
		
		System.out.println(videos);
		
		System.out.println("Merging videos:");
		File video = new File("samples/video.mp4");
		File listFile = new File(tempDir, "videos.txt");
		
		List<String> lineas = videos.stream().map(v -> "file '" + v + "'").collect(Collectors.toList());
		
		FileUtils.write(listFile, StringUtils.join(lineas.toArray(), "\r\n"), Charset.defaultCharset());
		VideoUtils.mergeVideos(listFile, video);
		
		System.out.println("video " + video.getName() + " generated");
		
		FileUtils.deleteDirectory(tempDir);
		
	}

	public static void main(String[] args) throws IOException, InterruptedException, CommandLineException {
		convert();
	}

}
