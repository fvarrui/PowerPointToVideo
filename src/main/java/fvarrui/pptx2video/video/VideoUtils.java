package fvarrui.pptx2video.video;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.cli.CommandLineException;

import fvarrui.pptx2video.utils.ProcessUtils;

public class VideoUtils {
	
	public static void createVideo(File slide, File video) throws CommandLineException, IOException {	
		ProcessUtils.execute("ffmpeg/ffmpeg.exe", "-f", "lavfi", "-i", "aevalsrc=0", "-shortest", "-i", slide, "-c:v", "libx264", "-t", "5", "-vf", "fps=25,format=yuv420p", video);
	}
	
	public static void createVideo(File slide, File speech, File video) throws CommandLineException, IOException {		
		ProcessUtils.execute("ffmpeg/ffmpeg.exe", "-f", "image2", "-i", slide, "-i", speech, "-c:v", "libx264", "-vf", "fps=25,format=yuv420p", video);
	}
	
	public static List<File> createVideos(List<File> slides, List<File> speeches, File destination) throws CommandLineException, IOException {
		List<File> videos = new ArrayList<File>();
		for (int i = 0; i < slides.size(); i++) {
			File video = new File(destination, "video-" + (i + 1) + ".mp4");
			if (!speeches.get(i).getName().isEmpty())
				createVideo(slides.get(i), speeches.get(i), video);	// video with audio
			else
				createVideo(slides.get(i), video);					// video without audio
			videos.add(video);
		}
		return videos;
	}
	
	public static void mergeVideos(File list, File output) throws CommandLineException, IOException {
		ProcessUtils.execute("ffmpeg/ffmpeg.exe", "-y", "-f", "concat", "-safe", "0", "-i", list, "-max_muxing_queue_size", "10240", output);
	}

}
