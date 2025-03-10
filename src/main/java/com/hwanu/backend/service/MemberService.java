package com.hwanu.backend.service;

import com.hwanu.backend.DTO.MemberDTO;
import com.hwanu.backend.DTO.MemberLoginDTO;
import com.hwanu.backend.DTO.MemberRegisterDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MemberService {

    String register(MemberRegisterDTO memberRegisterDTO);
    String login(MemberLoginDTO memberLoginDTO);


}
