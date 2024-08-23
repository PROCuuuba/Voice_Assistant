package com.example.voiceassistant.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.voiceassistant.Adapters.NotificationMessageAdapter;
import com.example.voiceassistant.Classes.NotificationMessage;
import com.example.voiceassistant.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private List<NotificationMessage> notifications = new ArrayList<>();
    private NotificationMessageAdapter notificationMessageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ListView notificationsListView = view.findViewById(R.id.notificationsListView);
        notificationMessageAdapter = new NotificationMessageAdapter(getContext(), notifications);
        notificationsListView.setAdapter(notificationMessageAdapter);

        fetchNotificationsFromDatabase();

        return view;
    }

    private void fetchNotificationsFromDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference notificationsRef = database.getReference("notifications");

        notificationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notifications.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NotificationMessage notification = snapshot.getValue(NotificationMessage.class);
                    notifications.add(notification);
                }
                notificationMessageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    
    public void addNotification(NotificationMessage notification) {
        notifications.add(notification);
        notificationMessageAdapter.notifyDataSetChanged();
    }
}