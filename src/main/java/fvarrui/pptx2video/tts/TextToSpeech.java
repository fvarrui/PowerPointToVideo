package fvarrui.pptx2video.tts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.cli.CommandLineException;

import fvarrui.pptx2video.utils.ProcessUtils;

public class TextToSpeech {
	
	public static void say(String text) {
		try {
			ProcessUtils.execute("balcon/balcon.exe", "-t", text);
		} catch (CommandLineException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void textToWav(String text, File wavFile) {
		try {
			ProcessUtils.execute("balcon/balcon.exe", "-t", text, "-w", wavFile);
		} catch (CommandLineException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<File> textsToWav(List<String> texts, File destination) {
		List<File> speeches = new ArrayList<File>();
		int i = 1;
		for (String text : texts) {
			File wavFile = new File(destination, "speech-" + i + ".wav");
			speeches.add(wavFile);
			System.out.println(i + ") saving speech to " + wavFile.getName());
			TextToSpeech.textToWav(text, wavFile);
			i++;
		}
		return speeches;
	}

}
