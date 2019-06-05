package org.firehound.devfestclone;

import android.app.Application;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class BaseAppClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("C:\\Users\\Amogh\\StudioProjects\\DevFestClone\\app\\src\\main\\res\\font\\googlesans_regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build())).build());
    }
}
