/*
  Copyright (C) 2011 Volker Berlin (i-net software)

  This software is provided 'as-is', without any express or implied
  warranty.  In no event will the authors be held liable for any damages
  arising from the use of this software.

  Permission is granted to anyone to use this software for any purpose,
  including commercial applications, and to alter it and redistribute it
  freely, subject to the following restrictions:

  1. The origin of this software must not be misrepresented; you must not
     claim that you wrote the original software. If you use this software
     in a product, an acknowledgment in the product documentation would be
     appreciated but is not required.
  2. Altered source versions must be plainly marked as such, and must not be
     misrepresented as being the original software.
  3. This notice may not be removed or altered from any source distribution.

  Jeroen Frijters
  jeroen@frijters.net
  
 */
package cacar;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class CompareAvailableClassesAndResources {

	private ArrayList<Pattern> skipFiles = new ArrayList<>();
	private ArrayList<String> skipPackages = new ArrayList<>();
	private HashSet<String> skipClasses = new HashSet<>();
	private HashSet<String> skipResources = new HashSet<>();

	private String javaRootName;
	
	private static final String META_INF_SERVICES = "META-INF/services/";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		System.setErr(new PrintStream(new File("missing.txt")));

		CompareAvailableClassesAndResources main = new CompareAvailableClassesAndResources();
		main.start(args);
	}

	private void usage() {
		System.err.println("usage todo");
	}

	private void start(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("No Java Home was set.");
			usage();
			return;
		}

		javaRootName = args[0];
		File javaRoot = new File(javaRootName);
		if (!javaRoot.isDirectory()) {
			System.err.println(javaRoot + " is not a directory.");
			usage();
			return;
		}

		URL skipUrl;
		if (args.length < 2) {
			skipUrl = getClass().getResource("skip.txt");
			if (skipUrl == null) {
				System.err.println("No Skip file was set.");
				usage();
				return;
			}
		} else {
			File skipFile = new File(args[1]);
			if (!skipFile.exists()) {
				System.err.println(skipFile + " not found.");
				usage();
				return;
			}
			skipUrl = skipFile.toURI().toURL();
		}

		DataInputStream dis = new DataInputStream(skipUrl.openStream());
		String line = dis.readLine();
		while (line != null) {
			switch (line) {
			case "":
				break;
			case "-file":
				line = dis.readLine();
				do {
					if (!line.isEmpty() && !line.startsWith("#")) {
						String sf = line.replace("\\", "\\\\")
								.replace("?", ".?").replace("*", ".*?");
						skipFiles.add(Pattern.compile(sf));
					}
					line = dis.readLine();
				} while (line != null && !line.startsWith("-"));
				continue;
			case "-package":
				line = dis.readLine();
				do {
					if (!line.isEmpty() && !line.startsWith("#")) {
						skipPackages.add(line + '.');
					}
					line = dis.readLine();
				} while (line != null && !line.startsWith("-"));
				continue;
			case "-class":
				line = dis.readLine();
				do {
					if (!line.isEmpty() && !line.startsWith("#")) {
						skipClasses.add(line);
					}
					line = dis.readLine();
				} while (line != null && !line.startsWith("-"));
				continue;
			case "-resource":
				line = dis.readLine();
				do {
					if (!line.isEmpty() && !line.startsWith("#")) {
						skipResources.add(line);
					}
					line = dis.readLine();
				} while (line != null && !line.startsWith("-"));
				continue;
			default:
				if (!line.isEmpty() && !line.startsWith("#")) {
					System.err.println("Wrong Skip file format: " + line);
					usage();
					return;
				}
			}
			line = dis.readLine();
		}

		searchJarFiles(javaRoot);
	}

	private void searchJarFiles(File javaRoot) throws IOException {
		File[] files = javaRoot.listFiles();
		for (File file : files) {
			String name = file.getName();
			if (file.isDirectory()) {
				if (name.equals("demo") || name.equals("sample")
						|| name.equals("visualvm")) {
					continue;
				}
				searchJarFiles(file);
			} else {
				if (name.endsWith(".jar") && !isSkipFile(file)) {
					scanJarFile(file);
				}
			}
		}
	}

	private boolean isSkipFile(File file) {
		String name = file.getAbsolutePath();
		if (!name.startsWith(javaRootName)) {
			return false;
		}
		name = name.substring(javaRootName.length());
		for (Pattern pattern : skipFiles) {
			if (pattern.matcher(name).find()) {
				System.out.println("Skip file: " + name);
				return true;
			}
		}
		return false;
	}

	private boolean isSkipPackage(String className) {

		for (String pack : skipPackages) {
			if (className.startsWith(pack)) {
				return true;
			}
		}
		return false;
	}

	private void scanJarFile(File file) throws IOException {
		JarFile jarFile = new JarFile(file);
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if( entry.isDirectory() ){
				continue;
			}
			String name = entry.getName();
			if (name.endsWith(".class")) {
				String className = name.substring(0, name.length() - 6)
						.replace('/', '.');
				if (!isSkipPackage(className) && !className.contains("$")
						&& !skipClasses.contains(className)) {
					try {
						Class.forName(className, false, null);
					} catch (ClassNotFoundException e) {
						System.err.println("Missing class: " + className
								+ " from jar file: " + jarFile.getName());
					} catch (Throwable th) {
						System.err.println(th);
					}
				}
			} else {
				URL resource = getClass().getResource('/' + name);
				if (resource == null){
					if( isSkipPackage(name.replace('/', '.'))) {
						continue;					
					}
					if( name.startsWith(META_INF_SERVICES) && isSkipPackage(name.substring(META_INF_SERVICES.length()).replace('/', '.'))){
						continue;	
					}
					if( skipResources.contains(name)){
						continue;
					}
					System.err.println("Missing resource: " + name
							+ " from jar file: " + jarFile.getName());
				}
			}
		}
	}
}
