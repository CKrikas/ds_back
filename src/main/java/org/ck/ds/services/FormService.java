package org.ck.ds.services;

import org.ck.ds.entities.Form;
import org.ck.ds.entities.FormDTO;
import org.ck.ds.entities.Role;
import org.ck.ds.entities.User;
import org.ck.ds.repositories.FormRepository;
import org.ck.ds.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class FormService {

    private final FormRepository formRepository;
    private final UserRepository userRepository;

    @Autowired
    public FormService(FormRepository formRepository, UserRepository userRepository) {
        this.formRepository = formRepository;
        this.userRepository = userRepository;
    }

    public Form createForm(FormDTO formDTO) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int lawyerId = ((User) userDetails).getId();
        Form form = new Form(formDTO.getSpouse1id(), formDTO.getSpouse2id(), lawyerId, formDTO.getLawyerSecondaryid(), formDTO.getNotaryid());

        // Check Primary Lawyer
        if (!userRepository.existsById(lawyerId)) {
            throw new IllegalArgumentException("Primary Lawyer not found.");
        }

        // Check Spouse1
        User spouse1 = userRepository.findById(form.getSpouse1id()).orElseThrow(() -> new IllegalArgumentException("Spouse1 not found."));
        if (!spouse1.getRoles().stream().anyMatch(role -> role.getName().equals("SPOUSE"))) {
            throw new IllegalArgumentException("Provided Spouse1 ID does not correspond to a spouse.");
        }

        // Check Spouse2
        User spouse2 = userRepository.findById(form.getSpouse2id()).orElseThrow(() -> new IllegalArgumentException("Spouse2 not found."));
        if (!spouse2.getRoles().stream().anyMatch(role -> role.getName().equals("SPOUSE"))) {
            throw new IllegalArgumentException("Provided Spouse2 ID does not correspond to a spouse.");
        }

        // Check Secondary Lawyer
        User secondaryLawyer = userRepository.findById(form.getLawyerSecondaryid()).orElseThrow(() -> new IllegalArgumentException("Secondary Lawyer not found."));
        if (!secondaryLawyer.getRoles().stream().anyMatch(role -> role.getName().equals("LAWYER"))) {
            throw new IllegalArgumentException("Provided Secondary Lawyer ID does not correspond to a lawyer.");
        }

        // Check Notary
        User notary = userRepository.findById(form.getNotaryid()).orElseThrow(() -> new IllegalArgumentException("Notary not found."));
        if (!notary.getRoles().stream().anyMatch(role -> role.getName().equals("NOTARY"))) {
            throw new IllegalArgumentException("Provided Notary ID does not correspond to a notary.");
        }

        return formRepository.save(form);
    }


    public Form updateForm(int id, Form form) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int lawyerId = ((User) userDetails).getId();

        // a) Check to ensure that the form exists
        Form existingForm = formRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Form not found."));

        // b) Check if the form is marked as "Completed"
        if ("Completed".equals(existingForm.getStatus())) {
            throw new IllegalArgumentException("Cannot update a form that's marked as 'Completed'.");
        }

        // c) Check to ensure the form has the same primaryLawyerId as the user trying to update it
        if (existingForm.getLawyerPrimaryid() != lawyerId) {
            throw new IllegalArgumentException("Unauthorized: You are not the primary lawyer for this form.");
        }

        return formRepository.save(form);
    }


    public Form acceptForm(int formId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = ((User) userDetails).getId();
        Set<Role> roles = ((User) userDetails).getRoles();

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found."));
        Form form = formRepository.findById(formId).orElseThrow(() -> new IllegalArgumentException("Form not found."));

        if (roles.stream().anyMatch(role -> role.getName().equals("LAWYER"))) {
            if (form.getLawyerPrimaryid() == userId) {
                throw new IllegalArgumentException("Primary lawyer cannot accept the form.");
            }
            if (form.getLawyerSecondaryid() != userId) {
                throw new IllegalArgumentException("You are not the secondary lawyer for this form.");
            }
            form.setLawyer2ndAccepted(true);
        } else if (roles.stream().anyMatch(role -> role.getName().equals("SPOUSE"))) {
            if (form.getSpouse1id() == userId) {
                form.setSpouse1accepted(true);
            } else if (form.getSpouse2id() == userId) {
                form.setSpouse2accepted(true);
            } else {
                throw new IllegalArgumentException("User is not associated with this form.");
            }
        } else {
            throw new IllegalArgumentException("Only a lawyer or spouse can accept a form.");
        }

        return formRepository.save(form);
    }

    public Form completeForm(int formId, String details) {
        Form form = formRepository.findById(formId).orElseThrow(() -> new IllegalArgumentException("Form not found."));

        // Check if both spouses and the secondary lawyer have accepted the form
        if (!form.isSpouse1accepted()) {
            throw new IllegalArgumentException("Spouse 1 has not accepted the form.");
        }
        if (!form.isSpouse2accepted()) {
            throw new IllegalArgumentException("Spouse 2 has not accepted the form.");
        }
        if (!form.isLawyer2ndAccepted()) {
            throw new IllegalArgumentException("Secondary lawyer has not accepted the form.");
        }

        form.setStatus("Completed");
        form.setCompletionDate(new Date());
        form.setDetails(details);

        return formRepository.save(form);
    }

    public Optional<Form> getFormById(int formId) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = ((User) userDetails).getId();

        Optional<Form> formOptional = formRepository.findById(formId);

        if (formOptional.isPresent()) {
            Form form = formOptional.get();

            // Check if the user is associated with the form
            if (form.getSpouse1id() == userId || form.getSpouse2id() == userId || form.getLawyerPrimaryid() == userId || form.getLawyerSecondaryid() == userId || form.getNotaryid() == userId) {
                return formOptional;
            } else {
                throw new IllegalArgumentException("User is not associated with this form.");
            }
        }

        return Optional.empty();
    }

    public List<Form> browseForms() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = ((User) userDetails).getId();

        // Fetch forms where the user is either a spouse, lawyer, or notary
        List<Form> userForms = formRepository.findBySpouse1idOrSpouse2idOrLawyerPrimaryidOrLawyerSecondaryidOrNotaryid(userId, userId, userId, userId, userId);

        return userForms;
    }


    /* public boolean deleteForm(int formId) {
        if (formRepository.existsById(formId)) {
            formRepository.deleteById(formId);
            return true;
        } else {
            return false;
        }
    } */
}