package com.shpp.glubchenko.learning.downloadAndResize_4_0;

/**
 * Created by glebl on 20.09.2015.
 */
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;

public class Task {

    public String  pictureName;
    public URL url;
    public File file;
    public boolean killTask = false;
    public int width, height;
    public Exception e = null;
    public static String finalForlder, URLPackName;
    public static int picturesNumber = 0;

    public Task() {}

    public Task(String link) {

        try {

            url = new URL(link);

            JsonElement jsonElement = new JsonParser().parse(new FileReader(URLPackName));
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonArray jsonArray = jsonObject.get("URLlist").getAsJsonArray();
            jsonObject = jsonArray.get(picturesNumber++).getAsJsonObject();

            width = jsonObject.get("Widht").getAsInt();
            height = jsonObject.get("Height").getAsInt();

        }catch (Exception e) {

            this.e = e;
            System.err.println(link + " - Damaged URL, task wasn't added ");

        }
    }

    public static ArrayList initializeTaskArrayAndConstants(String[] args) {

        ArrayList<Task> taskArray = new ArrayList<>();

        if(args.length != 0)
            URLPackName = args[0];
        else
            URLPackName = "URLpack.txt";

        try {

            JsonElement jsonElement = new JsonParser().parse(new FileReader(URLPackName));
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            finalForlder = jsonObject.get("finalFolderPath").getAsString();

            JsonArray jsonArray = jsonObject.get("URLlist").getAsJsonArray();
            System.out.println(jsonArray.size() + " objects was found");

            for (int i = 0; i < jsonArray.size(); i++) {

                JsonObject arr = jsonArray.get(i).getAsJsonObject();
                Task task = new Task(arr.get("URL").getAsString());
                task.pictureName = "image_" + (i+1) + ".jpg";
                taskArray.add(task);
            }

            System.out.println(taskArray.size() + " tasks was added");

        }catch (Exception e) {
            System.err.println("File with links wasn't found");
        }

        return taskArray;
    }
}

class KillTask extends Task {

    public KillTask() {
        killTask = true;
    }

}
