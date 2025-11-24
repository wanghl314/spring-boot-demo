package com.whl.spring.demo.controller;

import com.whl.spring.demo.BootstrapTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
public class DemoControllerTests extends BootstrapTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void demoIndexTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/demo"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(MockMvcResultMatchers.content().string("Demo"))
                .andReturn();
    }

    @Test
    public void demoTestTest() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/demo/test"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(MockMvcResultMatchers.header().stringValues("test", "test"))
                .andReturn();
    }

}
