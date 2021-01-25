package com.ziyiou.file.util;

import java.io.File;
import java.util.List;

public class FileUtil {

    /*public static HashMap<String, String> typeMap = new HashMap<String, String>() {
        {
            put("jpg", "img");
            put("png", "img");
            put("jpeg", "img");
            put("mp4", "video");
        }
    };*/

    public static String getType(String str){
        switch (str){
            case "jpg":
            case "png":
            case "jpeg":return "img";
            case "mp4": return "video";
            default:return "other";
        }
    }
//
//    public static List getFiles(ArrayList<MyFile> list, File file){
//        if (file.exists()){
//            for (File f : file.listFiles()) {
//                if (f.isDirectory()){
//                    list.add(new MyFile(getFiles(new ArrayList(),f),f.getName()));
//                }else {
//                    list.add(new MyFile(f.getName(),getType(f.getName().substring(f.getName().lastIndexOf(".")+1).toLowerCase())));
//                }
//            }
//            return list;
//        }
//        return list;
//    }

    private List<File> listFiles(File root, List<File> list) {
        for (File file : root.listFiles()) {
            if (file.isDirectory()) {
                listFiles(file, list);
            } else if (file.isFile()) {
                list.add(file);
            }
        }
        return list;
    }

}
