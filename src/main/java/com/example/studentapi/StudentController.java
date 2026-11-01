
package com.example.studentapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.*;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final Map<Integer, Map<String, Object>> students =
            new LinkedHashMap<>();

    private final AtomicInteger nextId = new AtomicInteger(1);

    // POST: Add a new student
    @PostMapping
    public ResponseEntity<?> addStudent(
            @RequestBody Map<String, Object> details) {

        List<String> errors = validateStudent(details);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("errors", errors));
        }

        int id = nextId.getAndIncrement();

        Map<String, Object> student = new LinkedHashMap<>(details);

        student.put("id", id);

        students.put(id, student);

        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

    // GET: Retrieve all students
    @GetMapping
    public ResponseEntity<?> getAllStudents() {

        return ResponseEntity.ok(
                new ArrayList<>(students.values())
        );
    }

    // GET: Get student by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable int id) {

        Map<String, Object> student = students.get(id);

        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Student not found"));
        }

        return ResponseEntity.ok(student);
    }

    // PATCH: Update existing student
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStudent(
            @PathVariable int id,
            @RequestBody Map<String, Object> updates) {

        Map<String, Object> existing = students.get(id);

        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Student not found"));
        }

        Map<String, Object> updatedStudent =
                new LinkedHashMap<>(existing);

        updatedStudent.putAll(updates);
        updatedStudent.put("id", id);

        List<String> errors = validateStudent(updatedStudent);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("errors", errors));
        }

        students.put(id, updatedStudent);

        return ResponseEntity.ok(updatedStudent);
    }

    // DELETE: Delete student by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable int id) {

        Map<String, Object> student = students.remove(id);

        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Student not found"));
        }

        return ResponseEntity.ok(
                Map.of("message", "Student deleted successfully")
        );
    }

    // Validation method
    private List<String> validateStudent(
            Map<String, Object> student) {

        List<String> errors = new ArrayList<>();

        String[] requiredFields = {
                "name",
                "class",
                "rollNumber",
                "section",
                "phone",
                "email",
                "fatherName",
                "motherName"
        };

        // Check mandatory fields
        for (String field : requiredFields) {

            Object value = student.get(field);

            if (value == null || value.toString().trim().isEmpty()) {
                errors.add(field + " is required");
            }
        }

        // Validate name
        if (student.get("name") != null &&
                student.get("name").toString().trim().length() < 3) {

            errors.add("Name must contain at least 3 characters");
        }

        // Validate section
        if (student.get("section") != null &&
                student.get("section").toString().length() != 1) {

            errors.add("Section must contain exactly one character");
        }

        // Validate phone
        if (student.get("phone") != null &&
                !student.get("phone").toString().matches("\\d{10}")) {

            errors.add("Phone number must contain exactly 10 digits");
        }

        // Validate email
        if (student.get("email") != null) {

            String email = student.get("email").toString();

            if (!email.contains("@") || !email.endsWith(".com")) {
                errors.add("Email must contain @ and end with .com");
            }
        }

        return errors;
    }
}
