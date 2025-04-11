package com.example.cst2335final;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * The FavouritesActivity class is an activity that displays the favourites, getting the values from the database.
 */
public class FavouritesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    /**
     * The articles variable is a List of Article objects that is used to store the articles.
     * The db variable is a SQLiteDatabase object that is used to access the database.
     * The adapter variable is a MyListAdapter object that is used to display the articles in a listview.
     */
    List<Article> articles = new ArrayList<>();
    SQLiteDatabase db;
    private MyListAdapter adapter;

    /**
     * <p>This method is called when the activity is first created. It sets up the activity for FavouritesActivity.</p>
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favourites);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        loadDataFromDatabase();

        Toolbar supportToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(supportToolbar);
        if (getSupportActionBar() != null) {
            String string = getResources().getString(R.string.menu_favourites);
            getSupportActionBar().setTitle(string + " 1.0");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, supportToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Log.d("Navigation", "NavigationView: " + navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        ListView listview = findViewById(R.id.list_favourites);
        adapter = new MyListAdapter(FavouritesActivity.this, articles);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listview.setOnItemClickListener( (p, b, pos, id) -> {
            Intent intent = new Intent(FavouritesActivity.this, DetailsActivity.class);
            intent.putExtra("title", articles.get(pos).getTitle());
            intent.putExtra("section", articles.get(pos).getSection());
            intent.putExtra("url", articles.get(pos).getUrl());
            startActivity(intent);
        });

        String s = getResources().getString(R.string.selected_row);

        listview.setOnItemLongClickListener( (p, b, pos, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(R.string.alert_text)
                    .setMessage(s + pos)

                    .setPositiveButton(R.string.yes, (click, arg) -> {

                        db.delete(FavouritesDatabase.TABLE_NAME, FavouritesDatabase.COL_TITLE + "=?", new String[] {articles.get(pos).getTitle()});
                        articles.remove(pos);
                        adapter.notifyDataSetChanged();
                        Snackbar.make(listview, R.string.deleted, Snackbar.LENGTH_LONG).setAction(R.string.dismiss, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("Snackbar", "Dismissed");
                            }
                        }).show();
                    })
                    .setNegativeButton(R.string.no, (click, arg) -> {

                    });
            alertDialogBuilder.create().show();
            return true;
        });

    }

    /**
     * <p>This method is called when the activity is resumed. It is used to refresh the listview.</p>
     */
    @Override
    protected void onResume() {
        super.onResume();
        articles.clear();
        loadDataFromDatabase();
        adapter.notifyDataSetChanged();
    }

    /**
     * <p>This method is used to load the data from the database.</p>
     */
    private void loadDataFromDatabase() {
        FavouritesDatabase dbOpener = new FavouritesDatabase(this);
        db = dbOpener.getWritableDatabase();

        String[] columns = {FavouritesDatabase.COL_SECTION, FavouritesDatabase.COL_TITLE, FavouritesDatabase.COL_URL};

        Cursor results = db.query(FavouritesDatabase.TABLE_NAME, columns, "URL NOT NULL", null, null, null, null);

        int sectionColIndex = results.getColumnIndex(FavouritesDatabase.COL_SECTION);
        int titleColIndex = results.getColumnIndex(FavouritesDatabase.COL_TITLE);
        int urlColIndex = results.getColumnIndex(FavouritesDatabase.COL_URL);

        while(results.moveToNext()) {
            String section = results.getString(sectionColIndex);
            String title = results.getString(titleColIndex);
            String url = results.getString(urlColIndex);

            articles.add(new Article(title, section, url));
        }

        results.close();
    }

    /**
     * <p>This method is called when the user clicks on an item in the navigation drawer.</p>
     * @param item The item that was clicked.
     * @return True after an Intent occurs.
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Intent intent = new Intent(FavouritesActivity.this, MainActivity.class);
            intent.putExtra("fragment", "home");
            intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }else if (id == R.id.nav_search) {
            Intent intent = new Intent(FavouritesActivity.this, SearchActivity.class);
            intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }else if (id == R.id.nav_favourites) {
            Intent intent = new Intent(FavouritesActivity.this, FavouritesActivity.class);
            intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(FavouritesActivity.this, MainActivity.class);
            intent.putExtra("fragment", "about");
            intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * <p>This class is used to display the articles in a listview.</p>
     */
    private class MyListAdapter extends BaseAdapter {
        FavouritesActivity favouritesActivity;
        List<Article> articles;

        /**
         * <p>This constructor is used to create a new MyListAdapter object.</p>
         * @param favouritesActivity The activity that is using this adapter.
         * @param articles The list of articles to be displayed.
         */
        private MyListAdapter(FavouritesActivity favouritesActivity, List<Article> articles) {
            this.favouritesActivity = favouritesActivity;
            this.articles = articles;
        }

        /**
         * <p>This method is used to get the number of articles.</p>
         * @return the number of articles.
         */
        @Override
        public int getCount() { return articles.size(); }

        /**
         * <p>This method is used to get the article at a given position.</p>
         * @param position The position of the article.
         * @return the article at the given position.
         */
        @Override
        public Object getItem(int position) {
            return articles.get(position);
        }

        /**
         * <p>This method is used to get the id of the article at a given position.</p>
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the given position.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * <p>This method is used to get the view for a given position.</p>
         * @param position The position of the item within the adapter's data set of the item whose view
         *        we want.
         * @param old The old view to reuse, if possible. Note: You should check that this view
         *        is non-null and of an appropriate type before using. If it is not possible to convert
         *        this view to display the correct data, this method can create a new view.
         *        Heterogeneous lists can specify their number of view types, so that this View is
         *        always of the right type (see {@link #getViewTypeCount()} and
         *        {@link #getItemViewType(int)}).
         * @param parent The parent that this view will eventually be attached to
         * @return the new view
         */
        @Override
        public View getView(int position, View old, ViewGroup parent) {
            View newView = old;
            LayoutInflater inflater = getLayoutInflater();

            if(newView == null) {
                newView = inflater.inflate(R.layout.layout_list_item, parent, false);
            }

            TextView textView = newView.findViewById(R.id.article);
            textView.setText(articles.get(position).getTitle());

            return newView;
        }
    }
}
