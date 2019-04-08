package com.vonallin.lib.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * add by zhaoshuchao
 */
public class JsonUtils {
    private static final String TAG = JsonUtils.class.getSimpleName();

    public static String optString(JSONObject json, String... path) {
        for (int i = 0; i < path.length; i++) {
            if (json == null) {
                return null;
            }
            String childName = path[i];
            if (i != path.length - 1) {
                json = json.optJSONObject(childName);
            } else {
                return json.optString(childName);
            }
        }
        return null;
    }

    /**
     * 不用catch的{@link JSONObject#JSONObject(JSONObject, String[])}
     *
     * @param source
     * @param pickNames
     * @return
     */
    public static JSONObject pickCopy(JSONObject source, String... pickNames) {
        JSONObject target = new JSONObject();
        for (String name : pickNames) {
            try {
                Object value = source.opt(name);
                if (name != null && value != null) {
                    target.put(name, value);
                }
            } catch (JSONException e) {
            }
        }
        return target;
    }

    /**
     * 深拷贝
     *
     * @param source
     * @return
     */
    public static JSONObject deepCopy(JSONObject source) {
        JSONArray sourceNames = source.names();
        if (sourceNames == null) {
            return new JSONObject();
        }

        String[] names = new String[sourceNames.length()];
        for (int i = 0; i < sourceNames.length() && i < names.length; i++) {
            try {
                names[i] = sourceNames.getString(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return pickCopy(source, names);
    }

    /**
     * 创建{@link JSONObject}而不需要catch
     *
     * @return
     */
    public static JSONBuilder build() {
        return new JSONBuilder();
    }

    public static JSONBuilder build(String json) {
        return new JSONBuilder(json);
    }

    public static JSONBuilder build(JSONObject json) {
        return new JSONBuilder(json);
    }

    public static class JSONBuilder implements Jsonable {
        private JSONObject mJson;

        private JSONBuilder() {
            mJson = new JSONObject();
        }

        private JSONBuilder(String json) {
            try {
                mJson = new JSONObject(json);
            } catch (JSONException e) {
            }
        }

        public JSONBuilder(JSONObject json) {
            mJson = deepCopy(json);
        }

        public JSONBuilder put(String name, Object value) {
            try {
                mJson.put(name, value);
            } catch (JSONException e) {
            }
            return this;
        }

        @Override
        public JSONObject toJSON() {
            return mJson;
        }

        @Override
        public String toString() {
            return mJson.toString();
        }

        public String toString(int indent) {
            try {
                return mJson.toString(indent);
            } catch (JSONException ignored) {
                return mJson.toString();
            }
        }
    }

    public interface Jsonable {
        JSONObject toJSON();
    }
}

