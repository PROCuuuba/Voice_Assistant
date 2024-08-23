package com.example.voiceassistant.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.voiceassistant.Classes.Client;
import com.example.voiceassistant.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ClientAdapter extends ArrayAdapter<Client> {

    private List<Client> clients;

    public ClientAdapter(Context context, List<Client> clients) {
        super(context, 0, clients);
        this.clients = clients;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false);
        }

        Client client = getItem(position);

        TextView clientSurnameTextView = convertView.findViewById(R.id.clientSurnameTextView);
        TextView clientNameTextView = convertView.findViewById(R.id.clientNameTextView);
        TextView clientPatronymicTextView = convertView.findViewById(R.id.clientPatronymicTextView);
        TextView clientTelephoneNumberTextView = convertView.findViewById(R.id.clientTelephoneNumberTextView);
        TextView clientProblemTextView = convertView.findViewById(R.id.clientProblemTextView);
        TextView clientDateTextView = convertView.findViewById(R.id.clientDateTextView);
        TextView clientModelTextView = convertView.findViewById(R.id.clientModelTextView);
        ImageView phoneImageView = convertView.findViewById(R.id.phoneImageView);
        ImageView orderStatusIndicator = convertView.findViewById(R.id.orderStatusIndicator);

        clientSurnameTextView.setText(client.getSurname());
        clientNameTextView.setText(client.getName());
        clientPatronymicTextView.setText(client.getPatronymic());
        clientTelephoneNumberTextView.setText(client.getTelephoneNumber());
        clientProblemTextView.setText(client.getProblem());
        clientDateTextView.setText(client.getDate());
        clientModelTextView.setText(client.getModel());

        if (client.getImageUrl() != null && !client.getImageUrl().isEmpty()) {
            Picasso.with(getContext()).load(client.getImageUrl()).into(phoneImageView);
        } else {
            phoneImageView.setImageDrawable(null);
        }

        String status = client.getStatus();
        if (status.equals("Готов")) {
            orderStatusIndicator.setImageResource(R.drawable.circle_green);
        } else if (status.equals("В процессе")) {
            orderStatusIndicator.setImageResource(R.drawable.circle_yellow);
        } else if (status.equals("Отменён")) {
            orderStatusIndicator.setImageResource(R.drawable.circle_red);
        } else {
            // orderStatusIndicator.setImageResource(R.drawable.default_status_icon);
        }

        return convertView;
    }

    public void updateList(List<Client> newList) {
        clients.clear();
        clients.addAll(newList);
        notifyDataSetChanged();
    }
}