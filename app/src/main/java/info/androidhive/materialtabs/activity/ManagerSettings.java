package info.androidhive.materialtabs.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import info.androidhive.materialtabs.GeoObjects.Company;
import info.androidhive.materialtabs.GeoObjects.User;
import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.common.GeoAppDBHelper;
import info.androidhive.materialtabs.common.Globals;

public class ManagerSettings extends AppCompatActivity implements OnCheckedChangeListener {

    private User Manager;
    private Company company;
    private Switch AutoLogin;
    private Switch SendEmail;
    private TextView CompanyName;
    private TextView CompanyCode;
    private GeoAppDBHelper DB = new GeoAppDBHelper(Globals.GeoAppContext);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_settings);
        Manager = (User) getIntent().getSerializableExtra(Globals.EXTRA_USER);
        Bundle extras = getIntent().getExtras();
        if(extras.containsKey(Globals.EXTRA_COMPANY)) {
            company = (Company) extras.getSerializable(Globals.EXTRA_COMPANY);
        }
        else{
            company = new Company(Manager.getCompanyCode(),Manager.getCompanyName());
        }
        CompanyCode = (TextView) findViewById(R.id.companyCode);
        CompanyName = (TextView) findViewById(R.id.companyName);
        AutoLogin = (Switch) findViewById(R.id.autoLoginS);
        AutoLogin.setOnCheckedChangeListener(this);
        if(Manager.getAutoLogin()) AutoLogin.setChecked(true);
        SendEmail = (Switch) findViewById(R.id.sendEmailS);
        CompanyCode.setText(company.getCompanyCode());
        CompanyName.setText(company.getCompanyName());

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            DB.updateUserAutoLogin(Manager,true);
        }else{
            DB.updateUserAutoLogin(Manager,false);
        }
    }
}
