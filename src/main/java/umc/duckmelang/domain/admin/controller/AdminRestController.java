package umc.duckmelang.domain.admin.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import umc.duckmelang.domain.admin.service.AdminCommandService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name ="Admin", description = "관리자 API")
@Validated
public class AdminRestController {
    private final AdminCommandService adminCommandService;



}
