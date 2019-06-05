package org.firehound.devfestclone.fragments;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.firehound.devfestclone.R;
import org.firehound.devfestclone.models.Block;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

import static org.firehound.devfestclone.MainActivity.QR_KEY;


/**
 * A simple {@link Fragment} subclass.
 */
public class VoteFragment extends Fragment {


    private static final String TAG = "VoteFragment";
    private String vote = null;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public VoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vote, container, false);
        RadioButton button1 = view.findViewById(R.id.bjp);
        RadioButton button2 = view.findViewById(R.id.pappu);
        RadioButton button3 = view.findViewById(R.id.mufflerman);
        RadioButton button4 = view.findViewById(R.id.sip);
        RadioButton button5 = view.findViewById(R.id.nota);
        Button submitButton = view.findViewById(R.id.submit_vote);
        Button verifyButton = view.findViewById(R.id.verify_blockchain);
        Button resultsButton = view.findViewById(R.id.results_button);
        button1.setOnClickListener(l -> onRadioButtonClicked(button1));
        button2.setOnClickListener(l -> onRadioButtonClicked(button2));
        button3.setOnClickListener(l -> onRadioButtonClicked(button3));
        button4.setOnClickListener(l -> onRadioButtonClicked(button4));
        button5.setOnClickListener(l -> onRadioButtonClicked(button5));
        submitButton.setOnClickListener(l -> {
            if (vote == null) {
                Toast.makeText(getActivity(), "You must cast a vote!", Toast.LENGTH_SHORT).show();
            } else {
                createBlock(vote);
            }
        });
        verifyButton.setOnClickListener(l -> isChainValid());
        resultsButton.setOnClickListener(l -> countVotes());
        return view;
    }

    private void createBlock(String vote) {
        String currentVoter = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(QR_KEY, null);
        db.collection("votes").orderBy("timeStamp", Query.Direction.DESCENDING).limit(1).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                if (queryDocumentSnapshot.get("voterHash").toString().equals(currentVoter)) {
                    Toast.makeText(getActivity(), "Your vote is INVALID! Do not try to vote twice!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            try {
                Block previousBlock = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1).toObject(Block.class);
                Block voteBlock = new Block(currentVoter, vote, previousBlock.hash);
                db.collection("votes").document(voteBlock.hash).set(voteBlock);
            } catch (IndexOutOfBoundsException e) {
                Block voteBlock = new Block("Initial block", "Initial block", "0");
                db.collection("votes").document(voteBlock.hash).set(voteBlock);
            }
        });
    }

    private void isChainValid() {

        List<Block> blockchain = new ArrayList<>();
        db.collection("votes").get().addOnSuccessListener(queryDocumentSnapshots -> {
            Block currentBlock;
            Block previousBlock;
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                blockchain.add(queryDocumentSnapshot.toObject(Block.class));
            }
            //loop through blockchain to check hashes:
            for (int i = 1; i < blockchain.size(); i++) {
                currentBlock = blockchain.get(i);
                previousBlock = blockchain.get(i - 1);
                //compare registered hash and calculated hash:
                if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                    Log.d(TAG, currentBlock.hash + " : " + currentBlock.calculateHash() + "Currentblock");
                    Toast.makeText(getActivity(), "Blockchain compromised!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //compare previous hash and registered previous hash
                if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                    Log.d(TAG, previousBlock.hash + " : " + currentBlock.previousHash + "Previoushash");
                    Toast.makeText(getActivity(), "Blockchain compromised!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(getActivity(), "Blockchain integrity maintained.", Toast.LENGTH_SHORT).show();
        });
    }

    private void countVotes() {

        List<Block> blockchain = new ArrayList<>();
        db.collection("votes").get().addOnSuccessListener(queryDocumentSnapshots -> {
            Block currentBlock;
            Block previousBlock;
            ResultsDialogFragment fragment = new ResultsDialogFragment();
            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                blockchain.add(queryDocumentSnapshot.toObject(Block.class));
            }
            //loop through blockchain to check hashes:
            for (Block block : blockchain) {
                switch (block.getVote()) {
                    case "BJP":
                        fragment.setBjpVotes();
                        break;
                    case "Congress":
                        fragment.setCongressVotes();
                        break;
                    case "AAP":
                        fragment.setAapVotes();
                        break;
                    case "SIP":
                        fragment.setSipVotes();
                        break;
                    case "NOTA":
                        fragment.setNotaVotes();
                        break;
                }
            }
            fragment.show(requireActivity().getSupportFragmentManager(), TAG);
        });
    }


    private void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.bjp:
                if (checked) {
                    vote = "BJP";
                }
                break;
            case R.id.pappu:
                if (checked) {
                    vote = "Congress";
                }
                break;
            case R.id.mufflerman:
                if (checked) {
                    vote = "AAP";
                }
                break;
            case R.id.sip:
                if (checked) {
                    vote = "SIP";
                }
                break;
            case R.id.nota:
                if (checked) {
                    vote = "NOTA";
                }
        }
    }

}
