package org.firehound.devfestclone.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;

import org.firehound.devfestclone.R;

import androidx.fragment.app.Fragment;

import static org.firehound.devfestclone.MainActivity.QR_KEY;


public class QRFragment extends Fragment {
    private static final String TAG = "DashboardFragment";
    private CodeScanner codeScanner;


    public QRFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qrcode, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());


        CodeScannerView scannerView = view.findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(requireContext(), scannerView);
        codeScanner.setDecodeCallback(result -> {
            sharedPreferences.edit().putString(QR_KEY, result.getText().toLowerCase()).apply();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PinFragment()).commit();

        });
        codeScanner.startPreview();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    public void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

}
