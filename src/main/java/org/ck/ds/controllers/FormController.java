package org.ck.ds.controllers;

import org.ck.ds.entities.Form;
import org.ck.ds.entities.FormDTO;
import org.ck.ds.services.FormService;
import org.ck.ds.entities.FormDetailsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/forms")
public class FormController {

    private final FormService formService;

    @Autowired
    public FormController(FormService formService) {
        this.formService = formService;
    }


    @GetMapping("/all")
    public List<Form> getAllForms() {
        return formService.browseForms();
    }


    @GetMapping("/viewForm/{id}")
    public ResponseEntity<Form> getFormById(@PathVariable int id) {
        return formService.getFormById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/lawyer")
    public Form createForm(@RequestBody FormDTO formDTO) {
        return formService.createForm(formDTO);
    }


    @PutMapping("/lawyer/{id}")
    public ResponseEntity<Form> updateForm(@PathVariable int id, @RequestBody Form form) {
        Form updatedForm = formService.updateForm(id, form);
        if (updatedForm != null) {
            return ResponseEntity.ok(updatedForm);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{formId}/accept")
    public ResponseEntity<Form> acceptForm(@PathVariable int formId) {
        try {
            Form acceptedForm = formService.acceptForm(formId);
            return ResponseEntity.ok(acceptedForm);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @PutMapping("/notary/{formId}")
    public ResponseEntity<Form> completeForm(@PathVariable int formId, @RequestBody FormDetailsRequest formDetailsRequest) {
        try {
            Form completedForm = formService.completeForm(formId, formDetailsRequest.getDetails());
            return ResponseEntity.ok(completedForm);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /* @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable int id) {
        if (formService.deleteForm(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    } */
}