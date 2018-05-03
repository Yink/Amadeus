package com.example.yink.amadeus;

/* Solution: http://stackoverflow.com/a/40704077 */

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.preference.PreferenceManager;

import java.util.Locale;

public class ContextLocalWrapper  extends ContextWrapper {

    public ContextLocalWrapper(Context base) {
        super(base);
    }

    private static Locale getLocale(Context context) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String lang = settings.getString("lang", "system");
        Locale locale = null;
        if (!lang.equals("system")) {
            String[] langArr = lang.split("-");
            switch(langArr.length){
                case 3:
                    locale = new Locale(langArr[0],langArr[1], langArr[2]);
                    break;
                case 2:
                    locale = new Locale(langArr[0],langArr[1]);
                    break;
                case 1:
                    locale = new Locale(langArr[0]);
                    break;
            }
        }

        if (locale == null) {
            locale = Resources.getSystem().getConfiguration().locale;
        }

        return locale;

    }

    public static ContextLocalWrapper wrap(Context context) {
        Resources res = context.getResources();
        Configuration configuration = res.getConfiguration();

        Locale locale = getLocale(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale);
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
            context = context.createConfigurationContext(configuration);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
            context = context.createConfigurationContext(configuration);
        } else {
            configuration.locale = locale;
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }

        return new ContextLocalWrapper(context);
    }

    public static Context load(Context context, String lang) {
        Configuration config = context.getResources().getConfiguration();

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context = context.createConfigurationContext(config);
        } else {
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }

        return context;
    }


}
