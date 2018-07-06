package com.example.sayone.contentprovidertrial

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.content.OperationApplicationException
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.content.ContentProviderOperation
import android.os.RemoteException
import android.provider.ContactsContract.CommonDataKinds.StructuredName

import android.provider.ContactsContract.Data
import android.provider.ContactsContract.RawContacts
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    val PHONE_DISPLAY_NAME =ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
    val PHONE_NUMBER =ContactsContract.CommonDataKinds.Phone.NUMBER
    val TAG = "Phone Tag"

    var uri:Uri?=ContactsContract.CommonDataKinds.Phone.CONTENT_URI
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_btn.setOnClickListener{
            fetchContacts()
        }
        add_btn.setOnClickListener{
            writeContact("demo","1234567890")
        }


    }

    fun fetchContacts(){


        val projection:Array<String> = arrayOf(PHONE_DISPLAY_NAME,PHONE_NUMBER)
        val selection:String?=null
        val selectionArgs:Array<String>?=null
        val sortOrder:String?=null

        var content = contentResolver
        var cursor = content.query(uri,projection,selection,selectionArgs,sortOrder)

        while (cursor.moveToNext()){
            var name = cursor.getString(cursor.getColumnIndex(PHONE_DISPLAY_NAME))
            var number= cursor.getString(cursor.getColumnIndex(PHONE_NUMBER))

            Log.d(TAG,"name : $name")
            Log.d(TAG,"phone : $number")
        }
    }


    private fun writeContact(displayName: String, number: String) {
        var contentProviderOperations:ArrayList<ContentProviderOperation> = ArrayList()

       //insert raw contact using RawContacts.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, null).withValue(RawContacts.ACCOUNT_NAME, null).build())
        //insert contact display name using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0).withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, displayName).build())
        //insert mobile number using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0).withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, number).withValue(Phone.TYPE, Phone.TYPE_MOBILE).build())
        try {
            applicationContext.contentResolver.applyBatch(ContactsContract.AUTHORITY, contentProviderOperations)
            Toast.makeText(applicationContext,"demo contact added",Toast.LENGTH_LONG).show()
        } catch (e: RemoteException) {
            e.printStackTrace()
        } catch (e: OperationApplicationException) {
            e.printStackTrace()
        }

    }
}
