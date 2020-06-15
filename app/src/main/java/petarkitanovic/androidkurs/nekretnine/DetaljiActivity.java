package petarkitanovic.androidkurs.nekretnine;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import petarkitanovic.androidkurs.nekretnine.db.DatabaseHelper;
import petarkitanovic.androidkurs.nekretnine.db.Nekretnine;
import petarkitanovic.androidkurs.nekretnine.db.Slike;

public class DetaljiActivity extends AppCompatActivity implements SlikaAdapter.OnItemClickListener {

    Nekretnine nekretnine;
    DatabaseHelper databaseHelper;

    Toolbar toolbar;
    List<String> drawerItems;
    DrawerLayout drawerLayout;
    ListView drawerList;
    RelativeLayout drawerPane;
    ActionBarDrawerToggle drawerToggle;
    SharedPreferences prefs;

    SlikaAdapter adapter;
//    private RecyclerView rec_list;

    ImageView preview;
    String imagePath = null;
    private static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalji);

        fillData();
        setupToolbar();
        setupDrawer();


        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        int nekretninaId = getIntent().getExtras().getInt(MainActivity.NEKRETNINA_ID);

        try {
            nekretnine = getDatabaseHelper().getNekretnineDao().queryForId(nekretninaId);

            TextView naziv = findViewById(R.id.detalji_naziv);
            TextView adresa = findViewById(R.id.detalji_adresa);
            TextView telefon = findViewById(R.id.detalji_telefon);
            TextView kvadratura = findViewById(R.id.detalji_kvadratura);
            TextView brojSoba = findViewById(R.id.detalji_broj_soba);
            TextView cena = findViewById(R.id.detalji_cena);
            TextView opis = findViewById(R.id.detalji_opis);

            naziv.setText(nekretnine.getmNaziv());
            adresa.setText("Adresa: " + nekretnine.getmAdresa());
            telefon.setText("Telefon: " + nekretnine.getmTelefon());
            kvadratura.setText("Kvadratura: " + nekretnine.getmKvadratura() + "m2");
            brojSoba.setText("Broj soba: " + nekretnine.getmBrojSoba());
            cena.setText("Cena: " + nekretnine.getmCena() + "eur");
            opis.setText(nekretnine.getmOpis());

            telefon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + nekretnine.getmTelefon()));
                    startActivity(intent);
                }
            });


        } catch (SQLException e) {
            e.printStackTrace();
        }

