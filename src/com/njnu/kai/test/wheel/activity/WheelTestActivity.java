package com.njnu.kai.test.wheel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.njnu.kai.test.R;
import com.njnu.kai.test.wheel.OnWheelChangedListener;
import com.njnu.kai.test.wheel.OnWheelScrollListener;
import com.njnu.kai.test.wheel.WheelView;
import com.njnu.kai.test.wheel.activity.model.ShareCsdnActivity;
import com.njnu.kai.test.wheel.adapters.AbstractWheelTextAdapter;
import com.njnu.kai.test.wheel.adapters.ArrayWheelAdapter;

/**
 * @author hongkai.qian
 * @version 1.0.0
 * @since 15-8-5
 */
public class WheelTestActivity extends Activity {

    // Scrolling flag
    private boolean scrolling = false;

    private TextView mTvResult;
    private CountryAdapter mCountryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cities_layout);

        final WheelView country = (WheelView) findViewById(R.id.country);
        country.setVisibleItems(5);
        mCountryAdapter = new CountryAdapter(this);
        country.setViewAdapter(mCountryAdapter);

        final String cities[][] = new String[][]{
                new String[]{"New York", "Washington", "Chicago", "Atlanta", "Orlando"},
                new String[]{"Ottawa", "Vancouver", "Toronto", "Windsor", "Montreal"},
                new String[]{"Kiev", "Dnipro", "Lviv", "Kharkiv"},
                new String[]{"Paris", "Bordeaux"},
        };

        final WheelView city = (WheelView) findViewById(R.id.city);
        city.setVisibleItems(5);

        country.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    updateCities(city, cities, newValue);
                }
            }
        });

        country.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                scrolling = true;
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                scrolling = false;
                updateCities(city, cities, country.getCurrentItem());
            }
        });

        country.setCurrentItem(1);
        mTvResult = (TextView) findViewById(R.id.tv_result);
        findViewById(R.id.btn_result).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int countryItem = country.getCurrentItem();
                String[] cityssss = cities[countryItem];
                final int cityItem = city.getCurrentItem();
                mTvResult.setText(mCountryAdapter.getStr(cityItem) + " " + cityssss[cityItem]);
            }
        });
        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WheelTestActivity.this, ShareCsdnActivity.class));
            }
        });
    }

    /**
     * Updates the city wheel
     */
    private void updateCities(WheelView city, String cities[][], int index) {
        ArrayWheelAdapter<String> adapter =
                new ArrayWheelAdapter<String>(this, cities[index]);
        adapter.setTextSize(18);
        city.setViewAdapter(adapter);
        city.setCurrentItem(cities[index].length / 2);
    }

    /**
     * Adapter for countries
     */
    private class CountryAdapter extends AbstractWheelTextAdapter {
        // Countries names
        private String countries[] =
                new String[]{"USA", "Canada", "Ukraine", "France"};
        // Countries flags
//        private int flags[] =
//                new int[] {R.drawable.usa, R.drawable.canada, R.drawable.ukraine, R.drawable.france};

        String getStr(int idx) {
            return countries[idx];
        }

        /**
         * Constructor
         */
        protected CountryAdapter(Context context) {
            super(context, R.layout.country_layout, NO_RESOURCE);

            setItemTextResource(R.id.country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
//            ImageView img = (ImageView) view.findViewById(R.id.flag);
//            img.setImageResource(flags[index]);
            return view;
        }

        @Override
        public int getItemsCount() {
            return countries.length;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return countries[index];
        }
    }

}
