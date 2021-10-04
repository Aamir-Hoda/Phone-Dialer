package com.example.phonedialer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phonedialer.R;

import java.util.ArrayList;

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.ContactsViewHolder> {

    Context context;
    ArrayList<String> contactsArrayList;

    public ContactsRecyclerViewAdapter(Context context, ArrayList<String> contactsArrayList) {
        this.context = context;
        this.contactsArrayList = contactsArrayList;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View rowView = layoutInflater.inflate(R.layout.contacts_adapter_row, parent, false);
        ContactsViewHolder contactsViewHolder = new ContactsViewHolder(rowView);

        return contactsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {



    }

    @Override
    public int getItemCount() {
        return contactsArrayList.size();
    }

    /*VIEWHOLDER STATIC CLASS*/
    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView contactNameTextView;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            contactNameTextView = itemView.findViewById(R.id.contactNameTextView);
        }
    }
}
