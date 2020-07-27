package com.vrihas.assignment.pratilipi.pratilipiapp.ui.activity

import android.Manifest
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vrihas.assignment.pratilipi.pratilipiapp.MyApplication
import com.vrihas.assignment.pratilipi.pratilipiapp.R
import com.vrihas.assignment.pratilipi.pratilipiapp.data.model.User
import com.vrihas.assignment.pratilipi.pratilipiapp.service.MyService
import com.vrihas.assignment.pratilipi.pratilipiapp.ui.adapter.ContactsAdapter
import com.vrihas.assignment.pratilipi.pratilipiapp.utils.SharedPrefs
import com.vrihas.assignment.pratilipi.pratilipiapp.viewmodel.HomeViewModel
import java.util.*

@Suppress("IMPLICIT_CAST_TO_ANY", "SameParameterValue")
class HomeActivity : AppCompatActivity(), ContactsAdapter.ContactAdapterCallback {

    companion object {
        const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    }

    private lateinit var rvContacts: RecyclerView
    private lateinit var btnImport: Button
    private lateinit var llImport: LinearLayout
    private lateinit var pbContacts: ProgressBar
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var homeViewModel: HomeViewModel
    var contactsFromDevice: MutableList<User> = ArrayList()
    private lateinit var sharedPrefs: SharedPrefs

    private val TAG = "permissiontag"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        MyApplication.instance.getComponent().inject(this)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.title = "Tap on the contacts to block..."
        sharedPrefs = SharedPrefs(this)
        initialise()
        setupContactRecyclerView()
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.allContacts.observe(this, Observer { contacts ->
            contacts.let { contactsAdapter.setContactList(it) }
        })

