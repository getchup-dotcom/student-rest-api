package com.example.studentapi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.*;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.atomic.AtomicInteger;
@RestController
@RequestMapping("/students")
public class StudentController{
    private final Map<Integer,Map<String,Object>> students = new LinkedHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);
    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody Map<String,Object> details){
        List<String> errors = validateStudent(details);
        if(!errors.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("Erros",errors));
        }
        int id = nextId.getAndIncrement();
        Map<String,Object> student = new LinkedHashMap<>(details);
        students.put("id",id);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);

    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable int id){
        Map<String,Object> student = students.get(id);
        if(student == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","Student not foud"));
        }
        return ResponseEntity.ok(student);
    }
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

}
