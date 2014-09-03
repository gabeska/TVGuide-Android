package com.apperall.gabe.tvguide.Model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.UUID;

/**
 * Created by gabe on 01/09/14.
 */

@ParseClassName("Query")
public class Query extends ParseObject {
    public String getKeyword() {
        return getString("keyword");
    }
    public void setKeyword(String keyword) {
        put("keyword", keyword);
    }

    public void setUuidString() {
        UUID uuid = UUID.randomUUID();
        put("uuid", uuid.toString());
    }

    public String getUuidString() {
        return getString("uuid");
    }

    public static ParseQuery<Query> getQuery() {
        return ParseQuery.getQuery(Query.class);
    }

}
