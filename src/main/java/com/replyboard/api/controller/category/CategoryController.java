package com.replyboard.api.controller.category;

import com.replyboard.api.controller.category.request.CreateCategoryRequest;
import com.replyboard.api.controller.category.request.EditCategoryRequest;
import com.replyboard.api.dto.ApiDataResponse;
import com.replyboard.api.security.auth.CustomUserDetails;
import com.replyboard.api.service.category.CategoryService;
import com.replyboard.api.service.category.response.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("${api.url-prefix}")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/categories")
    public ResponseEntity<ApiDataResponse<Long>> addCategory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        Long memberId = userDetails.getMemberDto().getId();
        Long categoryId = categoryService.addCategory(memberId, request.toServiceRequest());

        return ResponseEntity.ok().body(ApiDataResponse.of(categoryId));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiDataResponse<List<CategoryResponse>>> getCategories() {
        List<CategoryResponse> response = categoryService.getCategories();

        return ResponseEntity.ok().body(ApiDataResponse.of(response));
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<ApiDataResponse<Void>> removeCategory(
            @PathVariable("categoryId") Long categoryId
    ) {
        categoryService.removeCategory(categoryId);

        return ResponseEntity.ok().body(ApiDataResponse.empty());
    }

    @PatchMapping("/categories/{categoryId}")
    public ResponseEntity<ApiDataResponse<Void>> editCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody EditCategoryRequest request
    ) {
        categoryService.editCategory(categoryId, request.toServiceRequest());

        return ResponseEntity.ok().body(ApiDataResponse.empty());
    }
}
