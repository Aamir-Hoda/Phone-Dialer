package com.example.phonedialer.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.example.phonedialer.Repositories.ContactsRepository;

import java.util.ArrayList;

//public class ContactsViewModel extends ViewModel {
public class ContactsViewModel extends AndroidViewModel {

    /* Instead of using ViewModel, I'm using AndroidViewModel which extends ViewModel,
    * because I need context in Repository class.
    * This AndroidViewModel will help me get Application Context in repository.
    * Alternatively I can use Application class having static application context.
    * Modern approach would be to use Dependency Injection (DI) to dynamically inject the
    * application context at places required.*/

    private static final String TAG = "ContactsViewModel";

    private Application application;
    private ContactsRepository contactsRepository;
    private ArrayList<String> contactsArrayList;

    public ContactsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    @NonNull
    public Application getApplication() {
        return application;
    }

    public void initContactsViewModel()
    {
        Log.i(TAG, "initContactsViewModel: fired!");

        if(contactsRepository != null)
        {
            Log.i(TAG, "initContactsViewModel: ContactsRepositoryInstance ALREADY initialized.");
            return;
        }
        
        contactsRepository = ContactsRepository.getContactsRepositoryInstance(getApplication().getApplicationContext());
        Log.i(TAG, "initContactsViewModel: ContactsRepository was NULL, now initialized!");
        contactsArrayList = contactsRepository.getContacts();
        Log.i(TAG, "initContactsViewModel: getContacts of TRIAL fired!");
    }
}
