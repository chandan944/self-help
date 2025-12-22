package com.selfhelp.diary;

import com.selfhelp.user.User;
import com.selfhelp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    // 📝 CREATE
    public Diary createDiary(Diary diary, User user) {
        diary.setAuthor(user);
        diary.setEntryDate(LocalDate.now());
        return diaryRepository.save(diary);
    }

    // 👤 MY DIARIES (PUBLIC + PRIVATE)
    public List<Diary> getMyDiaries(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return diaryRepository.findByAuthor(user);
    }


    // 🌍 PUBLIC FEED
    public List<Diary> getPublicDiaries() {
        return diaryRepository.findByVisibility(Visibility.PUBLIC);
    }

    // ✏️ UPDATE (AUTHOR ONLY)
    public Diary updateDiary(Long id, DiaryUpdateRequest request, User user) {

        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diary not found ❌"));

        if (!diary.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed ❌");
        }

        diary.setTitle(request.getTitle());
        diary.setGoodThings(request.getGoodThings());
        diary.setBadThings(request.getBadThings());
        diary.setMood(request.getMood());
        diary.setVisibility(request.getVisibility());

        return diaryRepository.save(diary);
    }

    // 🗑️ DELETE (AUTHOR ONLY)
    public void deleteDiary(Long id, User user) {

        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diary not found ❌"));

        if (!diary.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed ❌");
        }

        diaryRepository.delete(diary);
    }
}
