package com.example.phonedialer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

public class ContactDetailsActivity extends AppCompatActivity {

    /*EVERYTHING INSIDE LINEAR LAYOUT WRAPPED IN SCROLL VIEW.
    BELOW VIEWS ARE INSERTED PROGRAMMATICALLY IF THEIR DATA IS AVAIALBLE.
    * PHOTO [DONE]
    * NAME [DONE]
    * NUMBER [DONE]
    * EMAIL ID
    * ADDRESS
    * CALL HISTORY*/

    LinearLayout bgLL;
    TextView nameTV, numberTV;
    ImageView contactIV;
    ListView callHistoryLV;

    ArrayList<String> phoneNumAL, callHistoryAL;
    ArrayAdapter<String> callHistoryAdapter;
    String contactName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        setTitle("Contact Details:");

        bgLL = findViewById(R.id.backgroundLinearLayout);
        nameTV = findViewById(R.id.nameTextView);
        numberTV = findViewById(R.id.numberTextView);
        contactIV = findViewById(R.id.contactImageView);
        callHistoryLV = findViewById(R.id.callHistoryListView);

        Intent objIntent = this.getIntent();
        contactName = objIntent.getStringExtra("CONTACT_NAME");

        phoneNumAL = new ArrayList<String>();
        callHistoryAL = new ArrayList<String>();
        callHistoryAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, callHistoryAL);
        callHistoryLV.setAdapter(callHistoryAdapter);

        //CURSOR STUFF TO FETCH DETAILS FOR THIS CONTACT
        Cursor contactCursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,
                "DISPLAY_NAME = '" + contactName + "'", null, null);
        Log.i("CONTACT_CURSOR", "Count is: " + contactCursor.getCount());

        nameTV.setText("\n" + contactName);

        if(contactCursor.moveToFirst())
        {
            String contactId = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));

            //By default, I'm inserting an icon in the place of contact's image, which gets overridden if contact has an image.
            contactIV.setImageResource(R.drawable.ic_person);

            //Retrieving contact photo.
            InputStream objIS = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId)), true);
            if(objIS != null)   //WORKING
            {
                Bitmap bmp = BitmapFactory.decodeStream(objIS);
                contactIV.setImageBitmap(bmp);
            }

            /*WORKING - ALSO, AN ALTERNATIVE METHOD IS TO USE THE FUNCTION ContactsContract.Contacts.openContactPhotoInputStream()
            Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
            Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            Cursor cursor = getContentResolver().query(photoUri,
                    new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);

            try {
                if (cursor.moveToFirst()) {
                    byte[] data = cursor.getBlob(0);
                    if (data != null) {
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                        Bitmap bmp = BitmapFactory.decodeStream(byteArrayInputStream);
                        contactIV.setImageBitmap(bmp);
                    }
                }
            } finally {
                cursor.close();
            }*/

            Cursor contactPhoneNumCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null, null);

            if(contactPhoneNumCursor.getCount() == 0)
            {
                Log.i("PHONE_NUMBER_CURSOR", "Count is 0");
                numberTV.setText("EMPTY CONTACT!");
            }
            else
            {
                //WILL FETCH CALL HISTORY OVER HERE - WORKING
                Cursor callHistoryCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                        "NAME = '" + contactName + "'", null, null);

                if(callHistoryCursor.getCount() == 0)
                {
                    callHistoryAL.add("NO CALL HISTORY");
                    callHistoryAdapter.notifyDataSetChanged();
                }
                else
                {
                    int number = callHistoryCursor.getColumnIndex(CallLog.Calls.NUMBER);
                    int type = callHistoryCursor.getColumnIndex(CallLog.Calls.TYPE);
                    int date = callHistoryCursor.getColumnIndex(CallLog.Calls.DATE);
                    int duration = callHistoryCursor.getColumnIndex(CallLog.Calls.DURATION);
                    int phoneAcctId = callHistoryCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);     //SIM Info - Returns Subscription ID + ICC ID

                    String strSubId_ICCId_slot_1 = null;
                    String strSubId_ICCId_slot_2 = null;

                    SubscriptionManager objSubscriptionManager = (SubscriptionManager) getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                        {
                            strSubId_ICCId_slot_1 = String.valueOf(objSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0).getSubscriptionId()) + String.valueOf(objSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0).getIccId());
                            strSubId_ICCId_slot_2 = String.valueOf(objSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(1).getSubscriptionId()) + String.valueOf(objSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(1).getIccId());
                            Log.i("SLOT_1_SLOT_2", "Slot 1: " + strSubId_ICCId_slot_1 + "\tSlot 2: " + strSubId_ICCId_slot_2);  //I/SLOT_1_SLOT_2: Slot 1: 189918580400024451624	Slot 2: 38991000900298369250
                        }
                    }

                    //NO NEED TO CLEAR CALL HISTORY ARRAY LIST, BECAUSE THIS ACTIVITY DOESN'T REPEATEDLY FETCH THE CALL HISTORY.
                    //UPON ACTIVITY FINISH, AUTOMATICALLY ARRAY LIST WILL BE CLEARED (GARBAGE COLLECTED).
                    while(callHistoryCursor.moveToNext())
                    {
                        StringBuffer sb = new StringBuffer();
                        sb.append("Number:--- " + callHistoryCursor.getString(number));

                        String strPhoneAcctId = callHistoryCursor.getString(phoneAcctId);   //SIM 1 - 189918580400024451624   SIM 2 - 38991000900298369250
                        String strSim = null;

                        if(strPhoneAcctId.equals(strSubId_ICCId_slot_1))
                        {
                            strSim = "SIM 1";
                        }
                        else if(strPhoneAcctId.equals(strSubId_ICCId_slot_2))
                        {
                            strSim = "SIM 2";
                        }
                        sb.append("\nSIM No.:--- " + strSim);

                        String callType = callHistoryCursor.getString( type );
                        String callDate = callHistoryCursor.getString( date );
                        Date callDayTime = new Date(Long.valueOf(callDate));
                        String callDuration = callHistoryCursor.getString( duration );

                        String dir = null;
                        int dircode = Integer.parseInt( callType );
                        switch( dircode )
                        {
                            case CallLog.Calls.INCOMING_TYPE:   //1
                                dir = "INCOMING";
                                break;

                            case CallLog.Calls.OUTGOING_TYPE:   //2
                                dir = "OUTGOING";
                                break;

                            case CallLog.Calls.MISSED_TYPE:     //3
                                dir = "MISSED";
                                break;

                            case CallLog.Calls.REJECTED_TYPE:   //5
                                dir = "REJECTED";
                                break;

                            case CallLog.Calls.BLOCKED_TYPE:   //6
                                dir = "BLOCKED";
                                break;
                        }
                        sb.append("\nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall Duration (sec) :--- " + callDuration);
                        //Log.i("CALL_HISTORY", ": " + sb.toString());
                        callHistoryAL.add(String.valueOf(sb));
                        sb.delete(0, sb.length());
                    }
                    callHistoryAdapter.notifyDataSetChanged();
                    callHistoryCursor.close();
                }

                //WILL FETCH ALL PHONE NUMBERS OF THIS CONTACT.
                while(contactPhoneNumCursor.moveToNext())
                {
                    String strContactPhoneNum = contactPhoneNumCursor.getString(contactPhoneNumCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if(!phoneNumAL.contains(strContactPhoneNum))
                    {
                        Log.i("PHONE_NUMBER", "is: " + strContactPhoneNum);
                        phoneNumAL.add(strContactPhoneNum);
                        numberTV.append("\n" + strContactPhoneNum + "\n");
                    }
                }
            }
            contactPhoneNumCursor.close();
        }
        contactCursor.close();



        /*for(int i=0; i<phoneNumAL.size(); i++)
        {
            TextView objTextView = new TextView(getApplicationContext());
            objTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }*/


        /*ImageView objIV = new ImageView(getApplicationContext());   //Programmatically creating imageViews inside linearLayout.
        objIV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));//width/height*/
    }


    /* Get raw contact id by contact given name and family name.
     *  Return raw contact id.
     * */
    //private long getRawContactIdByName(String givenName, String familyName)
    private long getRawContactIdByName(String givenName)
    {
        ContentResolver contentResolver = getContentResolver();

        // Query raw_contacts table by display name field ( given_name family_name ) to get raw contact id.

        // Create query column array.
        String queryColumnArr[] = {ContactsContract.RawContacts._ID};

        // Create where condition clause.
        //String displayName = givenName + " " + familyName;
        String displayName = givenName;
        String whereClause = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " = '" + displayName + "'";

        // Query raw contact id through RawContacts uri.
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

        // Return the query cursor.
        Cursor cursor = contentResolver.query(rawContactUri, queryColumnArr, whereClause, null, null);

        long rawContactId = -1;

        if(cursor!=null)
        {
            // Get contact count that has same display name, generally it should be one.
            int queryResultCount = cursor.getCount();
            Log.i("QUERY_RESULT_COUNT", "is: " + queryResultCount);
            // This check is used to avoid cursor index out of bounds exception. android.database.CursorIndexOutOfBoundsException
            if(queryResultCount > 0)
            {
                // Move to the first row in the result cursor.
                cursor.moveToFirst();
                // Get raw_contact_id.
                rawContactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
            }
        }

        return rawContactId;
    }

