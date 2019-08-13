package ru.belugagroup.khrapachevrv.questioningapp.ui.polls;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
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
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbTemplate;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.NetworkModule;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.QuestioningService;
import ru.belugagroup.khrapachevrv.questioningapp.ui.adapters.LinearLayoutManagerWrapper;
import ru.belugagroup.khrapachevrv.questioningapp.ui.adapters.PollsAdapter;
import ru.belugagroup.khrapachevrv.questioningapp.ui.questionnaire.QuestionnaireActivity;

import static ru.belugagroup.khrapachevrv.questioningapp.ui.partners.PartnersActivity.PARTNER_DB_ID;
import static ru.belugagroup.khrapachevrv.questioningapp.ui.partners.PartnersActivity.PARTNER_NAME;

public class PollsActivity extends AppCompatActivity implements PollsView {
    public final static String TEMPLATE_DB_ID = "TEMPLATE_DB_ID";
    public final static String TEMPLATE_NAME = "TEMPLATE_NAME";
    public final static int REQUEST_CODE_QUESTIONNAIRE = 1;
    private final static String TAG = "PollsActivity";

    @Inject
    QuestioningService questioningService;
    private PollsPresenter pollsPresenter;
    private Deps deps;
    private PollsAdapter pollsAdapter;
    private final List<DbTemplate> mTemplatesData = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String mPartnerName;
    private Long mPartnerDbId;
    private Toolbar toolbar;
    private DaoSession mDaoSession;
    private ProgressDialog mProgressDialog;
    private TextView tvPartnerName;


    private final PollsAdapter.OnItemClickListener onPollItemClickListener = new PollsAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(DbTemplate Item) {

            Log.d(TAG,"onItemClick " + Item.getTitle());
            Intent intent = new Intent(getApplicationContext(), QuestionnaireActivity.class);
            intent.putExtra(PARTNER_DB_ID, mPartnerDbId);
            intent.putExtra(PARTNER_NAME, mPartnerName);
            intent.putExtra(TEMPLATE_DB_ID, Item.getId());
            intent.putExtra(TEMPLATE_NAME, Item.getTitle());
            startActivityForResult(intent,REQUEST_CODE_QUESTIONNAIRE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_polls);
        File cacheFile = new File(getCacheDir(), "responses");
        deps = DaggerDeps.builder().networkModule(new NetworkModule(cacheFile)).build();
        deps.inject(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPartnerDbId = bundle.getLong(PARTNER_DB_ID, 0);
            mPartnerName = bundle.getString(PARTNER_NAME, "");
        }

        init();
        attachPresenter();
    }

    private void init(){
        //Подключаем pref
        Hawk.init(this).build();
        // Attaching the layout to the toolbar object
        toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle(getResources().getString(R.string.polls_activity_label));
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get the note DAO
        mDaoSession = ((App) getApplication()).getDaoSession();

        //init pd
        mProgressDialog = new ProgressDialog(PollsActivity.this,
                R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.progress_dialog));

        //init text views
        tvPartnerName = findViewById(R.id.activity_polls_tv_partner);
        tvPartnerName.setText(mPartnerName);

        //init recyclerView
        recyclerView = findViewById(R.id.activity_polls_rv);
        recyclerView.setLayoutManager(new LinearLayoutManagerWrapper(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        pollsAdapter = new PollsAdapter(mTemplatesData,onPollItemClickListener);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(pollsAdapter);

        //init swipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.activity_polls_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView();
            }
        });

    }

    private void attachPresenter() {

        pollsPresenter = (PollsPresenter) getLastCustomNonConfigurationInstance();
        if (pollsPresenter == null) {
            pollsPresenter = new PollsPresenter(questioningService,mDaoSession);
        }
        pollsPresenter.attachView(this);

    }



    @Override
    protected void onDestroy() {
        pollsPresenter.detachView();
        super.onDestroy();

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return pollsPresenter;
    }


    @Override
    public Long getPartnersDbId() {
        return mPartnerDbId;
    }

    @Override
    public void partnersSuccess(List<DbTemplate> templateList) {
        mTemplatesData.clear();
        mTemplatesData.addAll(templateList);
        pollsAdapter.notifyDataSetChanged();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // если пришло ОК
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_QUESTIONNAIRE:
                    refreshView();
                    break;
            }
            // если вернулось не ОК
        } else {
            refreshView();
        }
    }

    private void refreshView() {
        finish();
        startActivity(getIntent());
    }
}
