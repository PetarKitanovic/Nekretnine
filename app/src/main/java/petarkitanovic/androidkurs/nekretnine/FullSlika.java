package petarkitanovic.androidkurs.nekretnine;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import petarkitanovic.androidkurs.nekretnine.db.Slike;

public class FullSlika extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_slika);

        String slika = getIntent().getExtras().getString("slika");
        ImageView imageView = findViewById(R.id.imageView);

        Uri mUri = Uri.parse(slika);
        imageView.setImageURI(mUri);

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
