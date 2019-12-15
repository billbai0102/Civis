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
import android.widget.TextView;

public class ReportActivity extends AppCompatActivity{

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
    }

    @TargetApi(21)
    public void addNotifBar(){
        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
    }

}
