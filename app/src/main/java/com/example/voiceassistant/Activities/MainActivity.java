package com.example.voiceassistant.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.voiceassistant.Fragments.ClientsFragment;
import com.example.voiceassistant.Fragments.HomeFragment;
import com.example.voiceassistant.Fragments.SettingsFragment;
import com.example.voiceassistant.Fragments.ChatGPTFragment;
import com.example.voiceassistant.R;

public class MainActivity extends AppCompatActivity {

    private int selectedTab = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LinearLayout homeLayout = findViewById(R.id.homeLayout);
        final LinearLayout addClientLayout = findViewById(R.id.addClientLayout);
        final LinearLayout voiceAssistantLayout = findViewById(R.id.voiceAssistantLayout);
        final LinearLayout profileLayout = findViewById(R.id.profileLayout);

        final ImageView homeImage = findViewById(R.id.homeImage);
        final ImageView addClientImage = findViewById(R.id.addCleintImage);
        final ImageView voiceAssistantImage = findViewById(R.id.voiceAssistantImage);
        final ImageView profileImage = findViewById(R.id.profileImage);

        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragmentContainer, HomeFragment.class, null).commit();

        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTab != 1) {

                    getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragmentContainer, HomeFragment.class, null).commit();

                    addClientImage.setImageResource(R.drawable.clients_icon);
                    voiceAssistantImage.setImageResource(R.drawable.voice_assistant_icon);
                    profileImage.setImageResource(R.drawable.settings_icon);

                    homeImage.setImageResource(R.drawable.home_selected_icon);

                    selectedTab = 1;
                }
            }
        });

        addClientLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTab != 2) {

                    getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragmentContainer, ClientsFragment.class, null).commit();

                    homeImage.setImageResource(R.drawable.home_icon);
                    voiceAssistantImage.setImageResource(R.drawable.voice_assistant_icon);
                    profileImage.setImageResource(R.drawable.settings_icon);

                    addClientImage.setImageResource(R.drawable.clients_selected_icon);

                    selectedTab = 2;
                }
            }
        });

        voiceAssistantLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTab != 3) {

                    getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragmentContainer, ChatGPTFragment.class, null).commit();

                    homeImage.setImageResource(R.drawable.home_icon);
                    addClientImage.setImageResource(R.drawable.clients_icon);
                    profileImage.setImageResource(R.drawable.settings_icon);

                    voiceAssistantImage.setImageResource(R.drawable.voice_assistant_selected_icon);

                    selectedTab = 3;
                }
            }
        });

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTab != 4) {

                    getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.fragmentContainer, SettingsFragment.class, null).commit();

                    homeImage.setImageResource(R.drawable.home_icon);
                    addClientImage.setImageResource(R.drawable.clients_icon);
                    voiceAssistantImage.setImageResource(R.drawable.voice_assistant_icon);

                    profileImage.setImageResource(R.drawable.settings_selected_icon);

                    selectedTab = 4;
                }
            }
        });
    }

    public void showSettingsFragment() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainer, SettingsFragment.class, null)
                .commit();

        ImageView homeImage = findViewById(R.id.homeImage);
        ImageView addClientImage = findViewById(R.id.addCleintImage);
        ImageView voiceAssistantImage = findViewById(R.id.voiceAssistantImage);
        ImageView profileImage = findViewById(R.id.profileImage);

        homeImage.setImageResource(R.drawable.home_icon);
        addClientImage.setImageResource(R.drawable.clients_icon);
        voiceAssistantImage.setImageResource(R.drawable.voice_assistant_icon);
        profileImage.setImageResource(R.drawable.settings_selected_icon);

        selectedTab = 4;
    }
}