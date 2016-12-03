package id.sch.smktelkom_mlg.project.xirpl506152433.ok;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import id.sch.smktelkom_mlg.project.xirpl506152433.ok.adapter.NoteAdapter;
import id.sch.smktelkom_mlg.project.xirpl506152433.ok.model.Note;

public class MainActivity extends AppCompatActivity implements NoteAdapter.IHotelAdapter {
    public static final String HOTEL = "hotel";
    public static final int REQUEST_CODE_ADD = 88;
    public static final int REQUEST_CODE_EDIT = 99;
    ArrayList<Note> mList = new ArrayList<>();
    NoteAdapter mAdapter;
    DialogInterface.OnClickListener listener;

    int itemPos;

    ArrayList<Note> mListAll = new ArrayList<>();
    boolean isFiltered;
    ArrayList<Integer> mListMapFilter = new ArrayList<>();
    String mQuery;

    //scenes to transition
    private Scene scene1, scene2;
    //transition to move between scenes
    private Transition transition;
    //flag to swap between scenes
    private boolean start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //get the layout ID
        RelativeLayout baseLayout = (RelativeLayout) findViewById(R.id.base);

        //first scene
        ViewGroup startViews = (ViewGroup) getLayoutInflater()
                .inflate(R.layout.activity_main, baseLayout, false);

        //second scene
        ViewGroup endViews = (ViewGroup) getLayoutInflater()
                .inflate(R.layout.activity_main, baseLayout, false);
        //create the two scenes
        scene1 = new Scene(baseLayout, startViews);
        scene2 = new Scene(baseLayout, endViews);

        //create transition, set properties
        transition = new AutoTransition();
        transition.setDuration(5000);
        transition.setInterpolator(new AccelerateDecelerateInterpolator());

        //initialize flag
        start = true;


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new NoteAdapter(this, mList);
        recyclerView.setAdapter(mAdapter);

        fillData();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goAdd();
            }
        });
    }

    private void goAdd() {
        startActivityForResult(new Intent(this, InputActivity.class), REQUEST_CODE_ADD);
    }

    private void fillData() {
        Resources resources = getResources();
        String[] arTanggal = resources.getStringArray(R.array.date);
        String[] arCategory = resources.getStringArray(R.array.category);
        String[] arDiary = resources.getStringArray(R.array.diary);
        String[] arQuotes = resources.getStringArray(R.array.quotes);
        TypedArray a = resources.obtainTypedArray(R.array.picture);
        String[] arFoto = new String[a.length()];

        for (int i = 0; i < arFoto.length; i++) {
            int id = a.getResourceId(i, 0);
            arFoto[i] = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + resources.getResourcePackageName(id) + '/'
                    + resources.getResourceTypeName(id) + '/'
                    + resources.getResourceEntryName(id);
        }
        a.recycle();

        for (int i = 0; i < arTanggal.length; i++) {
            mList.add(new Note(arTanggal[i], arCategory[i],
                    arDiary[i], arQuotes[i], arFoto[i]));
        }
        mAdapter.notifyDataSetChanged();
    }

    public void changeScene(View v) {

        //check flag
        if (start) {
            TransitionManager.go(scene2, transition);
            start = false;
        } else {
            TransitionManager.go(scene1, transition);
            start = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)
                MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        mQuery = newText.toLowerCase();
                        doFilter(mQuery);
                        return true;
                    }
                }
        );

        return true;
    }

    private void doFilter(String query) {

        if (!isFiltered) {
            mListAll.clear();
            mListAll.addAll(mList);
            isFiltered = true;
        }

        mList.clear();
        if (query == null || query.isEmpty()) {
            mList.addAll(mListAll);
            isFiltered = false;
        } else {
            mListMapFilter.clear();
            for (int i = 0; i < mListAll.size(); i++) {
                Note hotel = mListAll.get(i);
                if (hotel.tanggal.toLowerCase().contains(query) ||
                        hotel.category.toLowerCase().contains(query) ||
                        hotel.diary.toLowerCase().contains(query)) {
                    mList.add(hotel);
                    mListMapFilter.add(i);
                }
            }
        }
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //jika tombol BACK ditekan
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Keluar();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void Keluar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin keluar?");
        builder.setCancelable(false);//tombol BACK tidak bisa tekan

        listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    finish(); //keluar aplikasi
                } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                    dialog.cancel(); //batal keluar
                }
            }
        };

        builder.setPositiveButton("Ya", listener);
        builder.setNegativeButton("Tidak", listener);
        builder.show(); //menampilkan dialog

    }


    @Override
    public void doClick(int pos) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(HOTEL, mList.get(pos));
        startActivity(intent);
    }
    @Override
    public void doEdit(int pos) {
        itemPos = pos;
        Intent intent = new Intent(this, InputActivity.class);
        intent.putExtra(HOTEL, mList.get(pos));
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }

    @Override
    public void doDelete(int pos) {
        itemPos = pos;
        final Note hotel = mList.get(pos);
        mList.remove(itemPos);
        if (isFiltered) mListAll.remove(mListMapFilter.get(itemPos).intValue());
        mAdapter.notifyDataSetChanged();
        Snackbar.make(findViewById(R.id.fab), hotel.tanggal + " Terhapus", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mList.add(itemPos, hotel);
                        if (isFiltered) mListAll.add(mListMapFilter.get(itemPos), hotel);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .show();
    }

    @Override
    public void doShare(int pos) {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "AndroidSolved");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Now Learn Android with AndroidSolved clicke here to visit https://androidsolved.wordpress.com/ ");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK) {
            Note hotel = (Note) data.getSerializableExtra(HOTEL);
            mList.add(hotel);
            if (isFiltered) mListAll.add(hotel);
            doFilter(mQuery);
            //mAdapter.notifyDataSetChanged();
        } else if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            Note hotel = (Note) data.getSerializableExtra(HOTEL);
            mList.remove(itemPos);
            if (isFiltered) mListAll.remove(mListMapFilter.get(itemPos).intValue());
            mList.add(itemPos, hotel);
            if (isFiltered) mListAll.add(mListMapFilter.get(itemPos), hotel);
            mAdapter.notifyDataSetChanged();
        }

    }
}
