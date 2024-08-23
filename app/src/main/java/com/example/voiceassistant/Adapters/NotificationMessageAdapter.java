package com.example.voiceassistant.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.voiceassistant.Classes.NotificationMessage;
import com.example.voiceassistant.R;

import java.util.List;

public class NotificationMessageAdapter extends ArrayAdapter<NotificationMessage> {
    private Context context;
    private List<NotificationMessage> notifications;

    public NotificationMessageAdapter(Context context, List<NotificationMessage> notifications) {
        super(context, 0, notifications);
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.notification_message_item, parent, false);
        }

        NotificationMessage notification = notifications.get(position);
        TextView messageTextView = convertView.findViewById(R.id.notificationMessageTextView);
        TextView timestampTextView = convertView.findViewById(R.id.notificationTimestampTextView);

        messageTextView.setText(notification.getText());
        timestampTextView.setText(notification.getTimestamp());

        return convertView;
    }
}