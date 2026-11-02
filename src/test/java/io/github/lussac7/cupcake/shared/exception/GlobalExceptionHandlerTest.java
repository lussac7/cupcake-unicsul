package io.github.lussac7.cupcake.shared.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(useDefaultFilters = false)
@Import({
        GlobalExceptionHandler.class,
        GlobalExceptionHandlerTest.TestController.class
})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn404WhenResourceIsNotFound() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail")
                        .value("Produto não encontrado: 99"))
                .andExpect(jsonPath("$.instance")
                        .value("/test/not-found"));
    }

    @Test
    void shouldReturn409WhenBusinessRuleIsViolated() throws Exception {
        mockMvc.perform(get("/test/business-rule"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail")
                        .value("Produto indisponível."));
    }

    @Test
    void shouldReturn400WhenRequestValidationFails() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .contentType("application/json")
                        .content("""
                                {
                                    "name": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.errors[0].field").value("name"));
    }

    @Test
    void shouldReturn400WhenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/test/validation")
                        .contentType("application/json")
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail")
                        .value("O corpo da requisição está ausente ou possui formato inválido."));
    }

    @Test
    void shouldReturn500WithoutExposingInternalDetails() throws Exception {
        mockMvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.detail")
                        .value("Ocorreu um erro interno. Tente novamente mais tarde."))
                .andExpect(jsonPath("$.sensitive").doesNotHaveJsonPath());
    }

    @RestController
    @RequestMapping("/test")
    public static class TestController {

        @GetMapping("/not-found")
        public ResponseEntity<Void> notFound() {
            throw new ResourceNotFoundException(
                    "Produto não encontrado: 99"
            );
        }

        @GetMapping("/business-rule")
        public ResponseEntity<Void> businessRule() {
            throw new BusinessRuleException(
                    "Produto indisponível."
            );
        }

        @PostMapping("/validation")
        public ResponseEntity<Void> validation(
                @Valid @RequestBody TestRequest request) {
            return ResponseEntity.ok().build();
        }

        @GetMapping("/unexpected")
        public ResponseEntity<Void> unexpected() {
            throw new IllegalStateException(
                    "INTERNAL_SENSITIVE_INFORMATION"
            );
        }
    }

    public record TestRequest(
            @NotBlank String name
    ) {
    }
}