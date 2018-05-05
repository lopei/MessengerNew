package com.anotap.messenger.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.anotap.messenger.R;
import com.anotap.messenger.adapter.RecyclerMenuAdapter;
import com.anotap.messenger.domain.IOwnersInteractor;
import com.anotap.messenger.domain.InteractorFactory;
import com.anotap.messenger.model.Icon;
import com.anotap.messenger.model.Owner;
import com.anotap.messenger.model.Text;
import com.anotap.messenger.model.WallEditorAttrs;
import com.anotap.messenger.model.menu.AdvancedItem;
import com.anotap.messenger.settings.ISettings;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.util.Objects;
import com.anotap.messenger.util.RxUtils;
import io.reactivex.disposables.CompositeDisposable;

import static com.anotap.messenger.util.Utils.firstNonEmptyString;

/**
 * Created by admin on 3/27/2018.
 * Phoenix-for-VK
 * Запускается при публикации изображений/текста из других приложений
 */
public class PostPublishPrepareActivity extends AppCompatActivity implements RecyclerMenuAdapter.ActionListener {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private RecyclerMenuAdapter adapter;
    private RecyclerView recyclerView;
    private View proressView;

    private ArrayList<Uri> streams;
    private int accountId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_publish_prepare);

        adapter = new RecyclerMenuAdapter(R.layout.item_advanced_menu_alternative, Collections.emptyList());
        adapter.setActionListener(this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        proressView = findViewById(R.id.progress_view);

        if (Objects.isNull(savedInstanceState)) {
            accountId = Settings.get().accounts().getCurrent();

            if (accountId == ISettings.IAccountsSettings.INVALID_ID) {
                Toast.makeText(this, R.string.error_post_creation_no_auth, Toast.LENGTH_LONG).show();
                finish();
            }

            streams = ActivityUtils.checkLocalStreams(this);

            setLoading(true);
            IOwnersInteractor interactor = InteractorFactory.createOwnerInteractor();
            compositeDisposable.add(interactor.getCommunitiesWhereAdmin(accountId, true, true, false)
                    .zipWith(interactor.getBaseOwnerInfo(accountId, accountId, IOwnersInteractor.MODE_NET), (owners, owner) -> {
                        List<Owner> result = new ArrayList<>();
                        result.add(owner);
                        result.addAll(owners);
                        return result;
                    })
                    .compose(RxUtils.applySingleIOToMainSchedulers())
                    .subscribe(this::onOwnersReceived, this::onOwnersGetError));
        }

        updateViews();
    }

    private void onOwnersGetError(Throwable throwable) {
        setLoading(false);
        Toast.makeText(this, firstNonEmptyString(throwable.getMessage(), throwable.toString()), Toast.LENGTH_LONG).show();

        finish();
    }

    private void onOwnersReceived(List<Owner> owners) {
        setLoading(false);

        if (owners.isEmpty()) {
            finish(); // wtf???
            return;
        }

        final Owner iam = owners.get(0);

        List<AdvancedItem> items = new ArrayList<>();

        for (Owner owner : owners) {
            WallEditorAttrs attrs = new WallEditorAttrs(owner, iam);

            items.add(new AdvancedItem(owner.getOwnerId(), new Text(owner.getFullName()))
                    .setIcon(Icon.fromUrl(owner.get100photoOrSmaller()))
                    .setSubtitle(new Text("@" + owner.getDomain()))
                    .setTag(attrs));
        }

        adapter.setItems(items);
    }

    private boolean loading;

    private void setLoading(boolean loading) {
        this.loading = loading;
        updateViews();
    }

    private void updateViews() {
        recyclerView.setVisibility(loading ? View.GONE : View.VISIBLE);
        proressView.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onClick(AdvancedItem item) {
        WallEditorAttrs attrs = (WallEditorAttrs) item.getTag();

        Intent intent = PostCreateActivity.newIntent(this, accountId, attrs, streams);
        startActivity(intent);

        finish();
    }

    @Override
    public void onLongClick(AdvancedItem item) {

    }
}