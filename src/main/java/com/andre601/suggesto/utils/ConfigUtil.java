package com.andre601.suggesto.utils;

import java.io.FileOutputStream;
import java.util.Properties;

public class ConfigUtil {

    Properties configFile;

    public ConfigUtil(){
        this.configFile = new Properties();
        try{
            this.configFile.load(getClass().getClassLoader().getResourceAsStream("config.cfg"));
        }catch (Exception ex){
            System.out.println("[WANR] Error while creating config-file:\n");
            ex.printStackTrace();
        }
    }

    public void create(){
        Properties prop = new Properties();
        try{
            prop.setProperty("token", "");
            prop.setProperty("ticketID", "0");
            prop.setProperty("suggestID", "0");
            prop.store(new FileOutputStream("config.cfg"), null);
        }catch (Exception ex){
            System.out.println("[WANR] Error while creating config-file:");
            ex.printStackTrace();
        }
    }

    public String getProperty(String key){
        String value = this.configFile.getProperty(key);
        return value;
    }

    public void setTicketID(int id){
        this.configFile.setProperty("ticketID", String.valueOf(id));
        try{
            this.configFile.store(new FileOutputStream("config.cfg"), null);
        }catch (Exception ex){
            System.out.println("[WANR] Error while saving the file:");
            ex.printStackTrace();
        }
    }

    public void setSuggestID(int id){
        this.configFile.setProperty("suggestID", String.valueOf(id));
        try{
            this.configFile.store(new FileOutputStream("config.cfg"), null);
        }catch (Exception ex){
            System.out.println("[WANR] Error while saving the file:");
            ex.printStackTrace();
        }
    }
}
