package info.androidhive.materialtabs.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.GeoObjects.User;
import info.androidhive.materialtabs.common.GeoAppDBHelper;
import info.androidhive.materialtabs.common.Globals;
import info.androidhive.materialtabs.common.Server;
import info.androidhive.materialtabs.common.User_callback;

public class Login_Activity extends AppCompatActivity {


    private EditText Email,Password;
    private TextView Error;
    private GeoAppDBHelper DB;
    private Button Login_button,Sign_button;
    private ImageView Logo;
    private User currentUser = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        DB = new GeoAppDBHelper(getApplicationContext());
        Login_button = (Button)findViewById(R.id.login_btn);
        Login_button.setBackgroundResource(R.drawable.button_custom_setting);
        Sign_button = (Button) findViewById(R.id.signin_btn);
        Sign_button.setBackgroundResource(R.drawable.button_custom_setting);
        init();

    }


    public void on_click_login_btn(View view){

        Error.setText("");
        hideKeyBoard();                             //---hide the Android Soft Keyboard---//
        Logo.animate().rotation(360f*6).setDuration(10000);
        currentUser.setEmail(Email.getText().toString().trim());
        currentUser.setPassword(Password.getText().toString().trim());
        Object returnObject = Server.getUserDetails(currentUser); //---Send To Server Parse---//
        if(returnObject instanceof String)
            Error.setText((String)returnObject);
        else {
            currentUser = (User) returnObject;

            Intent ActivityByRole;
            if (currentUser.isWorker())
                ActivityByRole = new Intent(this, IconTabsActivity.class);
            else {
                if(currentUser.getCompanyCode().equals(""))
                    ActivityByRole = new Intent(this, createNewCompany.class);
                else
                    ActivityByRole = new Intent(this, Manager_screen.class);
            }
            ActivityByRole.putExtra(Globals.EXTRA_USER, currentUser);
            startActivity(ActivityByRole);
        }

    }

    /**
     * This function hide Keyboard
     */
    private void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    public void  on_click_signin_btn(View view){
        startActivity(new Intent(this, SignIn_Activity.class));
    }
    @Override
    /**
     * This function cancel return button
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            return false; //I have tried here true also
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Reset Email & Password & Error
     */
    public void init(){
        Email = (EditText)findViewById(R.id.login_email);
        Password = (EditText)findViewById(R.id.login_password);
        Error = (TextView)findViewById(R.id.error_text);
        Object returnObject = DB.getLastLoginUser();
        if(returnObject instanceof User)
        {
            User user = (User)returnObject;
            Email.setText(user.getEmail());
            Password.setText(user.getPassword());
        }
        Error.setText("");

        Logo = (ImageView) findViewById(R.id.imageView8);
        //get_new_current_user();

        // Set user details if cane from signin activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.containsKey(Globals.EXTRA_USER)) {
                User user = (User) extras.getSerializable(Globals.EXTRA_USER);
                Email.setText(user.getEmail());
                Password.setText(user.getPassword());
            }

        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        init();
    }
}
