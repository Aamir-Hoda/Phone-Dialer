package com.example.phonedialer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

//import com.baoyz.swipemenulistview.SwipeMenu;
//import com.baoyz.swipemenulistview.SwipeMenuCreator;
//import com.baoyz.swipemenulistview.SwipeMenuItem;
//import com.baoyz.swipemenulistview.SwipeMenuListView;

import com.example.phonedialer.Adapters.ContactsRecyclerViewAdapter;
import com.example.phonedialer.ViewModels.ContactsViewModel;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    private static final String TAG = "ContactsActivity";

    ArrayList<String> contactsAL, phNumberAL;
    ArrayAdapter<String> objArrayAdapter;

    TextView contactsCountTV;
    RecyclerView contactsRecyclerView;

    ContactsRecyclerViewAdapter contactsRecyclerViewAdapter;

    ContactsViewModel contactsViewModel;

//    SwipeMenuListView contactsSwipeLV;

    TelecomManager objTelecomManager;

    //Taken from: https://github.com/mitchtabian/SwipeMenuListView - somehow NOT working anymore.
    //Most probably the current gradle version (6.7.1) doesn't support deprecated libraries.
    //Not able to resolve com.baoyz.swipemenulistview.SwipeMenu; external library
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Log.i(TAG, "onCreate: fired!");

        setTitle("CONTACTS:");

        objTelecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);

        contactsCountTV = findViewById(R.id.contactsCountTextView);
        contactsRecyclerView = findViewById(R.id.contactsRecyclerView);

        phNumberAL = new ArrayList<String>();
        contactsAL = new ArrayList<String>();

        //contactsSwipeLV = findViewById(R.id.swipeMenuListView);

        contactsViewModel = new ViewModelProvider(ContactsActivity.this).get(ContactsViewModel.class);

        // THIS SHOULD RETURN ME AN ARRAYLIST SO THAT IT CAN BE SET OVER HERE
        contactsViewModel.initContactsViewModel();

        initContactsRecyclerView();

        objArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, contactsAL);

//        contactsSwipeLV.setAdapter(objArrayAdapter);
//        contactsSwipeLV.setFastScrollAlwaysVisible(true);
//        contactsSwipeLV.setFastScrollEnabled(true);

//        SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
//            @Override
//            public void create(SwipeMenu menu) {
//                SwipeMenuItem editItem = new SwipeMenuItem(getApplicationContext());
//                // set item background
//                editItem.setBackground(new ColorDrawable(Color.rgb(0x00, 0x66, 0xff)));
//                // set item width
//                editItem.setWidth(130);
//                // set item title
//                editItem.setTitle("Edit");
//                // set item title fontsize
//                editItem.setTitleSize(16);
//                // set item title font color
//                editItem.setTitleColor(Color.WHITE);
//                // add to menu
//                menu.addMenuItem(editItem);
//
//                // create "call" item
//                SwipeMenuItem callItem = new SwipeMenuItem(getApplicationContext());
//                // set item background
//                callItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
//                // set item width
//                callItem.setWidth(130);
//                // set a icon
//                callItem.setIcon(R.drawable.ic_phone);
//                // add to menu
//                menu.addMenuItem(callItem);
//
//                /*// create "delete" item
//                SwipeMenuItem callItem = new SwipeMenuItem(getApplicationContext());
//                // set item background
//                callItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
//                // set item width
//                callItem.setWidth(170);
//                // set a icon
//                callItem.setIcon(R.drawable.ic_phone);
//                // add to menu
//                menu.addMenuItem(callItem);*/
//            }
//        };

//        contactsSwipeLV.setMenuCreator(swipeMenuCreator);

