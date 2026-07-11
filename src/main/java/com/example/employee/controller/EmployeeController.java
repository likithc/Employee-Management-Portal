package com.example.employee.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final Map<Long, Employee> employees = new HashMap<>();

    @PostMapping
    public Employee addEmployee(@RequestBody Employee employee) {
        employees.put(employee.getId(), employee);
        return employee;
    }

    @GetMapping
    public Collection<Employee> getAllEmployees() {
        return employees.values();
    }

    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable Long id) {
        return employees.get(id);
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id,
                                   @RequestBody Employee employee) {
        employees.put(id, employee);
        return employee;
    }

    @DeleteMapping("/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employees.remove(id);
        return "Employee Deleted";
    }
}
