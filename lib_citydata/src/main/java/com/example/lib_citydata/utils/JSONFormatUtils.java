package com.example.lib_citydata.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JSONFormatUtils {

    public static  <T> void jsonWriter(T data, String filePath) {
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        try(FileWriter writer = new FileWriter(filePath)) {
//            gson.toJson(data, writer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static <T> void jsonWriter(List<T> data, String filePath) {
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        try(FileWriter writer = new FileWriter(filePath)) {
//            gson.toJson(data, writer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
