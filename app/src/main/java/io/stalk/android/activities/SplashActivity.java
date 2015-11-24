package io.stalk.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.concurrent.atomic.AtomicInteger;

import io.xpush.chat.core.XPushCore;
import io.xpush.chat.services.RegistrationIntentService;
import io.xpush.chat.services.XPushService;
import io.stalk.android.R;


public class SplashActivity extends Activity {

    public static final String TAG = SplashActivity.class.getSimpleName();

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private int SPLASH_TIME = 1500;

    private int NETWORK_WIFI            = 1;
    private int NETWORK_MOBILE          = 2;
    private int NETWORK_NOT_AVAILABLE   = 0;

    private Activity mActivity;
    private Button mReloadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        Fresco.initialize(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mActivity = this;

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        mReloadBtn = (Button) findViewById(R.id.reloadBtn);
        mReloadBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mActivity.finish();
                mActivity.startActivity(getIntent());
            }
        });

        if (checkPlayServices()) {
            if( null == pref.getString("REGISTERED_NOTIFICATION_ID", null ) ){
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }

        XPushService.actionStart(this);

        final long started = System.currentTimeMillis();
        final AtomicInteger i = new AtomicInteger();

        final Handler handler = new Handler();
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ((XPushCore.getInstance().isGlobalConnected() && System.currentTimeMillis() - started < SPLASH_TIME)
                        || ( XPushCore.getInstance().getXpushSession() != null && !XPushCore.getInstance().isGlobalConnected() && System.currentTimeMillis() - started < (SPLASH_TIME * 4))) {
                    handler.postDelayed(this, 150);
                } else {

                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    overridePendingTransition(0, android.R.anim.fade_in);

                    Intent intent = null;
                    if( pref.getBoolean("SHOW_INTRO", false) ){
                        if (null == XPushCore.getInstance().getXpushSession()) {
                            intent = new Intent(SplashActivity.this, LoginActivity.class);
                        } else {
                            if( pref.getBoolean("SITE_READY", false) ) {
                                intent = new Intent(SplashActivity.this, MainActivity.class);
                            } else {
                                intent = new Intent(SplashActivity.this, MainActivity.class);
                                //intent = new Intent(SplashActivity.this, UnreadyActivity.class);
                            }
                        }
                    } else {
                        intent = new Intent(SplashActivity.this, IntroActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }
            }
        }, 150);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}