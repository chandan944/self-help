package com.selfhelp.diary;

import com.selfhelp.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    // 📝 CREATE OR UPDATE TODAY'S DIARY
    @PostMapping
    public ResponseEntity<Diary> createOrUpdateDiary(
            @RequestBody Diary diary,
            @AuthenticationPrincipal String email
    ) {
        Diary savedDiary = diaryService.createOrUpdateTodayDiary(diary, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDiary);
    }

    // 📖 GET TODAY'S DIARY


    // 👤 MY DIARIES (ONLY LOGGED-IN USER)
    @GetMapping("/me")
    public ResponseEntity<Page<Diary>> myDiaries(
            @AuthenticationPrincipal String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Diary> diaries = diaryService.getMyDiaries(email, page, size);
        return ResponseEntity.ok(diaries);
    }

    // 🌍 PUBLIC FEED (ANYONE CAN SEE)
    @GetMapping("/public")
    public ResponseEntity<Page<PublicDiaryDTO>> publicFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                diaryService.getPublicDiaries(page, size)
        );
    }

    // ✏️ UPDATE DIARY
    @PutMapping("/{id}")
    public ResponseEntity<Diary> updateDiary(
            @PathVariable Long id,
            @RequestBody DiaryUpdateRequest request,
            @AuthenticationPrincipal String email
    ) {
        Diary updatedDiary = diaryService.updateDiary(id, request, email);
        return ResponseEntity.ok(updatedDiary);
    }

    // 🗑️ DELETE DIARY
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(
            @PathVariable Long id,
            @AuthenticationPrincipal String email
    ) {
        diaryService.deleteDiary(id, email);
        return ResponseEntity.noContent().build();
    }
}