/*
  Copyright (C) 2009-2011 Volker Berlin (i-net software)

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
import java.io.*;
import java.net.URL;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Build a Eclipse project from the sources of the IKVM project. See the usage() methods for details.
 * Created on 19.04.2009
 * @author Volker Berlin
 */
public class CreateEclipseProject{

    /**
     * The start point of the program.
     * @param args the program arguments
     * @throws IOException if an I/O error occurs.
     */
    public static void main(String[] args) throws IOException{
        if(args.length != 2){
            usage();
        }
        
        new CreateEclipseProject(args[0],args[1]);
    }

    /**
     * print a command line help
     */
    static private void usage(){
        System.out.println("Create a Eclipse project from the IKVM project.");
        System.out.println("Usage: java CreateEclipseProject [Ikvm dictionary] [Eclpse project dictionary]");
        System.out.println();
        System.out.println("After executing the program you can import the project in Eclipse. " +
        		"If you have already a Eclipse project with the name 'ikvm' " +
        		"then you need to rename it in the .project file with a text editor before you import it.");
        System.exit(0);
    }
    
    final String src;
    final String dest;
    final URL openjdk;
    final HashSet<String> sources = new HashSet<String>();
    
    /**
     * Create a instance
     * @param src path to the checkout IKVM directory
     * @param dest path to the creating Eclipse project
     * @throws IOException if the paths are invalid or there are no write and read rights
     */
    CreateEclipseProject(String src, String dest) throws IOException{
        src = src.replace('\\', '/');
        if(!src.endsWith("/")){
            src += '/';
        }
        this.src = src;
        URL srcURL = new URL("file", null, src);
        openjdk = new URL(srcURL, "openjdk/");
        String allSourcesName = new URL(openjdk, "allsources.gen.lst").getFile();
        File allSourcesFile = new File(allSourcesName);
        if(!allSourcesFile.exists()){
        	System.out.println( "File '" + allSourcesName + "' does not exist. You need to build the project first.");
            usage();
        }

        dest = dest.replace('\\', '/');
        if(!dest.endsWith("/")){
            dest += '/';
        }
        this.dest = dest;

        copyAllSources(allSourcesFile);

        String classpath = createClasspathFile();
        saveFile(classpath.getBytes("UTF8"), dest + ".classpath", null);
        
        String project = createProjectFile();
        saveFile(project.getBytes("UTF8"), dest + ".project", null);
        
        String prefs = createPrefsFile();
        saveFile(prefs.getBytes("UTF8"), dest + ".settings/org.eclipse.jdt.core.prefs", null);
        
        new File(dest + "src/").mkdirs();
    }
    
    /**
     * Read the context of allsources.txt and copy the listed source files.
     * @param allSourcesFile file handle to allsources.txt
     * @throws IOException if an I/O error occurs.
     */
    private void copyAllSources(File allSourcesFile) throws IOException{
        FileInputStream stream = new FileInputStream(allSourcesFile);
        int totalSize = stream.available();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line = reader.readLine();

        System.out.println();
        
        while(line != null){
            URL fileURL;
			fileURL = line.indexOf(':') > 0 ? new URL("file", null, line) : new URL(openjdk, line);
            File file = new File(fileURL.getFile());
            if(!file.exists()){
                System.err.println("\nFile not found:"+file);
            }else{
                int percent = (int)(100.0 * (totalSize - stream.available()) / totalSize);
                System.out.print("\r" + percent + "%  ");
                String folder = destFolder(line);
                byte[] data = readFile(file);
                String packageName = findPackage(data);
                
                String destFileName = dest + folder + "/" + packageName.replace('.', '/') + "/" + file.getName();
                saveFile( data, destFileName, file);
            }
            line = reader.readLine();
        }
        stream.close();
    }
    
    /**
     * Calculate the target folder in the Eclipse project from a single source file.
     * Every new target folder will be add to the sources list.
     * @param line a single line from the allsources.txt
     * @return a source folder
     */
    private String destFolder(String line){
        boolean isTop = false;
		int topIdx = line.lastIndexOf("../");
		if (topIdx >= 0) {
			isTop = true;
			line = line.substring(topIdx + 3);
		}
        String folder;
        if(!isTop){
            if(line.startsWith("icedtea")){
                folder = "icedtea";
            }else{
                folder = "ikvm";
            }
        }else{
            int idx = line.indexOf('/');
            folder = line.substring(0, idx);
        }
        sources.add(folder);
        return folder;
    }
    
