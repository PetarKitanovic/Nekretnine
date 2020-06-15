package petarkitanovic.androidkurs.nekretnine;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import petarkitanovic.androidkurs.nekretnine.db.DatabaseHelper;
import petarkitanovic.androidkurs.nekretnine.db.Nekretnine;

public class MainActivity extends AppCompatActivity implements MyAdapter.OnItemClickListener {

    Toolbar toolbar;
    List<String> drawerItems;
    DrawerLayout drawerLayout;
    ListView drawerList;
    RelativeLayout drawerPane;
    ActionBarDrawerToggle drawerToggle;
    SharedPreferences prefs;
    DatabaseHelper databaseHelper;
    public static String NEKRETNINA_ID = "NEKRETNINA_ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillData();
        setupToolbar();
        setupDrawer();


        prefs = PreferenceManager.getDefaultSharedPreferences(this);

    }

    private void addNekretnina() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_layout);
        dialog.setTitle("Unesite podatke");
        dialog.setCanceledOnTouchOutside(false);

        Button add = dialog.findViewById(R.id.add_nekretnina);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText naziv = dialog.findViewById(R.id.naziv);
                EditText opis = dialog.findViewById(R.id.opis);
                EditText adresa = dialog.findViewById(R.id.adresa);
                EditText telefon = dialog.findViewById(R.id.telefon);
                EditText kvadratura = dialog.findViewById(R.id.kvadratura);
                EditText brojSoba = dialog.findViewById(R.id.brojSoba);
                EditText cena = dialog.findViewById(R.id.cena);


                Nekretnine nekretnine = new Nekretnine();
                nekretnine.setmNaziv(naziv.getText().toString());
                nekretnine.setmOpis(opis.getText().toString());
                nekretnine.setmAdresa(adresa.getText().toString());
                nekretnine.setmTelefon(telefon.getText().toString());
                nekretnine.setmKvadratura(kvadratura.getText().toString());
                nekretnine.setmBrojSoba(brojSoba.getText().toString());
                nekretnine.setmCena(cena.getText().toString());

                try {
                    getDatabaseHelper().getNekretnineDao().create(nekretnine);


                    boolean toast = prefs.getBoolean(getString(R.string.toast_key), false);

                    if (toast) {
                        Toast.makeText(MainActivity.this, "Uneta nova nekretnina", Toast.LENGTH_LONG).show();

                    }

                    refresh();

                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Cena karte mora biti broj", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();


            }

        });

        Button cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showNekretnine() {

        final RecyclerView recyclerView = this.findViewById(R.id.rvList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);

        MyAdapter adapter = new MyAdapter(getDatabaseHelper(), MainActivity.this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                addNekretnina();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void fillData() {
        drawerItems = new ArrayList<>();
        drawerItems.add("Sve nekretnine");
        drawerItems.add("Podesavanja");

    }

    public void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.drawer_menu);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }
    }

    private void setupDrawer() {
        drawerList = findViewById(R.id.left_drawer);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerPane = findViewById(R.id.drawerPane);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = "Unknown";
                switch (i) {
                    case 0:
                        title = "Sve nekretnine";
                        showNekretnine();
                        break;
                    case 1:
                        title = "Podesavanja";
                        Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(settings);
                        break;
                    default:
                        break;
                }
                drawerList.setItemChecked(i, true);
                setTitle(title);
                drawerLayout.closeDrawer(drawerPane);
            }
        });
        drawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, drawerItems));


        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

    }

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    public void onItemClick(int position) {
        Intent i = new Intent(MainActivity.this, DetaljiActivity.class);
        try {
            i.putExtra(NEKRETNINA_ID, getDatabaseHelper().getNekretnineDao().queryForAll().get(position).getmId());
            i.putExtra("position",getDatabaseHelper().getNekretnineDao().queryForAll().get(position).getmId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        startActivity(i);
    }

    private void refresh() {

        RecyclerView recyclerView = findViewById(R.id.rvList);
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);


            MyAdapter adapter = new MyAdapter(getDatabaseHelper(), MainActivity.this);
            recyclerView.setAdapter(adapter);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
}
