package com.example.phonedialer;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

public class MyListener implements AdapterView.OnItemClickListener, View.OnLongClickListener {

    MainActivity objMainActivity;
    //Context context;
    static MyListener myListenerInstance;

    MyListener(Context context)
    {
        objMainActivity = (MainActivity) context;
        //this.context = context;
        Log.i("MY_LISTENER_CONSTRUCTOR", "Inside MyListener Parameterized Constructor.");
    }

    synchronized static MyListener getInstance(Context context)
    {
        Log.i("MY_LISTENER_INSTANCE", "Inside MyListener Instance.");
        if(myListenerInstance == null)
        {
            myListenerInstance = new MyListener(context);
            Log.i("MY_LISTENER_INSTANCE", "After initializing MyListener Instance.");
        }
        return myListenerInstance;
    }

    public void myOnDestroy()
    {
        /*if(context != null)
        {   context = null; }*/

        Log.i("MY_ON_DESTROY", "myOnDestroy called from MyListener.");
        if(objMainActivity != null)
        {
            Log.i("OBJ_MAIN_ACTIVITY", "objMainActivity was not null.");
            objMainActivity = null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {

    }


    @Override
    public boolean onLongClick(View view)
    {
        if(view.getId() == R.id.eraseNumButton)
        {
            Log.i("ERASE_BTN", "Erase Button was clicked.");
            objMainActivity.phoneNumberET.getText().clear();
        }
        Log.i("ON_LONG_CLICK", "Logging from MyListener");

        return true;
    }
}