//        rec_list = findViewById(R.id.rvList);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO urediti meni
        getMenuInflater().inflate(R.menu.detalji_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_slika:
                addSlike();
                break;
            case R.id.edit:
                editNekretninu();
                break;
            case R.id.delete:
                deleteNekretninu();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    private void addSlike() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_slike);
        dialog.setTitle("Unesite podatke");
        dialog.setCanceledOnTouchOutside(false);

        Button chooseBtn = dialog.findViewById(R.id.btn_izaberi);
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preview = dialog.findViewById(R.id.preview_image1);
                selectPicture();
            }
        });

        Button add = dialog.findViewById(R.id.add_atrakcija);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (preview == null || imagePath == null) {
                    Toast.makeText(DetaljiActivity.this, "Slika mora biti izabrana", Toast.LENGTH_SHORT).show();
                    return;
                }

                int position = getIntent().getExtras().getInt("position");
                try {
                    nekretnine = getDatabaseHelper().getNekretnineDao().queryForId(position);
                    Slike slike = new Slike();

                    slike.setmSLika(imagePath);
                    slike.setmNekretnine(nekretnine);
                    getDatabaseHelper().getSlikeDao().create(slike);

                    refresh();
                    reset();

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

    private void editNekretninu() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_layout);
        dialog.setCanceledOnTouchOutside(false);

        final EditText naziv = dialog.findViewById(R.id.edit_naziv);
        final EditText adresa = dialog.findViewById(R.id.edit_adresa);
        final EditText telefon = dialog.findViewById(R.id.edit_telefon);
        final EditText kvadratura = dialog.findViewById(R.id.edit_kvadratura);
        final EditText brojSoba = dialog.findViewById(R.id.edit_brojSoba);
        final EditText cena = dialog.findViewById(R.id.edit_cena);
        final EditText opis = dialog.findViewById(R.id.edit_opis);


        naziv.setText(nekretnine.getmNaziv());
        adresa.setText(nekretnine.getmAdresa());
        telefon.setText(nekretnine.getmTelefon());
        kvadratura.setText(nekretnine.getmKvadratura());
        brojSoba.setText(nekretnine.getmBrojSoba());
        cena.setText(nekretnine.getmCena());
        opis.setText(nekretnine.getmOpis());

        Button add = dialog.findViewById(R.id.edit_nekretnina);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateInput(naziv)
                        && validateInput(opis)
                        && validateInput(adresa)
                        && validateInput(telefon)
                        && validateInput(kvadratura)
                        && validateInput(brojSoba)
                        && validateInput(cena)
                ) {


                    nekretnine.setmNaziv(naziv.getText().toString());
                    nekretnine.setmAdresa(adresa.getText().toString());
                    nekretnine.setmTelefon(telefon.getText().toString());
                    nekretnine.setmKvadratura(kvadratura.getText().toString());
                    nekretnine.setmBrojSoba(brojSoba.getText().toString());
                    nekretnine.setmCena(cena.getText().toString());
                    nekretnine.setmOpis(opis.getText().toString());


                    try {
                        getDatabaseHelper().getNekretnineDao().update(nekretnine);

                        refreshNekretnine();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    dialog.dismiss();

                }
            }

        });

        Button cancel = dialog.findViewById(R.id.cancel_edit);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void deleteNekretninu() {
        AlertDialog dialogDelete = new AlertDialog.Builder(this)
                .setTitle("Brisanje nekretnine")
                .setMessage("Da li zelite da obrisete \"" + nekretnine.getmNaziv() + "\"?")
                .setPositiveButton("DA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            List<Slike> slike = getDatabaseHelper().getSlikeDao().queryForEq(Slike.FIELD_NAME, nekretnine.getmId());

                            getDatabaseHelper().getNekretnineDao().delete(nekretnine);
                            for (Slike slika : slike) {
                                getDatabaseHelper().getSlikeDao().delete(slika);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        finish();
                    }
                })
                .setNegativeButton("NE", null)
                .show();
    }

    public static boolean validateInput(EditText editText) {
        String titleInput = editText.getText().toString().trim();

        if (titleInput.isEmpty()) {
            editText.setError("Polje ne moze da bude prazno");
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }

    private void refreshNekretnine() {
        int nekretninaId = getIntent().getExtras().getInt(MainActivity.NEKRETNINA_ID);

        try {
            nekretnine = getDatabaseHelper().getNekretnineDao().queryForId(nekretninaId);

            TextView naziv = findViewById(R.id.detalji_naziv);
            TextView adresa = findViewById(R.id.detalji_adresa);
            TextView telefon = findViewById(R.id.detalji_telefon);
            TextView kvadratura = findViewById(R.id.detalji_kvadratura);
            TextView brojSoba = findViewById(R.id.detalji_broj_soba);
            TextView cena = findViewById(R.id.detalji_cena);
            TextView opis = findViewById(R.id.detalji_opis);

            naziv.setText(nekretnine.getmNaziv());
            adresa.setText("Adresa: " + nekretnine.getmAdresa());
            telefon.setText("Telefon: " + nekretnine.getmTelefon());
            kvadratura.setText("Kvadratura: " + nekretnine.getmKvadratura() + "m2");
            brojSoba.setText("Broj soba: " + nekretnine.getmBrojSoba());
            cena.setText("Cena: " + nekretnine.getmCena() + "eur");
            opis.setText(nekretnine.getmOpis());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refresh() {
        List<Slike> listaSlika = null;
        try {
            listaSlika = getDatabaseHelper().getSlikeDao().queryBuilder()
                    .where()
                    .eq(Slike.FIELD_NAME, nekretnine.getmId())
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (adapter != null) {
            adapter.clear();
            adapter.addAll(listaSlika);
            adapter.notifyDataSetChanged();
        } else {
            adapter = new SlikaAdapter(DetaljiActivity.this, listaSlika, DetaljiActivity.this);

            final ViewPager2 viewPager = findViewById(R.id.viewpager);

            final ImageButton leftNav = findViewById(R.id.left_nav);
            ImageButton rightNav = findViewById(R.id.right_nav);

            viewPager.setAdapter(adapter);


            // Images left navigation
            leftNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tab = viewPager.getCurrentItem();
                    if (tab > 0) {
                        tab--;
                        viewPager.setCurrentItem(tab);

                    } else if (tab == 0) {
                        viewPager.setCurrentItem(tab);

                    }


                }
            });

            // Images right navigatin
            rightNav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tab = viewPager.getCurrentItem();
                    tab++;
                    viewPager.setCurrentItem(tab);
                }
            });
        }
    }


    private void fillData() {
        drawerItems = new ArrayList<>();
        drawerItems.add("Sve atrakcije");
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
                        finish();
                        break;
                    case 1:
                        title = "Podesavanja";
                        Intent settings = new Intent(DetaljiActivity.this, SettingsActivity.class);
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

    private void reset() {
        imagePath = "";
        preview = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            if (selectedImage != null) {
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imagePath = cursor.getString(columnIndex);
                    cursor.close();

                    if (preview != null) {
                        preview.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                    }
                }
            }
        }
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {


                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation

            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

        }
    }

    private void selectPicture() {
        if (isStoragePermissionGranted()) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, SELECT_PICTURE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onItemClick(int position) {
        List<Slike> listaSlika = null;
        try {
            listaSlika = getDatabaseHelper().getSlikeDao().queryBuilder()
                    .where()
                    .eq(Slike.FIELD_NAME, nekretnine.getmId())
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(DetaljiActivity.this, FullSlika.class);
        intent.putExtra("slika", listaSlika.get(position).getmSLika());
        startActivity(intent);
    }
}
