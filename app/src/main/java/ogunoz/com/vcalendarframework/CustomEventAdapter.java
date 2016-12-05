package ogunoz.com.vcalendarframework;


import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ogunoz.com.vcalendar.adapters.EventAdapter;
import ogunoz.com.vcalendar.models.Event;

/**
 * Created by Ogün Öz on 03/12/16.
 */
public class CustomEventAdapter extends EventAdapter {

    private Context context;

    public CustomEventAdapter(Context context) {
        super(context);
        this.context = context;

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.event_list_item, viewGroup, false);
        TextView title = (TextView) view.findViewById(R.id.event_title_text_view);
        TextView detail = (TextView) view.findViewById(R.id.event_detail_text_view);
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.event_list_view_layout);

        Event e = ((Event) super.getItem(i));
        title.setText(e.getTitle());
        detail.setText(e.getText());
        layout.setBackgroundColor(e.getColor());

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}
