package com.anotap.messenger.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;

import com.anotap.messenger.Extra;
import com.anotap.messenger.Injection;
import com.anotap.messenger.R;
import com.anotap.messenger.api.ICaptchaProvider;
import com.anotap.messenger.api.PicassoInstance;
import com.anotap.messenger.util.Utils;
import io.reactivex.disposables.CompositeDisposable;

import static com.anotap.messenger.util.RxUtils.ignore;

/**
 * Created by ruslan.kolbasa on 27.10.2016.
 * phoenix
 */
public class CaptchaActivity extends AppCompatActivity {

    private EditText mTextField;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private ICaptchaProvider captchaProvider;

    private String requestSid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(false);

        setContentView(R.layout.activity_captcha);

        ImageView imageView = findViewById(R.id.captcha_view);
        mTextField = findViewById(R.id.captcha_text);

        String image = getIntent().getStringExtra(Extra.CAPTCHA_URL);

        //onSuccess, w: 130, h: 50
        PicassoInstance.with()
                .load(image)
                .into(imageView);

        findViewById(R.id.button_cancel).setOnClickListener(v -> cancel());
        findViewById(R.id.button_ok).setOnClickListener(v -> onOkButtonClick());

        requestSid = getIntent().getStringExtra(Extra.CAPTCHA_SID);

        captchaProvider = Injection.provideCaptchaProvider();

        mCompositeDisposable.add(captchaProvider.observeWaiting()
                .filter(sid -> sid.equals(requestSid))
                .observeOn(Injection.provideMainThreadScheduler())
                .subscribe(rid -> onWaitingRequestRecieved(), ignore()));

        mCompositeDisposable.add(captchaProvider.observeCanceling()
                .filter(sid -> sid.equals(requestSid))
                .observeOn(Injection.provideMainThreadScheduler())
                .subscribe(integer -> onRequestCancelled(), ignore()));
    }

    private void cancel(){
        captchaProvider.cancel(requestSid);
        finish();
    }

    private void onRequestCancelled(){
        finish();
    }

    private void onWaitingRequestRecieved(){
        captchaProvider.notifyThatCaptchaEntryActive(requestSid);
    }

    @Override
    protected void onDestroy() {
        mCompositeDisposable.dispose();
        super.onDestroy();
    }

    public static Intent createIntent(@NonNull Context context, String captchaSid, String captchaImg){
        return new Intent(context, CaptchaActivity.class)
                .putExtra(Extra.CAPTCHA_SID, captchaSid)
                .putExtra(Extra.CAPTCHA_URL, captchaImg);
    }

    private void onOkButtonClick() {
        CharSequence text = mTextField.getText();
        if (TextUtils.isEmpty(text)) {
            Utils.showRedTopToast(this, getString(R.string.enter_captcha_text));
            return;
        }

        this.captchaProvider.enterCode(requestSid, text.toString());

        finish();
    }
}
