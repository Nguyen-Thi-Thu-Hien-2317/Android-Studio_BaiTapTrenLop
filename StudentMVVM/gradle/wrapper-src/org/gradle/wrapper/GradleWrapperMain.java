package org.gradle.wrapper;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/**
 * Lightweight, auditable Gradle distribution launcher.
 * Not the binary distributed by Gradle. Fetches the official distribution URL
 * declared in gradle-wrapper.properties, unpacks it safely and runs Gradle.
 */
public final class GradleWrapperMain {
    public static void main(String[] args) throws Exception {
        File jar = new File(GradleWrapperMain.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Path project = jar.toPath().toAbsolutePath().getParent().getParent().getParent();
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(project.resolve("gradle/wrapper/gradle-wrapper.properties"))) {
            props.load(in);
        }
        String uri = props.getProperty("distributionUrl");
        if (uri == null || !uri.startsWith("https://services.gradle.org/distributions/"))
            throw new IllegalStateException("Unsupported Gradle distribution URL");
        String filename = uri.substring(uri.lastIndexOf('/')+1);
        String dist = filename.endsWith("-bin.zip") ? filename.substring(0,filename.length()-8) : "gradle-8.7";
        Path base = Paths.get(System.getProperty("user.home"),".gradle","wrapper","dists","nhom16");
        Path exe = base.resolve(dist).resolve("bin").resolve(isWindows() ? "gradle.bat":"gradle");
        if (!Files.isRegularFile(exe)) {
            Files.createDirectories(base);
            Path archive = base.resolve(filename);
            if (!Files.exists(archive)) {
                System.out.println("Downloading official Gradle distribution: " + uri);
                Path part = base.resolve(filename+".part");
                URLConnection conn = new URL(uri).openConnection();
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(120000);
                try(InputStream in=conn.getInputStream()) { Files.copy(in,part,StandardCopyOption.REPLACE_EXISTING); }
                Files.move(part,archive,StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Extracting " + filename);
            try(ZipInputStream zin = new ZipInputStream(Files.newInputStream(archive))) {
                ZipEntry entry;
                while ((entry=zin.getNextEntry())!=null) {
                    Path target = base.resolve(entry.getName()).normalize();
                    if (!target.startsWith(base)) throw new IOException("Unsafe ZIP path");
                    if (entry.isDirectory()) Files.createDirectories(target);
                    else { Files.createDirectories(target.getParent());Files.copy(zin,target,StandardCopyOption.REPLACE_EXISTING); }
                    zin.closeEntry();
                }
            }
            if (!isWindows()) exe.toFile().setExecutable(true);
        }
        if (!Files.isRegularFile(exe)) throw new FileNotFoundException("Gradle executable missing after extraction: " + exe);
        ArrayList<String> command = new ArrayList<>();
        if(isWindows()) {command.add("cmd.exe");command.add("/c");}
        command.add(exe.toString());
        command.addAll(Arrays.asList(args));
        Process child = new ProcessBuilder(command).directory(project.toFile()).inheritIO().start();
        int exit=child.waitFor();
        System.exit(exit);
    }
    private static boolean isWindows(){return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");}
}
