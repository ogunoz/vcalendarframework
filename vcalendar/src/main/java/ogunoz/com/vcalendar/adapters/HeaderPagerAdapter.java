package ogunoz.com.vcalendar.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ogunoz.com.vcalendar.R;
import ogunoz.com.vcalendar.util.CalendarUtil;

public class HeaderPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> monthList;
    private ViewPager calendarPager;

    public HeaderPagerAdapter(Context context, ArrayList<String> monthList, ViewPager calenderPager) {
        this.context = context;
        this.monthList = monthList;
        this.calendarPager = calenderPager;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View v = inflater.inflate(R.layout.month_name_pager_item, container, false);
        v.setTag(position + "");



        TextView monthNameTextView = (TextView) v.findViewById(R.id.month_name_text_view);
        final String monthName = monthList.get(position);
        monthNameTextView.setText(monthName);

        if (CalendarUtil.getCurrentMonthIndex() == position) {
            if (calendarPager.getCurrentItem() == position) {
                monthNameTextView.setTextSize(32);
                monthNameTextView.setAlpha(1.0f);
            } else {
                monthNameTextView.setTextSize(14);
                monthNameTextView.setAlpha(0.5f);
            }
        }

/*
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position > calendarPager.getCurrentItem()) {
                    calendarPager.setCurrentItem(calendarPager.getCurrentItem() + 1, true);
                } else if (position < calendarPager.getCurrentItem()) {
                    calendarPager.setCurrentItem(calendarPager.getCurrentItem() - 1, true);
                }
            }
        });
*/
        container.addView(v);
        return v;
    }

    @Override
    public int getCount() {
        return monthList.size();
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


}
