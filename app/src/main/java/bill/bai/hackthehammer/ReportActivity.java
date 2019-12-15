package bill.bai.hackthehammer;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Debug;
import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ReportActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    public ArrayAdapter<String> adapter;

    public String typeSelected = "Fire";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.report_layout);

        Spinner spinner = (Spinner)findViewById(R.id.report_type_dropdown);
        spinner.setOnItemSelectedListener(this);

        if (Build.VERSION.SDK_INT > 15) {
            addNotifBar();
        }

        TextView txH = (TextView)findViewById(R.id.report_heading);
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/HelveticaNeue_Medium.ttf");
        txH.setTypeface(custom_font1);

        TextView txT = (TextView)findViewById(R.id.report_title);
        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/HelveticaNeue_Light.ttf");
        txT.setTypeface(custom_font2);

        EditText etxT = (EditText)findViewById(R.id.report_title_edit);
        etxT.setTypeface(custom_font2);

        TextView rtxT = (TextView)findViewById(R.id.report_type);
        rtxT.setTypeface(custom_font2);

        TextView authT = (TextView)findViewById(R.id.auth_present);
        authT.setTypeface(custom_font2);

        TextView descT = (TextView)findViewById(R.id.description_title);
        descT.setTypeface(custom_font2);

        EditText descE = (EditText)findViewById(R.id.description);
        descE.setTypeface(custom_font2);

        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.report_type_dropdown);
        //create a list of items for the spinner.
        String[] items = new String[]{"Fire", "Crime", "Natural", "Emergency", "Other"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        adapter.notifyDataSetChanged();
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
    }

    @TargetApi(21)
    public void addNotifBar(){
        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        adapter.notifyDataSetChanged();

        switch (position) {
            case 0:
                typeSelected = "Fire";
                break;
            case 1:
                typeSelected = "Crime";
                break;
            case 2:
                typeSelected = "Natural";
                break;
            case 3:
                typeSelected = "Emergency";
                break;
            case 4:
                typeSelected = "Other";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    public void send_report(View f){
        EditText etxT = (EditText)findViewById(R.id.report_title_edit);
        String title = etxT.getText().toString();
        CheckBox chkbox = (CheckBox) findViewById(R.id.auth_checkbox);
        boolean is_auth_present = chkbox.isChecked();
        EditText descT = (EditText)findViewById(R.id.description);

        String description = descT.getText().toString();

        Log.d("F","PRESSED");

        System.out.println("_______________________________");
        System.out.println("Title: " + title);
        System.out.println("Type: " + typeSelected);
        System.out.println("Auth Present?: " + is_auth_present);
        System.out.println("Description: " + description);
        System.out.println("_______________________________");


        /**
         * SEND YO SHIT HERE
         * The above variables are used u just need to send them
         */


        Intent intent = new Intent(ReportActivity.this, MapsActivity.class);
        startActivityForResult(intent, 0);

        Toast toast = Toast.makeText(getApplicationContext(), "Report Submitted", Toast.LENGTH_LONG);
        toast.show();
    }

}
