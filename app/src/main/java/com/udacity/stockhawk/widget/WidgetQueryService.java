package com.udacity.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.MainActivity;

public class WidgetQueryService extends IntentService {

    public WidgetQueryService() {
        super("WidgetQueryService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName componentName = new ComponentName(this, StockWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        String[] projections = {Contract.Quote.COLUMN_SYMBOL, Contract.Quote.COLUMN_PRICE,
                Contract.Quote.COLUMN_PERCENTAGE_CHANGE};
        Cursor cursor;
        int random = (int) (Math.random() * 2);
        if (random == 0) {
            String sortOrder = Contract.Quote.COLUMN_SYMBOL + " desc";
            cursor = getContentResolver().query(Contract.Quote.uri, projections, null, null, sortOrder);
        } else {
            String sortOrder = Contract.Quote.COLUMN_SYMBOL + " asc";
            cursor = getContentResolver().query(Contract.Quote.uri, projections, null, null, sortOrder);
        }
        for (int appWidgetId : appWidgetIds) {
            Bundle bundle = appWidgetManager.getAppWidgetOptions(appWidgetId);
            int minHeight = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
            if (minHeight < 100) {
                int layout = R.layout.stock_widget;
                RemoteViews remoteViews = new RemoteViews(this.getPackageName(), layout);
                if (cursor != null) {
                    cursor.moveToFirst();
                }
                remoteViews.setTextViewText(R.id.widget_symbol_line1_text_view, cursor.getString(0));
                remoteViews.setTextViewText(R.id.widget_price_line1_text_view,
                        String.format("$%.2f", cursor.getDouble(1)));
                if (cursor.getDouble(2) >= 0) {
                    remoteViews.setTextColor(R.id.widget_percent_line1_text_view,
                            ContextCompat.getColor(this, R.color.colorPercentPositive));
                    remoteViews.setTextViewText(R.id.widget_percent_line1_text_view,
                            String.format("%.2f", cursor.getDouble(2)) + "%");
                } else {
                    remoteViews.setTextColor(R.id.widget_percent_line1_text_view,
                            ContextCompat.getColor(this, R.color.colorPercentNegative));
                    remoteViews.setTextViewText(R.id.widget_percent_line1_text_view,
                            String.format("%.2f", cursor.getDouble(2)) + "%");
                }
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                Intent sendIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, sendIntent, 0);
                remoteViews.setOnClickPendingIntent(R.id.widget_symbol_line1_text_view, pendingIntent);
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            } else if (minHeight > 100 && minHeight < 150) {
                int layout = R.layout.stock_widget2;
                if (cursor != null) {
                    cursor.moveToFirst();
                }
                String[] symbols = new String[cursor.getCount()];
                String[] prices = new String[cursor.getCount()];
                String[] percents = new String[cursor.getCount()];
                for (int i = 0; i < cursor.getCount(); i++) {
                    symbols[i] = cursor.getString(0);
                    prices[i] = String.format("$%.2f", cursor.getDouble(1));
                    percents[i] = String.format("%.2f", cursor.getDouble(2)) + "%";
                    cursor.moveToNext();
                }
                RemoteViews remoteViews = new RemoteViews(this.getPackageName(), layout);
                for (int i = 0; i < symbols.length; i++) {
                    if (i == 0) {
                        remoteViews.setTextViewText(R.id.widget_symbol_line1_text_view, symbols[i]);
                        remoteViews.setTextViewText(R.id.widget_price_line1_text_view, prices[i]);
                        if (percents[i].charAt(0) == '-') {
                            remoteViews.setTextColor(R.id.widget_percent_line1_text_view,
                                    ContextCompat.getColor(this, R.color.colorPercentNegative));
                            remoteViews.setTextViewText(R.id.widget_percent_line1_text_view, percents[i]);
                        } else {
                            remoteViews.setTextColor(R.id.widget_percent_line1_text_view,
                                    ContextCompat.getColor(this, R.color.colorPercentPositive));
                            remoteViews.setTextViewText(R.id.widget_percent_line1_text_view, percents[i]);
                        }
                    } else if (i == 1) {
                        remoteViews.setTextViewText(R.id.widget_symbol_line2_text_view, symbols[i]);
                        remoteViews.setTextViewText(R.id.widget_price_line2_text_view, prices[i]);
                        if (percents[i].charAt(0) == '-') {
                            remoteViews.setTextColor(R.id.widget_percent_line2_text_view,
                                    ContextCompat.getColor(this, R.color.colorPercentNegative));
                            remoteViews.setTextViewText(R.id.widget_percent_line2_text_view, percents[i]);
                        } else {
                            remoteViews.setTextColor(R.id.widget_percent_line2_text_view,
                                    ContextCompat.getColor(this, R.color.colorPercentPositive));
                            remoteViews.setTextViewText(R.id.widget_percent_line2_text_view, percents[i]);
                        }
                    }
                }
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                Intent sendIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, sendIntent, 0);
                remoteViews.setOnClickPendingIntent(R.id.widget_symbol_line1_text_view, pendingIntent);
                remoteViews.setOnClickPendingIntent(R.id.widget_symbol_line2_text_view, pendingIntent);
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            } else if (minHeight > 160) {
                int layout = R.layout.stock_widget3;
                if (cursor != null) {
                    cursor.moveToFirst();
                }
                String[] symbols = new String[cursor.getCount()];
                String[] prices = new String[cursor.getCount()];
                String[] percents = new String[cursor.getCount()];
                for (int i = 0; i < cursor.getCount(); i++) {
                    symbols[i] = cursor.getString(0);
                    prices[i] = String.format("$%.2f", cursor.getDouble(1));
                    percents[i] = String.format("%.2f", cursor.getDouble(2)) + "%";
                    cursor.moveToNext();
                }
                RemoteViews remoteViews = new RemoteViews(this.getPackageName(), layout);
                for (int i = 0; i < symbols.length; i++) {
                    if (i == 0) {
                        remoteViews.setTextViewText(R.id.widget_symbol_line1_text_view, symbols[i]);
                        remoteViews.setTextViewText(R.id.widget_price_line1_text_view, prices[i]);
                        if (percents[i].charAt(0) == '-') {
                            remoteViews.setTextColor(R.id.widget_percent_line1_text_view,
                                    ContextCompat.getColor(this, R.color.colorPercentNegative));
                            remoteViews.setTextViewText(R.id.widget_percent_line1_text_view, percents[i]);
                        } else {
                            remoteViews.setTextColor(R.id.widget_percent_line1_text_view,
                                    ContextCompat.getColor(this, R.color.colorPercentPositive));
                            remoteViews.setTextViewText(R.id.widget_percent_line1_text_view, percents[i]);
                        }
                    } else if (i == 1) {
                        remoteViews.setTextViewText(R.id.widget_symbol_line2_text_view, symbols[i]);
                        remoteViews.setTextViewText(R.id.widget_price_line2_text_view, prices[i]);
                        if (percents[i].charAt(0) == '-') {
                            remoteViews.setTextColor(R.id.widget_percent_line2_text_view,
                                    ContextCompat.getColor(this, R.color.colorPercentNegative));
                            remoteViews.setTextViewText(R.id.widget_percent_line2_text_view, percents[i]);
                        } else {
                            remoteViews.setTextColor(R.id.widget_percent_line2_text_view,
                                    ContextCompat.getColor(this, R.color.colorPercentPositive));
                            remoteViews.setTextViewText(R.id.widget_percent_line2_text_view, percents[i]);
                        }
                    } else if (i == 2) {
                        remoteViews.setTextViewText(R.id.widget_symbol_line3_text_view, symbols[i]);
                        remoteViews.setTextViewText(R.id.widget_price_line3_text_view, prices[i]);
                        if (percents[i].charAt(0) == '-') {
                            remoteViews.setTextColor(R.id.widget_percent_line3_text_view,
                                    ContextCompat.getColor(this, R.color.colorPercentNegative));
                            remoteViews.setTextViewText(R.id.widget_percent_line3_text_view, percents[i]);
                        } else {
                            remoteViews.setTextColor(R.id.widget_percent_line3_text_view,
                                    ContextCompat.getColor(this, R.color.colorPercentPositive));
                            remoteViews.setTextViewText(R.id.widget_percent_line3_text_view, percents[i]);
                        }
                    }
                }
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
                Intent sendIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, sendIntent, 0);
                remoteViews.setOnClickPendingIntent(R.id.widget_symbol_line1_text_view, pendingIntent);
                remoteViews.setOnClickPendingIntent(R.id.widget_symbol_line2_text_view, pendingIntent);
                remoteViews.setOnClickPendingIntent(R.id.widget_symbol_line3_text_view, pendingIntent);
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }
}


