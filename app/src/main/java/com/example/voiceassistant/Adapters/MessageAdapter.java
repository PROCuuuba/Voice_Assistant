package com.example.voiceassistant.Adapters;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voiceassistant.Classes.Message;
import com.example.voiceassistant.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    List<Message> messageList;
    Context context;
    TextToSpeech tts;

    public MessageAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
        tts = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.getDefault());
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_items, null);
        MyViewHolder myViewHolder = new MyViewHolder(chatView);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (message.getSentBy().equals(Message.SENT_BY_USER)) {
            holder.leftChatItem.setVisibility(View.GONE);
            holder.rightChatItem.setVisibility(View.VISIBLE);
            holder.rightChatTextItem.setText(message.getMessage());
            holder.rightChatTime.setText(formatTime(message.getTimestamp()));
        } else {
            holder.rightChatItem.setVisibility(View.GONE);
            holder.leftChatItem.setVisibility(View.VISIBLE);
            holder.leftChatTextItem.setText(message.getMessage());
            holder.leftChatTime.setText(formatTime(message.getTimestamp()));

            holder.speakButton.setOnClickListener(v -> {
                String text = message.getMessage();
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            });
        }
    }

    private String formatTime(Date timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(timestamp);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void stopTextToSpeech() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout leftChatItem, rightChatItem;
        TextView leftChatTextItem, rightChatTextItem;
        TextView leftChatTime, rightChatTime;
        ImageButton speakButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatItem = itemView.findViewById(R.id.left_chat_item);
            rightChatItem = itemView.findViewById(R.id.right_chat_item);
            leftChatTextItem = itemView.findViewById(R.id.left_chat_text_item);
            rightChatTextItem = itemView.findViewById(R.id.right_chat_text_item);
            leftChatTime = itemView.findViewById(R.id.left_chat_time);
            rightChatTime = itemView.findViewById(R.id.right_chat_time);
            speakButton = itemView.findViewById(R.id.speak_button);
        }
    }
}