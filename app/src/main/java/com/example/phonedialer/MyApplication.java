package com.example.phonedialer;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraMailSender;
//import org.acra.annotation.AcraToast;

@AcraCore(buildConfigClass = BuildConfig.class)
//@AcraToast(resText = R.string.app_crash, length = 1)
@AcraMailSender(mailTo = "aamir2456@gmail.com", reportAsFile = true)
public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        Log.i("attachBaseContext()", "In MyApplication.");

        /*CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this);
        //builder.setBuildConfigClass(BuildConfig.class).setReportFormat(StringFormat.JSON);
        builder.getPluginConfigurationBuilder(ToastConfigurationBuilder.class).setResText(R.string.app_crash);

        //Please note that plugins are disabled if their respective annotation is not present. You can activate them by calling:

        builder.getPluginConfigurationBuilder(ToastConfigurationBuilder.class).setEnabled(true);
        ACRA.init(this, builder);*/

        ACRA.init(this);
    }
}