/////////////////////////DELETE/////////////////////////////

    /*
     * Delete contact by it's display name.
     * */
    private void deleteContact(String givenName)
    {
        // First select raw contact id by given name and family name.
        //long rawContactId = getRawContactIdByName(givenName, familyName);
        long rawContactId = getRawContactIdByName(givenName);
        Log.i("RAW_CONTACT_ID", "is: " + rawContactId);

        ContentResolver contentResolver = getContentResolver();

        //******************************* delete data table related data ****************************************
        // Data table content process uri.
        Uri dataContentUri = ContactsContract.Data.CONTENT_URI;

        // Create data table where clause.
        StringBuffer dataWhereClauseBuf = new StringBuffer();
        dataWhereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
        dataWhereClauseBuf.append(" = ");
        dataWhereClauseBuf.append(rawContactId);

        // Delete all this contact related data in data table.
        contentResolver.delete(dataContentUri, dataWhereClauseBuf.toString(), null);


        //******************************** delete raw_contacts table related data ***************************************
        // raw_contacts table content process uri.
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

        // Create raw_contacts table where clause.
        StringBuffer rawContactWhereClause = new StringBuffer();
        rawContactWhereClause.append(ContactsContract.RawContacts._ID);
        rawContactWhereClause.append(" = ");
        rawContactWhereClause.append(rawContactId);

        // Delete raw_contacts table related data.
        contentResolver.delete(rawContactUri, rawContactWhereClause.toString(), null);


        //******************************** delete contacts table related data ***************************************
        // contacts table content process uri.
        Uri contactUri = ContactsContract.Contacts.CONTENT_URI;

        // Create contacts table where clause.
        StringBuffer contactWhereClause = new StringBuffer();
        contactWhereClause.append(ContactsContract.Contacts._ID);
        contactWhereClause.append(" = ");
        contactWhereClause.append(rawContactId);

        // Delete raw_contacts table related data.
        contentResolver.delete(contactUri, contactWhereClause.toString(), null);

        Toast.makeText(getApplicationContext(), "Contact Deleted!", Toast.LENGTH_SHORT).show();
    }

    /*public void deleteTest(View view)
    {

    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if(requestCode == 3)
                {
                    Log.i("PERMISSION", "Granted upon Request!");
                    //deleteContact("test", "contact");
                    deleteContact(contactName);
                }
            }
            else
            {
                Log.i("PERMISSION", "Denied upon Request!");
                Toast.makeText(getApplicationContext(), "Please grant permission to proceed further.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = new MenuInflater(getApplicationContext());
        menuInflater.inflate(R.menu.contact_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.deleteContact:
                Log.i("MENU_OPTION", "Delete Contact Menu Option clicked.");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED)
                        requestPermissions(new String[] {Manifest.permission.WRITE_CONTACTS}, 3);
                    else
                        deleteContact(contactName);
                }
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}