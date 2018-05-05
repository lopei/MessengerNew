package com.anotap.messenger.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.ActivityFeatures;
import com.anotap.messenger.activity.ActivityUtils;
import com.anotap.messenger.adapter.RecyclerMenuAdapter;
import com.anotap.messenger.fragment.base.BasePresenterFragment;
import com.anotap.messenger.listener.OnSectionResumeCallback;
import com.anotap.messenger.model.Owner;
import com.anotap.messenger.model.User;
import com.anotap.messenger.model.UserDetails;
import com.anotap.messenger.model.menu.AdvancedItem;
import com.anotap.messenger.mvp.presenter.UserDetailsPresenter;
import com.anotap.messenger.mvp.view.IUserDetailsView;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.util.Utils;
import com.anotap.mvp.core.IPresenterFactory;

import static com.anotap.messenger.util.Objects.nonNull;

/**
 * Created by admin on 3/19/2018.
 * Phoenix-for-VK
 */
public class UserDetailsFragment extends BasePresenterFragment<UserDetailsPresenter, IUserDetailsView> implements IUserDetailsView, RecyclerMenuAdapter.ActionListener {

    public static UserDetailsFragment newInstance(int accountId, @NonNull User user, @NonNull UserDetails details) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putParcelable(Extra.USER, user);
        args.putParcelable("details", details);
        UserDetailsFragment fragment = new UserDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerMenuAdapter menuAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(view.findViewById(R.id.toolbar));

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        menuAdapter = new RecyclerMenuAdapter(Collections.emptyList());
        menuAdapter.setActionListener(this);

        recyclerView.setAdapter(menuAdapter);
        return view;
    }

    @Override
    public void displayData(@NonNull List<AdvancedItem> items) {
        menuAdapter.setItems(items);
    }

    @Override
    public void displayToolbarTitle(String title) {
        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (nonNull(actionBar)) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void openOwnerProfile(int accountId, int ownerId, @Nullable Owner owner) {
        PlaceFactory.getOwnerWallPlace(accountId, ownerId, owner).tryOpenWith(requireActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof OnSectionResumeCallback) {
            ((OnSectionResumeCallback) getActivity()).onClearSelection();
        }

        new ActivityFeatures.Builder()
                .begin()
                .setBlockNavigationDrawer(false)
                .setStatusBarColored(requireActivity(), true)
                .build()
                .apply(requireActivity());
    }

    @Override
    protected String tag() {
        return UserDetailsFragment.class.getSimpleName();
    }

    @Override
    public IPresenterFactory<UserDetailsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new UserDetailsPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getParcelable(Extra.USER),
                getArguments().getParcelable("details"),
                saveInstanceState
        );
    }

    @Override
    public void onLongClick(AdvancedItem item) {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            String title = item.getTitle().getText(requireContext());
            String subtitle = nonNull(item.getSubtitle()) ? item.getSubtitle().getText(requireContext()) : null;
            String details = Utils.joinNonEmptyStrings("\n", title, subtitle);

            ClipData clip = ClipData.newPlainText("Details", details);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(requireContext(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(AdvancedItem item) {
        getPresenter().fireItemClick(item);
    }
}