package com.michaelt.bikeapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import java.util.Map;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends Activity {

    private Map<String, String> mUserMap;
    private Context mContext;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    @InjectView(R.id.edit_login_name) EditText mLoginName;
    @InjectView(R.id.edit_login_password) EditText mLoginPassword;
    @InjectView(R.id.image_logo) ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mContext = this;
        mUserMap = new android.support.v4.util.ArrayMap<>();
        populateUserMap();

    }

    /**
     * Populates default users into mUserMap
     */
    private void populateUserMap() {
        mUserMap.put("DARE2000", "stubble");
        mUserMap.put("MTCARR4", "marchto40");
        mUserMap.put("abc", "abc");
    }

    /**
     * Grabs the user input and checks against the current user data (mUserMap) to see if the
     * input is proper.  If it is not, an AlertDialog is shown letting the user know, otherwise,
     * the BikeListActivity is run.
     */
    @OnClick(R.id.button_login)
    public void login() {
        String name = mLoginName.getText().toString();
        String inputPassword = mLoginPassword.getText().toString();
        String actualPassword = mUserMap.get(name);

        if(actualPassword == null || !actualPassword.equals(inputPassword)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Wrong Login Name or Password").setTitle("Improper Input");
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            //Save current user to preferences for "cart" applications later
            sp = getSharedPreferences("UserName", Context.MODE_PRIVATE );
            editor = sp.edit();
            editor.putString("UserName", name);
            editor.commit();
            int[] screenLocation = new int[2];
            mImageView.getLocationOnScreen(screenLocation);
            Intent i = new Intent(this, BikeListActivity.class);
            i.putExtra("left", screenLocation[0]);
            i.putExtra("top", screenLocation[1]);
            startActivity(i);

            // Override transitions: we don't want the normal window animation in addition
            // to our custom one
            overridePendingTransition(0, 0);
        }

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.register) {
//            addUser();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @OnClick(R.id.button_register)
    public void register() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View addUserView = inflater.inflate(R.layout.add_user, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("Add User")
               .setView(addUserView)
               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       AlertDialog dlg = (AlertDialog) dialog;
                       EditText userName = (EditText) dlg.findViewById(R.id.edit_dlg_user_name);
                       EditText userPassword = (EditText) dlg.findViewById(R.id.edit_dlg_user_password);

                       String name = userName.getText().toString();
                       String password = userPassword.getText().toString();

                       if (name.length() > 0 && password.length() > 0) {
                           mUserMap.put(name, password);
                       }
                   }
               })
               .show();
    }
}
