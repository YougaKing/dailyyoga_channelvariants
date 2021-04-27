package com.dailyyoga.plugin.channelvariants.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileOperation {

    private static final int BUFFER = 8192;

    public static boolean deleteFile(String filePath) {
        if (filePath == null) {
            return true;
        }

        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    public static boolean deleteDir(File file) {
        if (file == null || (!file.exists())) {
            return false;
        }
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteDir(files[i]);
            }
        }
        file.delete();
        return true;
    }

    public static boolean mkdirs(File dir) {
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }

    public static boolean checkDirectory(File dir) {
        deleteDir(dir);
        return mkdirs(dir);
    }

    public static HashMap<String, Integer> unZipAPk(File apkFile,
                                                    File resourcesDir) throws IOException {
        checkDirectory(resourcesDir);
        ZipFile zipFile = new ZipFile(apkFile);
        Enumeration<ZipEntry> emu = (Enumeration<ZipEntry>) zipFile.entries();
        HashMap<String, Integer> compress = new HashMap<>();
        try {
            while (emu.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) emu.nextElement();
                if (entry.isDirectory()) {
                    new File(resourcesDir, entry.getName()).mkdirs();
                    continue;
                }
                BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));

                File file = new File(resourcesDir.getAbsolutePath() + File.separator + entry.getName());

                File parent = file.getParentFile();
                if (parent != null && (!parent.exists())) {
                    parent.mkdirs();
                }
                String compatibaleresult = entry.getName();
                if (compatibaleresult.contains("\\")) {
                    compatibaleresult = compatibaleresult.replace("\\", "/");
                }
                compress.put(compatibaleresult, entry.getMethod());
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);

                byte[] buf = new byte[BUFFER];
                int len;
                while ((len = bis.read(buf, 0, BUFFER)) != -1) {
                    fos.write(buf, 0, len);
                }
                bos.flush();
                bos.close();
                bis.close();
            }
        } finally {
            zipFile.close();
        }
        return compress;
    }

    /**
     * zip list of file
     *
     * @param resFileList  file(dir) list
     * @param baseFolder   file(dir) base folder, we should calc relative path of resFile with base
     * @param zipFile      output zip file
     * @param compressData compress data
     * @throws IOException io exception
     */
    public static void zipFiles(Collection<File> resFileList,
                                File baseFolder,
                                File zipFile,
                                HashMap<String, Integer> compressData) throws IOException {
        ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile), BUFFER));
        for (File resFile : resFileList) {
            if (resFile.exists()) {
                if (resFile.getAbsolutePath().contains(baseFolder.getAbsolutePath())) {
                    String relativePath = baseFolder.toURI().relativize(resFile.getParentFile().toURI()).getPath();
                    // remove slash at end of relativePath
                    if (relativePath.length() > 1) {
                        relativePath = relativePath.substring(0, relativePath.length() - 1);
                    } else {
                        relativePath = "";
                    }
                    zipFile(resFile, zipOut, relativePath, compressData);
                } else {
                    zipFile(resFile, zipOut, "", compressData);
                }
            }
        }
        zipOut.close();
    }

    private static void zipFile(File resFile,
                                ZipOutputStream zipout,
                                String rootpath,
                                HashMap<String, Integer> compressData) throws IOException {
        rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator) + resFile.getName();
        if (resFile.isDirectory()) {
            File[] fileList = resFile.listFiles();
            for (File file : fileList) {
                zipFile(file, zipout, rootpath, compressData);
            }
        } else {
            final byte[] fileContents = readContents(resFile);
            if (rootpath.contains("\\")) {
                rootpath = rootpath.replace("\\", "/");
            }
            if (!compressData.containsKey(rootpath)) {
                System.err.printf(String.format("do not have the compress data path =%s in resource.asrc\n", rootpath));
                //throw new IOException(String.format("do not have the compress data path=%s", rootpath));
                return;
            }
            int compressMethod = compressData.get(rootpath);
            ZipEntry entry = new ZipEntry(rootpath);

            if (compressMethod == ZipEntry.DEFLATED) {
                entry.setMethod(ZipEntry.DEFLATED);
            } else {
                entry.setMethod(ZipEntry.STORED);
                entry.setSize(fileContents.length);
                final CRC32 checksumCalculator = new CRC32();
                checksumCalculator.update(fileContents);
                entry.setCrc(checksumCalculator.getValue());
            }
            zipout.putNextEntry(entry);
            zipout.write(fileContents);
            zipout.flush();
            zipout.closeEntry();
        }
    }

    private static byte[] readContents(final File file) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final int bufferSize = 4096;
        try {
            final FileInputStream in = new FileInputStream(file);
            final BufferedInputStream bIn = new BufferedInputStream(in);
            int length;
            byte[] buffer = new byte[bufferSize];
            byte[] bufferCopy;
            while ((length = bIn.read(buffer, 0, bufferSize)) != -1) {
                bufferCopy = new byte[length];
                System.arraycopy(buffer, 0, bufferCopy, 0, length);
                output.write(bufferCopy);
            }
            bIn.close();
        } finally {
            output.close();
        }
        return output.toByteArray();
    }
}
