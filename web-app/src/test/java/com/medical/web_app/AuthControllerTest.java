//package com.medical.web_app.controller;
//
//import com.medical.web_app.model.User;
//import com.medical.web_app.service.WebAppService;
//import com.medical.web_app.service.form.SignupForm;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.ui.ExtendedModelMap;
//import org.springframework.validation.BeanPropertyBindingResult;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.servlet.ModelAndView;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AuthControllerTest {
//
//    @Mock
//    private WebAppService webAppService;
//
//    @InjectMocks
//    private AuthController authController;
//
//    @Test
//    void shouldDisplaySigninPage() {
//        ModelAndView result = authController.signin();
//
//        assertEquals("signin", result.getViewName());
//    }
//
//    @Test
//    void shouldDisplaySignupPage() {
//        ExtendedModelMap model = new ExtendedModelMap();
//
//        String result = authController.showSignupForm(model);
//
//        assertEquals("signup", result);
//        assertNotNull(model.getAttribute("signupForm"));
//        assertInstanceOf(SignupForm.class, model.getAttribute("signupForm"));
//    }
//
//    @Test
//    void shouldReturnSignupPageWhenFormHasErrors() {
//        SignupForm form = new SignupForm();
//        BindingResult bindingResult =
//                new BeanPropertyBindingResult(form, "signupForm");
//
//        bindingResult.rejectValue("email", "error.email");
//
//        String result = authController.processSignup(form, bindingResult);
//
//        assertEquals("signup", result);
//        verifyNoInteractions(webAppService);
//    }
//
//    @Test
//    void shouldReturnSignupPageWhenPasswordsDoNotMatch() {
//        SignupForm form = new SignupForm();
//        form.setPassword("password123");
//        form.setConfirmPassword("differentPassword");
//
//        BindingResult bindingResult =
//                new BeanPropertyBindingResult(form, "signupForm");
//
//        String result = authController.processSignup(form, bindingResult);
//
//        assertEquals("signup", result);
//        assertTrue(bindingResult.hasFieldErrors("confirmPassword"));
//        assertEquals(
//                "Les mots de passe ne correspondent pas",
//                bindingResult.getFieldError("confirmPassword").getDefaultMessage()
//        );
//
//        verifyNoInteractions(webAppService);
//    }
//
//    @Test
//    void shouldCreateAccountWhenFormIsValid() {
//        SignupForm form = new SignupForm();
//        form.setUsername("testuser");
//        form.setPrenom("Jean");
//        form.setEmail("test@email.com");
//        form.setPassword("password123");
//        form.setConfirmPassword("password123");
//
//        BindingResult bindingResult =
//                new BeanPropertyBindingResult(form, "signupForm");
//
//        when(webAppService.createUser(form)).thenReturn(new User());
//
//        String result = authController.processSignup(form, bindingResult);
//
//        assertEquals("redirect:/signin", result);
//        verify(webAppService).createUser(form);
//    }
//}
