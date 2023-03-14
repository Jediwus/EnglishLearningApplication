package com.jediwus.learningapplication.myUtil;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 文件处理相关工具类
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    @Nullable
    public static String readJsonData(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        File file = new File(MyApplication.getContext().getFilesDir(), fileName);
        if (!file.exists()) { // 若文件不存在
            return null;
        }
        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String string = stringBuilder.toString().replace("{\"wordRank\"", ",{\"wordRank\"");

        return "[" + string.substring(1) + "]";
    }


    /**
     * 解压文件.
     *
     * @param archive       解压文件源路径
     * @param decompressDir 解压文件目标路径
     * @param isDeleteZip   解压完毕是否删除解压文件
     * @throws IOException the io exception
     */
    public static void unZipFile(String archive, String decompressDir, boolean isDeleteZip) throws IOException {
        BufferedInputStream bufferedInputStream;
        ZipFile zipFile = new ZipFile(archive);
        Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry zipEntry = enumeration.nextElement();
            String entryName = zipEntry.getName();
            String path = decompressDir + "/" + entryName;
            if (zipEntry.isDirectory()) {
                File decompressDirFile = new File(path);
                if (!decompressDirFile.exists()) {
                    boolean wasSuccessful = decompressDirFile.mkdirs();
                    Log.d(TAG, "unZipFile: decompressDirFile.mkdirs() " + wasSuccessful);
                }
            } else {
                if (decompressDir.endsWith(".zip")) {
                    decompressDir = decompressDir.substring(0, decompressDir.lastIndexOf(".zip"));
                }
                File fileDirFile = new File(decompressDir);
                if (!fileDirFile.exists()) {
                    boolean wasSuccessful = fileDirFile.mkdirs();
                    Log.d(TAG, "unZipFile: fileDirFile.mkdirs() " + wasSuccessful);
                }
                String substring = entryName.substring(entryName.lastIndexOf("/") + 1);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                        new FileOutputStream(decompressDir + "/" + substring));
                bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                byte[] readContent = new byte[1024];
                int readCount = bufferedInputStream.read(readContent);
                while (readCount != -1) {
                    bufferedOutputStream.write(readContent, 0, readCount);
                    readCount = bufferedInputStream.read(readContent);
                }
                bufferedOutputStream.close();
            }
        }
        zipFile.close();
        if (isDeleteZip) {
            File zipFile_2 = new File(archive);
            if (zipFile_2.exists() && zipFile_2.getName().endsWith(".zip")) {
                boolean wasSuccessful = zipFile_2.delete();
                Log.d(TAG, "unZipFile: zipFile_2.delete() " + wasSuccessful);
            }
        }
    }

    @NonNull
    public static String readFileToString(String filePath, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileInputStream inputStream = new FileInputStream(filePath + "/" + fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    /**
     * 将 byte 数组转换成文件.
     *
     * @param bytes    the bytes
     * @param filePath the file path
     * @param fileName the file name
     */
    public static void getFileByBytes(byte[] bytes, String filePath, String fileName) {
        File dir = new File(filePath);
        if (!dir.exists()) { // 判断文件目录是否存在
            boolean wasSuccessful = dir.mkdirs();
            Log.d(TAG, "getFileByBytes: 没有这个目录,创建文件根目录：" + wasSuccessful);
        }
        File file = new File(filePath + "//" + fileName);

        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
            bufferedOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 将字符串保存到文件中.
     *
     * @param content  the content
     * @param filePath the file path
     * @param fileName the file name
     */
    public static void saveStringToFile(String content, String filePath, String fileName) {
        File dir = new File(filePath);
        if (!dir.exists()) { // 判断文件目录是否存在
            boolean wasSuccessful = dir.mkdirs();
            Log.d(TAG, "getFileByBytes: 没有这个目录,创建文件根目录：" + wasSuccessful);
        }
        File file = new File(filePath + "//" + fileName);

        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
            bufferedOutputStream.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断文件是否存在.
     *
     * @param filePath the file path
     * @param fileName the file name
     * @return the boolean
     */
    public static boolean fileIsExit(String filePath, String fileName) {
        File file = new File(filePath + fileName);
        return file.exists();
    }

    /**
     * 列出目录下所有文件.
     *
     * @param filePath the file path
     * @return the string [ ]
     */
    public static String[] allFiles(String filePath) {
        File file = new File(filePath);
        return file.list();
    }


    /**
     * 压缩.
     *
     * @param bitmap the bitmap
     * @param size   the size
     * @return the byte [ ]
     */
    public static byte[] bitmapCompress(Bitmap bitmap, int size) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到 byteArrayOutputStream 中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        int quality = 100;
        // 循环判断如果压缩后图片是否大于 100kb，大于继续压缩，这里的要求数值可根据需求设置
        while (byteArrayOutputStream.toByteArray().length / 1024 > size) {
            // 清空 byteArrayOutputStream
            byteArrayOutputStream.reset();
            // 这里压缩 quality%，把压缩后的数据存放到 byteArrayOutputStream 中
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
            if (quality - 10 <= 0) {
                break;
            } else { // 每次都减少10
                quality -= 10;
            }
        }
        //转为字节数组返回
        return byteArrayOutputStream.toByteArray();
    }


}
