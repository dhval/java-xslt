package com.dhval.utils;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Recursively find all files in path of provided type.
     * @param path
     * @param type
     * @return
     * @throws IOException
     */
    public static String[] allFilesByType(String path, String type) throws IOException{

        if(!new File(path).exists()) {
            return new String[]{};
        }

        List<String> directories = new ArrayList<>();

        // find all files in path.
        String[] files = new File(path).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith("." + type))
                    return true;
                try {
                    if (isDirectoryPresent(path + "/" + name)) {
                        directories.add(path + "/" + name);
                    }
                } catch (IOException ioe) {
                    LOG.warn(ioe.getMessage());
                }
                return false;
            }
        });
        List<String> list = new ArrayList<>();
        for(String f: files) {
            list.add(path + "/" + f);
        }
        for (String directory: directories) {
            list.addAll(Arrays.asList(allFilesByType(directory, type)));
        }

        return list.toArray(new String[0]);
    }

    public static void writeToDisk(String path, String content) throws IOException {
        try(BufferedWriter w = new BufferedWriter(new FileWriter(path,true)))
        {
            w.write(content);
        } catch(IOException e) {
            throw e;
        }
    }

    public static boolean isFilePresent(String fileName) throws IOException {
        if(new File(fileName).exists())
            return true;
        return false;
    }

    public static boolean isDirectoryPresent(String directory) throws IOException {
        File dir = new File(directory);
        return dir.isDirectory();
    }

    public static String readFile(String fileName) throws IOException {
        // String content = new String(Files.readAllBytes(Paths.get("duke.java")));
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
                return sb.toString();
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }  finally {
                br.close();
            }
            return null;
    }

    public static void createDirectory(String path) {
        File directory = new File(path);
        if (! directory.exists()){
            directory.mkdirs();
        }
    }

    public static String searchFile(String directory, String fileStartsWith) {
        File dir = new File(directory);
        String name = null;
        if(!dir.isDirectory()) throw new IllegalStateException("is not a directory !!! : " + directory);
        final java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(fileStartsWith + ".*");
        for(File file : dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pattern.matcher(pathname.getName()).find();
            }
        })) {
            name = file.getAbsolutePath();
        }
        return name;
    }

    public static String addFileExtension(String outFile, String ext) {
        return FilenameUtils.getFullPath(outFile) + FilenameUtils.getBaseName(outFile) + ext + "." + FilenameUtils.getExtension(outFile);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(searchFile("/Users/dhval/projects/ibmmq/data/receive", "414d51204a4e45542e414f50432e5155b1f1f75820f94431"));
        final String fileName = "/Users/dhval/projects/ibmmq/test.csv";
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }  finally {
            br.close();
        }
    }
}
