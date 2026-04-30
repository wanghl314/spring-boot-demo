package com.whl.spring.demo.controller;

import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import oshi.hardware.*;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(OshiController.OSHI_URI)
public class OshiController {
    static final String OSHI_URI = "/oshi";

    @GetMapping
    public Map<String, String> stat() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("os", OSHI_URI + "/os");
        map.put("currentProcess", OSHI_URI + "/currentProcess");
        map.put("hardware", OSHI_URI + "/hardware");
        map.put("system", OSHI_URI + "/system");
        map.put("memory", OSHI_URI + "/memory");
        map.put("processor", OSHI_URI + "/processor");
        map.put("sensors", OSHI_URI + "/sensors");
        map.put("diskStores", OSHI_URI + "/diskStores");
        map.put("networkIFs", OSHI_URI + "/networkIFs");
        map.put("cpuInfo", OSHI_URI + "/cpuInfo");
        return map;
    }

    @GetMapping("/os")
    public OperatingSystem os() throws Exception {
        return OshiUtil.getOs();
    }

    @GetMapping("/currentProcess")
    public OSProcess currentProcess() throws Exception {
        return OshiUtil.getCurrentProcess();
    }

    @GetMapping("/hardware")
    public HardwareAbstractionLayer hardware() throws Exception {
        return OshiUtil.getHardware();
    }

    @GetMapping("/system")
    public ComputerSystem system() throws Exception {
        return OshiUtil.getSystem();
    }

    @GetMapping("/memory")
    public GlobalMemory memory() throws Exception {
        return OshiUtil.getMemory();
    }

    @GetMapping("/processor")
    public CentralProcessor processor() throws Exception {
        return OshiUtil.getProcessor();
    }

    @GetMapping("/sensors")
    public Sensors sensors() throws Exception {
        return OshiUtil.getSensors();
    }

    @GetMapping("/diskStores")
    public List<HWDiskStore> diskStores() throws Exception {
        return OshiUtil.getDiskStores();
    }

    @GetMapping("/networkIFs")
    public List<NetworkIF> networkIFs() throws Exception {
        return OshiUtil.getNetworkIFs();
    }

    @GetMapping("/cpuInfo")
    public CpuInfo cpuInfo() throws Exception {
        return OshiUtil.getCpuInfo();
    }

}
