package com.apperall.gabe.tvguide.Model;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by gabe on 27/08/14.
 */
public class Programme {



    public static final String CATEGORY = "category";
    public static final String TITLE = "title";
    public static final String START = "start";
    public static final String STOP = "stop";
    public static final String LENGTH = "length";
    public static final String CHANNEL = "channel";
    public static final String CHANNELID = "channelid";
    public static final String DESC = "desc";
    public static final String SHOW = "show";
    public static final String URISTR = "uri";
    public static final String SOURCE = "source";
    public static final String ID = "ID";


    public static final String NO_URI = "NO_URI";

    private String category;
    private String title;
    private Date start;
    private Date stop;
    private int length;
    private String channel;
    private String channelId;
    private String desc;
    private boolean show;
    private String uriStr = NO_URI;
  //  private String source;
    private String _id;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date date) {
        this.start = date;
    }

    public ContentValues asContentValues() {
        ContentValues values = new ContentValues();

        values.put(CATEGORY, this.getCategory());
        values.put(TITLE, this.getTitle());
        values.put(START, this.getStart().getTime());
        values.put(STOP, this.getStop().getTime());
        values.put(LENGTH, this.getLength());
        values.put(CHANNEL, this.getChannel());
        values.put(CHANNELID, this.getChannelId());
        values.put(DESC, this.getDesc());
        values.put(SHOW, this.getShow());
        values.put(URISTR, this.getUriStr());
        //values.put(SOURCE, this.getSource());
        //values.put(ID, this.get_id());

        return values;
    }
    public Programme() {

    }

    public Programme(ContentValues values) {
        this.category = values.getAsString(CATEGORY);
        this.title = values.getAsString(TITLE);
        this.setStart(new Date(values.getAsLong(START)));
        this.setStop(new Date(values.getAsLong(STOP)));
        this.setLength(values.getAsInteger(LENGTH));
        this.setChannel(values.getAsString(CHANNEL));
        this.setChannelId(values.getAsString(CHANNELID));
        this.setDesc(values.getAsString(DESC));
        this.setShow(values.getAsBoolean(SHOW));

        //if (values.containsKey("URISTR")) {
        //    this.setUriStr(values.getAsString(URISTR));
        //}
      //  this.setSource(values.getAsString(SOURCE));
        this.set_id(values.getAsString(ID));

    }

    public void setFromJSON (JSONObject jsonObject) {
        try {
            setCategory(jsonObject.getString("category"));
            setTitle(jsonObject.getString("title"));
            setStart(jsonObject.getString("start"));
            setStop(jsonObject.getString("stop"));
            set_id(jsonObject.getString("_id"));
            setDesc(jsonObject.getString("desc"));
            setLength(jsonObject.getInt("length"));
            setShow(jsonObject.getBoolean("show"));
            setChannel(jsonObject.getString("channel"));
            if (jsonObject.has("uri")) {
                setUriStr(jsonObject.getString("uri"));
            }
           // setSource(jsonObject.getString("source"));
        } catch (JSONException e) {
            Log.e("Programme", "Error reading from JSON: "+e.getMessage());
        }

    }


    public void setStart(String start) {


        //Log.i("Programme", "start time: "+start);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            this.start = format.parse(start);;
            //Log.i("Programme", "stored in db as: "+ this.start.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public Date getStop() {
        return stop;
    }


    public void setStop (Date date) {
        this.stop = date;
    }

    public String getStartTime () {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());

        return format.format(this.start);

    }

    public String getStartDateStr () {
        SimpleDateFormat format = new SimpleDateFormat("EE dd MMM");

        return format.format(this.start);

    }


    public String getStopTime () {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");

        return format.format(this.stop);

    }


    public void setStop(String stop) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            this.stop  = format.parse(stop);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean getShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getUriStr() {

        if (uriStr!=null) {
            return uriStr;
        } else {
            return NO_URI;
        }


    }

    public void setUriStr(String uriStr) {
        this.uriStr = uriStr;
    }

    public String getSource() {
        return "doesn't matter";
    }

    public void setSource(String source) {
        /*this.source = source;*/
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


}
