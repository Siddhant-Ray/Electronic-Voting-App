package org.firehound.devfestclone.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.firehound.devfestclone.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ResultsDialogFragment extends DialogFragment {
    private int bjpVotes = 0, congressVotes = 0, aapVotes = 0, sipVotes = 0, notaVotes = 0;

    public void setBjpVotes() {
        this.bjpVotes++;
    }

    public void setCongressVotes() {
        this.congressVotes++;
    }

    public void setAapVotes() {
        this.aapVotes++;
    }

    public void setSipVotes() {
        this.sipVotes++;
    }

    public void setNotaVotes() {
        this.notaVotes++;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireActivity()).inflate(R.layout.layout_alert_dialog, null);
        ((TextView) view.findViewById(R.id.bjp)).setText("BJP votes: " + bjpVotes);
        ((TextView) view.findViewById(R.id.congress)).setText("Congress votes: " + congressVotes);
        ((TextView) view.findViewById(R.id.aap)).setText("AAP votes: " + aapVotes);
        ((TextView) view.findViewById(R.id.sip)).setText("SIP votes: " + sipVotes);
        ((TextView) view.findViewById(R.id.nota)).setText("NOTA votes: " + notaVotes);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("Results")
                .setPositiveButton("Ok", ((dialog, which) -> dialog.dismiss()))
                .setView(view);
        return builder.create();
    }
}
