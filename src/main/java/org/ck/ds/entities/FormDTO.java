package org.ck.ds.entities;

public class FormDTO {
    private int lawyerSecondaryid;
    private int spouse1id;
    private int spouse2id;
    private int notaryid;

    // Getters and Setters
    public int getLawyerSecondaryid() {
        return lawyerSecondaryid;
    }

    public void setLawyerSecondaryid(int lawyerSecondaryid) {
        this.lawyerSecondaryid = lawyerSecondaryid;
    }

    public int getSpouse1id() {
        return spouse1id;
    }

    public void setSpouse1id(int spouse1id) {
        this.spouse1id = spouse1id;
    }

    public int getSpouse2id() {
        return spouse2id;
    }

    public void setSpouse2id(int spouse2id) {
        this.spouse2id = spouse2id;
    }

    public int getNotaryid() {
        return notaryid;
    }

    public void setNotaryid(int notaryid) {
        this.notaryid = notaryid;
    }
}
