package org.firehound.devfestclone.models;


import com.google.firebase.Timestamp;

import org.firehound.devfestclone.Utils.StringUtil;


public class Block {

    public String hash;
    public String previousHash;
    private String vote;
    private String voterHash;
    private Timestamp timeStamp;

    //Block Constructor.
    public Block(String voterHash, String vote, String previousHash) {
        this.voterHash = voterHash;
        this.vote = vote;
        this.previousHash = previousHash;
        this.timeStamp = Timestamp.now();
        this.hash = calculateHash();
    }

    public Block() {
    }

    public String getHash() {
        return hash;
    }

    public String getVote() {
        return vote;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getVoterHash() {
        return voterHash;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public String calculateHash() {
        return StringUtil.getHash(previousHash + timeStamp.toString() + voterHash + vote);
    }
}
