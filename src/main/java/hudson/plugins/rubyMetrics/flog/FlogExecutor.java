package hudson.plugins.rubyMetrics.flog;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.util.ArgumentListBuilder;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import jregex.util.io.PathPattern;

import org.codehaus.plexus.util.StringOutputStream;

@SuppressWarnings("unchecked")
public class FlogExecutor {
	
	public boolean isFlogInstalled(Launcher launcher, EnvVars environment, FilePath workspace) {
		try {
			OutputStream out = launch(arguments("--help"), launcher, environment, workspace);
			
			return out != null;
		} catch (Exception e) {
			return false;
		}
	}
	
	
	public Map<String, StringOutputStream> execute(String[] rbDirectories, Launcher launcher, EnvVars environment, FilePath workspace) throws InterruptedException, IOException {
		Map<String, StringOutputStream> results = new HashMap<String, StringOutputStream>();
		
		for (String path : rbDirectories) {
			String filePattern = path + "/**/*.rb";
			if (workspace != null) {
				filePattern = workspace.toURI().getPath() + filePattern;
			}
			launcher.getListener().getLogger().println("searching ruby classes into: " + filePattern);
			
			PathPattern pattern = new PathPattern(filePattern);
			Enumeration rubyFiles = pattern.enumerateFiles();
			
			while (rubyFiles.hasMoreElements()) {
				File rubyFile = (File) rubyFiles.nextElement();
				ArgumentListBuilder arguments = arguments("-ad", rubyFile.getAbsolutePath());
				
				StringOutputStream out = launch(arguments, launcher, environment, workspace);
				if (out == null) {
					results.clear();
					return results;
				}
				results.put(prettifyFilePath(path, rubyFile), out);
			}
		}
		
		return results;
	}
	
	public StringOutputStream launch(ArgumentListBuilder arguments, Launcher launcher, EnvVars environment, FilePath workspace) throws InterruptedException, IOException {
		StringOutputStream out = new StringOutputStream();
		
		int result = launcher.launch()
			.cmds(arguments)
			.envs(environment)
			.stdout(out)
			.pwd(workspace)
			.join();
		
		return result >= 0 ? out : null;
	}
	
	public ArgumentListBuilder arguments(String... args) {
		ArgumentListBuilder flogArguments = new ArgumentListBuilder();
		flogArguments.add("flog");
		for (String arg : args) {
			flogArguments.add(arg);
		}
		
		return flogArguments;
	}
	
	private String prettifyFilePath(String path, File rubyFile) {
		String absPath = rubyFile.getAbsolutePath();
		return absPath.substring(absPath.indexOf(path));
	}
}
