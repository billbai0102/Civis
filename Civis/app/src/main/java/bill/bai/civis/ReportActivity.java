package bill.bai.civis;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    public ArrayAdapter<String> adapter;

    public String typeSelected = "Fire";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.report_layout);

        Spinner spinner = findViewById(R.id.report_type_dropdown);
        spinner.setOnItemSelectedListener(this);

        if (Build.VERSION.SDK_INT > 15) {
            addNotifBar();
        }

        TextView txH = findViewById(R.id.report_heading);
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/HelveticaNeue_Medium.ttf");
        txH.setTypeface(custom_font1);

        TextView txT = findViewById(R.id.report_title);
        Typeface custom_font2 = Typeface.createFromAsset(getAssets(),  "fonts/HelveticaNeue_Light.ttf");
        txT.setTypeface(custom_font2);

        EditText etxT = findViewById(R.id.report_title_edit);
        etxT.setTypeface(custom_font2);

        TextView rtxT = findViewById(R.id.report_type);
        rtxT.setTypeface(custom_font2);

//        TextView authT = findViewById(R.id.auth_present);
//        authT.setTypeface(custom_font2);

        TextView descT = findViewById(R.id.description_title);
        descT.setTypeface(custom_font2);

        EditText descE = findViewById(R.id.description);
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
        EditText etxT = findViewById(R.id.report_title_edit);
        String title = etxT.getText().toString();
//        CheckBox chkbox = findViewById(R.id.auth_checkbox);
//        boolean is_auth_present = chkbox.isChecked();
        EditText descT = findViewById(R.id.description);

        String description = descT.getText().toString();

        Log.d("F","PRESSED");

        System.out.println("_______________________________");
        System.out.println("Title: " + title);
        System.out.println("Type: " + typeSelected);
//        System.out.println("Auth Present?: " + is_auth_present);
        System.out.println("Description: " + description);
        System.out.println("_______________________________");


        /**
         * SEND YO SHIT HERE
         * The above variables are used u just need to send them
         */
        ArrayList<MapObject> testPost = new ArrayList<>();
        // "Takes out current location"
        // TODO camera image
        testPost.add(new MapObject(title, description, typeSelected, 43.257009, -79.900810, "https://lh5.googleusercontent.com/p/AF1QipPnZFuskVu1WYJCt_zNKsLuX_wLV4c1EjeFHJZs=w235-h160-k-no"));

        API.postData(testPost);

        Intent intent = new Intent(ReportActivity.this, MainActivity.class);
        startActivityForResult(intent, 0);

        Toast toast = Toast.makeText(getApplicationContext(), "Report Submitted", Toast.LENGTH_LONG);
        toast.show();
    }

}
