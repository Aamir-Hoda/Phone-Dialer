package com.example.phonedialer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.util.ArrayList;
import java.util.Date;

//@AcraToast(resText = R.string.app_crash, length = 1)
public class CallLogsActivity extends AppCompatActivity {

    ArrayList<String> callLogsAL;
    ArrayAdapter<String> objArrayAdapter;
    ListView callLogsLV;

    Cursor managedCursor;
    SubscriptionManager objSubscriptionManager;

    protected SwipeActionAdapter mAdapter;

    //Taken From: https://github.com/wdullaer/SwipeActionAdapter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_logs);
        //try {
            setTitle("CALL LOGS:");

            callLogsAL = new ArrayList<String>();
            callLogsLV = findViewById(R.id.callLogsListView);
            objArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, callLogsAL);
            //callLogsLV.setAdapter(objArrayAdapter);

            mAdapter = new SwipeActionAdapter(objArrayAdapter);
            mAdapter.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener() {
                @Override
                public boolean hasActions(int position, SwipeDirection direction) {
                    //if(direction.isLeft()) return true;
                    if (direction.isRight()) return true;
                    return false;
                }

                @Override
                public boolean shouldDismiss(int position, SwipeDirection direction) {
                    return false;
                }

                @Override
                public void onSwipe(int[] positionList, SwipeDirection[] directionList) {
                    for (int i = 0; i < positionList.length; i++) {
                        SwipeDirection direction = directionList[i];
                        int position = positionList[i];
                        String dir = "";
                        Log.i("DIRECTION_POSITION", "Direction: " + direction + "\nPosition: " + position);
                        switch (direction) {
                        /*case DIRECTION_FAR_LEFT:
                            dir = "Far left";
                            break;
                        case DIRECTION_NORMAL_LEFT:
                            dir = "Left";
                            break;*/
                            case DIRECTION_NORMAL_RIGHT:
                                //dir = "Right";
                                //break;
                            case DIRECTION_FAR_RIGHT:
                            /*AlertDialog.Builder builder = new AlertDialog.Builder(CallLogsActivity.this);
                            builder.setTitle("Test Dialog").setMessage("You swiped right").create().show();*/
                                //dir = "Far right";

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 4);
                                    } else {
                                        /*Name: dvcabc     OR
                                         * Phone Number: 6754378
                                         * SIM:
                                         * ......
                                         * ......*/

                                        String swipedItem = String.valueOf(mAdapter.getItem(position));
                                        Log.i("SWIPED_ITEM", "is: " + swipedItem);
                                        String strFetchedData = swipedItem.substring(0, swipedItem.indexOf("SIM No.:"));

                                        String phoneNumber = null;
                                        if (swipedItem.contains("Phone Number:")) {
                                            Log.i("INTERMEDIATE_DATA", "Substring on swiped item: " + strFetchedData);

                                            phoneNumber = strFetchedData.substring(17);
                                            Log.i("NUMBER", "is: " + phoneNumber);
                                        } else {
                                            managedCursor.moveToPosition(position);
                                            phoneNumber = managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.NUMBER));
                                            Log.i("PHONE_NUMBER_CURSOR", "Phone Number for " + strFetchedData + " is " + phoneNumber);
                                        }

                                        TelecomManager objTelecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
                                        objTelecomManager.placeCall(Uri.parse("tel:" + phoneNumber), null);
                                    }
                                }
                                break;
                        }
                        //Toast.makeText(getApplicationContext(), dir + " swipe Action triggered on " + mAdapter.getItem(position), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "Calling", Toast.LENGTH_SHORT).show();
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
            mAdapter.setDimBackgrounds(true)
                    .setListView(callLogsLV);

            callLogsLV.setAdapter(mAdapter);

            mAdapter/*.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.row_bg_left_far)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT, R.layout.row_bg_left)*/
                    .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT, R.layout.row_bg_right_far)
                    .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT, R.layout.row_bg_right);


            objSubscriptionManager = (SubscriptionManager) getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 6);
                } else {
                    Log.i("ACTIVE_SUB_INFO_LIST", "Active Sub Info List: " + objSubscriptionManager.getActiveSubscriptionInfoList());
                    Log.i("SLOT_1", "Slot 1: " + objSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0));
                    Log.i("SLOT_1", "Slot 1: Subscription ID: " + objSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0).getSubscriptionId());
                    Log.i("SLOT_1", "Slot 1: ICC ID: " + objSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0).getIccId());
                    Log.i("SLOT_1", "Slot 1: Number: " + objSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0).getNumber());
                    Log.i("SLOT_2", "Slot 2: " + objSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(1));
                /*I/ACTIVE_SUB_INFO_LIST: Active Sub Info List: [{id=1, iccId=899185804[****] simSlotIndex=0 displayName=Jio 4G carrierName=Jio 4G nameSource=0 iconTint=-16746133 dataRoaming=1 iconBitmap=android.graphics.Bitmap@dd44315 mcc 405 mnc 858 mStatus=0 mNwMode=-1}, {id=3, iccId=899100090[****] simSlotIndex=1 displayName=airtel carrierName=Airtel | airtel nameSource=0 iconTint=-13615201 dataRoaming=0 iconBitmap=android.graphics.Bitmap@70f262a mcc 404 mnc 96 mStatus=0 mNwMode=-1}]
                I/SLOT_1: Slot 1: {id=1, iccId=899185804[****] simSlotIndex=0 displayName=Jio 4G carrierName=Jio 4G nameSource=0 iconTint=-16746133 dataRoaming=1 iconBitmap=android.graphics.Bitmap@89de805 mcc 405 mnc 858 mStatus=0 mNwMode=-1}
                I/SLOT_1: Slot 1: Subscription ID: 1
                I/SLOT_1: Slot 1: ICC ID: 89918580400024451624
                I/SLOT_1: Slot 1: Number: null
                I/SLOT_2: Slot 2: {id=3, iccId=899100090[****] simSlotIndex=1 displayName=airtel carrierName=Airtel | airtel nameSource=0 iconTint=-13615201 dataRoaming=0 iconBitmap=android.graphics.Bitmap@cb805a mcc 404 mnc 96 mStatus=0 mNwMode=-1}*/
                }
            }

            callLogsLV.setFastScrollEnabled(true);  //Enables fast scrolling.
            //callLogsLV.setFastScrollAlwaysVisible(true);    //Fast scrolling visible always.

            //managedCursor.close();  //PURPOSELY TRYING TO CAUSE EXCEPTION.
        /*}
        catch(Exception E)
        {
            Log.i("ON_CREATE_EXCEPTION", "Some exception caught!\n" + E.getMessage());
            ACRA.getErrorReporter().handleException(E);
            //HAVE TO CHECK ABOVE LOC, THEN REMOVE THE TRY CATCH BLOCK TO LET THE APP CRASH.
            //ABOVE LOC WORKS PERFECTLY FINE, WHEN AN APP CRASH OCCURS.
            //CURRENTLY OPENING MAIL APPLICATION TO SEND AN EMAIL WITH CRASH REPORT.
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        callLogsAL.clear();

        //DUE TO THIS PERMISSION CHECK OVER HERE, IT'LL KEEP REQUESTING PERMISSION & IF DENIED, THEN
        //ELSE STATEMENT OF ONREQUESTPERMISSIONSRESULT() WILL KEEP FIRING, THUS TOAST BEING SHOWN CONTINUOUSLY, LEADING TO APP CRASH
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                Log.i("CALL_LOG", "Permission not granted yet.");
                requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG}, 5);
            } else {
                Log.i("CALL_LOG", "Permission already granted.");
                getCallDetails();
                objArrayAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!managedCursor.isClosed()) {
            Log.i("CURSOR", "Cursor closed upon pause!");
            managedCursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 5) {
                Log.i("PERMISSION", "Read & Write Call Log Permission granted now.");
                getCallDetails();
            } else if (requestCode == 6) {
                Log.i("PERMISSION", "Read Phone State Permission granted now.");
            } else if (requestCode == 4) {
                Log.i("PERMISSION", "Call Phone Permission granted now.");
                Toast.makeText(getApplicationContext(), "Permission now granted.\nProceed with your call.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.i("PERMISSION", "Permission not granted after request.");
            Toast.makeText(getApplicationContext(), "Please provide permission to proceed further!", Toast.LENGTH_SHORT).show();
        }
    }

    //TAKEN FROM: http://android2011dev.blogspot.com/2011/08/get-android-phone-call-historylog.html
    private void getCallDetails()       //WORKING
    {
        //Better to do all these retrievals in the background thread, using LoaderManager.
        StringBuffer sb = new StringBuffer();

        managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int cachedName = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int phoneAcctId = managedCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);     //SIM Info - Returns Subscription ID + ICC ID

        //READ PHONE STATE PERMISSION IS REQUESTED IN ONCREATE(), WHICH GETS GRANTED IMPLICITLY, UPON THE REQUEST, THUS IT CAN NEVER BE DENIED IN THE FUTURE
        String strSubId_ICCId_slot_1 = null;
        String strSubId_ICCId_slot_2 = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
            {
                strSubId_ICCId_slot_1 = String.valueOf(objSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0).getSubscriptionId()) + String.valueOf(objSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0).getIccId());
                strSubId_ICCId_slot_2 = String.valueOf(objSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(1).getSubscriptionId()) + String.valueOf(objSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(1).getIccId());
                Log.i("SLOT_1_SLOT_2", "Slot 1: " + strSubId_ICCId_slot_1 + "\tSlot 2: " + strSubId_ICCId_slot_2);  //I/SLOT_1_SLOT_2: Slot 1: 189918580400024451624	Slot 2: 38991000900298369250
            }
        }

        //Log.i("CALL_LOG_COUNT", "Total Number of Calls: " + managedCursor.getCount());  //500, as calculated & expected

        while ( managedCursor.moveToNext() )
        {
            if(managedCursor.getString(managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME)) == null)
            {
                Log.i("CALL_CONTACT_NAME", "No name for this contact.");
                Log.i("RESPECTIVE_CALL_NUMBER", managedCursor.getString( number ));
            }

            String strCachedName = managedCursor.getString(cachedName);
            Log.i("CACHED_NAME", "Contact Name, if Present: " + strCachedName);     //WORKING

            if(strCachedName != null)
                sb.append("Name:--- " + strCachedName);
            else
                sb.append("Phone Number:--- " + managedCursor.getString(number));

            /*but there is a problem with this i.e some device store sim ICCID [ It's a 19-20 digit code which is unique for each sim] in PHONE_ACCOUNT_ID but
            some devices store The actual subscription id which is 1 or 2 digit only. So we need to check whether the subscription_id is equal to original sim
            subscription_id or it is equal to iccid.*/
            /*[KOTLIN CODE] fun getCallHistoryOfSim(simInfo:SubscriptionInfo?, allCallList:MutableList<CallHistory> ) : MutableList<CallHistory> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1){
                    return allCallList.filter { it.subscriberId==simInfo?.subscriptionId.toString() || it.subscriberId.contains(simInfo?.iccId?:"_")}.toMutableList()
                }else{
                    throw Exception("This Feature Is Not Available On This Device")
                }
            }*/

            String strPhoneAcctId = managedCursor.getString(phoneAcctId);   //SIM 1 - 189918580400024451624   SIM 2 - 38991000900298369250
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

            String callType = managedCursor.getString( type );
            String callDate = managedCursor.getString( date );
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString( duration );

            String dir = null;
            int dircode = Integer.parseInt( callType );
            switch( dircode )   //This is required because, we just get the number corresponding to type of call.
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

                    /*Voicemail [4], Answered Externally Type [7] - Call log type for a call which was answered on another device.
                    Used in situations where a call rings on multiple devices simultaneously and it ended up being answered on a device other than the current one.*/
            }

            sb.append("\nCall Type:--- "+dir+" \nCall Date:--- "+callDayTime+" \nCall duration in sec :--- "+callDuration );
            callLogsAL.add(String.valueOf(sb));
            sb.delete(0, sb.length());
        }
        mAdapter.notifyDataSetChanged();
    }
}