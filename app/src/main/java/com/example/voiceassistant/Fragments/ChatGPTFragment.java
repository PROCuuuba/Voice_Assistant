package com.example.voiceassistant.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voiceassistant.Adapters.MessageAdapter;
import com.example.voiceassistant.Classes.Message;
import com.example.voiceassistant.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGPTFragment extends Fragment {
    RecyclerView recyclerView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    TextView welcomeTextView;
    OkHttpClient client = new OkHttpClient();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatgpt, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        messageEditText = view.findViewById(R.id.edit_text);
        sendButton = view.findViewById(R.id.send_button);
        welcomeTextView = view.findViewById(R.id.welcome_text);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList, getContext());
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager LLM = new LinearLayoutManager(requireContext());
        LLM.setStackFromEnd(true);
        recyclerView.setLayoutManager(LLM);

        sendButton.setOnClickListener((v) -> {
            String request = messageEditText.getText().toString().trim();
            addToChat(request, Message.SENT_BY_USER);
            messageEditText.getText().clear();
            welcomeTextView.setVisibility(View.GONE);
            callAPI(request);
        });

        return view;
    }

    void addToChat(String message, String sentBy) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Message newMessage = new Message(message, sentBy, new Date());
                messageList.add(newMessage);
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String response) { addToChat(response, Message.SENT_BY_BOT); }

    void callAPI(String request) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo-instruct");
            jsonBody.put("prompt", request);
            jsonBody.put("max_tokens", 4000);
            jsonBody.put("temperature", 0);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody body = okhttp3.RequestBody.create(jsonBody.toString(), okhttp3.MediaType.parse("application/json"));
        Request question = new Request.Builder()
                .url("https://api.openai.com/v1/completions")

                .post(body)
                .build();

        client.newCall(question).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Не удалось загрузить ответ из-за " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    addResponse("Не удалось загрузить ответ из-за " + response.body().string());
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTextToSpeech();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTextToSpeech();
    }

    private void stopTextToSpeech() {
        if (messageAdapter != null) {
            messageAdapter.stopTextToSpeech();
        }
    }
}