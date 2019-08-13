package ru.belugagroup.khrapachevrv.questioningapp.ui.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;

import java.io.File;

import javax.inject.Inject;

import ru.belugagroup.khrapachevrv.questioningapp.App;
import ru.belugagroup.khrapachevrv.questioningapp.R;
import ru.belugagroup.khrapachevrv.questioningapp.deps.DaggerDeps;
import ru.belugagroup.khrapachevrv.questioningapp.deps.Deps;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DaoSession;
import ru.belugagroup.khrapachevrv.questioningapp.models.prefs.PreferenceUtils;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.NetworkModule;
import ru.belugagroup.khrapachevrv.questioningapp.networking.asynchronous.QuestioningService;


public class SettingsActivity extends AppCompatActivity implements SettingsView {
    private final static String TAG = "SettingsActivity";
    @Inject
    QuestioningService questioningService;
    private Toolbar toolbar;
    private Deps deps;
    private SettingsPresenter mSettingsPresenter;
    private EditText etUrl, etUserName, etPassword, etInterviewer;
    private TextInputLayout tilUrl, tilUserName, tilPassword,tilInterviewer;
    private Button btnExchange;
    private DaoSession daoSession;
    private LinearLayout llMain;
    private ProgressBar progressBar;
    private TextView tvPBMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        File cacheFile = new File(getCacheDir(), "responses");
        deps = DaggerDeps.builder().networkModule(new NetworkModule(cacheFile)).build();
        deps.inject(this);

        init();
        attachPresenter();

    }

    private void attachPresenter() {

        mSettingsPresenter = (SettingsPresenter) getLastCustomNonConfigurationInstance();
        if (mSettingsPresenter == null) {
            mSettingsPresenter = new SettingsPresenter(questioningService, daoSession);
        }
        mSettingsPresenter.attachView(this);

    }

    @Override
    protected void onDestroy() {
        mSettingsPresenter.detachView();

        super.onDestroy();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mSettingsPresenter;
    }

    private void init() {
        //Подключаем pref
        Hawk.init(this).build();

        // get the note DAO
        daoSession = ((App) getApplication()).getDaoSession();

        // Attaching the layout to the toolbar object
        toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle(getResources().getString(R.string.settings_activity_label));
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //render views
        etUrl = findViewById(R.id.activity_settings_et_url);
        etUrl.setText(PreferenceUtils.getUrl());

        etUserName = findViewById(R.id.activity_settings_et_user_name);
        etUserName.setText(PreferenceUtils.getUserName());
        etPassword = findViewById(R.id.activity_settings_et_password);
        etPassword.setText(PreferenceUtils.getPassword());
        etInterviewer = findViewById(R.id.activity_settings_et_interviewer);
        etInterviewer.setText(PreferenceUtils.getInterviewer());
        tilUrl = findViewById(R.id.activity_settings_input_layout_url);
        tilInterviewer = findViewById(R.id.activity_settings_input_layout_interviewer);
        tilPassword = findViewById(R.id.activity_settings_input_layout_password);
        tilUserName = findViewById(R.id.activity_settings_input_layout_user_name);
        llMain  =   findViewById(R.id.activity_settings_ll_main);
        tvPBMessage =   findViewById(R.id.progress_bar_message);
        progressBar =   findViewById(R.id.activity_settings_progress_bar);

        btnExchange = findViewById(R.id.activity_settings_btn_exchange);
        btnExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSettingsPresenter.exchange();
            }
        });

    }

    private void refreshView() {
        finish();
        startActivity(getIntent());
    }

    public boolean validate() {
        boolean valid = true;

        String url = etUrl.getText().toString();
        String userName = etUserName.getText().toString();
        String password = etPassword.getText().toString();
        String interviewer = etInterviewer.getText().toString();


        if (url.isEmpty() ) {
            tilUrl.setError(getString(R.string.input_edit_text_error));
            valid = false;
        } else {
            tilUrl.setError(null);
        }
        if (userName.isEmpty() ) {
            tilUserName.setError(getString(R.string.input_edit_text_error));
            valid = false;
        } else {
            tilUserName.setError(null);
        }
        if (password.isEmpty() ) {
            tilPassword.setError(getString(R.string.input_edit_text_error));
            valid = false;
        } else {
            tilPassword.setError(null);
        }
        if (interviewer.isEmpty() ) {
            tilInterviewer.setError(getString(R.string.input_edit_text_error));
            valid = false;
        } else {
            tilInterviewer.setError(null);
        }


        return valid;
    }

    @Override
    public void showWait() {
        if (progressBar != null && llMain!=null) {
            progressBar.setVisibility(View.VISIBLE);
            tvPBMessage.setVisibility(View.VISIBLE);
            llMain.setVisibility(View.GONE);
        }
    }

    @Override
    public void removeWait() {
        if (progressBar != null  && llMain!=null) {
            progressBar.setVisibility(View.GONE);
            tvPBMessage.setVisibility(View.GONE);
            llMain.setVisibility(View.VISIBLE);
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

        //Toast.makeText(getBaseContext(),appErrorMessage, Toast.LENGTH_LONG).show();
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
    public String getUrl() {
        return etUrl.getText().toString();
    }

    @Override
    public String getUserName() {
        return etUserName.getText().toString();
    }

    @Override
    public String getInterviewer() {
        return etInterviewer.getText().toString();
    }

    @Override
    public String getPassword() {
        return etPassword.getText().toString();
    }


    @Override
    public void exchangeSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.exchange_success)
                .setTitle(R.string.success)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setResult(RESULT_OK);
                        finish();
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

}
