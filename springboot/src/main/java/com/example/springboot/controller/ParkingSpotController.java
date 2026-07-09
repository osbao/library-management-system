package com.example.springboot.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.example.springboot.common.Result;
import com.example.springboot.controller.request.SpotPageRequest;
import com.example.springboot.entity.GarageAdmin;
import com.example.springboot.entity.ParkingSpot;
import com.example.springboot.service.IParkingSpotService;
import com.example.springboot.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/spot")
public class ParkingSpotController {

    @Autowired
    IParkingSpotService spotService;

    private static final String BASE_FILE_PATH = System.getProperty("user.dir") + "/files/";

    @PostMapping("/file/upload")
    public Result file(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            return Result.error("文件上传失败");
        }
        long flag = System.currentTimeMillis();
        String filePath = BASE_FILE_PATH + flag + "_" + originalFilename;
        try {
            FileUtil.mkParentDirs(filePath);
            file.transferTo(FileUtil.file(filePath));
            GarageAdmin currentAdmin = TokenUtils.getCurrentAdmin();
            String token = TokenUtils.genToken(currentAdmin.getId().toString(), 15);
            String url = "http://localhost:9090/api/spot/file/download/" + flag + "?token=" + token;
            if (originalFilename.endsWith(".jpg") || originalFilename.endsWith(".jpeg") || originalFilename.endsWith(".png")) {
                url += "&play=1";
            }
            return Result.success(url);
        } catch (Exception e) {
            log.error("文件上传失败", e);
        }
        return Result.error("文件上传失败");
    }

    @GetMapping("/file/download/{flag}")
    public void downloadFile(@PathVariable String flag, @RequestParam(required = false) String play, HttpServletResponse response) {
        OutputStream os;
        List<String> fileNames = FileUtil.listFileNames(BASE_FILE_PATH);
        String fileName = fileNames.stream().filter(name -> name.contains(flag)).findAny().orElse(" ");
        try {
            if (StrUtil.isNotEmpty(fileName)) {
                String realName = fileName.substring(fileName.indexOf("_") + 1);
                if ("1".equals(play)) {
                    response.addHeader("Content-Disposition", "inline;filename=" + URLEncoder.encode(realName, "UTF-8"));
                } else {
                    response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(realName, "UTF-8"));
                }
                byte[] bytes = FileUtil.readBytes(BASE_FILE_PATH + fileName);
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            log.error("文件下载错误", e);
        }
    }

    @PostMapping("/save")
    public Result save(@RequestBody ParkingSpot obj) {
        spotService.save(obj);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        spotService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody ParkingSpot obj) {
        spotService.update(obj);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        ParkingSpot obj = spotService.getById(id);
        return Result.success(obj);
    }

    @GetMapping("/list")
    public Result list() {
        List<ParkingSpot> list = spotService.list();
        return Result.success(list);
    }

    @GetMapping("/page")
    public Result page(SpotPageRequest pageRequest) {
        return Result.success(spotService.page(pageRequest));
    }

    @GetMapping("/pageByType")
    public Result pageByType(
            @RequestParam(required = false) String spotType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "12") Integer pageSize) {
        return Result.success(spotService.pageByType(spotType, keyword, pageNum, pageSize));
    }
}