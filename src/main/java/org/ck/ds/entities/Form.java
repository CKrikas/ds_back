package org.ck.ds.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class Form {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int spouse1id;
    private int spouse2id;
    private int lawyerPrimaryid;
    private int lawyerSecondaryid;
    private int notaryid;
    private String status = "Pending";
    private boolean spouse1accepted = false;
    private boolean spouse2accepted = false;
    private boolean lawyer2ndAccepted = false;
    private String details = "";
    private Date completionDate;

    @ManyToOne
    @JoinColumn(name="spouse1id", referencedColumnName = "id", insertable = false, updatable = false)
    private User spouse1;

    @ManyToOne
    @JoinColumn(name="spouse2id", referencedColumnName = "id", insertable = false, updatable = false)
    private User spouse2;

    @ManyToOne
    @JoinColumn(name="lawyerPrimaryid", referencedColumnName = "id", insertable = false, updatable = false)
    private User lawyerPrimary;

    @ManyToOne
    @JoinColumn(name="lawyerSecondaryid", referencedColumnName = "id", insertable = false, updatable = false)
    private User lawyerSecondary;

    @ManyToOne
    @JoinColumn(name="notaryid", referencedColumnName = "id", insertable = false, updatable = false)
    private User notary;

    public Form() {
        // Default constructor for JPA
    }

    public Form(int spouse1id, int spouse2id, int lawyerPrimaryid, int lawyerSecondaryid, int notaryid) {
        this.spouse1id = spouse1id;
        this.spouse2id = spouse2id;
        this.lawyerPrimaryid = lawyerPrimaryid;
        this.lawyerSecondaryid = lawyerSecondaryid;
        this.notaryid = notaryid;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getLawyerPrimaryid() {
        return lawyerPrimaryid;
    }

    public void setLawyerPrimaryid(int lawyerPrimaryid) {
        this.lawyerPrimaryid = lawyerPrimaryid;
    }

    public int getLawyerSecondaryid() {
        return lawyerSecondaryid;
    }

    public void setLawyerSecondaryid(int lawyerSecondaryid) {
        this.lawyerSecondaryid = lawyerSecondaryid;
    }

    public int getNotaryid() {
        return notaryid;
    }

    public void setNotaryid(int notaryid) {
        this.notaryid = notaryid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSpouse1accepted() {
        return spouse1accepted;
    }

    public void setSpouse1accepted(boolean spouse1accepted) {
        this.spouse1accepted = spouse1accepted;
    }

    public boolean isSpouse2accepted() {
        return spouse2accepted;
    }

    public void setSpouse2accepted(boolean spouse2accepted) {
        this.spouse2accepted = spouse2accepted;
    }

    public boolean isLawyer2ndAccepted() {
        return lawyer2ndAccepted;
    }

    public void setLawyer2ndAccepted(boolean lawyer2ndAccepted) {
        this.lawyer2ndAccepted = lawyer2ndAccepted;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }
}
