package ru.belugagroup.khrapachevrv.questioningapp.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ru.belugagroup.khrapachevrv.questioningapp.App;
import ru.belugagroup.khrapachevrv.questioningapp.R;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DaoSession;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionnaire;
import ru.belugagroup.khrapachevrv.questioningapp.models.prefs.PreferenceUtils;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.QuestioningService;
import ru.belugagroup.khrapachevrv.questioningapp.ui.adapters.LinearLayoutManagerWrapper;
import ru.belugagroup.khrapachevrv.questioningapp.ui.adapters.QuestionnairesAdapter;
import ru.belugagroup.khrapachevrv.questioningapp.ui.partners.PartnersActivity;
import ru.belugagroup.khrapachevrv.questioningapp.ui.questionnaire.QuestionnaireActivity;
import ru.belugagroup.khrapachevrv.questioningapp.ui.settings.SettingsActivity;

import static ru.belugagroup.khrapachevrv.questioningapp.ui.partners.PartnersActivity.PARTNER_DB_ID;
import static ru.belugagroup.khrapachevrv.questioningapp.ui.partners.PartnersActivity.PARTNER_NAME;
import static ru.belugagroup.khrapachevrv.questioningapp.ui.persons.PersonsActivity.PERSON_DB_ID;
import static ru.belugagroup.khrapachevrv.questioningapp.ui.persons.PersonsActivity.PERSON_NAME;
import static ru.belugagroup.khrapachevrv.questioningapp.ui.polls.PollsActivity.REQUEST_CODE_QUESTIONNAIRE;
import static ru.belugagroup.khrapachevrv.questioningapp.ui.polls.PollsActivity.TEMPLATE_DB_ID;
import static ru.belugagroup.khrapachevrv.questioningapp.ui.polls.PollsActivity.TEMPLATE_NAME;


public class MainActivity extends AppCompatActivity implements MainView {

    private final static String TAG = "MainActivity";
    public final static String IS_HISTORY = "IS_HISTORY";
    public final static String EDITABLE = "EDITABLE";
    public final static String QUESTIONNAIRE_ID = "QUESTIONNAIRE_ID";
    public final static String COMMENT = "COMMENT";
    private final static int REQUEST_CODE_PARTNERS = 90;
    public final static int REQUEST_CODE_SETTINGS = 34;

    @Inject
    public QuestioningService questioningService;
    private MainPresenter mainPresenter;
    private QuestionnairesAdapter adapter;
    private final List<DbQuestionnaire> mData = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private Button btnStartNew;
    private DaoSession mDaoSession;
    private TextView tvEmptyRv;
    private ProgressBar progressBar;
    private TextView tvPBMessage;
    private NestedScrollView nestedScrollView;
    private RelativeLayout rvMain;

    private final QuestionnairesAdapter.OnItemClickListener onItemClickListener = new QuestionnairesAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(DbQuestionnaire Item) {

            Intent intent = new Intent(getApplicationContext(), QuestionnaireActivity.class);
            intent.putExtra(QUESTIONNAIRE_ID, Item.getId());
            intent.putExtra(IS_HISTORY, true);
            intent.putExtra(PARTNER_DB_ID, Item.getRespondentId());
            intent.putExtra(PARTNER_NAME, Item.getRespondent().getName());
            intent.putExtra(TEMPLATE_DB_ID, Item.getTemplateId());
            intent.putExtra(TEMPLATE_NAME, Item.getTemplate().getTitle());
            if(Item.getPerson()!=null){
                intent.putExtra(PERSON_NAME, Item.getPerson().getName());
                intent.putExtra(PERSON_DB_ID, Item.getPersonId());
            }
            if (Item.getDateInMillis()<=PreferenceUtils.getLastSyncTime()){
                intent.putExtra(EDITABLE, false);
            }
            intent.putExtra(COMMENT,Item.getComment());

            startActivityForResult(intent,REQUEST_CODE_QUESTIONNAIRE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        init();
        attachPresenter();
    }

    void init() {
        //Подключаем pref
        Hawk.init(this).build();

        // get the note DAO
        mDaoSession = ((App) getApplication()).getDaoSession();

        // Attaching the layout to the toolbar object
        toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle(getResources().getString(R.string.main_activity_label));
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);

        //init recyclerView
        recyclerView = findViewById(R.id.activity_main_rv);
        recyclerView.setLayoutManager(new LinearLayoutManagerWrapper(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new QuestionnairesAdapter(mData, onItemClickListener);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);

        tvPBMessage =   findViewById(R.id.progress_bar_message);
        progressBar =   findViewById(R.id.activity_main_progress_bar);
        nestedScrollView = findViewById(R.id.activity_main_sv);
        tvEmptyRv = findViewById(R.id.activity_main_empty_rv_text);
        rvMain = findViewById(R.id.activity_main_rv_main);

        //init swipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.activity_main_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView();
            }
        });


        btnStartNew = findViewById(R.id.activity_main_btn_start_new);
        btnStartNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PartnersActivity.class);
                startActivityForResult(intent, REQUEST_CODE_PARTNERS);
            }
        });

    }



    private void attachPresenter() {

        mainPresenter = (MainPresenter) getLastCustomNonConfigurationInstance();
        if (mainPresenter == null) {
            mainPresenter = new MainPresenter(questioningService, mDaoSession);
        }
        mainPresenter.attachView(this);

    }

    private void refreshView() {
        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SETTINGS);

            return true;
        }else if (id == R.id.action_exchange){

            mainPresenter.exchange();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {

        mainPresenter.detachView();
        super.onDestroy();

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mainPresenter;
    }


    @Override
    public void showWait() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            tvPBMessage.setVisibility(View.VISIBLE);
            rvMain.setVisibility(View.GONE);
        }
    }

    @Override
    public void removeWait() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
            tvPBMessage.setVisibility(View.GONE);
            rvMain.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFailure(String appErrorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(appErrorMessage)
                .setTitle("Ошибка!")
                .setPositiveButton("Продолжить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
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
    public void questionnaireSuccess(List<DbQuestionnaire> questionnaireList) {
        mData.clear();
        mData.addAll(questionnaireList);
        adapter.notifyDataSetChanged();

        if (mData.isEmpty()){
            tvEmptyRv.setVisibility(View.VISIBLE);
        }else{
            tvEmptyRv.setVisibility(View.GONE);
        }
    }

    @Override
    public void exchangeSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.exchange_success)
                .setTitle(R.string.success)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        adapter.notifyDataSetChanged();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();

        final Handler handler  = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    adapter.notifyDataSetChanged();
                }
            }
        };

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });

        handler.postDelayed(runnable, 2000);
    }

    @Override
    public void setProgressMessage(String message) {
        tvPBMessage.setText(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // если пришло ОК
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_PARTNERS:
                    adapter.notifyDataSetChanged();
                    break;
                case REQUEST_CODE_SETTINGS:

                    break;
                case REQUEST_CODE_QUESTIONNAIRE:
                    refreshView();
                    break;
            }
            // если вернулось не ОК
        } else {
            refreshView();
        }
    }
}
