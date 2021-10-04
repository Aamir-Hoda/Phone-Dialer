package com.example.phonedialer.Repositories;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.phonedialer.ViewModels.ContactsViewModel;

import java.util.ArrayList;

public class ContactsRepository {

    private static final String TAG = "ContactsRepository";

    private static ContactsRepository contactsRepositoryInstance;
    private static Context applicationContext;
    private ArrayList<String> contactsArrayList = new ArrayList<>();

    /* Making a static function to have only one instance of Repository in the project.*/
    public static ContactsRepository getContactsRepositoryInstance(Context context)
    {
        Log.i(TAG, "getContactsRepositoryInstance: fired!");

        if(contactsRepositoryInstance == null)
        {
            contactsRepositoryInstance = new ContactsRepository();
            applicationContext = context;
            Log.i(TAG, "getContactsRepositoryInstance: was earlier NULL, Initialized NOW.");
        }
        return contactsRepositoryInstance;
    }

    /* This will be the private function inside the repository which will actually initialize the
    * contactsArrayList.*/
    private void setContactsArrayList()
    {
        Log.i(TAG, "setContactsArrayList: fired! Will initialize the contactsArrayList now.");

        //NOW JUST NEED TO PUT RELEVANT CODE FOR RETRIEVING CONTACTS

        ContentResolver cr = applicationContext.getContentResolver();

        Log.i(TAG, "setContactsArrayList: successfully got contentResolver through contactRepositoryInstance's context.");

    }
    /////////////////////******************************************/////////////////////////////////
    //Have to put relevant code from below function into setContactsArrayList()
//    public void getContacts()
//    {
//        //WOULD BE BETTER IF I WOULD RUN THIS IN THE BACKGROUND THREAD, BECAUSE DATABASE QUERIES SHOULD BE RUN IN THE BACKGROUND THREAD
//        //FOR APP PERFORMANCE OPTIMIZATION.
//        //Better to do all these retrievals in the background thread, using LoaderManager.
//
//
//
//        contactsAL.clear();
//        objArrayAdapter.notifyDataSetChanged();
//
//        //Taken From - https://www.tutorialspoint.com/how-to-read-all-contacts-in-android
//        ContentResolver cr = getContentResolver();  //WORKING
//        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
//                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
//        if ((cur != null ? cur.getCount() : 0) > 0)
//        {
//            Log.i("CURSOR_COUNT", "Contacts count in cursor " + cur.getCount());
//            while (cur.moveToNext())
//            {
//                try
//                {   //SOMEWHERE CODE BROKE DUE TO BLANK CONTACT [CONTACT WITHOUT ANY DETAILS], SHOWING IN CONTACTS AS "UNKNOWN CONTACT".
//                    //String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
//                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                    contactsAL.add(name);
//                    Log.i("CONTACT_NAME", "Contact Name: " + name);
//                }
//                catch (NullPointerException NPE)
//                {
//                    Log.i("NULL_EXCEPTION", "Null Exception caught " + NPE.getMessage());
//                    Toast.makeText(getApplicationContext(), "Check Contacts manually for UnKnown Contacts.", Toast.LENGTH_SHORT).show();
//                }
//                catch (Exception e)
//                {
//                    Log.i("EXCEPTION", "Some Exception caught " + e.getMessage());
//                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//
//                /*if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0)
//                {
//                    Cursor pCur = cr.query(
//                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                            null,
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                            new String[]{id}, null);
//                    while (pCur.moveToNext())
//                    {
//                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        phoneNumAL.add(phoneNo);
//                        Log.i("NAME_PHONE_NUM", "Name: " + name + ",  Phone No.: " + phoneNo);
//
//                        I/NAME_PHONE_NUM: Name: 9shin Phone No.: +918207837873
//                        I/NAME_PHONE_NUM: Name: 9shin Phone No.: +918207837873
//                        I/NAME_PHONE_NUM: Name: Aamir Hoda Phone No.: 7988821805
//                            Name: Aamir Hoda Phone No.: 9729805425
//                        I/NAME_PHONE_NUM: Name: Aamir Hoda Phone No.: 775-980-9906
//                        I/NAME_PHONE_NUM: Name: Aamir Hoda Phone No.: 775-980-9906
//                        I/NAME_PHONE_NUM: Name: Aamir Hoda Phone No.: 9729805425
//                            Name: Aamir Hoda Phone No.: 7988821805
//                        I/NAME_PHONE_NUM: Name: Abhishek Bhaiya Gym Phone No.: 9308884343
//                            Name: Abhishek Bhaiya Gym Phone No.: 9308884343     .....& so on.
//                    }
//                    pCur.close();
//                }*/
//            }
//            //contactsCountTV.setText("Total Contacts Present: " + contactsAL.size());
//            contactsCountTV.append(" " + contactsAL.size());
//        }
//        if (cur != null) {
//            cur.close();
//        }
//        Log.i("CONTACTS_AL_COUNT", "Number of contacts in arrayList: " + contactsAL.size());    //138
//        //SOMEWHERE CODE BROKE DUE TO BLANK CONTACT [CONTACT WITHOUT ANY DETAILS], SHOWING IN CONTACTS AS "UNKNOWN CONTACT".
//    }
    /////////////////////******************************************/////////////////////////////////

    /* This will be the public function, which will be called by the ViewModel to access the
    * contactsArrayList. */
    public ArrayList<String> getContacts()
    {
        Log.i(TAG, "getContacts: fired! Public method to access contacts of the repository " +
                "from viewModel.");

        setContactsArrayList();

        return contactsArrayList;
    }
}
