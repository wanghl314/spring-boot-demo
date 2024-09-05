package com.whl.spring.demo.jmh;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whl.spring.demo.bean.FileInfo;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 3, time = 1)
public class JsonJMHTest {
    private final String json = "{\"Name\":\"a.txt\",\"Size\":\"1111\",\"Last modified\":\"2024-09-05 18:00:00\"}";

    private final FileInfo fileInfo = new FileInfo("a.txt", 1111L, new Date());

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 测试String to Object
     */
    @Benchmark
    public FileInfo objectMapper2ObjTest() throws JsonProcessingException {
        return objectMapper.readValue(json, FileInfo.class);
    }

    /**
     * 测试Object to String
     */
    @Benchmark
    public String objectMapper2StringTest() throws JsonProcessingException {
        return objectMapper.writeValueAsString(fileInfo);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JsonJMHTest.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

}