    /**
     * Read a single source file in the memory.
     * @param file the file handle to the Java source file
     * @return the content of the source file
     * @throws IOException if an I/O error occurs.
     */
    private byte[] readFile(File file) throws IOException{
        InputStream stream = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        int count = stream.read(buffer);
        do{
            baos.write(buffer, 0, count );
            count = stream.read(buffer);
        }while(count > 0);
        stream.close();
        return baos.toByteArray();
    }
    
    /**
     * Find the package name in the source data. In the IKVM project not all files are placed
     * on the right position.
     * @param data the content of a Java source file
     * @return
     */
    private String findPackage(byte[] data){
        String fileData = new String(data, 0);
        Pattern pattern = Pattern.compile("(?<!// )package\\s.*?;");
        Matcher matcher = pattern.matcher(fileData);
        if(matcher.find()){
            String match = fileData.substring(matcher.start(), matcher.end() - 1);
            int idx = match.indexOf("package") + "package".length();
            return match.substring(idx).trim();
        }
        return "";
    }
    
    /**
     * Save a single file 
     * @param data the content of the file 
     * @param fileName the filename to save
     * @param srcFile for the last modified, can be null
     * @throws IOException
     */
    private void saveFile(byte[] data, String fileName, File srcFile ) throws IOException{
        File destFile = new File(fileName);
        destFile.getParentFile().mkdirs();
        OutputStream stream = new FileOutputStream(destFile);
        stream.write(data);
        stream.close();
        if(srcFile != null){
            destFile.setLastModified(srcFile.lastModified());
        }
    }
    
    /**
     * Create the .classpath file of the Eclipse project.
     * @return the content of the file
     * @throws IOException
     */
    private String createClasspathFile() throws IOException{
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        builder.append("<classpath>\n");
        for(String path : sources){
            builder.append("<classpathentry kind=\"src\" path=\"");
            builder.append(path);
            builder.append("\"/>\n");
        }
        builder.append("<classpathentry kind=\"src\" path=\"src\"/>\n");
        
        String baseDir = src + "openjdk/";
        String[] files = new File(baseDir).list();
        for(int i = 0; i < files.length; i++){
            String fileName = files[i];
            if(fileName.endsWith(".jar")){
            	File file = new File(baseDir + '/' + fileName);
                byte[] data = readFile(file);
                String destFileName = dest + file.getName();
                saveFile( data, destFileName, file);

                builder.append("<classpathentry kind=\"lib\" path=\"");
                builder.append(destFileName);
                builder.append("\"/>\n");
            }
        }
        builder.append("<classpathentry kind=\"output\" path=\"bin\"/>\n");
        builder.append("</classpath>\n");
        return builder.toString();
    }
    
    /**
     * Create the .project file of Eclipse project
     * @return the content of the file
     */
    private String createProjectFile(){
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        builder.append("<projectDescription>\n");
        builder.append("<name>ikvm</name>\n");
        builder.append("    <buildSpec>\n        <buildCommand>\n            <name>org.eclipse.jdt.core.javabuilder</name>\n            <arguments>\n            </arguments>\n        </buildCommand>\n    </buildSpec>\n    <natures>\n        <nature>org.eclipse.jdt.core.javanature</nature>\n    </natures>\n");
        builder.append("</projectDescription>\n");
        return builder.toString();
    }
    
    /**
     * Create the .settings/org.eclipse.jdt.core.prefsfile of Eclipse project
     * @return the content of the file
     */
    private String createPrefsFile(){
        StringBuilder builder = new StringBuilder();
        builder.append("eclipse.preferences.version=1\n");
        builder.append("org.eclipse.jdt.core.compiler.codegen.targetPlatform=1.8\n");
        builder.append("org.eclipse.jdt.core.compiler.compliance=1.8\n");
        builder.append("org.eclipse.jdt.core.compiler.source=1.8\n");
        builder.append("org.eclipse.jdt.core.compiler.problem.autoboxing=ignore\n");
        return builder.toString();
    }
}
