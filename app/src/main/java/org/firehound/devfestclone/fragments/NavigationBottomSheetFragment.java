package org.firehound.devfestclone.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.firehound.devfestclone.MainActivity;
import org.firehound.devfestclone.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NavigationBottomSheetFragment extends BottomSheetDialogFragment {
    private static int RC_SIGN_IN = 69;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private GoogleSignInClient googleSignInClient;
    private static final String TAG = "NavigationBottomSheetFr";


    public NavigationBottomSheetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.gso_key))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
        updateAccountUI(firebaseAuth.getCurrentUser());
        view.findViewById(R.id.sign_in_button).setOnClickListener(l -> signIn());
        view.findViewById(R.id.sign_out_button).setOnClickListener(l -> signOut());
        setUpNavListViews();
        updateNavList(MainActivity.selectedFragment);

    }

    private void updateNavList(int selectedFragment) {
        int bgResource = R.drawable.bg_nav_sheet_selection_gradient;
        View view = getView();

        switch (selectedFragment) {
            case 0:
                view.findViewById(R.id.text_nav_main).setBackgroundResource(bgResource);
            case 1:
                view.findViewById(R.id.text_qrcode_scan).setBackgroundResource(bgResource);
                break;
            case 2:
                view.findViewById(R.id.text_pin_entry).setBackgroundResource(bgResource);
                break;
            case 3:
                view.findViewById(R.id.text_vote).setBackgroundResource(bgResource);
                break;
            case 4:
                view.findViewById(R.id.text_nav_about).setBackgroundResource(bgResource);
                break;
        }
    }

    private void setUpNavListViews() {
        View view = getView();
        view.findViewById(R.id.layout_nav_main).setOnClickListener(l -> {
            ((NavClickListener) requireActivity()).onNavItemClicked(0);
        });
        view.findViewById(R.id.layout_qrcode_scan).setOnClickListener(l -> {
            ((NavClickListener) requireActivity()).onNavItemClicked(1);
        });
        view.findViewById(R.id.layout_pin_entry).setOnClickListener(l -> {
            ((NavClickListener) requireActivity()).onNavItemClicked(2);
        });
        view.findViewById(R.id.layout_vote).setOnClickListener(l -> {
            ((NavClickListener) requireActivity()).onNavItemClicked(3);
        });
        view.findViewById(R.id.layout_nav_about).setOnClickListener(l -> {
            ((NavClickListener) requireActivity()).onNavItemClicked(4);
        });
        resetBackgrounds();

    }

    private void resetBackgrounds() {
        View view = getView();
        view.findViewById(R.id.layout_nav_main).setBackgroundResource(0);
        view.findViewById(R.id.layout_qrcode_scan).setBackgroundResource(0);
        view.findViewById(R.id.layout_pin_entry).setBackgroundResource(0);
        view.findViewById(R.id.layout_vote).setBackgroundResource(0);
        view.findViewById(R.id.layout_nav_about).setBackgroundResource(0);

    }

    private void signOut() {
        firebaseAuth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            updateAccountUI(null);
        });
    }

    private void signIn() {
        try {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error signing in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAccountUI(FirebaseUser currentUser) {
        View view = getView();
        if (currentUser != null) {
            view.findViewById(R.id.layout_nav_before_auth).setVisibility(View.GONE);
            view.findViewById(R.id.layout_nav_after_auth).setVisibility(View.VISIBLE);
            Glide.with(requireContext()).load(currentUser.getPhotoUrl()).into((ImageView) view.findViewById(R.id.image_account));
            ((TextView) view.findViewById(R.id.text_account_username)).setText(currentUser.getDisplayName());
            ((TextView) view.findViewById(R.id.text_account_email)).setText(currentUser.getEmail());
        } else {
            view.findViewById(R.id.layout_nav_before_auth).setVisibility(View.VISIBLE);
            view.findViewById(R.id.layout_nav_after_auth).setVisibility(View.GONE);
            ((ImageView) view.findViewById(R.id.image_account)).setImageResource(R.drawable.ic_account_circle_white_24dp);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                updateAccountUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        updateAccountUI(firebaseAuth.getCurrentUser());
                    } else {
                        Log.w(TAG, "Sign in: ", task.getException());
                        Toast.makeText(requireContext(), "Sign in failed", Toast.LENGTH_SHORT).show();
                        updateAccountUI(null);
                    }
                });
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(), getTheme());
    }

    public interface NavClickListener {
        void onNavItemClicked(int index);
    }
}
