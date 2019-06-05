package org.firehound.devfestclone.fragments;


import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;

import org.firehound.devfestclone.R;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static org.firehound.devfestclone.MainActivity.QR_KEY;


public class PinFragment extends Fragment {


    private static final String TAG = "PinFragment";

    public PinFragment() {
        // Required empty public constructor
    }

    private static String getMd5(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext.toLowerCase();
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pin, container, false);
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        PinEntryEditText pinEntryEditText = view.findViewById(R.id.pin_entry);
        pinEntryEditText.requestFocus();
        String qrHash = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(QR_KEY, null);
        pinEntryEditText.setOnPinEnteredListener(str -> {
            if (getMd5(str.toString()).equals(qrHash)) {
                Toast.makeText(getActivity(), "Correct pin entered.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Generated value: " + getMd5(str.toString()));
                Log.d(TAG, "Prefs value: " + qrHash);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VoteFragment()).commit();

            } else {
                Toast.makeText(getActivity(), "Wrong pin entered.", Toast.LENGTH_SHORT).show();
            }
            Log.d(TAG, "Generated value: " + getMd5(str.toString()));
            Log.d(TAG, "Prefs value: " + qrHash);
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }
}
