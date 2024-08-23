package com.example.voiceassistant.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;

import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.voiceassistant.Activities.MainActivity;
import com.example.voiceassistant.Adapters.ClientAdapter;
import com.example.voiceassistant.Classes.Client;
import com.example.voiceassistant.Classes.NotificationMessage;
import com.example.voiceassistant.Classes.Staff;
import com.example.voiceassistant.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class ClientsFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private List<Client> clientList;
    private ClientAdapter clientAdapter;
    private ImageView imagePreview;
    private BottomSheetDialog bottomSheetDialog;
    private ShapeableImageView smallProfilePictureImageView;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_clients, container, false);

        mAuth = FirebaseAuth.getInstance();
        smallProfilePictureImageView = view.findViewById(R.id.smallProfilePictureImageView);
        smallProfilePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) requireActivity();
                activity.showSettingsFragment();
            }
        });
        displayProfilePicture();

        FloatingActionButton fab = view.findViewById(R.id.fab);
        SearchView searchView = view.findViewById(R.id.searchView);

        bottomSheetDialog = new BottomSheetDialog(requireContext());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("clients");
        clientList = new ArrayList<>();
        clientAdapter = new ClientAdapter(getContext(), clientList);
        ListView clientsListView = view.findViewById(R.id.clientsListView);
        clientsListView.setAdapter(clientAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    updateListViewWithAllClients();
                } else {
                    searchClients(newText);
                }
                return true;
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clientList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Client client = snapshot.getValue(Client.class);
                    String surname = client.getSurname();
                    String capitalizedSurname = surname.substring(0, 1).toUpperCase() + surname.substring(1);
                    client.setSurname(capitalizedSurname);

                    client.setFirebaseKey(snapshot.getKey());

                    clientList.add(client);
                }

                clientAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ClientsFragment", "Ошибка при получении данных", databaseError.toException());
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

        clientsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Client client = clientList.get(position);
                showDialog(client);
            }
        });

        return view;
    }

    private void displayProfilePicture() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference staffRef = FirebaseDatabase.getInstance().getReference().child("staff").child(userId);
            staffRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Staff staff = dataSnapshot.getValue(Staff.class);
                        if (staff != null && staff.getProfilePictureUrl() != null) {
                            Glide.with(requireContext())
                                    .load(staff.getProfilePictureUrl())
                                    .placeholder(R.color.non_selected)
                                    .error(R.color.non_selected)
                                    .centerCrop()
                                    .into(smallProfilePictureImageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ClientsFragment", "Error fetching profile picture: " + databaseError.getMessage());
                }
            });
        }
    }

    private void searchClients(String query) {
        Query searchQuery = FirebaseDatabase.getInstance().getReference("clients")
                .orderByChild("surname")
                .startAt(query)
                .endAt(query + "\uf8ff");

        searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Client> searchResults = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Client client = snapshot.getValue(Client.class);
                    searchResults.add(client);
                }
                clientAdapter.updateList(searchResults);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ClientsFragment", "Ошибка при выполнении поиска", databaseError.toException());
            }
        });
    }

    private void updateListViewWithAllClients() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("clients");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Client> allClients = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Client client = snapshot.getValue(Client.class);
                    allClients.add(client);
                }
                clientAdapter.updateList(allClients);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ClientsFragment", "Ошибка при получении полного списка клиентов", databaseError.toException());
            }
        });
    }

    private void deleteClient(Client client) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("clients");
        databaseReference.child(client.getFirebaseKey()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(requireContext(), "Клиент успешно удален", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Ошибка при удалении клиента: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDeleteConfirmationDialog(Client client) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Удаление клиента")
                .setMessage("Вы уверены, что хотите удалить этого клиента?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteClient(client);
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void addNotificationToDatabase(NotificationMessage notification) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference notificationsRef = database.getReference("notifications");
        notificationsRef.push().setValue(notification);
    }

    private void addNotificationToHomeFragment(String message) {
        HomeFragment homeFragment = (HomeFragment) getParentFragmentManager().findFragmentByTag("home_fragment");
        NotificationMessage notification = new NotificationMessage(message, getCurrentTimestamp());

        if (homeFragment != null) {
            homeFragment.addNotification(notification);
        }

        addNotificationToDatabase(notification);
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void showDialog(Client client) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_row);
        dialog.setCancelable(true);

        TextInputEditText dialogClientSurnameEditText = dialog.findViewById(R.id.dialogClientSurnameEditText);
        TextInputEditText dialogClientNameEditText = dialog.findViewById(R.id.dialogClientNameEditText);
        TextInputEditText dialogClientPatronymicEditText = dialog.findViewById(R.id.dialogClientPatronymicEditText);
        TextInputEditText dialogClientTelephoneNumberEditText = dialog.findViewById(R.id.dialogClientTelephoneNumberEditText);
        TextInputEditText dialogClientProblemEditText = dialog.findViewById(R.id.dialogClientProblemEditText);
        TextInputEditText dialogClientDateEditText = dialog.findViewById(R.id.dialogClientDateEditText);
        TextInputEditText dialogClientModelEditText = dialog.findViewById(R.id.dialogClientModelEditText);
        ShapeableImageView dialogPhoneImageView = dialog.findViewById(R.id.dialogPhoneImageView);
        Spinner statusSpinner = dialog.findViewById(R.id.statusSpinner);

        dialogClientSurnameEditText.setText(client.getSurname());
        dialogClientNameEditText.setText(client.getName());
        dialogClientPatronymicEditText.setText(client.getPatronymic());
        dialogClientTelephoneNumberEditText.setText(client.getTelephoneNumber());
        dialogClientProblemEditText.setText(client.getProblem());
        dialogClientDateEditText.setText(client.getDate());
        dialogClientModelEditText.setText(client.getModel());

        String imageUrl = client.getImageUrl();
        Glide.with(getContext())
                .load(imageUrl)
                .fitCenter()
                .into(dialogPhoneImageView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);

        String currentStatus = client.getStatus();
        int spinnerPosition = adapter.getPosition(currentStatus);
        statusSpinner.setSelection(spinnerPosition);

        Button deleteButton = dialog.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(client);
                dialog.dismiss();
            }
        });

        Button editButton = dialog.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogClientSurnameEditText.setEnabled(true);
                dialogClientNameEditText.setEnabled(true);
                dialogClientPatronymicEditText.setEnabled(true);
                dialogClientTelephoneNumberEditText.setEnabled(true);
                dialogClientProblemEditText.setEnabled(true);
                dialogClientDateEditText.setEnabled(true);
                dialogClientModelEditText.setEnabled(true);

                String newSurname = dialogClientSurnameEditText.getText().toString();
                String newName = dialogClientNameEditText.getText().toString();
                String newPatronymic = dialogClientPatronymicEditText.getText().toString();
                String newTelephoneNumber = dialogClientTelephoneNumberEditText.getText().toString();
                String newProblem = dialogClientProblemEditText.getText().toString();
                String newDate = dialogClientDateEditText.getText().toString();
                String newModel = dialogClientModelEditText.getText().toString();
                String newStatus = statusSpinner.getSelectedItem().toString();

                updateClientInFirebase(client, newSurname, newName, newPatronymic, newTelephoneNumber, newProblem, newDate, newModel, newStatus);
                addNotificationToHomeFragment("Статус заявки для модели: " + client.getModel() + " изменен на " + newStatus);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateClientInFirebase(Client client, String newSurname, String newName, String newPatronymic, String newTelephoneNumber, String newProblem, String newDate, String newModel, String newStatus) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("clients").child(client.getFirebaseKey());
        Client updatedClient = new Client(newSurname, newName, newPatronymic, newTelephoneNumber, newProblem, newDate, newModel, client.getImageUrl(), newStatus);
        databaseReference.setValue(updatedClient)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(requireContext(), "Данные клиента успешно обновлены", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Ошибка при обновлении данных клиента: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_add_client, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        TextInputEditText surnameEditText = bottomSheetView.findViewById(R.id.text_input_surname);
        TextInputEditText nameEditText = bottomSheetView.findViewById(R.id.text_input_name);
        TextInputEditText patronymicEditText = bottomSheetView.findViewById(R.id.text_input_patronymic);
        TextInputEditText problemEditText = bottomSheetView.findViewById(R.id.text_input_problem);
        TextInputEditText modelEditText = bottomSheetView.findViewById(R.id.text_input_telephone_model);
        TextInputEditText telephoneNumberEditText = bottomSheetView.findViewById(R.id.text_input_telephone_number);
        TextInputEditText dateEditText = bottomSheetView.findViewById(R.id.text_input_date);

        MaskImpl mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
        MaskFormatWatcher formatWatcher = new MaskFormatWatcher(mask);
        formatWatcher.installOn(telephoneNumberEditText);

        dateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerDialog(requireContext(), dateEditText);
                } else {
                    hideKeyboard(requireContext(), v);
                }
            }
        });

        Button sendButton = bottomSheetView.findViewById(R.id.button_add);
        Button cameraButton = bottomSheetView.findViewById(R.id.button_camera);
        imagePreview = bottomSheetView.findViewById(R.id.imagePreview);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String surname = surnameEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String patronymic = patronymicEditText.getText().toString();
                String telephoneNumber = telephoneNumberEditText.getText().toString();
                String problem = problemEditText.getText().toString();
                String date = dateEditText.getText().toString();
                String model = modelEditText.getText().toString();

                String status = "В процессе";

                Bitmap imageBitmap = null;
                if (imagePreview.getDrawable() != null) {
                    imageBitmap = ((BitmapDrawable) imagePreview.getDrawable()).getBitmap();
                }

                if (imageBitmap != null) {
                    uploadImageToStorage(imageBitmap, surname, name, patronymic, telephoneNumber, problem, date, model, status);
                    addNotificationToHomeFragment("Новая заявка добавлена для модели: " + model);
                } else {
                    Toast.makeText(requireContext(), "Добавьте изображение", Toast.LENGTH_SHORT).show();
                }

                bottomSheetDialog.dismiss();
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    openCamera();
                }
            }
        });

        bottomSheetDialog.show();
    }

    private void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void uploadImageToStorage(Bitmap imageBitmap, String surname, String name, String patronymic, String telephoneNumber, String problem, String date, String model, String status) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        String imageName = UUID.randomUUID().toString() + ".jpg";

        StorageReference imageRef = storageRef.child("images/" + imageName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();

                        DatabaseReference clientsRef = FirebaseDatabase.getInstance().getReference("clients");
                        Client newClient = new Client(surname, name, patronymic, telephoneNumber, problem, date, model, imageUrl, status); // Добавлен параметр статуса
                        clientsRef.push().setValue(newClient);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Ошибка при загрузке изображения: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Ошибка при загрузке изображения: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(requireContext(), "Доступ к камере отклонен", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imagePreview.setImageBitmap(imageBitmap);
            imagePreview.setVisibility(View.VISIBLE);
        }
    }

    private void showDatePickerDialog(Context context, final TextInputEditText dateEditText) {
        Calendar calendar = Calendar.getInstance();
        String currentDateStr = dateEditText.getText().toString();
        if (!TextUtils.isEmpty(currentDateStr)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                Date currentDate = sdf.parse(currentDateStr);
                calendar.setTime(currentDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, monthOfYear, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                        String formattedDate = sdf.format(selectedDate.getTime());
                        dateEditText.setText(formattedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }
}