package com.itpartners.transmissionrss;

import com.itpartners.transmissionrss.model.Feed;
import com.itpartners.transmissionrss.model.FeedMessage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.*;

public class Main {
    static private FileHandler fileTxt;
    static private SimpleFormatter formatterTxt;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) {
        String rssURL = "";
        String dataProperties = "";
        String transUser = "";
        String transPwd = "";
        String logFile = "";
        Map<String, String> processedShows = new HashMap<String, String>();

        try (InputStream input = new FileInputStream(args[0])) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);
            rssURL = prop.getProperty("rss.url");
            dataProperties = prop.getProperty("data.properties");
            transUser = prop.getProperty("transmission.user");
            transPwd = prop.getProperty("transmission.password");
            logFile = prop.getProperty("log.file");

            // get the property value and print it out
            /*System.out.println(prop.getProperty("db.url"));
            System.out.println(prop.getProperty("db.user"));
            System.out.println(prop.getProperty("db.password"));*/

            loadProcessedShows(processedShows, dataProperties);
            setupLog(logFile);

        } catch (IOException ex) {
            System.out.println("Failed reading parameters");
            ex.printStackTrace();
            return;
        }

        LOGGER.info("Start processing");
        RSSFeedParser parser = new RSSFeedParser(rssURL);
        Feed feed = parser.readFeed();
        //System.out.println(feed);
        for (FeedMessage message : feed.getMessages()) {
            //System.out.println(message);
            if (!processedShows.containsKey(message.getGuid())) {
                //String cmd = "transmission-remote -n '" + transUser + ":" + transPwd + "' -a '" + message.getLink() + "'";
                String auth = transUser + ":" + transPwd;
                String magnet = message.getLink();
                //System.out.println(cmd);

                try {
                    Process p = Runtime.getRuntime().exec(new String[]{"transmission-remote", "-n", auth, "-a", magnet});
                    p.waitFor();
                    if (p.exitValue() != 0) {
                        //System.out.println("Failed download show: " + message.getTitle());
                        LOGGER.info("Failed download show: " + message.getTitle());

                        String line;
                        BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                        while((line = error.readLine()) != null){
                            //System.out.println(line);
                            LOGGER.info(line);
                        }
                        error.close();
                    }
                    else {
                        //System.out.println("Queued show : " + message.getTitle() + " - Exit code:" + p.exitValue());
                        LOGGER.info("Queued show : " + message.getTitle() + " - Exit code:" + p.exitValue());
                        processedShows.put(message.getGuid(), "OK");
                    }

                    p.destroy();
                }
                catch (Exception e) {
                    //System.out.println("Failed download show: " + message.getTitle());
                    LOGGER.info("Failed download show: " + message.getTitle());
                    LOGGER.info(e.getMessage());
                    e.printStackTrace();
                }
            }
            else {
                //System.out.println("Skipped show: " + message.getTitle());
                LOGGER.info("Skipped show: " + message.getTitle());
            }
        }

        try {
            saveProcessedShows(processedShows, dataProperties);
        }
        catch (Exception e) {
            //System.out.println("Failed saving processed shows");
            LOGGER.info("Failed saving processed shows");
            e.printStackTrace();
        }
        LOGGER.info("End processing");
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

    static public void setupLog(String logFileName) throws IOException {
        cycleLogFile(logFileName);
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-6s %2$s %5$s%6$s%n");
        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        // suppress the logging output to the console
        /*Logger rootLogger = Logger.*getLogger*("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }*/

        logger.setLevel(Level.INFO);
        fileTxt = new FileHandler(logFileName, true);

        // create a TXT formatter
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);

        LOGGER.setLevel(Level.INFO);
    }

    static public void cycleLogFile(String logFileName) {
        File file =new File(logFileName);
        if (file.exists() && (file.length()/1024) > 1000  ) {
            file.delete();
        }
    }
}
