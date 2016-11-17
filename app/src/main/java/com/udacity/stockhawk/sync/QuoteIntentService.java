package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.udacity.stockhawk.R;

import timber.log.Timber;


public class QuoteIntentService extends IntentService {

    public static boolean isNoSymbol = false;
    public static String sSymbol;

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");
        QuoteSyncJob.getQuotes(getApplicationContext());
        if (isNoSymbol) {
            Handler handler = new Handler(getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(QuoteIntentService.this, getString(R.string.error_stock_not_found, sSymbol), Toast.LENGTH_SHORT).show();
                    isNoSymbol = false;
                }
            });
        }
    }
}
