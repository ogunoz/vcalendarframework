package ogunoz.com.vcalendar.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ogunoz.com.vcalendar.R;
import ogunoz.com.vcalendar.models.Event;
import ogunoz.com.vcalendar.CalendarUtil;

/**
 * Created by Ogün Öz on 25/08/16.
 */
public class EventAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Event> events;

    public EventAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        this.events = events;

    }

    public EventAdapter(Context context) {
        this.context = context;
        if (CalendarUtil.getSelectedDayEventList() != null) {
            events = CalendarUtil.getSelectedDayEventList();
        } else {
            events = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.default_event_list_item, viewGroup, false);
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.default_event_layout);
        TextView title = (TextView) view.findViewById(R.id.text_title);
        TextView content = (TextView) view.findViewById(R.id.text_content);
        Event e = events.get(i);

        layout.setBackgroundColor(e.getColor());
        title.setText(e.getTitle());
        content.setText(e.getText());
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }


}
