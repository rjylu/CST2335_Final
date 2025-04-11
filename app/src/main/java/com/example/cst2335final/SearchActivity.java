package com.example.cst2335final;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * The SearchActivity class is an activity that is used to search for articles. It takes in whatever the users enters and queries the Guardian.
 */
public class SearchActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    private ProgressBar progressBar;
    private JSONArray results;
    private List<Article> articles = new ArrayList<>();
    private MyListAdapter adapter;

    /**
     * <p>This method is called when the activity is first created. It sets up the activity for SearchActivity.</p>
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        SharedPreferences sharedPreferences = this.getSharedPreferences("sharedPrefs", MODE_PRIVATE);



        progressBar = findViewById(R.id.progressBar);

        Toolbar supportToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(supportToolbar);
        if (getSupportActionBar() != null) {
            String string = getResources().getString(R.string.menu_search);
            getSupportActionBar().setTitle(string + " 1.0");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, supportToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Log.d("Navigation", "NavigationView: " + navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        ListView listView = findViewById(R.id.listView);
        adapter = new MyListAdapter(SearchActivity.this, articles);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        EditText editText = findViewById(R.id.search_editText);
        if (sharedPreferences.contains("search")) {
            editText.setText(sharedPreferences.getString("search", ""));
        }
        Button button = findViewById(R.id.button_search);
        button.setOnClickListener(new View.OnClickListener() {
            /**
             * <p>This method is called when the button is clicked. It is used to search for articles taking the query from the user's edittext.</p>
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                articles.clear();
                adapter.notifyDataSetChanged();
                MyHTTPRequest req = new MyHTTPRequest();
                req.execute("https://content.guardianapis.com/search?api-key=4f732a4a-b27e-4ac7-9350-e9d0b11dd949&q=" + editText.getText().toString());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("search", editText.getText().toString());
                editor.apply();
                progressBar.setVisibility(VISIBLE);
            }
        });

        listView.setOnItemClickListener( (p, b, pos, id) -> {
            Intent intent = new Intent(SearchActivity.this, DetailsActivity.class);
            intent.putExtra("title", articles.get(pos).getTitle());
            intent.putExtra("section", articles.get(pos).getSection());
            intent.putExtra("url", articles.get(pos).getUrl());
            startActivity(intent);
        });


    }

    /**
     * <p>This method is called when the user clicks on an item in the navigation drawer. It is used to navigate to the correct fragment.</p>
     * @param item The selected item
     * @return True
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            intent.putExtra("fragment", "home");
            intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }else if (id == R.id.nav_search) {
            Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
            intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }else if (id == R.id.nav_favourites) {
            Intent intent = new Intent(SearchActivity.this, FavouritesActivity.class);
            intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            intent.putExtra("fragment", "about");
            intent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * <p>This class is used to make an HTTP request to the Guardian API.
     * It then parses the JSON response and stores the results in a list.</p>
     */
    private class MyHTTPRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);;
                }
                reader.close();
                return buffer.toString();

            }catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            view.setText(result);
            if (result != null) {
                try {
                    JSONObject responseObject = new JSONObject(result);
                    JSONObject response = responseObject.getJSONObject("response");
                    results = response.getJSONArray("results");

                    for (int i=0; i < response.length(); i++) {
                        JSONObject article = results.getJSONObject(i);
                        String title = article.getString("webTitle");
                        String section = article.getString("sectionName");
                        String webUrl = article.getString("webUrl");

                        Log.d("Title", title);
                        Log.d("Section", section);
                        Log.d("URL", webUrl);

                        articles.add(new Article(title, section, webUrl));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(GONE);
            }
        }

    }

    /**
     * <p>This class is used to display the articles in a listview.</p>
     */
    private class MyListAdapter extends BaseAdapter {
        SearchActivity searchActivity;
        List<Article> articles;

        private MyListAdapter(SearchActivity searchActivity, List<Article> articles) {
            this.searchActivity = searchActivity;
            this.articles = articles;
        }

        @Override
        public int getCount() { return articles.size(); }

        @Override
        public Object getItem(int position) {
            return articles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

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
