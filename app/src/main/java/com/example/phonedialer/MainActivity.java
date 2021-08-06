package com.example.phonedialer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import static android.provider.ContactsContract.*;
import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {

    /*HAVE TO CHECK HOW TO SET MY APP AS DEFAULT APP FOR CALLING FUNCTIONALITY.*/

    Intent callIntent;
    TelecomManager telecomManager;

    //INSTEAD OF USING AN ARRAYLIST, I COULD PLACE TEXT VIEWS, THUS ALLOWING CUSTOMIZATION OF NUM PAD.
    ArrayList<String> numberPadAL = new ArrayList<String>(asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#", "", "+", ""));
    ArrayList<String> matchingContactsNameAL = new ArrayList<String>();
    ArrayList<String> matchingContactsNumAL = new ArrayList<String>();
    ArrayAdapter<String> objArrayAdapter, objArrayAdapter2;
    GridView numPadGV;
    EditText phoneNumberET;
    Button eraseNumBtn;
    ListView matchingContactsLV;

    MyListener objMyListener;   //TESTING - WORKING

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("ON_CREATE", "MainActivity");

        numPadGV = findViewById(R.id.numPadGridView);
        objArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, numberPadAL);
        //Instead of default layout, I could use my own custom layout to improve my design for number pad.
        numPadGV.setAdapter(objArrayAdapter);

        matchingContactsLV = findViewById(R.id.matchingContactsListView);
        objArrayAdapter2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, matchingContactsNameAL);
        matchingContactsLV.setAdapter(objArrayAdapter2);

        phoneNumberET = findViewById(R.id.phoneNumberEditText);
        eraseNumBtn = findViewById(R.id.eraseNumButton);

        phoneNumberET.setShowSoftInputOnFocus(false);   //WORKING - Soft keyboard not visible even upon selecting text & cursor not lost.

        //objMyListener = new MyListener(getApplicationContext());
        objMyListener = MyListener.getInstance(this);

        eraseNumBtn.setOnLongClickListener(objMyListener);
        //SHOULD BE BETTER DOING THIS AND OTHER REGISTERING OF LISTENERS IN ONSTART() METHOD

        phoneNumberET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (phoneNumberET.getText().toString().matches(""))
                {
                    phoneNumberET.clearFocus();
                    phoneNumberET.setVisibility(View.INVISIBLE);
                    eraseNumBtn.setVisibility(View.GONE);

                    matchingContactsNameAL.clear();
                    matchingContactsNumAL.clear();
                    objArrayAdapter2.notifyDataSetChanged();
                }
                else
                {   //Get contact suggestions as number is typed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                            getContactByNumber(phoneNumberET.getText().toString(), getApplicationContext());
                        }
                        else {
                            Log.i("CONTACT", "Grant contact permission to view matching contacts.");
                            Toast.makeText(getApplicationContext(), "Grant Contacts Permission!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        numPadGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String pressedNum = numPadGV.getItemAtPosition(i).toString();
                Log.i("PRESSED_NUM", pressedNum);
                //phoneNumberET.append(pressedNum);   //WORKING

                if(!pressedNum.matches("")) //WORKING
                {
                    if (phoneNumberET.getVisibility() != View.VISIBLE) {
                        phoneNumberET.setVisibility(View.VISIBLE);
                        eraseNumBtn.setVisibility(View.VISIBLE);
                    }
                    /*if (eraseNumBtn.getVisibility() != View.VISIBLE) {
                        eraseNumBtn.setVisibility(View.VISIBLE);
                    }*/

                    /*Now I can insert number at cursor position & also replace selected numbers.*/
                    //int cursorPosition = phoneNumberET.getSelectionStart(); //Both getSelectionStart() & getSelectionEnd() return the same position if no text is selected.
                    int start = Math.max(phoneNumberET.getSelectionStart(), 0);
                    int end = Math.max(phoneNumberET.getSelectionEnd(), 0);
                    phoneNumberET.getText().replace(Math.min(start, end), Math.max(start, end), pressedNum, 0, pressedNum.length());
                    //WORKING
                }
            }
        });

        matchingContactsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                phoneNumberET.setText(matchingContactsNumAL.get(i));
            }
        });

        /*These below managers are basically used for complete designing of phone dialer app, so that developers can create a standalone app,
        which will not redirect users to system's default phone app for any call related services.*/
        telecomManager = (TelecomManager) this.getSystemService(TELECOM_SERVICE);   //telecom

        //TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);   //phone
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("ON_START", "MainActivity");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            {
                Log.i("onStart()", "Requesting Contacts Permission!");
                requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, 6);
            }
            else
            {
                Log.i("onStart()", "Contacts Permission already Granted!");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ON_RESUME", "MainActivity");

        if(!phoneNumberET.getText().toString().matches(""))
        {
            getContactByNumber(phoneNumberET.getText().toString(), getApplicationContext());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("ON_PAUSE", "MainActivity");
        phoneNumberET.clearFocus();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("ON_STOP", "MainActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        objMyListener.myOnDestroy();
        /*This should remove the reference of MainActivity in MyListener class, thus allowing GC to free memory after MainActivity
        * is destroyed.*/
    }

    public void eraseNum(View view)
    {
        phoneNumberET.requestFocus();
        dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)); //THIS WORKS WHEN CURSOR IS VISIBLE ON EDIT TEXT
        //Here I'm mimicking the DEL key to delete number in editText.
    }

    public void callPhoneViaTelecomManager(View view)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            {
                Log.i("PERMISSION", "Permission not granted, Requesting permission...");
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
            }
            else
            {
                String strPhoneNumber = phoneNumberET.getText().toString();
                Log.i("PHONE_NUMBER", "Entered Phone Number is: " + strPhoneNumber);

                if(!strPhoneNumber.matches(""))
                {
                    Log.i("PERMISSION", "Permission ALREADY granted! Placing call via TelecomManager...");
                    /*Needs Permission: <uses-permission android:name="android.permission.CALL_PHONE"/>*/
                    telecomManager.placeCall(Uri.parse("tel:" + strPhoneNumber), null);     //WORKING PERFECTLY FINE - CALL PLACED
                }
                else
                    Toast.makeText(getApplicationContext(), "No Phone Number to call!", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            //THINKING ABOUT USING INTENT OVER HERE FOR OS LOWER THAN MARSHMALLOW.
            Log.i("INSIDE_ELSE_<_M", "Calling from device having OS lower than Marshmallow.");
            String strPhoneNumber = phoneNumberET.getText().toString();
            if(!strPhoneNumber.matches(""))
            {
                callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + strPhoneNumber));
                startActivity(callIntent);
            }
            else {
                Log.i("PHONE_NUMBER", "No phone number to call.");
                Toast.makeText(getApplicationContext(), "No Phone Number to call!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void callPhoneViaIntent(View view)
    {   //CALLING VIA INTENT ALLLOWS CALLING ON DEVICES LOWER THAN LOLLIPOP
        String strPhoneNumber = phoneNumberET.getText().toString();
        if(strPhoneNumber.matches(""))
            Toast.makeText(getApplicationContext(), "No Phone Number to call!", Toast.LENGTH_SHORT).show();
        else
        {
            callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + strPhoneNumber));
            //"tel:" prefix is used because it specifies the type of data being passed into the intent.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                /*This version check is done over here because the feature to check for user permission at run-time is not available in android devices
                 * running on OS lower than Marshmallow (M), thus resulting in app crash, if this app runs on devices lower than Marshmallow OS;
                 * which is possible, because in this app's manifest, I've specified minSdkVersion = 22 (Lollipop)*/

                if(checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                {
                    Log.i("PERMISSION", "Permission not granted, Requesting permission...");
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
                }
                else
                {
                /*PackageManager - Class for retrieving various kinds of information related to the application packages that are currently installed on the device.
                 You can find this class through Context#getPackageManager.*/
                /*resolveActivity() - Returns the Activity component that should be used to handle this intent. The appropriate component is determined based on the
                 information in the intent.*/

                    if(callIntent.resolveActivity(getPackageManager()) != null)
                    {
                    /*This condition check verifies if the device has an app to handle the intent activity. It'll only work after permission has been provided.
                    It probably does so by checking all the apps' manifest file for intent-filter.
                    * For ex: If the device doesn't have any app to handle phone calls, then we'll get null (possible in tablets).*/
                        Log.i("CALL_INTENT_RESOLVE_ACT", String.valueOf(callIntent.resolveActivity(getPackageManager())));
                        //CALL_INTENT_RESOLVE_ACT: ComponentInfo{com.android.server.telecom/com.android.server.telecom.components.UserCallActivity}

                        Log.i("PERMISSION", "Permission ALREADY granted! Placing call...");
                        /*NEEDS PERMISSION: <uses-permission android:name="android.permission.CALL_PHONE"/>*/
                        startActivity(callIntent);
                        //WORKING PERFECTLY FINE - CALL PLACED
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "No suitable application found to place call.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else
            {
                //OS IS LESS THAN MARSHMALLOW
                startActivity(callIntent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if(requestCode == 3)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                    {
                        Log.i("PERMISSION", "Permission granted NOW! Placing call...");
                        String strPhoneNumber = phoneNumberET.getText().toString();
                        if(strPhoneNumber.matches(""))
                            Toast.makeText(getApplicationContext(), "No Phone Number to call!", Toast.LENGTH_SHORT).show();
                        else
                        {
                            callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + strPhoneNumber));
                            startActivity(callIntent);  //WORKING PERFECTLY FINE - CALL PLACED
                        }
                    }
                }
            }
            else if(requestCode == 6)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                    {
                        Log.i("PERMISSION", "Contacts Permission granted after request!");
                    }
                    else
                    {
                        Log.i("PERMISSION", "Contacts Permission denied after request!");
                    }
                }
            }
        }
        else
        {
            Log.i("PERMISSION", "Permission Still Denied!");
            Toast.makeText(getApplicationContext(), "Please provide Permission to place call!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        new MenuInflater(getApplicationContext()).inflate(R.menu.dialer_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.add_contact)
        {
            if(!phoneNumberET.getText().toString().matches(""))
            {
                if(matchingContactsNumAL.contains(phoneNumberET.getText().toString()))
                {
                    Log.i("CONTACT_EXISTS", "Contact already exists.");
                    Toast.makeText(getApplicationContext(), "Contact already exists!", Toast.LENGTH_SHORT).show();
                    //OR CAN DISPLAY A DIALOG ALLOWING TO EDIT THE EXISTING CONTACT [WILL HAVE TO MAKE A NEW CURSOR OVER HERE]
                }
                else
                {
                    Intent objIntent = new Intent(Intents.Insert.ACTION);
                    objIntent.setType(RawContacts.CONTENT_TYPE);
                    objIntent.putExtra(Intents.Insert.PHONE, phoneNumberET.getText().toString());
                    objIntent.putExtra("finishActivityOnSaveCompleted", true);
                    startActivity(objIntent);
                }
            }
            else
            {
                Log.i("PHONE_NUMBER", "No phone number available to create contact!");
                Toast.makeText(getApplicationContext(), "No Phone Number Available!", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToCallLogs(View view)
    {
        Intent objIntent = new Intent(getApplicationContext(), CallLogsActivity.class);
        startActivity(objIntent);
    }
    public void goToContacts(View view)
    {
        Intent objIntent = new Intent(getApplicationContext(), ContactsActivity.class);
        startActivity(objIntent);
    }

    //Better to do all these retrievals in the background thread, using LoaderManager.
    public void getContactByNumber(final String phoneNumber, Context context) //WILL GIVE ME CONTACT NAME & NUMBER, UPON NUMBER INPUT
    {
        //Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode( phoneNumber ));   //WORKS, BUT RETURNS CONTACT ONLY UPON COMPLETE NUMBER
        Log.i("PHONE_NUMBER_OBTAINED", "is: " + phoneNumber);

        String[] projection = new String[]{CommonDataKinds.Phone.DISPLAY_NAME, CommonDataKinds.Phone.NUMBER};
        String selection = "DATA1 " +  " LIKE '%" + phoneNumber + "%'";

        Cursor cursor = context.getContentResolver().query(CommonDataKinds.Phone.CONTENT_URI,
                projection, selection,null,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if(cursor != null)
        {
            Log.i("CURSOR_ROW_COUNT", "is: " + cursor.getCount());
            Log.i("CURSOR_COLUMN_COUNT", "is: " + cursor.getColumnCount());
            Log.i("CURSOR_COLUMN_NAMES", "are: " + Arrays.toString(cursor.getColumnNames()));

            matchingContactsNumAL.clear();
            matchingContactsNameAL.clear();

            while(cursor.moveToNext())
            {
                if(!matchingContactsNameAL.contains(cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME))))
                {
                    matchingContactsNumAL.add(cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER)));
                    matchingContactsNameAL.add(cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME)));
                }
            }
            Log.i("MATCHING_CONTACTS_NO", matchingContactsNumAL.size() + " matched: " + String.valueOf(matchingContactsNumAL));
            Log.i("MATCHING_CONTACTS_NAME", matchingContactsNameAL.size() + " matched: " + String.valueOf(matchingContactsNameAL));
            cursor.close();
            objArrayAdapter2.notifyDataSetChanged();
        }
    }

    public void getContactByName()  //WILL GIVE ME CONTACT NAME & NUMBER, UPON NAME INPUT
    {
        //FUTURE ADDITION IN THE PROGRAM
    }
}