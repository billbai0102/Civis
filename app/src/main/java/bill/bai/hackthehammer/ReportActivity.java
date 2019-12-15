package bill.bai.hackthehammer;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ReportActivity extends AppCompatActivity{

    private Spinner spinner;
    private static final String[] paths = {"item 1", "item 2", "item 3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.report_layout);

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

        Spinner spinner = (Spinner) findViewById(R.id.report_type_dropdown);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.report_types_array, R.id.report_type_dropdown);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


    }

    @TargetApi(21)
    public void addNotifBar(){
        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }

}
