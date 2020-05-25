package com.itpartners.transmissionrss;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import com.itpartners.transmissionrss.model.FeedMessage;
import com.itpartners.transmissionrss.model.Feed;

public class RSSFeedParser {
    final URL url;

    public RSSFeedParser(String feedUrl) {
        try {
            this.url = new URL(feedUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public Feed readFeed() {
        Feed feed = null;
        try {
            boolean isFeedHeader = true;
            // Set header values intial to the empty string
            String title = "";
            String link = "";
            String guid = "";

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = read();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // read the XML document
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    String localPart = event.asStartElement().getName()
                            .getLocalPart();
                    switch (localPart) {
                        case "item":
                            if (isFeedHeader) {
                                isFeedHeader = false;
                                feed = new Feed();
                            }
                            //event = eventReader.nextEvent();
                            break;
                        case FeedMessage.TITLE:
                            title = getCharacterData(event, eventReader);
                            break;
                        //case FeedMessage.LINK:
                            //link = getCharacterData(event, eventReader);
                            //break;
                        case FeedMessage.ENCLOSURE:
                            link = getAttribute(event, "url");
                            break;
                        case FeedMessage.GUID:
                            guid = getCharacterData(event, eventReader);
                            break;
                    }
                } else if (event.isEndElement()) {
                    if (event.asEndElement().getName().getLocalPart() == ("item")) {
                        FeedMessage message = new FeedMessage();
                        message.setGuid(guid);
                        message.setLink(link);
                        message.setTitle(title);
                        feed.getMessages().add(message);
                        //event = eventReader.nextEvent();
                        continue;
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return feed;
    }

    private String getAttribute(XMLEvent event, String attribute) {
        String result = "";
        Iterator<Attribute> attribue = event.asStartElement().getAttributes();
        while(attribue.hasNext()){
            Attribute myAttribute = attribue.next();
            if(myAttribute.getName().toString().equals(attribute)){
                result = myAttribute.getValue();
            }
        }
        return result;
    }

    private String getCharacterData(XMLEvent event, XMLEventReader eventReader)
            throws XMLStreamException {
        String result = "";
        event = eventReader.nextEvent();
        if (event instanceof Characters) {
            result = event.asCharacters().getData();
        }
        return result;
    }

    private InputStream read() {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

