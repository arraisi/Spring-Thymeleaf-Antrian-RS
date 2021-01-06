package com.arraisi.antrianrumahsakit.controller;

import com.arraisi.antrianrumahsakit.entity.Patient;
import com.arraisi.antrianrumahsakit.entity.Queue;
import com.arraisi.antrianrumahsakit.repository.PatientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientDao dao;

    @GetMapping({"/", "/"})
    public String patientList(Model model) {

        List<Patient> list = dao.findAll();
        model.addAttribute("patientList", list);

        return "pages/patient";
    }
}
