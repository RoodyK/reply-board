package com.replyboard.api.controller.category;

import com.replyboard.ControllerTestSupport;
import com.replyboard.api.controller.category.request.CreateCategoryRequest;
import com.replyboard.api.controller.category.request.EditCategoryRequest;
import com.replyboard.api.service.category.request.CreateCategoryServiceRequest;
import com.replyboard.api.service.category.request.EditCategoryServiceRequest;
import com.replyboard.api.service.category.response.CategoryResponse;
import com.replyboard.config.admin.CustomMockRoleAdmin;
import com.replyboard.config.user.CustomMockRoleUser;
import com.replyboard.exception.CategoryNotFoundException;
import com.replyboard.exception.DuplicatedCategoryException;
import com.replyboard.exception.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerTest extends ControllerTestSupport {

    @CustomMockRoleAdmin
    @DisplayName("카테고리를 등록한다.")
    @Test
    void addCategoryRoleAdmin() throws Exception {
        CreateCategoryRequest request = createCategoryRequest("기타");

        BDDMockito.given(categoryService.addCategory(anyLong(), any(CreateCategoryServiceRequest.class)))
                .willReturn(1L);

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value(1L))
                ;

        BDDMockito.then(categoryService).should().addCategory(anyLong(), any(CreateCategoryServiceRequest.class));
    }

    @CustomMockRoleUser
    @DisplayName("카테고리를 등록할 때 ROLE_USER 권한을 갖는 사용자는 등록할 수 없다.")
    @Test
    void addCategoryRoleUser() throws Exception {
        CreateCategoryRequest request = createCategoryRequest("기타");

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1200))
                .andExpect(jsonPath("$.message").value("페이지에 접근할 수 없습니다."))
                .andExpect(jsonPath("$.validation").isEmpty())
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("카테고리를 등록할 때 카테고리명은 필수값이다.")
    @Test
    void addCategoryWithoutCategoryName() throws Exception {
        CreateCategoryRequest request = createCategoryRequest(null);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.name").value("카테고리명을 입력해주세요."))
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("카테고리를 등록할 때 카테고리명은 공백만 입력하면 예외가 발생한다.")
    @Test
    void addCategoryWithoutCategoryNameIsBlank() throws Exception {
        CreateCategoryRequest request = createCategoryRequest(" ");

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.name").value("카테고리명을 입력해주세요."))
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("카테고리를 등록할 때 사용자의 ID로 사용자 조회 실패시 예외가 발생한다.")
    @Test
    void addCategoryNotFoundMemberId() throws Exception {
        CreateCategoryRequest request = createCategoryRequest("기타");

        BDDMockito.given(categoryService.addCategory(anyLong(), any(CreateCategoryServiceRequest.class)))
                .willThrow(new MemberNotFoundException());

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1300))
                .andExpect(jsonPath("$.message").value("회원을 찾을 수 없습니다."))
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("카테고리를 등록할 때 카테고리명은 종복될 수 없다.")
    @Test
    void addCategoryDuplicatedCategoryName() throws Exception {
        CreateCategoryRequest request = createCategoryRequest("기타");

        BDDMockito.given(categoryService.addCategory(anyLong(), any(CreateCategoryServiceRequest.class)))
                .willThrow(new DuplicatedCategoryException());

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("중복된 카테고리명이 존재합니다."))
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("카테고리 목록을 조회한다.")
    @Test
    void getCategories() throws Exception {
        CategoryResponse response1 = createCategoryResponse(1L, "기타");
        CategoryResponse response2 = createCategoryResponse(2L, "요리");

        BDDMockito.given(categoryService.getCategories())
                .willReturn(List.of(response1, response2));

        mockMvc.perform(get("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].name").value("기타"))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[1].name").value("요리"))
        ;

        BDDMockito.then(categoryService).should().getCategories();
    }

    @CustomMockRoleAdmin
    @DisplayName("카테고리를 삭제한다.")
    @Test
    void removeCategory() throws Exception {

        BDDMockito.willDoNothing().given(categoryService).removeCategory(anyLong());

        mockMvc.perform(delete("/api/v1/categories/{categoryId}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("카테고리를 삭제할 때 카테고리가 없다면 예외가 발생한다.")
    @Test
    void removeCategoryNotExistsCategory() throws Exception {
        BDDMockito.willThrow(new CategoryNotFoundException()).given(categoryService).removeCategory(anyLong());

        mockMvc.perform(delete("/api/v1/categories/{categoryId}", 1L)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1300))
                .andExpect(jsonPath("$.message").value("카테고리를 찾을 수 없습니다."))
        ;
    }

    @CustomMockRoleUser
    @DisplayName("카테고리를 삭제할 때 ROLE_ADMIN 권한이 필요하다.")
    @Test
    void removeCategoryRoleUser() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/{categoryId}", 1L)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1200))
                .andExpect(jsonPath("$.message").value("페이지에 접근할 수 없습니다."))
                .andExpect(jsonPath("$.validation").isEmpty())
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("카테고리를 수정한다")
    @Test
    void editCategory() throws Exception {
        EditCategoryRequest request = editCategoryRequest("기타");

        BDDMockito.willDoNothing().given(categoryService).editCategory(anyLong(), any(EditCategoryServiceRequest.class));

        mockMvc.perform(patch("/api/v1/categories/{categoryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty())
        ;

        BDDMockito.then(categoryService).should().editCategory(anyLong(), any(EditCategoryServiceRequest.class));
    }

    @CustomMockRoleAdmin
    @DisplayName("카테고리를 수정할 때 카테고리명은 필수값이다.")
    @Test
    void editCategoryWithoutName() throws Exception {
        EditCategoryRequest request = editCategoryRequest(null);

        mockMvc.perform(patch("/api/v1/categories/{categoryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.validation.name").value("카테고리명을 입력해주세요."))
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("카테고리를 수정할 때 ID에 해당하는 카테고리가 없으면 예외가 발생한다.")
    @Test
    void editCategoryNotExistCategory() throws Exception {
        EditCategoryRequest request = editCategoryRequest("수정");

        BDDMockito.willThrow(new CategoryNotFoundException())
                .given(categoryService).editCategory(anyLong(), any(EditCategoryServiceRequest.class));

        mockMvc.perform(patch("/api/v1/categories/{categoryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1300))
                .andExpect(jsonPath("$.message").value("카테고리를 찾을 수 없습니다."))
        ;
    }

    @CustomMockRoleAdmin
    @DisplayName("카테고리를 수정할 때 중복된 카테고리명으로 수정할 수 없다,")
    @Test
    void editCategoryDuplicatedCategory() throws Exception {
        EditCategoryRequest request = editCategoryRequest("수정");

        BDDMockito.willThrow(new DuplicatedCategoryException())
                .given(categoryService).editCategory(anyLong(), any(EditCategoryServiceRequest.class));

        mockMvc.perform(patch("/api/v1/categories/{categoryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("중복된 카테고리명이 존재합니다."))
        ;
    }

    @CustomMockRoleUser
    @DisplayName("카테고리를 수정할 때 ROLE_ADMIN 권한이 필요하다.")
    @Test
    void editCategoryRoleUser() throws Exception {
        EditCategoryRequest request = editCategoryRequest("수정");

        mockMvc.perform(patch("/api/v1/categories/{categoryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.code").value(1200))
                .andExpect(jsonPath("$.message").value("페이지에 접근할 수 없습니다."))
                .andExpect(jsonPath("$.validation").isEmpty())
        ;
    }

    private CreateCategoryRequest createCategoryRequest(String name) {
        return CreateCategoryRequest.builder()
                .name(name)
                .build();
    }

    private CategoryResponse createCategoryResponse(long id, String name) {
        return CategoryResponse.builder()
                .id(id)
                .name(name)
                .build();
    }

    private EditCategoryRequest editCategoryRequest(String name) {
        return EditCategoryRequest.builder()
                .name(name)
                .build();
    }
}