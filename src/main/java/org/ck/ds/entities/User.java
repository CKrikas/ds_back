package org.ck.ds.entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String surname;

    @Column(unique = true)
    private int amka;
    private int afm;
    private String password;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @OneToMany(mappedBy = "spouse1")
    private List<Form> spouse1Forms;

    @OneToMany(mappedBy = "spouse2")
    private List<Form> spouse2Forms;

    @OneToMany(mappedBy = "lawyerPrimary")
    private List<Form> lawyerPrimaryForms;

    @OneToMany(mappedBy = "lawyerSecondary")
    private List<Form> lawyerSecondaryForms;

    @OneToMany(mappedBy = "notary")
    private List<Form> notaryForms;

    public User() {
        // Default constructor for JPA
    }

    public User(String name, String surname, int amka, int afm, String password) {
        this.name = name;
        this.surname = surname;
        this.amka = amka;
        this.afm = afm;
        this.password = password;
    }

    // Implementing UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAmka() {
        return amka;
    }

    public void setAmka(int amka) {
        this.amka = amka;
    }

    public int getAfm() {
        return afm;
    }

    public void setAfm(int afm) {
        this.afm = afm;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}

