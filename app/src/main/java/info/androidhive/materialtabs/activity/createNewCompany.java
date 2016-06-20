package info.androidhive.materialtabs.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseGeoPoint;

import info.androidhive.materialtabs.GeoObjects.Company;
import info.androidhive.materialtabs.GeoObjects.User;
import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.common.Globals;
import info.androidhive.materialtabs.common.Server;

public class createNewCompany extends AppCompatActivity {


    private EditText companyName, companyAddress, companyLatitude, companyLongitud;
    private Button companyCreate;
    private TextView errorMsg;
    private User manager;
    private Company company = new Company();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_company);
        init();

    }

    private void init(){
        companyName = (EditText) findViewById(R.id.company_name);
        companyAddress = (EditText) findViewById(R.id.company_address);
        companyLatitude = (EditText) findViewById(R.id.company_location_latitude);
        companyLongitud = (EditText) findViewById(R.id.company_location_longitude);
        companyCreate = (Button) findViewById(R.id.company_create_btn);
        errorMsg = (TextView)findViewById(R.id.errorMsg);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.containsKey(Globals.EXTRA_USER)) {
                manager = (User) extras.getSerializable(Globals.EXTRA_USER);
            }
        }
    }
    public void onClickCreateCompany(View view){
        errorMsg.setText(""); //Reset error message
        if(companyName.getText().toString().equals(""))
            errorMsg.setText(R.string.company_empty_name);
        else{
            company.setCompanyName(companyName.getText().toString().trim());
            company.setCompanyAddress(companyAddress.getText().toString().trim());
            company.setManagerEmail(manager.getEmail().trim());
            company.setManagerID(manager.getSystemID().trim());
            try{
                company.setLocation(
                        Double.valueOf(companyLatitude.getText().toString().trim()),
                        Double.valueOf(companyLongitud.getText().toString().trim()
                ));
            }
            catch (Exception e){
                company.setLocation(0.0,0.0);
            }

            Object result = Server.addNewCompany(manager,company);
            if(result instanceof String)
                errorMsg.setText((String)result);
            else{
                company = (Company)result;
                manager.setCompanyCode(company.getCompanyCode());
                manager.setCompanyName(company.getCompanyName());
                Toast.makeText(getApplicationContext(), R.string.company_created, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this,Manager_screen.class);
                intent.putExtra(Globals.EXTRA_USER,manager);
                intent.putExtra(Globals.EXTRA_COMPANY,company);
                startActivity(intent);
            }
        }
    }
}
