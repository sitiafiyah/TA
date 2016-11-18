package id.sch.smktelkom_mlg.project.xirpl506152433.ok;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import id.sch.smktelkom_mlg.project.xirpl506152433.ok.model.Hotel;

public class InputActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_GET = 1;
    EditText etJudul;
    EditText etKategori;
    EditText etDiary;
    EditText etQuotes;
    ImageView ivFoto;
    Uri uriFoto;
    Hotel hotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        etJudul = (EditText) findViewById(R.id.editTextNama);
        etKategori = (EditText) findViewById(R.id.editTextKategori);
        etDiary = (EditText) findViewById(R.id.editTextDiary);
        etQuotes = (EditText) findViewById(R.id.editTextQuotes);
        ivFoto = (ImageView) findViewById(R.id.imageViewFoto);

        ivFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhoto();
            }
        });

        findViewById(R.id.buttonSimpan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSave();
            }
        });

    }

    private void doSave() {
        String tanggal = etJudul.getText().toString();
        String kategori = etKategori.getText().toString();
        String diary = etDiary.getText().toString();
        String quotes = etQuotes.getText().toString();

        if (isValid(tanggal, kategori, diary, quotes, uriFoto)) {
            hotel = new Hotel(tanggal, kategori,
                    diary, quotes, uriFoto.toString());

            Intent intent = new Intent();
            intent.putExtra(MainActivity.HOTEL, hotel);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private boolean isValid(String tanggal, String kategori, String diary, String quotes, Uri uriFoto) {
        boolean valid = true;
        if (tanggal.isEmpty()) {
            setErrorEmpty(etJudul);
            valid = false;
        }

        if (kategori.isEmpty()) {
            setErrorEmpty(etKategori);
            valid = false;
        }

        if (diary.isEmpty()) {
            setErrorEmpty(etDiary);
            valid = false;
        }

        if (quotes.isEmpty()) {
            setErrorEmpty(etQuotes);
            valid = false;
        }

        if (uriFoto == null) {
            Snackbar.make(ivFoto, "Foto Belum Ada", Snackbar.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    private void setErrorEmpty(EditText editText) {
        editText.setError(((TextInputLayout) editText.getParent().getParent())
                .getHint() + "Belum Diisi");
    }

    private void pickPhoto() {
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, REQUEST_IMAGE_GET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            uriFoto = data.getData();
            ivFoto.setImageURI(uriFoto);
        }
    }
}