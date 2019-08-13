package ru.belugagroup.khrapachevrv.questioningapp.ui.partners;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
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
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbRespondent;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbRespondentDao;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.NetworkModule;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.QuestioningService;
import ru.belugagroup.khrapachevrv.questioningapp.ui.adapters.LinearLayoutManagerWrapper;
import ru.belugagroup.khrapachevrv.questioningapp.ui.adapters.PartnersAdapter;
import ru.belugagroup.khrapachevrv.questioningapp.ui.polls.PollsActivity;

public class PartnersActivity extends AppCompatActivity implements PartnersView , SearchView.OnQueryTextListener{

    public static final String PARTNER_NAME = "PARTNER_NAME";
    public static final String PARTNER_DB_ID = "PARTNER_DB_ID";

    private final static String TAG = "PartnersActivity";
    @Inject
    QuestioningService questioningService;
    private Deps deps;
    private PartnersAdapter partnersAdapter;
    private final List<DbRespondent> mPartnersData = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PartnersPresenter partnersPresenter;
    private Toolbar toolbar;
    private ProgressDialog mProgressDialog;
    private DbRespondentDao dbRespondentDao;



    private final PartnersAdapter.OnItemClickListener onPartnerItemClickListener = new PartnersAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(DbRespondent Item) {

            Log.d(TAG,"onItemClick " + Item.getName());
            Intent intent = new Intent(getApplicationContext(), PollsActivity.class);
            intent.putExtra(PARTNER_NAME, Item.getName());
            intent.putExtra(PARTNER_DB_ID, Item.getId());
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_partners);
        File cacheFile = new File(getCacheDir(), "responses");
        deps = DaggerDeps.builder().networkModule(new NetworkModule(cacheFile)).build();
        deps.inject(this);

        init();
        attachPresenter();
    }

    private void init(){

        // Attaching the layout to the toolbar object
        toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle(getResources().getString(R.string.partners_activity_label));
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Подключаем pref
        Hawk.init(this).build();
        // get the DAO
        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        dbRespondentDao = daoSession.getDbRespondentDao();

        //init pd
        mProgressDialog = new ProgressDialog(PartnersActivity.this,
                R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.progress_dialog));

        //render views
        recyclerView = findViewById(R.id.activity_partners_rv);
        swipeRefreshLayout = findViewById(R.id.activity_partners_swipe_layout);

        //init recyclerView
        recyclerView.setLayoutManager(new LinearLayoutManagerWrapper(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        partnersAdapter = new PartnersAdapter(mPartnersData,onPartnerItemClickListener);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(partnersAdapter);

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

        partnersPresenter = (PartnersPresenter) getLastCustomNonConfigurationInstance();
        if (partnersPresenter == null) {
            partnersPresenter = new PartnersPresenter(questioningService,dbRespondentDao);
        }
        partnersPresenter.attachView(this);

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
        partnersPresenter.detachView();
        super.onDestroy();

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return partnersPresenter;
    }

    @Override
    public void partnersSuccess(List<DbRespondent>  partnerList) {
        mPartnersData.clear();
        //recyclerView.getRecycledViewPool().clear();
        mPartnersData.addAll(partnerList);
        partnersAdapter.notifyDataSetChanged();
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
        partnersAdapter.getFilter().filter(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (s.isEmpty()){
            partnersAdapter.getFilter().filter(s);
        }

        return false;
    }
}
