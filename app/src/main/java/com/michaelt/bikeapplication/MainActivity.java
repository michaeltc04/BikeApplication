package com.michaelt.bikeapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
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
    @InjectView(R.id.image_logo) ImageView mLogoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mContext = this;
        mUserMap = new android.support.v4.util.ArrayMap<>();
        populateUserMap();

        mLogoImage.setImageResource(R.drawable.logo);
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
            mLogoImage.getLocationOnScreen(screenLocation);
            Intent i = new Intent(this, BikeListActivity.class);
            i.putExtra("left", screenLocation[0]);
            i.putExtra("top", screenLocation[1]);
            startActivity(i);

            // Override transitions: we don't want the normal window animation in addition
            // to our custom one
            overridePendingTransition(0, 0);
        }

    }

    /**
     * OnClick for the Register button that brings up an Alert Dialog for registering as a
     * user with a username and password.
     */
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

    @Override
    public void finish() {
        super.finish();

        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }
}
