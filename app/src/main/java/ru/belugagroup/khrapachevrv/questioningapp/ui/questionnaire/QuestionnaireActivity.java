package ru.belugagroup.khrapachevrv.questioningapp.ui.questionnaire;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import ru.belugagroup.khrapachevrv.questioningapp.App;
import ru.belugagroup.khrapachevrv.questioningapp.R;
import ru.belugagroup.khrapachevrv.questioningapp.deps.Deps;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DaoSession;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionForQuestionnaire;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbTemplate;
import ru.belugagroup.khrapachevrv.questioningapp.ui.adapters.QuestionsAdapter;
import ru.belugagroup.khrapachevrv.questioningapp.ui.persons.PersonsActivity;


import static ru.belugagroup.khrapachevrv.questioningapp.ui.main.MainActivity.COMMENT;
import static ru.belugagroup.khrapachevrv.questioningapp.ui.main.MainActivity.EDITABLE;
import static ru.belugagroup.khrapachevrv.questioningapp.ui.main.MainActivity.IS_HISTORY;
import static ru.belugagroup.khrapachevrv.questioningapp.ui.main.MainActivity.QUESTIONNAIRE_ID;
import static ru.belugagroup.khrapachevrv.questioningapp.ui.partners.PartnersActivity.PARTNER_DB_ID;
import static ru.belugagroup.khrapachevrv.questioningapp.ui.partners.PartnersActivity.PARTNER_NAME;
import static ru.belugagroup.khrapachevrv.questioningapp.ui.persons.PersonsActivity.PERSON_DB_ID;
import static ru.belugagroup.khrapachevrv.questioningapp.ui.persons.PersonsActivity.PERSON_NAME;
import static ru.belugagroup.khrapachevrv.questioningapp.ui.polls.PollsActivity.TEMPLATE_DB_ID;
import static ru.belugagroup.khrapachevrv.questioningapp.ui.polls.PollsActivity.TEMPLATE_NAME;

public class QuestionnaireActivity extends AppCompatActivity implements QuestionnaireView {

    private final static String TAG = "QuestionnaireActivity";
    public final static int REQUEST_CODE_PERSON = 103;

    private QuestionnairePresenter questionnairePresenter;
    private Deps deps;
    private String mTemplateName, mPartnerName, mPersonName, mComment;
    private TextView textViewIntroduction, textViewConclusion;
    private Long mPartnerDbId, mTemplateDbId, mQuestionnaireId, mPersonDbId;
    private QuestionsAdapter questionsAdapter;
    private final List<DbQuestionForQuestionnaire> mData = new ArrayList<>();
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private DaoSession mDaoSession;
    private Boolean isHistory, editable;
    private ProgressDialog mProgressDialog;
    private EditText etComment,etPerson,tvPartner, tvTemplate;
    private ImageButton btnClear;


