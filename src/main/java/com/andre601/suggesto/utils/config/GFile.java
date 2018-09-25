package com.andre601.suggesto.utils.config;

import com.andre601.suggesto.SuggestoBot;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/*
 * |------------------------------------------------------------------|
 *   Code from Gary (GitHub: https://github.com/help-chat/Gary)
 *
 *   Used with permission of PiggyPiglet
 *   Original Copyright (c) PiggyPiglet 2018 (https://piggypiglet.me)
 * |------------------------------------------------------------------|
 */

import static com.andre601.suggesto.SuggestoBot.getLogger;

public class GFile {

    private ConfigUtil cutil = new ConfigUtil();
    private Map<String, File> gFiles;

    public void make(String name, String externalPath, String internalPath){
        if(gFiles == null)
            gFiles = new HashMap<>();

        File file = new File(externalPath);
        String[] externalSplit = externalPath.split("/");

        try{
            if(!file.exists()){
                if((externalSplit.length == 2 && !externalSplit[0].equals(".")) || (externalSplit.length >= 3 &&
                externalSplit[0].equals("."))){
                    if(!file.getParentFile().mkdirs()){
                        getLogger().error("Failed to create directory " + externalSplit[0]);
                        return;
                    }
                }
                if(file.createNewFile()){
                    if(cutil.exportResource(SuggestoBot.class.getResourceAsStream(internalPath), externalPath)){
                        getLogger().info(name + " successfully created!");
                    }else{
                        getLogger().error("Failed to create " + name);
                    }
                }
            }else{
                getLogger().info(name + " successfully loaded!");
                gFiles.put(name, file);
            }
        }catch (Exception ex){
            getLogger().error("Error while creating/loading " + name, ex);
        }
    }

    public String getItem(String fileName, String item){
        File file = gFiles.get(fileName);
        try{
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(file));
            Map<String, String> data = gson.fromJson(reader, LinkedTreeMap.class);
            if(data.containsKey(item))
                return data.get(item);
        }catch (Exception ex){
            getLogger().error("Error while rading file " + fileName, ex);
        }
        return item + " not found in " + fileName;
    }

}
