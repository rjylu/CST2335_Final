package com.example.cst2335final;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

/**
 * The MainActivity class is the main activity of the application. It is used to display the home page and allows for the usage of fragments.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FragmentTransaction ft;

    /**
     * <p>This method is called when the activity is first created. It sets up the activity for MainActivity.
     * It sets up the fragments and the navigation drawer, as well as the toolbar.</p>
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        String fragment = getIntent().getStringExtra("fragment");
        if(fragment != null && fragment.equals("about")) {
            if (getSupportActionBar() != null) {
                String string = getResources().getString(R.string.menu_about);
                getSupportActionBar().setTitle(string + " 1.0");
            }
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragmentLocation, new AboutFragment());
            ft.commit();
        }else {
            if (getSupportActionBar() != null) {
                String string = getResources().getString(R.string.menu_home);
                getSupportActionBar().setTitle(string + " 1.0");
            }
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragmentLocation, new HomeFragment());
            ft.commit();
        }

        Toolbar supportToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(supportToolbar);
        if (getSupportActionBar() != null) {
            String string = getResources().getString(R.string.menu_home);
            getSupportActionBar().setTitle(string + " 1.0");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, supportToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Log.d("Navigation", "NavigationView: " + navigationView);
        navigationView.setNavigationItemSelectedListener(this);



    }


    /**
     * <p>This method is called when the user clicks on an item in the navigation drawer. It is used to navigate to the correct fragment.</p>
     * @param item The selected item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.menu_home);
            }
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragmentLocation, new HomeFragment());
            ft.commit();
        }else if (id == R.id.nav_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }else if (id == R.id.nav_favourites) {
            Intent intent = new Intent(this, FavouritesActivity.class);
            intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.menu_about);
            }
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragmentLocation, new AboutFragment());
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}