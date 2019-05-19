package fvarrui.pptx2video.ui;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import fvarrui.pptx2video.pptx.PowerPointUtils;
import fvarrui.pptx2video.tts.TextToSpeech;
import fvarrui.pptx2video.ui.background.Step;
import fvarrui.pptx2video.ui.background.SteppedTask;
import fvarrui.pptx2video.video.VideoUtils;

public class ConvertionTask extends SteppedTask {
	
	private Step cleanStep;
	
	@SuppressWarnings("unchecked")
	public ConvertionTask(File input, File output) {
		super();
		getData().put("input", input);
		getData().put("output", output);
		addStep("Creating temporary directory for intermmediate artifacts",
			data -> {
				File tempDir = Files.createTempDirectory("pptx2video_").toFile();
				tempDir.mkdir();
				data.put("tempDir", tempDir);
			}
		);
		addStep("Extracting slides to images",
			data -> {
				File inputFile = (File) data.get("input");
				File tempDir = (File) data.get("tempDir");
				String pptx = inputFile.getAbsolutePath();
				List<File> slides = PowerPointUtils.extractImages(pptx, tempDir);
				data.put("slides", slides);
			}
		);
		addStep("Extracting notes from slideshow",
			data -> {
				File inputFile = (File) data.get("input");
				String pptx = inputFile.getAbsolutePath();
				List<String> notes = PowerPointUtils.extractNotes(pptx);
				data.put("notes", notes);
			}
		);
		addStep("Generating text-to-speech audios from notes",
			data -> {
				List<String> notes = (List<String>) data.get("notes");
				File tempDir = (File) data.get("tempDir");				
				List<File> speeches = TextToSpeech.textsToWav(notes, tempDir);
				data.put("speeches", speeches);
			}
		);
		addStep("Creating videos from slide images and tts audios",
			data -> {
				List<File> slides = (List<File>) data.get("slides");
				List<File> speeches = (List<File>) data.get("speeches");
				File tempDir = (File) data.get("tempDir");				
				List<File> videos = VideoUtils.createVideos(slides, speeches, tempDir);
				data.put("videos", videos);
			}
		);
		addStep("Merging created slide videos",
			data -> {
				List<File> videos = (List<File>) data.get("videos");
				File tempDir = (File) data.get("tempDir");				
				File listFile = new File(tempDir, "videos.txt");
				List<String> lineas = videos.stream().map(v -> "file '" + v + "'").collect(Collectors.toList());
				FileUtils.write(listFile, StringUtils.join(lineas.toArray(), "\r\n"), Charset.defaultCharset());
				VideoUtils.mergeVideos(listFile, output);
			}
		);
		cleanStep = addStep("Removing temporary directory",
			data -> {
				File tempDir = (File) data.get("tempDir");				
				FileUtils.deleteDirectory(tempDir);
			}
		);
		addStep("Video " + output.getName() + " generation finished");
	}
	
	// if process is cancelled, remove temporary directory (clean)
	@Override
	protected void cancelled() {
		super.cancelled();
		try {
			cleanStep.getTask().doWork(getData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// if process failed, remove temporary directory (clean)
	@Override
	protected void failed() {
		super.failed();
		try {
			cleanStep.getTask().doWork(getData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
