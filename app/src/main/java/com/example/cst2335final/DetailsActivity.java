package com.example.cst2335final;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

/**
 * The DetailsActivity class is an activity that displays the details of an article.
 * This class appears after being called by the FavouritesActivity or the SearchActivity.
 */
public class DetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * The db variable is a SQLiteDatabase object that is used to access the database.
     */
    SQLiteDatabase db;

    /**
     *<p>This method is called when the activity is first created. It sets up the activity for DetailsActivity.</p>
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        FavouritesDatabase dbOpener = new FavouritesDatabase(this);
        db = dbOpener.getWritableDatabase();

        Toolbar supportToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(supportToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("section"));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, supportToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Log.d("Navigation", "NavigationView: " + navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        TextView textSubject = findViewById(R.id.text_subject);
        textSubject.setText(getIntent().getStringExtra("title"));

        Button buttonFavourite = findViewById(R.id.button_favourite);
        buttonFavourite.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked. Adds the article to the favourites database.
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(FavouritesDatabase.COL_SECTION, getIntent().getStringExtra("section"));
                values.put(FavouritesDatabase.COL_TITLE, getIntent().getStringExtra("title"));
                values.put(FavouritesDatabase.COL_URL, getIntent().getStringExtra("url"));
                db.insert(FavouritesDatabase.TABLE_NAME, null, values);
                Toast.makeText(DetailsActivity.this, "Added to favourites!", Toast.LENGTH_SHORT).show();
            }
        });
        Button buttonUrl = findViewById(R.id.button_url);
        buttonUrl.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked. Opens the article in a browser.
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getIntent().getStringExtra("url")));

                startActivity(intent);
            }
        });

    }

    /**
     * Called when an item in the navigation menu is selected.
     * Changes to a different fragment or activity based on what was selected.
     * @param item The selected item
     * @return true if the item was selected
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
            intent.putExtra("fragment", "home");
            intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }else if (id == R.id.nav_search) {
            Intent intent = new Intent(DetailsActivity.this, SearchActivity.class);
            intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }else if (id == R.id.nav_favourites) {
            Intent intent = new Intent(DetailsActivity.this, FavouritesActivity.class);
            intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
            intent.putExtra("fragment", "about");
            intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
