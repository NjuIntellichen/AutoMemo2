package imagecup.nju.intellichens.automemo.util;

import android.util.Log;

/**
 * Created by Hanifor on 3/27/2017.
 */
public class User {
    private static User instance = null;

    private String id = "158652";
    private String name = "Hanifor";
    private String phone = "13218882766";
    private static String sessionId;

    private User(String id, String name, String phone){
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public static void setUser(String id, String name, String phone){
        if(instance == null){
            Log.i("Creating User id", id);
            instance = new User(id, "Hanifor", phone);
        }
    }

    public static boolean isLogined(){
        return instance != null;
    }

    public static void removeUser(){
        instance = null;
    }

    public static String getId() {
        if(instance != null){
            return instance.id;
        }
        return null;
    }

    public static String getName() {
        if(instance != null){
            return instance.name;
        }
        return "Hanifor";
    }

    public static String getPhone() {
        if(instance != null){
            return instance.phone;
        }
        return null;
    }

    public static void setSessionId(String sessionId) {
        User.sessionId = sessionId;
    }

    public static String getSessionId() {
        return sessionId;
    }
}