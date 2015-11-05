package net.getsett.spotifyalarm;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

/**
 * Created by James on 19/01/2015.x
 */
@ReportsCrashes(
        formKey = "", // This is required for backward compatibility but not used
        formUri = "http://api.imaginarium.getsett.net/error-reporting/report/android",
        httpMethod = HttpSender.Method.PUT,
        reportType = org.acra.sender.HttpSender.Type.JSON
)
public class Application extends com.orm.SugarApp {
    @Override
    public void onCreate() {
        super.onCreate();

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}
