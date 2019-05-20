package fvarrui.pptx2video.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;

public class ProcessUtils {

	public static String execute(File workingDirectory, String executable, Object... arguments)
			throws CommandLineException, IOException {
		StringBuffer outputBuffer = new StringBuffer();
		
		Commandline command = new Commandline();
		command.setWorkingDirectory(workingDirectory);
		command.setExecutable(executable);
		for (Object argument : arguments) {
			if (argument instanceof File)
				command.createArg().setFile((File) argument);
			else
				command.createArg().setValue(argument.toString());
		}

		Process process = command.execute();

		BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
		BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));

		while (process.isAlive()) {
			if (output.ready()) {
				String line = output.readLine();
				outputBuffer.append(line);
			}
			if (error.ready()) {
				System.err.println(error.readLine());
			}
		}

		output.close();
		error.close();

		return outputBuffer.toString();
	}

	public static String execute(String executable, Object... arguments) throws CommandLineException, IOException {
		return execute(new File("."), executable, arguments);
	}

}
