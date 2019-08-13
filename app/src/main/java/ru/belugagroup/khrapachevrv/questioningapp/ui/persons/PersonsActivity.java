package ru.belugagroup.khrapachevrv.questioningapp.ui.persons;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.belugagroup.khrapachevrv.questioningapp.App;
import ru.belugagroup.khrapachevrv.questioningapp.R;
import ru.belugagroup.khrapachevrv.questioningapp.deps.DaggerDeps;
import ru.belugagroup.khrapachevrv.questioningapp.deps.Deps;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DaoSession;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbPerson;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbPersonDao;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbRespondent;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbRespondentDao;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.NetworkModule;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.QuestioningService;
import ru.belugagroup.khrapachevrv.questioningapp.ui.adapters.LinearLayoutManagerWrapper;
import ru.belugagroup.khrapachevrv.questioningapp.ui.adapters.PartnersAdapter;
import ru.belugagroup.khrapachevrv.questioningapp.ui.adapters.PersonsAdapter;
import ru.belugagroup.khrapachevrv.questioningapp.ui.partners.PartnersPresenter;
import ru.belugagroup.khrapachevrv.questioningapp.ui.polls.PollsActivity;

public class PersonsActivity extends AppCompatActivity implements PersonsView , SearchView.OnQueryTextListener{

    public static final String PERSON_NAME = "PERSON_NAME";
    public static final String PERSON_DB_ID = "PERSON_DB_ID";

    private final static String TAG = "PersonsActivity";

    private PersonsAdapter personsAdapter;
    private final List<DbPerson> dbPersonList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PersonsPresenter personsPresenter;
    private Toolbar toolbar;
    private ProgressDialog mProgressDialog;
    private DbPersonDao dbPersonDao;



    private final PersonsAdapter.OnItemClickListener onPersonsItemClickListener = new PersonsAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(DbPerson Item) {

            Log.d(TAG,"onItemClick " + Item.getName());
            Intent resultIntent = new Intent();
            resultIntent.putExtra(PERSON_NAME, Item.getName());
            resultIntent.putExtra(PERSON_DB_ID, Item.getId());
            setResult(RESULT_OK, resultIntent);
            finish();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_persons);

        init();
        attachPresenter();
    }

    private void init(){

        // Attaching the layout to the toolbar object
        toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle(getResources().getString(R.string.persons_activity_label));
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Подключаем pref
        Hawk.init(this).build();
        // get the DAO
        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        dbPersonDao = daoSession.getDbPersonDao();

        //init pd
        mProgressDialog = new ProgressDialog(PersonsActivity.this,
                R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.progress_dialog));

        //render views
        recyclerView = findViewById(R.id.activity_persons_rv);
        swipeRefreshLayout = findViewById(R.id.activity_persons_swipe_layout);

        //init recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManagerWrapper(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        personsAdapter = new PersonsAdapter(dbPersonList,onPersonsItemClickListener);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(personsAdapter);

        //init refresh listener
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                startActivity(getIntent());
            }
        });

    }

    //Если presenter уже создан используем его, иначе создаем новый
    private void attachPresenter() {

        personsPresenter = (PersonsPresenter) getLastCustomNonConfigurationInstance();
        if (personsPresenter == null) {
            personsPresenter = new PersonsPresenter(dbPersonDao);
        }
        personsPresenter.attachView(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(9999);
        searchView.setOnQueryTextListener(this);
        return true;
    }


    @Override
    protected void onDestroy() {
        personsPresenter.detachView();
        super.onDestroy();

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return personsPresenter;
    }

    @Override
    public void personsSuccess(List<DbPerson>  personList) {
        dbPersonList.clear();
        //recyclerView.getRecycledViewPool().clear();
        dbPersonList.addAll(personList);
        personsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showWait() {
        mProgressDialog.show();
    }

    @Override
    public void removeWait() {
         mProgressDialog.dismiss();
    }

    @Override
    public void onFailure(String appErrorMessage) {
        Toast.makeText(getBaseContext(),appErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        Log.d(TAG,"onQueryTextSubmit s = " + s);
        personsAdapter.getFilter().filter(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (s.isEmpty()){
            personsAdapter.getFilter().filter(s);
        }

        return false;
    }
}
