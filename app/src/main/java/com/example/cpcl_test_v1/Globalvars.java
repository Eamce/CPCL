package com.example.cpcl_test_v1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Globalvars
{
    public static String store_name = "PM";
//    public static String store_name = "ALTA";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public Globalvars(Context context, Activity act){
        preferences = act.getSharedPreferences("Global_vars",context.MODE_PRIVATE);
        editor = preferences.edit();
    }
    public void set(String varname, String varval)
    {
        editor.putString(varname,varval);
        editor.commit();
    }
    public String get(String varname)
    {
        return preferences.getString(varname,null);
    }

}