//        contactsSwipeLV.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
//                switch (index) {
//                    case 0: //EDIT
//                        Log.d("ITEM_CLICK", "onMenuItemClick: clicked item " + index + " Name: " + menu.getMenuItem(0).getTitle());
//
//                        ContentResolver editCR = getContentResolver();  //WORKING
//                        Cursor editCursor = editCR.query(ContactsContract.Contacts.CONTENT_URI, null,
//                                "DISPLAY_NAME = '" + contactsSwipeLV.getItemAtPosition(position).toString() + "'",
//                                null, null);
//
//                        if (editCursor.moveToFirst()) {
//                            String editContactId = editCursor.getString(editCursor.getColumnIndex(ContactsContract.Contacts._ID));
//                            String editLookUpKey = editCursor.getString(editCursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
//
//                            Log.i("ID_LOOKUPKEY", "Id: " + editContactId + "\nLookup key: " + editLookUpKey);
//
//                            Intent editIntent = new Intent(Intent.ACTION_EDIT);
//                            editIntent.setDataAndType(ContactsContract.Contacts.getLookupUri(Long.parseLong(editContactId), editLookUpKey), ContactsContract.RawContacts.CONTENT_ITEM_TYPE);
//                            editIntent.putExtra("finishActivityOnSaveCompleted", true);
//                            //THIS WILL REDIRECT USER BACK TO MY APP AFTER SAVING THEIR RESPECTIVE CONTACT
//                            startActivity(editIntent);
//                        }
//                        editCursor.close();
//
//                        break;
//
//                    case 1: //CALL
//                        phNumberAL.clear();
//
//                        ContentResolver cr = getContentResolver();  //WORKING
//                        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
//                                "DISPLAY_NAME = '" + contactsSwipeLV.getItemAtPosition(position).toString() + "'",
//                                null, null);
//
//                        if (cursor.moveToFirst())
//                        {
//                            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//
//                            //  Get all phone numbers for the selected contact.
//                            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
//                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
//                                    null, null);
//
//                            while (phones.moveToNext())
//                            {
//                                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                                Log.i("RETRIEVED_NUMBER", "Name: " + contactsSwipeLV.getItemAtPosition(position).toString() + ",  No.: " + number);
//                                if (!phNumberAL.contains(number)) {
//                                    phNumberAL.add(number);
//                                }
//
//                                //BELOW LOCS NOT SO RELEVANT
//                                /*int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
//                                switch (type)
//                                {
//                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
//                                        // do something with the Home number here...
//                                        Log.i("NUMBER_TYPE", "Home");
//                                        break;
//
//                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//                                        // do something with the Mobile number here...
//                                        Log.i("NUMBER_TYPE", "Mobile");
//                                        break;
//
//                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//                                        // do something with the Work number here...
//                                        Log.i("NUMBER_TYPE", "Work");
//                                        break;
//                                }*/
//                            }
//                            phones.close();
//                        }
//                        cursor.close();
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(ContactsActivity.this);
//                        builder.setTitle("Make your selection:");
//                        //builder.setView(getLayoutInflater().inflate(R.layout.dialog_layout, null)); //LINEAR LAYOUT COMES AFTER THE ITEMS
//                        builder.setItems(phNumberAL.toArray(new String[phNumberAL.size()]), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int item)
//                            {
//                                Log.i("DIALOG", "Dialog Item Position: " + item + "\nText inside dialog at position: " + phNumberAL.get(item));
//
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                                {
//                                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
//                                    {
//                                        Toast.makeText(getApplicationContext(), "Please grant permission to proceed further!", Toast.LENGTH_SHORT).show();
//                                    }
//                                    else
//                                    {
//                                        Toast.makeText(getApplicationContext(), "Calling " + contactsSwipeLV.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
//                                        objTelecomManager.placeCall(Uri.parse("tel:" + phNumberAL.get(item)), null);
//                                    }
//                                }
//                            }
//                        });
//                        AlertDialog alert = builder.create();
//                        alert.show();
//
//                        //BELOW LOCS ALLOW ME TO CUSTOMIZE MY ALERT DIALOG, APPEARING WHEN PLACING A CALL.
//                        /*LayoutInflater inflater = getLayoutInflater();
//                        final View dialoglayout = inflater.inflate(R.layout.dialog_layout, null);
//                        LinearLayout dialogLL = dialoglayout.findViewById(R.id.dialog_layout_root);
//
//                        for(int i=0; i<phNumberAL.size(); i++)  //WORKING - TEXT VIEWS ADDED PROGRAMMATICALLY & LISTENERS WORKING PERFECTLY.
//                        {
//                            final TextView objTV = new TextView(getApplicationContext());
//                            objTV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                            objTV.setPadding(30,30,30,30);
//                            objTV.setTextColor(Color.rgb(52, 76, 235));
//                            objTV.setTextSize(20f);
//                            objTV.setText(phNumberAL.get(i));
//
//                            dialogLL.addView(objTV);
//
//                            objTV.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view)
//                                {
//                                    Log.i("DIALOG_FIRST_TV", "Text inside TextView of dialog: " + objTV.getText());
//                                    Toast.makeText(getApplicationContext(), "Calling " + contactsSwipeLV.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
//
//                                    //NEED PERMISSION CHECK & VERSION CHECK
//                                    objTelecomManager.placeCall(Uri.parse("tel:" + objTV.getText()), null);
//                                }
//                            });
//                        }
//
//                        final AlertDialog.Builder builder = new AlertDialog.Builder(ContactsActivity.this);
//                        builder.setView(dialoglayout);
//                        //builder.setTitle("Select to call:");
//                        builder.show();*/
//
//                        break;
//                }
//                // false : close the menu; true : not close the menu
//                return false;
//            }
//        });

//        contactsSwipeLV.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);   //Default direction is LEFT

//        contactsSwipeLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
//            {
//                Intent objIntent = new Intent(getApplicationContext(), ContactDetailsActivity.class);
//                objIntent.putExtra("CONTACT_NAME", contactsSwipeLV.getItemAtPosition(i).toString());
//                startActivity(objIntent);
//            }
//        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            {
                Log.i("PERMISSION_CHECK", "Requesting Contacts Permission");
                requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, 6);
            }
            else
            {
                Log.i("PERMISSION_CHECK", "Contacts Permission already granted!\nRetrieving Contacts...");
                getContacts();
            }
        }

    }

    private void initContactsRecyclerView() {

        Log.i(TAG, "initContactsRecyclerView: fired!");

        contactsRecyclerViewAdapter = new ContactsRecyclerViewAdapter(ContactsActivity.this, contactsAL);

        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(ContactsActivity.this));
        contactsRecyclerView.setAdapter(contactsRecyclerViewAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume()", "Fired!");
    }

    public void getContacts()
    {
        //WOULD BE BETTER IF I WOULD RUN THIS IN THE BACKGROUND THREAD, BECAUSE DATABASE QUERIES SHOULD BE RUN IN THE BACKGROUND THREAD
        //FOR APP PERFORMANCE OPTIMIZATION.
        //Better to do all these retrievals in the background thread, using LoaderManager.

        contactsAL.clear();
        objArrayAdapter.notifyDataSetChanged();

        //Taken From - https://www.tutorialspoint.com/how-to-read-all-contacts-in-android
        ContentResolver cr = getContentResolver();  //WORKING
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if ((cur != null ? cur.getCount() : 0) > 0)
        {
            Log.i("CURSOR_COUNT", "Contacts count in cursor " + cur.getCount());
            while (cur.moveToNext())
            {
                try
                {   //SOMEWHERE CODE BROKE DUE TO BLANK CONTACT [CONTACT WITHOUT ANY DETAILS], SHOWING IN CONTACTS AS "UNKNOWN CONTACT".
                    //String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    contactsAL.add(name);
                    Log.i("CONTACT_NAME", "Contact Name: " + name);
                }
                catch (NullPointerException NPE)
                {
                    Log.i("NULL_EXCEPTION", "Null Exception caught " + NPE.getMessage());
                    Toast.makeText(getApplicationContext(), "Check Contacts manually for UnKnown Contacts.", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Log.i("EXCEPTION", "Some Exception caught " + e.getMessage());
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                /*if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0)
                {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext())
                    {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumAL.add(phoneNo);
                        Log.i("NAME_PHONE_NUM", "Name: " + name + ",  Phone No.: " + phoneNo);

                        I/NAME_PHONE_NUM: Name: 9shin Phone No.: +918207837873
                        I/NAME_PHONE_NUM: Name: 9shin Phone No.: +918207837873
                        I/NAME_PHONE_NUM: Name: Aamir Hoda Phone No.: 7988821805
                            Name: Aamir Hoda Phone No.: 9729805425
                        I/NAME_PHONE_NUM: Name: Aamir Hoda Phone No.: 775-980-9906
                        I/NAME_PHONE_NUM: Name: Aamir Hoda Phone No.: 775-980-9906
                        I/NAME_PHONE_NUM: Name: Aamir Hoda Phone No.: 9729805425
                            Name: Aamir Hoda Phone No.: 7988821805
                        I/NAME_PHONE_NUM: Name: Abhishek Bhaiya Gym Phone No.: 9308884343
                            Name: Abhishek Bhaiya Gym Phone No.: 9308884343     .....& so on.
                    }
                    pCur.close();
                }*/
            }
            //contactsCountTV.setText("Total Contacts Present: " + contactsAL.size());
            contactsCountTV.append(" " + contactsAL.size());
        }
        if (cur != null) {
            cur.close();
        }
        Log.i("CONTACTS_AL_COUNT", "Number of contacts in arrayList: " + contactsAL.size());    //138
        //SOMEWHERE CODE BROKE DUE TO BLANK CONTACT [CONTACT WITHOUT ANY DETAILS], SHOWING IN CONTACTS AS "UNKNOWN CONTACT".
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            getContacts();
        }
        else
        {
            Log.i("PERMISSION", "Contacts Permission denied upon request.");
            Toast.makeText(ContactsActivity.this, "Please provide permission to display contacts!", Toast.LENGTH_SHORT).show();

            setTitle("Grant Contact Permission.");
        }
    }
}