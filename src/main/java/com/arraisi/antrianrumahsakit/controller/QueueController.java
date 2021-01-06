package com.arraisi.antrianrumahsakit.controller;

import com.arraisi.antrianrumahsakit.entity.Patient;
import com.arraisi.antrianrumahsakit.entity.Queue;
import com.arraisi.antrianrumahsakit.entity.QueueNumbers;
import com.arraisi.antrianrumahsakit.repository.PatientDao;
import com.arraisi.antrianrumahsakit.repository.QueueDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/queue")
public class QueueController {
    @Autowired
    private QueueDao dao;

    @Autowired
    private PatientDao patientDao;

    @GetMapping({"/", "/"})
    public String index(Model model, @ModelAttribute Patient patient, @ModelAttribute Queue queue) {
        model.addAttribute("patient", patient);

        List<Queue> list = dao.findAll();
        model.addAttribute("totalQueuelist", list);

        List<Queue> pharmacyQueueList = dao.findPharmacyQueue();
        model.addAttribute("pharmacyQueueList", pharmacyQueueList);

        List<Queue> doneQueueList = dao.findDoneQueue();
        model.addAttribute("doneQueueList", doneQueueList);

        if (patient != null && patient.getId() != null) {
            try {
                final Patient _patient = patientDao.findById(patient.getId());
                dao.save(_patient);
                return "redirect:/queue/";
            } catch (EmptyResultDataAccessException e) {
                model.addAttribute("patientNotFound", true);
            }
        }
        return "pages/queue";
    }

    @PostMapping("/patient/new")
    public String patientNewSubmit(Model model, @ModelAttribute @Validated Patient patient) {
        if (patient.getId() != null && patient.getName().isEmpty()) {
            return index(model, patient, new Queue());
        } else {
            patientDao.save(patient);
            return "redirect:/queue/";
        }
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateStatus(@RequestBody Queue queue) {
        if (queue.getStatus().equalsIgnoreCase("check_up")) {
            try {
                dao.updateStatusCheckUpToPharmacy();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        try {
            dao.updateStatus(queue.getQueueNumber(), queue.getStatus());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeByQueueNumber(@RequestParam Integer queueNumber) {
        try {
            dao.remove(queueNumber);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @DeleteMapping("/reset")
    public ResponseEntity<String> removeQueue() {
        try {
        dao.removeQueu();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @GetMapping("/data")
    public ResponseEntity<QueueNumbers> data() {
        final QueueNumbers queueNumbers = dao.findQueueNumbers();
        return new ResponseEntity<>(queueNumbers, HttpStatus.OK);
    }

}
