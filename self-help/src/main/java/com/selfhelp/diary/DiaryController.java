package com.selfhelp.diary;

import com.selfhelp.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    // 📝 CREATE DIARY
    @PostMapping
    public ResponseEntity<Diary> createDiary(
            @RequestBody Diary diary,
            @AuthenticationPrincipal User user
    ) {
        Diary createdDiary = diaryService.createDiary(diary, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDiary);
    }

    // 👤 MY DIARIES (ONLY LOGGED-IN USER)
    @GetMapping("/me")
    public ResponseEntity<List<Diary>> myDiaries(
            @AuthenticationPrincipal String email
    ) {
        List<Diary> diaries = diaryService.getMyDiaries(email);
        return ResponseEntity.ok(diaries);
    }




    // 🌍 PUBLIC FEED (ANYONE CAN SEE)
    @GetMapping("/public")
    public ResponseEntity<List<Diary>> publicFeed() {
        List<Diary> diaries = diaryService.getPublicDiaries();
        return ResponseEntity.ok(diaries);
    }

    // ✏️ UPDATE DIARY
    @PutMapping("/{id}")
    public ResponseEntity<Diary> updateDiary(
            @PathVariable Long id,
            @RequestBody DiaryUpdateRequest request,
            @AuthenticationPrincipal User user
    ) {
        Diary updatedDiary = diaryService.updateDiary(id, request, user);
        return ResponseEntity.ok(updatedDiary);
    }

    // 🗑️ DELETE DIARY
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        diaryService.deleteDiary(id, user);
        return ResponseEntity.noContent().build();
    }
}
