package com.project.novel.api;

import com.project.novel.entity.Member;
import com.project.novel.repository.MemberRepository;
import com.project.novel.service.AdminService;
import com.project.novel.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class MemberApiController {
    private final MemberService memberService;
    private final AdminService adminService;
    private final MemberRepository memberRepository;
    @PutMapping("/member/{id}/profileImg/")
    public Map<String,Object> profileImgUpdate(@PathVariable Long id,
                                               MultipartFile profile_image) {
        Member memberInfo = memberService.changeProfileImg(id,profile_image);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("isState","Ok");
        resultMap.put("memberInfo",memberInfo);
        return resultMap;
    }
    @DeleteMapping("/deleteOne/{id}")
    public Map<String,Object> deleteMember(@PathVariable Long id) {
        adminService.deleteMember(id);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("deleteMember","OK");
        return resultMap;
    }
    @DeleteMapping("/deleteSelected")
    public Map<String,String> deleteSelected(@RequestParam(value="valueArr[]") List<Long> valueArr) {
        //log.info("==={}",valueArr);
        adminService.deleteSelected(valueArr);
        Map<String,String> resultMap = new HashMap<>();
        resultMap.put("deleteSelected","OK");
        return resultMap;
    }
}