    private final QuestionsAdapter.OnItemClickListener onSectionItemClickListener = new QuestionsAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position, boolean isChecked) {

            Log.d(TAG, " is checked =" + isChecked);

            questionnairePresenter.setItemChecked(position, isChecked);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_questionnaire);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mQuestionnaireId = bundle.getLong(QUESTIONNAIRE_ID, 0);
            isHistory = bundle.getBoolean(IS_HISTORY, false);
            mTemplateDbId = bundle.getLong(TEMPLATE_DB_ID, 0);
            mPartnerDbId = bundle.getLong(PARTNER_DB_ID, 0);
            mPartnerName = bundle.getString(PARTNER_NAME, "");
            mTemplateName = bundle.getString(TEMPLATE_NAME, "");
            mPersonName = bundle.getString(PERSON_NAME,"");
            mPersonDbId = bundle.getLong(PERSON_DB_ID,0L);
            mComment = bundle.getString(COMMENT,"");
            editable = bundle.getBoolean(EDITABLE,true);
        }

        init();
        attachPresenter();
    }

    private void init() {
        //Подключаем pref
        Hawk.init(this).build();
        // Attaching the layout to the toolbar object
        toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle(getResources().getString(R.string.questionnaire_activity_label));
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //render tv
        tvPartner = findViewById(R.id.activity_questionnaire_tv_partner);
        tvPartner.setText(mPartnerName);
        tvTemplate = findViewById(R.id.activity_questionnaire_tv_template);
        tvTemplate.setText(mTemplateName);
        textViewIntroduction = findViewById(R.id.activity_questionnaire_tv_introduction);
        textViewConclusion = findViewById(R.id.activity_questionnaire_tv_conclusion);
        etComment   =   findViewById(R.id.activity_questionnaire_et_comment);
        etComment.setText(mComment);

        // get the note DAO
        mDaoSession = ((App) getApplication()).getDaoSession();

        //init pd
        mProgressDialog = new ProgressDialog(QuestionnaireActivity.this,
                R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.progress_dialog_save));

        //init recyclerView
        recyclerView = findViewById(R.id.activity_questionnaire_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionsAdapter = new QuestionsAdapter(mData, onSectionItemClickListener, editable);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(questionsAdapter);

        //Clear button
        btnClear = findViewById(R.id.activity_questionnaire_button_clear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPerson.setText("");
                mPersonDbId = 0L;
                btnClear.setVisibility(View.GONE);
            }
        });

        //text view Person
        etPerson = findViewById(R.id.activity_questionnaire_et_person);
        etPerson.setText(mPersonName);

        if (isHistory & !editable){
            etPerson.setFocusable(false);
            etPerson.setHint("");
            etComment.setFocusable(false);
            btnClear.setVisibility(View.GONE);
        }else{

            if (mPersonName.equals("")){
                btnClear.setVisibility(View.GONE);
            }

            etPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), PersonsActivity.class);
                    startActivityForResult(intent,REQUEST_CODE_PERSON);
                }
            });
        }

    }

    private void attachPresenter() {

        questionnairePresenter = (QuestionnairePresenter) getLastCustomNonConfigurationInstance();
        if (questionnairePresenter == null) {
            questionnairePresenter = new QuestionnairePresenter(mDaoSession);
        }
        questionnairePresenter.attachView(this);

    }


    @Override
    protected void onDestroy() {

        questionnairePresenter.detachView();
        super.onDestroy();

    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return questionnairePresenter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        if (editable & isHistory) {
            getMenuInflater().inflate(R.menu.questionnaire_menu_update_delete, menu);
        }else if(editable & !isHistory){
            getMenuInflater().inflate(R.menu.questionnaire_menu_complete, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.menu_complete:
                questionnairePresenter.createQuestionnaire();
                break;
            case R.id.menu_update:
                 questionnairePresenter.updateQuestionnaire();
                break;
            case R.id.menu_delete:
                 questionnairePresenter.deleteQuestionnaire();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Long getTemplateDbId() {
        return mTemplateDbId;
    }

    @Override
    public Long getPartnerDbId() {
        return mPartnerDbId;
    }

    @Override
    public Long getQuestionnaireDbId() {
        return mQuestionnaireId;
    }

    @Override
    public Long getPersonDbId() {
          return mPersonDbId;
    }

    @Override
    public String getComment() {
        return etComment.getText().toString();
    }

    @Override
    public Boolean getIsHistory() {
        return isHistory;
    }


    @Override
    public void templateSuccess(DbTemplate template, List<DbQuestionForQuestionnaire> answerList) {
        textViewIntroduction.setText(template.getIntroduction());

        mData.clear();
        mData.addAll(answerList);
        questionsAdapter.notifyDataSetChanged();

        textViewConclusion.setText(template.getConclusion());
    }

    @Override
    public void createSuccess() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.questionnaire_save_success)
                .setTitle(R.string.success)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resultOk();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
                resultOk();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
                resultOk();
            }
        });

        handler.postDelayed(runnable, 2000);
    }

    @Override
    public void deleteSuccess() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.questionnaire_delete_success)
                .setTitle(R.string.success)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resultOk();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();

                }
            }
        };

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
                resultOk();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
                resultOk();
            }
        });

        handler.postDelayed(runnable, 2000);

    }

    @Override
    public void updateSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.questionnaire_update_success)
                .setTitle(R.string.success)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resultOk();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();

                }
            }
        };

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
                resultOk();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
                resultOk();
            }
        });

        handler.postDelayed(runnable, 2000);
    }

    void resultOk() {
        setResult(RESULT_OK, null);
        finish();
    }

    @Override
    public void showWait() {
        mProgressDialog.show();
    }

    @Override
    public void removeWait() {
        mProgressDialog.hide();
    }

    @Override
    public void onFailure(String appErrorMessage) {
        Toast.makeText(getBaseContext(), appErrorMessage, Toast.LENGTH_LONG).show();
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
                case REQUEST_CODE_PERSON:
                    if (data != null){
                        etPerson.setText(data.getStringExtra(PERSON_NAME));
                        mPersonDbId = data.getLongExtra(PERSON_DB_ID,0L);
                        btnClear.setVisibility(View.VISIBLE);
                    }

                    break;

            }
            // если вернулось не ОК
        }
    }
}
