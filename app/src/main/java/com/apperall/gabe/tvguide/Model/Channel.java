package com.apperall.gabe.tvguide.Model;

import android.content.ContentValues;

import com.parse.ParseObject;

/**
 * Created by gabe on 28/08/14.
 */
public class Channel {
    private String iconUrl;
    private String source;
    private String name;
    private String extId;
    private int _id;
    private int sortOrder;
    private String objectId;


    public static final String C_CHANNEL_ID = "_id";
    public static final String C_CHANNEL_OBJECTID = "objectId";
    public static final String C_CHANNEL_ICONURL = "iconURL";
    public static final String C_CHANNEL_NAME = "name";
    public static final String C_CHANNEL_SOURCE = "source";
    private static final String C_CHANNEL_EXT_ID = "ext_id";



    public Channel() {}

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public static Channel fromParseObject(ParseObject po) {
        Channel ch = new Channel();
        if (po.has("iconURL"))

            ch.iconUrl = po.getString("iconURL");

        ch.source = po.getString("source");
        ch.name = po.getString("name");
        ch.extId = po.getString("ch_id");
        ch.objectId = po.getObjectId();

        return ch;
    }

    public ContentValues asContentValues() {
        ContentValues values = new ContentValues();
        values.put(C_CHANNEL_OBJECTID, objectId);
        values.put(C_CHANNEL_ICONURL, iconUrl);
        values.put(C_CHANNEL_NAME, name);
        values.put(C_CHANNEL_SOURCE, source);
        values.put(C_CHANNEL_EXT_ID, extId);

        return values;
    }

    public Channel(ContentValues values) {
        this.setObjectId(values.getAsString(C_CHANNEL_OBJECTID));

        this.setIconUrl(values.getAsString(C_CHANNEL_ICONURL));
        this.setName(values.getAsString(C_CHANNEL_NAME));
        this.setSource(values.getAsString(C_CHANNEL_SOURCE));
        this.setExtId(values.getAsString(C_CHANNEL_EXT_ID));

    }

    @Override
    public String toString() {
        return String.format("Channel: name = %s extId = %s", name, extId);
    }


}
