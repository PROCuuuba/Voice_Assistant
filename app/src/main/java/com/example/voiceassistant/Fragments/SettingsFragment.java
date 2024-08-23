package com.example.voiceassistant.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.voiceassistant.Activities.LoginActivity;
import com.example.voiceassistant.Classes.Staff;
import com.example.voiceassistant.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;

import java.util.UUID;

public class SettingsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private TextView nameTextView, phoneNumberTextView;
    private ImageView profilePictureImageView;
    private StorageReference storageRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        nameTextView = view.findViewById(R.id.nameTextView);
        phoneNumberTextView = view.findViewById(R.id.phoneNumberTextView);
        profilePictureImageView = view.findViewById(R.id.profilePictureImageView);
        SwitchCompat notificationSwitch = view.findViewById(R.id.notificationSwitch);

        final RelativeLayout logoutLayout = view.findViewById(R.id.logoutLayout);
        final RelativeLayout infoLayout = view.findViewById(R.id.infoLayout);

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enableNotifications();
                } else {
                    disableNotifications();
                }
            }
        });

        profilePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProfilePicture();
            }
        });

        displayUserData();
        return view;
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void enableNotifications() {
        if (!NotificationManagerCompat.from(getActivity()).areNotificationsEnabled()) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName());
            startActivity(intent);
        }
    }

    private void disableNotifications() {
        NotificationManagerCompat.from(getActivity()).cancelAll();
    }

    private void displayUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference staffRef = FirebaseDatabase.getInstance().getReference().child("staff").child(userId);
            staffRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Staff staff = dataSnapshot.getValue(Staff.class);
                        if (staff != null) {
                            String fullName = staff.getSurname() + " " + staff.getName();
                            nameTextView.setText(fullName);
                            phoneNumberTextView.setText(staff.getPhoneNumber());
                            if (TextUtils.isEmpty(staff.getProfilePictureUrl())) {
                                profilePictureImageView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.non_selected));
                            } else {
                                Glide.with(requireContext())
                                        .load(staff.getProfilePictureUrl())
                                        .placeholder(R.color.non_selected)
                                        .error(R.color.non_selected)
                                        .centerCrop()
                                        .into(profilePictureImageView);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Ошибка при получении данных о пользователе", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadProfilePicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK) {
            if (data != null) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    uploadImageToStorage(imageUri);
                }
            }
        }
    }

    private void uploadImageToStorage(Uri imageUri) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String imageName = UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = storageRef.child("profilePictures/" + userId + "/" + imageName);
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            saveProfilePictureUrlToDatabase(userId, imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveProfilePictureUrlToDatabase(String userId, String imageUrl) {
        DatabaseReference staffRef = FirebaseDatabase.getInstance().getReference().child("staff").child(userId);
        staffRef.child("profilePictureUrl").setValue(imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Изображение профиля успешно обновлено", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Ошибка при сохранении изображения в базе данных", Toast.LENGTH_SHORT).show();
                });
    }
}