        btnImport.setOnClickListener {
            if ( checkAndRequestPermissions()) {
                getContacts()
//                Log.e("getcontacts", "" + contactsFromDevice.size)
                contactsAdapter.setContactList(contactsFromDevice)
            }

        }
        intent = Intent(this, MyService::class.java)
        startService(intent)
    }

    private fun initialise() {
        rvContacts = findViewById(R.id.rv_contacts)
        btnImport = findViewById(R.id.btn_import)
        llImport = findViewById(R.id.ll_import)
        pbContacts = findViewById(R.id.pb_contacts)
        Log.e("isfirst", " " + sharedPrefs.isFirst() )
        if (sharedPrefs.isFirst()) {
            llImport.visibility = View.VISIBLE
        } else{
            llImport.visibility = View.GONE
        }
    }

    private fun setupContactRecyclerView() {
        contactsAdapter = ContactsAdapter(this, this)
        rvContacts.adapter = contactsAdapter
        rvContacts.layoutManager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(this, LinearLayout.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.divider)?.let { divider.setDrawable(it) }
        rvContacts.addItemDecoration(
            DividerItemDecoration(this, LinearLayout.VERTICAL)
        )
    }

    override fun updateContactStatus(user: User) {
        homeViewModel.updateContact(user.id, user.blocked)
        if (user.blocked) {
            Toast.makeText(this, user.name + " is now blocked", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, user.name + " is now unblocked", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val readcontactpermision = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        val readphonepermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
        val callphonepermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
        val calllogpermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
        val ansphonepermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS)
        } else {

        }


        val listPermissionsNeeded: MutableList<String> =
            ArrayList()
        if (readcontactpermision != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS)
        }
        if (readphonepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (callphonepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE)
        }
        if (calllogpermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CALL_LOG)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ansphonepermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ANSWER_PHONE_CALLS)
            }

        }


        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms: MutableMap<String, Int> =
                    HashMap()
                // Initialize the map with all the permissions
                perms[Manifest.permission.READ_CONTACTS] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_PHONE_STATE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.CALL_PHONE] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.READ_CALL_LOG] = PackageManager.PERMISSION_GRANTED
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    perms[Manifest.permission.ANSWER_PHONE_CALLS] = PackageManager.PERMISSION_GRANTED
                }


                // Fill with actual results from user
                if (grantResults.isNotEmpty()) {
                    var i = 0
                    while (i < permissions.size) {
                        perms[permissions[i]] = grantResults[i]
                        i++
                    }
                    // Check for all permissions
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && perms[Manifest.permission.READ_CONTACTS] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.READ_PHONE_STATE] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.CALL_PHONE] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.READ_CALL_LOG] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.ANSWER_PHONE_CALLS] == PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d(TAG, "All permissions granted")
                        getContacts()

                        //else any one or all the permissions are not granted
                    } else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O && perms[Manifest.permission.READ_CONTACTS] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.READ_PHONE_STATE] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.CALL_PHONE] == PackageManager.PERMISSION_GRANTED && perms[Manifest.permission.READ_CALL_LOG] == PackageManager.PERMISSION_GRANTED
                    ) {
                        getContacts()
                    }
                    else {
                        // For OS versions greater than andorid 8.0
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Log.d(TAG, "Some permissions are not granted ask again ")
                            //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                            // shouldShowRequestPermissionRationale will return true
                            //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.READ_CONTACTS
                                )
                                || ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.READ_PHONE_STATE
                                )
                                || ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.CALL_PHONE
                                )
                                || ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.READ_CALL_LOG
                                )
                                || ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.ANSWER_PHONE_CALLS
                                )
                            ) {
                                showDialogOK("Service Permissions are required for this app",
                                    DialogInterface.OnClickListener { _, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE ->
                                                checkAndRequestPermissions()  // proceed with logic by disabling the related features or quit the app.
                                        }
                                    })
                            } else {
                                explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?")
                                //proceed with logic by disabling the related features or quit the app.
                            }
                        } else{
                            // For OS versions less than andorid 8.0
                            Log.d(TAG, "Some permissions are not granted ask again ")
                            //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                            // shouldShowRequestPermissionRationale will return true
                            //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.READ_CONTACTS
                                )
                                || ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.READ_PHONE_STATE
                                )
                                || ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.CALL_PHONE
                                )
                                || ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.READ_CALL_LOG
                                )
                            ) {
                                showDialogOK("Service Permissions are required for this app",
                                    DialogInterface.OnClickListener { _, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE ->
                                                checkAndRequestPermissions()  // proceed with logic by disabling the related features or quit the app.
                                        }
                                    })
                            } else {
                                explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?")
                                //proceed with logic by disabling the related features or quit the app.
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showDialogOK(
        message: String,
        okListener: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", okListener)
            .create()
            .show()
    }

    private fun explain(msg: String) {
        val dialog =
            AlertDialog.Builder(this)
        dialog.setMessage(msg)
            .setPositiveButton(
                "Yes"
            ) { _, _ ->
                startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:com.vrihas.assignment.pratilipi.pratilipiapp")
                    )
                )
            }
            .setNegativeButton(
                "Cancel"
            ) { _, _ ->
                checkAndRequestPermissions()
                // finish();
            }
        dialog.show()
    }

    private fun getContacts() {
        sharedPrefs.setFirst(false)
        btnImport.visibility = View.GONE
        pbContacts.visibility = View.VISIBLE

        val numberList: MutableList<String> = ArrayList()
        val resolver: ContentResolver = contentResolver
        val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
            null)

        if (cursor?.count!! > 0) {
            var i = 1
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phoneNumber = (cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))).toInt()

                if (phoneNumber > 0) {
                    val cursorPhone = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null)

                    if(cursorPhone?.count!! > 0) {
                        while (cursorPhone.moveToNext()) {
                            var phoneNumValue = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            phoneNumValue = phoneNumValue.replace("\\s".toRegex(), "")
                            if (!numberList.contains(phoneNumValue)) {
                                numberList.add(phoneNumValue)
                                val user = User(i, name, phoneNumValue, false)
//                                contactsFromDevice.add(user)
                                homeViewModel.insertContact(user)
                                i++
                            }
                        }
                    }
                    cursorPhone.close()
                }
            }
        } else {
            Toast.makeText(this, "No Contacts available", Toast.LENGTH_LONG).show()
        }
        cursor.close()
        pbContacts.visibility = View.GONE
        llImport.visibility = View.GONE
    }
}
