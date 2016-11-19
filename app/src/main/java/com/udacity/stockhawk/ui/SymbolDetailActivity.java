package com.udacity.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by printart on 11/17/2016.
 */
public class SymbolDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.symbol_detail_title_text_view)
    TextView mSymbolTextView;
    @BindView(R.id.bar_chart)
    BarChart mBarChart;
    @BindView(R.id.symbol_detail_price_text_view)
    TextView mPriceTextView;
    private String mSymbol;
    private List<BarEntry> mPrice;
    private ArrayList<String> mTimes;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symbol_detail);
        mSymbol = getIntent().getStringExtra("symbol");
        mTimes = new ArrayList<>();
        mPrice = new ArrayList<>();

        getLoaderManager().initLoader(100, null, this);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSymbolTextView.setText(mSymbol);
        buildChart();
    }

    private void buildChart() {
        mBarChart.setScaleEnabled(true);
        mBarChart.setDragEnabled(true);
        mBarChart.setDescription("");
        mBarChart.setDrawGridBackground(false);
        mBarChart.setDrawBarShadow(false);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mBarChart.zoom(1.3F, 1F, 2F, 1F);
        } else {
            mBarChart.zoom(1.7F, 1F, 2F, 1F);
        }
        mBarChart.setExtraOffsets(0, 0, 0, 1);
        mBarChart.setScaleEnabled(false);
//        mBarChart.setScaleMinima(1F, 1F);

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.colorChartTimeText));
        xAxis.setTextSize(13);
        xAxis.setDrawGridLines(false);

        YAxis yAxisRight = mBarChart.getAxisRight();
        yAxisRight.setEnabled(false);
        yAxisRight.setDrawAxisLine(false);

        YAxis yAxisLeft = mBarChart.getAxisLeft();
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setEnabled(false);

        Legend legend = mBarChart.getLegend();
        legend.setEnabled(false);

        BarDataSet barDataSet = new BarDataSet(mPrice, "Stock price");
        barDataSet.setColor(ContextCompat.getColor(this, R.color.colorBarChart));
        barDataSet.setBarSpacePercent(10F);
        barDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.colorChartPriceText));
        barDataSet.setValueTextSize(13);
        barDataSet.setHighLightColor(ContextCompat.getColor(this, R.color.colorChartHighlight));

        BarData barData = new BarData(mTimes, barDataSet);
        mBarChart.setData(barData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projections = {Contract.Quote.COLUMN_HISTORY, Contract.Quote.COLUMN_PRICE};
        Uri uri = Contract.Quote.uri.buildUpon().appendPath(mSymbol).build();
        return new CursorLoader(this, uri, projections, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int approximate = 10;//out of ~ 105/10 if!=10-> adjust mBarChart.zoom
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
        cursor.moveToFirst();
        String dataFromDb = cursor.getString(0);
        String[] singleRecord = dataFromDb.split("\n");
        double price = cursor.getDouble(1);

        for (int i = 0; i < (singleRecord.length / approximate); i++) {
            calendar.setTimeInMillis(Long.parseLong(singleRecord[i].split(", ")[0]));
            mTimes.add(simpleDateFormat.format(calendar.getTime()));
            float priceForDay = Float.parseFloat(decimalFormat.format(Float.parseFloat(singleRecord[i].split(", ")[1])));
            mPrice.add(new BarEntry(priceForDay, i));
        }
        mPriceTextView.setText(String.format(Locale.getDefault(), "$%.2f", price));
        buildChart();
        cursor.moveToFirst();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPrice.clear();
        mTimes.clear();
    }
}
