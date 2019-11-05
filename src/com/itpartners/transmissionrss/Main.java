package com.itpartners.transmissionrss;

import com.itpartners.transmissionrss.model.Feed;
import com.itpartners.transmissionrss.model.FeedMessage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        String rssURL = "";
        String dataProperties = "";
        Map<String, String> processedShows = new HashMap<String, String>();

        try (InputStream input = new FileInputStream(args[0])) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);
            rssURL = prop.getProperty("rss.url");
            dataProperties = prop.getProperty("data.properties");

            // get the property value and print it out
            /*System.out.println(prop.getProperty("db.url"));
            System.out.println(prop.getProperty("db.user"));
            System.out.println(prop.getProperty("db.password"));*/

            loadProcessedShows(processedShows, dataProperties);

        } catch (IOException ex) {
            System.out.println("Failed reading parameters");
            ex.printStackTrace();
            return;
        }


        RSSFeedParser parser = new RSSFeedParser(rssURL);
        Feed feed = parser.readFeed();
        //System.out.println(feed);
        for (FeedMessage message : feed.getMessages()) {
            System.out.println(message);
            if (!processedShows.containsKey(message.getGuid())) {
                String cmd = "transmission-remote -a \"" + message.getLink() + "\"";
                System.out.println(cmd);
                cmd = "ls -l";
                try {
                    Process p = Runtime.getRuntime().exec(cmd);
                    p.waitFor();
                    System.out.println ("Queued show : " + message.getTitle() + " - Exit code:" + p.exitValue());
                    p.destroy();
                    processedShows.put(message.getGuid(), "OK");
                }
                catch (Exception e) {
                    System.out.println("Failed download show: " + message.getTitle());
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("Skipped show: " + message.getTitle());
            }
        }

        try {
            saveProcessedShows(processedShows, dataProperties);
        }
        catch (Exception e) {
            System.out.println("Failed saving processed shows");
            e.printStackTrace();
        }
    }

    private static void saveProcessedShows(Map<String, String> shows, String dataFile) throws IOException  {
        Properties properties = new Properties();

        for (Map.Entry<String,String> entry : shows.entrySet()) {
            properties.put(entry.getKey(), entry.getValue());
        }

        properties.store(new FileOutputStream(dataFile), null);
    }

    private static void loadProcessedShows(Map<String, String> shows, String dataFile) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(dataFile));

        for (String key : properties.stringPropertyNames()) {
            shows.put(key, properties.get(key).toString());
        }
    }
}
