package com.dtl.gemini.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by xxg on 2016/12/23 0023.
 */
public class FileManager {

    private static String TAG = "FileManager";

    /***
     * 追加
     * @param pathName
     * @param fileName
     * @param strContent
     */
    public synchronized static void writeFileToSD( String formClass , String pathName,  String fileName, String strContent) {
        Log.e(TAG,"调用保存文件的类是 ： "+formClass);
        //String abc = strContent+"\n";
        String sdStatus = Environment.getExternalStorageState();
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Log.e(TAG, "SD card is not avaiable/writeable right now.");
            return;
        }
        try {
            File path = new File(pathName);
            File file = new File(pathName + fileName);
            if(!path.exists()) {
                Log.e(TAG, "Create the path:" + pathName);
                path.mkdirs();
            }
            if( !file.exists()) {
                Log.e(TAG, "Create the file:" + fileName);
                file.createNewFile();
            }
            Log.e(TAG, "开始写文件。。。"+pathName + fileName);
            FileOutputStream stream = new FileOutputStream(file,true);
            //String s = "this is a test string writing to file.";
            //byte[] buf = s.getBytes();
            byte[] buf = strContent.getBytes();
            stream.write(buf);
            stream.close();
        } catch(Exception e) {
            Log.e(TAG, "写文件失败 : "+e.toString());
        }
    }

    /*****
     * 覆盖
     * @param pathName
     * @param fileName
     * @param strContent
     */
    public synchronized static void writeFileToSDCover(String formClass , String pathName,  String fileName, String strContent) {
        Log.e(TAG,"调用保存文件的类是 ： "+formClass);

        //String abc = strContent+"\n";
        String sdStatus = Environment.getExternalStorageState();
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Log.e(TAG, "SD card is not avaiable/writeable right now.");
            return;
        }
        try {
            File path = new File(pathName);
            File file = new File(pathName + fileName);
            if(!path.exists()) {
                Log.e(TAG, "Create the path:" + pathName);
                path.mkdirs();
            }
            if( !file.exists()) {
                Log.e(TAG, "Create the file:" + fileName);
                file.createNewFile();
            }
            Log.e(TAG, "开始写文件。。。"+pathName + fileName);
            FileOutputStream stream = new FileOutputStream(file,false);
            //String s = "this is a test string writing to file.";
            //byte[] buf = s.getBytes();
            byte[] buf = strContent.getBytes();
            stream.write(buf);
            stream.close();
        } catch(Exception e) {
            Log.e(TAG, "写文件失败 : "+e.toString());
        }
    }

    /***
     * 读文件
     * @return
     */
    static int length = 0;
    static int available = 0;
    public synchronized static String readFile(String filePath){
        try {
            File file = new File(filePath);
            if(!file.exists()) {
                return null;
            }

            Log.e(TAG, "开始读文件。。。"+filePath);
            FileInputStream stream = new FileInputStream(file);
            if((available = stream.available()) == 0){
                return null;
            }
            byte[] b=new byte[available];//创建一个字节数组，数组长度与file中获得的字节数相等
            Log.e(TAG, "读文件长度："+available);
            while((length = stream.read(b))!=-1){
                Log.e(TAG,"读文件中 ： length = "+length);
            };
            stream.close();
            return new String (b);
        } catch(Exception e) {
            Log.e(TAG, "读文件失败 : "+e.toString());
            return null;
        }
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName
     *            要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public synchronized static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            Log.e(TAG,"删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public synchronized static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e(TAG,"删除单个文件" + fileName + "成功！");
                return true;
            } else {
                Log.e(TAG,"删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            Log.e(TAG,"删除单个文件：" + fileName + "不存在！");
            return true;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public synchronized static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            Log.e(TAG,"删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        int size = files.length;
        for (int i = 0; i < size; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            Log.e(TAG,"删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            Log.e(TAG,"删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }

}
