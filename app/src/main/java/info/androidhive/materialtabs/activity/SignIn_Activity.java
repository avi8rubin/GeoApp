package info.androidhive.materialtabs.activity;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import info.androidhive.materialtabs.GeoObjects.User;
import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.common.Globals;
import info.androidhive.materialtabs.common.Server;

public class SignIn_Activity extends Activity {


    private TextView FirstName,LastName,Password,Email,TZ,Phone,Error;
    private User user;
    private RadioGroup roleRG;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_);


        FirstName = (TextView) findViewById(R.id.company_name);
        LastName = (TextView)findViewById(R.id.company_address);
        Password = (TextView) findViewById(R.id.company_location_longitude);
        Email = (TextView) findViewById(R.id.company_location_latitude);
        TZ = (TextView)findViewById(R.id.tz_field);
        Phone=(TextView)findViewById(R.id.phone_field);
        Error = (TextView)findViewById(R.id.error_txt);
        roleRG = (RadioGroup) findViewById(R.id.radio_role_group);
        user = new User();
    }
    public void on_click_enter_btn(View view){

        //TODO check all field & send to Server
        if(!fieldsCheck())
            Error.setText(R.string.field_error);
        else
        {
            Error.setText("");
            user.setEmail(Email.getText().toString());
            user.setPassword(Password.getText().toString());
            user.setFirstName(FirstName.getText().toString());
            user.setLastName(LastName.getText().toString());
            user.setPhone(Phone.getText().toString());
            user.setUserID(TZ.getText().toString());
            //---Radio button handle---//
            if(roleRG.getCheckedRadioButtonId() == R.id.manager_radio_btn)
                user.setIsManager();
            else user.setIsWorker();

            Object result = Server.CreateNewUser(user);
            if(result instanceof String)
                Error.setText((String)result);
            if(result instanceof User) {
                user = (User) result;
                Intent intent = new Intent(this,Login_Activity.class);
                intent.putExtra(Globals.EXTRA_USER,user);
                startActivity(intent);
            }
        }
    }


    /**
     * Test for empty fields
     * @return
     */
    public boolean fieldsCheck(){
        return FirstName.getText().length()!=0 && LastName.getText().length()!=0  && Password.getText().length()!=0  &&
        TZ.getText().length()!=0 && Phone.getText().length()!=0 && Email.getText().length()!=0;
    }
}
