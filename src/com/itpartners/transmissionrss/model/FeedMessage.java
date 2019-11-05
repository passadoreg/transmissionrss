package com.itpartners.transmissionrss.model;

public class FeedMessage {
    String title;
    String link;
    String guid;
    String pubDate;
    String description;
    String tvshow_id;
    String tvexternal_id;
    String tvshow_name;
    String tvepisode_id;
    String tvraw_title;
    String tvinfo_hash;
    String enclosure;

    public static final String TITLE = "title";
    public static final String LINK = "link";
    public static final String GUID = "guid";
    public static final String ENCLOSURE = "enclosure";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTvshow_id() {
        return tvshow_id;
    }

    public void setTvshow_id(String tvshow_id) {
        this.tvshow_id = tvshow_id;
    }

    public String getTvexternal_id() {
        return tvexternal_id;
    }

    public void setTvexternal_id(String tvexternal_id) {
        this.tvexternal_id = tvexternal_id;
    }

    public String getTvshow_name() {
        return tvshow_name;
    }

    public void setTvshow_name(String tvshow_name) {
        this.tvshow_name = tvshow_name;
    }

    public String getTvepisode_id() {
        return tvepisode_id;
    }

    public void setTvepisode_id(String tvepisode_id) {
        this.tvepisode_id = tvepisode_id;
    }

    public String getTvraw_title() {
        return tvraw_title;
    }

    public void setTvraw_title(String tvraw_title) {
        this.tvraw_title = tvraw_title;
    }

    public String getTvinfo_hash() {
        return tvinfo_hash;
    }

    public void setTvinfo_hash(String tvinfo_hash) {
        this.tvinfo_hash = tvinfo_hash;
    }

    public String getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(String enclosure) {
        this.enclosure = enclosure;
    }

    @Override
    public String toString() {
        return "FeedMessage [title=" + title + ", description=" + description
                + ", link=" + link + ", guid=" + guid
                + "]";
    }

}
